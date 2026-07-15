package com.bytedesk.call.mrcp.rtp;

import com.bytedesk.call.mrcp.media.RtpCaptureControl;
import com.bytedesk.call.mrcp.media.RtpCaptureRoute;

/**
 * Opens an RTP-to-file capture route and owns the production side of capture lifecycle callbacks.
 */
public interface RtpCaptureFileWriter {

    default RtpCaptureRoute openFileCapture(String filePath, String contentType, RtpCaptureControl control) {
        return openFileCapture(filePath, contentType, null, 0, control);
    }

    RtpCaptureRoute openFileCapture(
            String filePath,
            String contentType,
            String codec,
            int sampleRate,
            RtpCaptureControl control);
}