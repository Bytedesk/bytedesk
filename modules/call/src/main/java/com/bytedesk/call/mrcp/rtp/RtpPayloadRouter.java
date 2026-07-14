package com.bytedesk.call.mrcp.rtp;

import com.bytedesk.call.mrcp.media.RtpCaptureControl;
import com.bytedesk.call.mrcp.media.RtpCaptureOutput;
import com.bytedesk.call.mrcp.media.RtpCaptureRoute;
import com.bytedesk.call.mrcp.protocol.MrcpMediaResource;

/**
 * Decides how RTP payload should be exposed to upper layers.
 */
public class RtpPayloadRouter {

    private final RtpCaptureFileWriter captureFileWriter;

    public RtpPayloadRouter() {
        this(new BasicRtpCaptureFileWriter());
    }

    public RtpPayloadRouter(RtpCaptureFileWriter captureFileWriter) {
        this.captureFileWriter = captureFileWriter;
    }

    public MrcpMediaResource routeToFile(String filePath, String contentType) {
        return MrcpMediaResource.file(filePath, contentType);
    }

    public RtpCaptureRoute routeToFile(String filePath, String contentType, RtpCaptureControl control) {
        return captureFileWriter.openFileCapture(filePath, contentType, control);
    }

    public void writePayload(RtpCaptureRoute captureRoute, byte[] payload) {
        if (payload == null) {
            throw new IllegalArgumentException("payload must not be null");
        }
        writePayload(captureRoute, payload, 0, payload.length);
    }

    public void writePayload(RtpCaptureRoute captureRoute, byte[] payload, int offset, int length) {
        resolveOutput(captureRoute).write(payload, offset, length);
    }

    public void completeCapture(RtpCaptureRoute captureRoute) {
        resolveOutput(captureRoute).complete();
    }

    public void failCapture(RtpCaptureRoute captureRoute, Exception exception) {
        resolveOutput(captureRoute).fail(exception);
    }

    public MrcpMediaResource routeToPipe(String pipePath, String contentType) {
        return MrcpMediaResource.pipe(pipePath, contentType);
    }

    private RtpCaptureOutput resolveOutput(RtpCaptureRoute captureRoute) {
        if (captureRoute == null) {
            throw new IllegalArgumentException("captureRoute must not be null");
        }
        if (captureRoute.output() == null) {
            throw new IllegalStateException("capture route does not expose an output sink");
        }
        return captureRoute.output();
    }
}
