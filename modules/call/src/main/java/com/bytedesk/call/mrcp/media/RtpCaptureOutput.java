package com.bytedesk.call.mrcp.media;

/**
 * Writable RTP capture sink exposed from the media layer.
 */
public interface RtpCaptureOutput extends AutoCloseable {

    void write(byte[] payload);

    void write(byte[] payload, int offset, int length);

    void complete();

    void fail(Exception exception);

    @Override
    void close();
}