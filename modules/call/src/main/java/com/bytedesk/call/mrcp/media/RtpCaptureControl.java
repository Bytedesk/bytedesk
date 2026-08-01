package com.bytedesk.call.mrcp.media;

/**
 * Generic lifecycle control for an RTP capture route.
 */
public interface RtpCaptureControl {

    void markCapturing();

    void markCompleted();

    void markFailed(Exception exception);
}