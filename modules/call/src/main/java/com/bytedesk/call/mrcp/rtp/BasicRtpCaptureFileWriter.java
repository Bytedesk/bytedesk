package com.bytedesk.call.mrcp.rtp;

import com.bytedesk.call.mrcp.media.RtpCaptureControl;
import com.bytedesk.call.mrcp.media.RtpCaptureOutput;
import com.bytedesk.call.mrcp.media.RtpCaptureRoute;
import com.bytedesk.call.mrcp.protocol.MrcpMediaResource;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Minimal file-backed RTP capture writer.
 */
public class BasicRtpCaptureFileWriter implements RtpCaptureFileWriter {

    @Override
    public RtpCaptureRoute openFileCapture(String filePath, String contentType, RtpCaptureControl control) {
        try {
            Path path = Path.of(filePath);
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            OutputStream outputStream = new BufferedOutputStream(
                    Files.newOutputStream(
                            path,
                            StandardOpenOption.CREATE,
                            StandardOpenOption.TRUNCATE_EXISTING,
                            StandardOpenOption.WRITE));
            FileCaptureOutput output = new FileCaptureOutput(outputStream, control);
            return new RtpCaptureRoute(MrcpMediaResource.file(filePath, contentType), control, output);
        } catch (IOException exception) {
            if (control != null) {
                control.markFailed(exception);
            }
            throw new IllegalStateException("Failed to open RTP capture file: " + filePath, exception);
        }
    }

    private static final class FileCaptureOutput implements RtpCaptureOutput {

        private final OutputStream outputStream;
        private final RtpCaptureControl control;
        private boolean finished;
        private boolean capturing;

        private FileCaptureOutput(OutputStream outputStream, RtpCaptureControl control) {
            this.outputStream = outputStream;
            this.control = control;
        }

        @Override
        public synchronized void write(byte[] payload) {
            if (payload == null) {
                throw new IllegalArgumentException("payload must not be null");
            }
            write(payload, 0, payload.length);
        }

        @Override
        public synchronized void write(byte[] payload, int offset, int length) {
            if (finished) {
                throw new IllegalStateException("capture output already finished");
            }
            if (!capturing) {
                capturing = true;
                if (control != null) {
                    control.markCapturing();
                }
            }
            try {
                outputStream.write(payload, offset, length);
            } catch (IOException exception) {
                fail(exception);
                throw new IllegalStateException("Failed to write RTP capture payload", exception);
            }
        }

        @Override
        public synchronized void complete() {
            if (finished) {
                return;
            }
            try {
                outputStream.flush();
                outputStream.close();
                finished = true;
                if (control != null) {
                    control.markCompleted();
                }
            } catch (IOException exception) {
                fail(exception);
                throw new IllegalStateException("Failed to complete RTP capture output", exception);
            }
        }

        @Override
        public synchronized void fail(Exception exception) {
            if (finished) {
                return;
            }
            finished = true;
            try {
                outputStream.close();
            } catch (IOException ignored) {
                // Best-effort close; original failure is reported through control callback.
            }
            if (control != null) {
                control.markFailed(exception != null ? exception : new IllegalStateException("RTP capture failed"));
            }
        }

        @Override
        public synchronized void close() {
            if (!finished) {
                complete();
            }
        }
    }
}