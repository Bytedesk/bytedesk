package com.bytedesk.call.mrcp.rtp;

import com.bytedesk.call.mrcp.media.RtpCaptureRoute;

/**
 * Minimal receiver that accepts RTP packet bytes and forwards decoded payload into a capture route.
 */
public class RtpCaptureReceiver {

    private static final long SSRC_UNRESOLVED = -1L;
    private static final int PT_UNRESOLVED = -1;

    private final BytedeskRtpSessionFactory.RtpSession session;
    private final RtpCaptureRoute captureRoute;
    private final RtpPayloadRouter payloadRouter;
    private final RtpPacketPayloadExtractor payloadExtractor;

    private boolean finished;
    private volatile long resolvedSsrc = SSRC_UNRESOLVED;
    private volatile int resolvedPayloadType = PT_UNRESOLVED;

    public RtpCaptureReceiver(BytedeskRtpSessionFactory.RtpSession session, RtpCaptureRoute captureRoute) {
        this(session, captureRoute, new RtpPayloadRouter(), new RtpPacketPayloadExtractor());
    }

    public RtpCaptureReceiver(
            BytedeskRtpSessionFactory.RtpSession session,
            RtpCaptureRoute captureRoute,
            RtpPayloadRouter payloadRouter,
            RtpPacketPayloadExtractor payloadExtractor) {
        if (session == null) {
            throw new IllegalArgumentException("session must not be null");
        }
        if (captureRoute == null) {
            throw new IllegalArgumentException("captureRoute must not be null");
        }
        if (payloadRouter == null) {
            throw new IllegalArgumentException("payloadRouter must not be null");
        }
        if (payloadExtractor == null) {
            throw new IllegalArgumentException("payloadExtractor must not be null");
        }
        this.session = session;
        this.captureRoute = captureRoute;
        this.payloadRouter = payloadRouter;
        this.payloadExtractor = payloadExtractor;
    }

    public void acceptPacket(byte[] packet) {
        if (packet == null) {
            throw new IllegalArgumentException("packet must not be null");
        }
        acceptPacket(packet, 0, packet.length);
    }

    public void acceptPacket(byte[] packet, int offset, int length) {
        ensureActive();
        RtpPacketPayloadExtractor.PayloadSlice payloadSlice = payloadExtractor.extract(packet, offset, length);
        anchorOrValidateSession(payloadSlice);
        payloadRouter.writePayload(
                captureRoute,
                payloadSlice.packet(),
                payloadSlice.offset(),
                payloadSlice.length());
    }

    private void anchorOrValidateSession(RtpPacketPayloadExtractor.PayloadSlice payloadSlice) {
        if (resolvedSsrc == SSRC_UNRESOLVED) {
            resolvedSsrc = payloadSlice.ssrc();
            resolvedPayloadType = payloadSlice.payloadType();
            return;
        }
        if (resolvedSsrc != payloadSlice.ssrc()) {
            throw new IllegalStateException(
                    "SSRC mismatch: expected " + resolvedSsrc + " but received " + payloadSlice.ssrc());
        }
        if (resolvedPayloadType != payloadSlice.payloadType()) {
            throw new IllegalStateException(
                    "Payload type mismatch: expected " + resolvedPayloadType + " but received " + payloadSlice.payloadType());
        }
    }

    public void complete() {
        if (finished) {
            return;
        }
        finished = true;
        payloadRouter.completeCapture(captureRoute);
    }

    public void fail(Exception exception) {
        if (finished) {
            return;
        }
        finished = true;
        payloadRouter.failCapture(captureRoute, exception);
    }

    public BytedeskRtpSessionFactory.RtpSession session() {
        return session;
    }

    public RtpCaptureRoute captureRoute() {
        return captureRoute;
    }

    private void ensureActive() {
        if (finished) {
            throw new IllegalStateException("RTP capture receiver already finished");
        }
    }
}