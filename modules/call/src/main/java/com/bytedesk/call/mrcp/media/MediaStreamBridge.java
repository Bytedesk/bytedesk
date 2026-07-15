package com.bytedesk.call.mrcp.media;

import com.bytedesk.call.mrcp.protocol.MrcpMediaResource;
import com.bytedesk.call.mrcp.rtp.RtpPayloadRouter;

/**
 * Minimal bridge that exposes RTP/file/pipe inputs as a common media resource.
 */
public class MediaStreamBridge {

    private final RtpPayloadRouter payloadRouter;

    public MediaStreamBridge() {
        this(new RtpPayloadRouter());
    }

    public MediaStreamBridge(RtpPayloadRouter payloadRouter) {
        this.payloadRouter = payloadRouter;
    }

    public MrcpMediaResource fromFile(String filePath, String contentType) {
        return MrcpMediaResource.file(filePath, contentType);
    }

    public MrcpMediaResource fromPipe(String pipePath, String contentType) {
        return MrcpMediaResource.pipe(pipePath, contentType);
    }

    public MrcpMediaResource fromRtpToFile(String filePath, String contentType) {
        return payloadRouter.routeToFile(filePath, contentType);
    }

    public RtpCaptureRoute openRtpToFileCapture(String filePath, String contentType, RtpCaptureControl control) {
        return payloadRouter.routeToFile(filePath, contentType, control);
    }

    public RtpCaptureRoute openRtpToFileCapture(
            String filePath,
            String contentType,
            String codec,
            int sampleRate,
            RtpCaptureControl control) {
        return payloadRouter.routeToFile(filePath, contentType, codec, sampleRate, control);
    }

    public void writeRtpPayload(RtpCaptureRoute captureRoute, byte[] payload) {
        payloadRouter.writePayload(captureRoute, payload);
    }

    public void writeRtpPayload(RtpCaptureRoute captureRoute, byte[] payload, int offset, int length) {
        payloadRouter.writePayload(captureRoute, payload, offset, length);
    }

    public void completeRtpCapture(RtpCaptureRoute captureRoute) {
        payloadRouter.completeCapture(captureRoute);
    }

    public void failRtpCapture(RtpCaptureRoute captureRoute, Exception exception) {
        payloadRouter.failCapture(captureRoute, exception);
    }

    public MrcpMediaResource fromRtpToPipe(String pipePath, String contentType) {
        return payloadRouter.routeToPipe(pipePath, contentType);
    }
}
