package com.bytedesk.call.mrcp.rtp;

import com.bytedesk.call.mrcp.media.RtpCaptureControl;
import com.bytedesk.call.mrcp.media.RtpCaptureOutput;
import com.bytedesk.call.mrcp.media.RtpCaptureRoute;
import com.bytedesk.call.mrcp.protocol.MrcpMediaResource;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import org.springframework.util.StringUtils;

/**
 * Minimal file-backed RTP capture writer.
 */
public class BasicRtpCaptureFileWriter implements RtpCaptureFileWriter {

    private static final int WAV_HEADER_SIZE = 44;

    @Override
    public RtpCaptureRoute openFileCapture(
            String filePath,
            String contentType,
            String codec,
            int sampleRate,
            RtpCaptureControl control) {
        try {
            Path path = Path.of(filePath);
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            FileCaptureOutput output = createOutput(path, contentType, codec, sampleRate, control);
            return new RtpCaptureRoute(MrcpMediaResource.file(filePath, contentType), control, output);
        } catch (IOException exception) {
            if (control != null) {
                control.markFailed(exception);
            }
            throw new IllegalStateException("Failed to open RTP capture file: " + filePath, exception);
        }
    }

    private FileCaptureOutput createOutput(
            Path path,
            String contentType,
            String codec,
            int sampleRate,
            RtpCaptureControl control) throws IOException {
        if (isWaveContentType(contentType)) {
            WavFormat wavFormat = WavFormat.resolve(codec, sampleRate);
            FileChannel fileChannel = FileChannel.open(
                    path,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);
            fileChannel.write(ByteBuffer.wrap(new byte[WAV_HEADER_SIZE]));
            OutputStream outputStream = new BufferedOutputStream(Channels.newOutputStream(fileChannel));
            return new WavFileCaptureOutput(fileChannel, outputStream, control, wavFormat);
        }

        OutputStream outputStream = new BufferedOutputStream(
                Files.newOutputStream(
                        path,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING,
                        StandardOpenOption.WRITE));
        return new FileCaptureOutput(outputStream, control);
    }

    private boolean isWaveContentType(String contentType) {
        return StringUtils.hasText(contentType)
                && contentType.trim().toLowerCase().startsWith("audio/wav");
    }

    private static class FileCaptureOutput implements RtpCaptureOutput {

        protected final OutputStream outputStream;
        protected final RtpCaptureControl control;
        protected boolean finished;
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

    private static final class WavFileCaptureOutput extends FileCaptureOutput {

        private final FileChannel fileChannel;
        private final WavFormat wavFormat;
        private long payloadBytes;

        private WavFileCaptureOutput(
                FileChannel fileChannel,
                OutputStream outputStream,
                RtpCaptureControl control,
                WavFormat wavFormat) {
            super(outputStream, control);
            this.fileChannel = fileChannel;
            this.wavFormat = wavFormat;
        }

        @Override
        public synchronized void write(byte[] payload, int offset, int length) {
            super.write(payload, offset, length);
            payloadBytes += length;
        }

        @Override
        public synchronized void complete() {
            if (finished) {
                return;
            }
            try {
                outputStream.flush();
                fileChannel.position(0);
                fileChannel.write(buildWavHeader(payloadBytes, wavFormat));
                outputStream.close();
                finished = true;
                if (control != null) {
                    control.markCompleted();
                }
            } catch (IOException exception) {
                fail(exception);
                throw new IllegalStateException("Failed to complete WAV RTP capture output", exception);
            }
        }
    }

    private static ByteBuffer buildWavHeader(long payloadBytes, WavFormat format) {
        int dataSize = Math.toIntExact(payloadBytes);
        int chunkSize = 36 + dataSize;
        ByteBuffer buffer = ByteBuffer.allocate(WAV_HEADER_SIZE).order(ByteOrder.LITTLE_ENDIAN);
        buffer.put((byte) 'R').put((byte) 'I').put((byte) 'F').put((byte) 'F');
        buffer.putInt(chunkSize);
        buffer.put((byte) 'W').put((byte) 'A').put((byte) 'V').put((byte) 'E');
        buffer.put((byte) 'f').put((byte) 'm').put((byte) 't').put((byte) ' ');
        buffer.putInt(16);
        buffer.putShort((short) format.formatCode());
        buffer.putShort((short) 1);
        buffer.putInt(format.sampleRate());
        buffer.putInt(format.byteRate());
        buffer.putShort((short) format.blockAlign());
        buffer.putShort((short) format.bitsPerSample());
        buffer.put((byte) 'd').put((byte) 'a').put((byte) 't').put((byte) 'a');
        buffer.putInt(dataSize);
        buffer.flip();
        return buffer;
    }

    private record WavFormat(int formatCode, int sampleRate, int bitsPerSample) {

        private static WavFormat resolve(String codec, int sampleRate) {
            int resolvedSampleRate = sampleRate > 0 ? sampleRate : 8000;
            String normalizedCodec = StringUtils.hasText(codec) ? codec.trim().toLowerCase() : "pcm_s16le";
            return switch (normalizedCodec) {
                case "pcm", "pcm_s16le", "l16", "linear16" -> new WavFormat(1, resolvedSampleRate, 16);
                case "pcmu", "g711_ulaw", "mulaw", "ulaw" -> new WavFormat(7, resolvedSampleRate, 8);
                case "pcma", "g711_alaw", "alaw" -> new WavFormat(6, resolvedSampleRate, 8);
                default -> throw new IllegalArgumentException("Unsupported WAV capture codec: " + codec);
            };
        }

        private int blockAlign() {
            return Math.max(1, bitsPerSample / 8);
        }

        private int byteRate() {
            return sampleRate * blockAlign();
        }
    }
}