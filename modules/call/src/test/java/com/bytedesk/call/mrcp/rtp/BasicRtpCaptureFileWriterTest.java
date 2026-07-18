package com.bytedesk.call.mrcp.rtp;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.bytedesk.call.mrcp.media.RtpCaptureRoute;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class BasicRtpCaptureFileWriterTest {

    @TempDir
    Path tempDir;

    @Test
    void writesWaveHeaderForPcmCapture() throws Exception {
        Path captureFile = tempDir.resolve("capture.wav");
        BasicRtpCaptureFileWriter writer = new BasicRtpCaptureFileWriter();

        RtpCaptureRoute route = writer.openFileCapture(
                captureFile.toString(),
                "audio/wav",
                "pcm_s16le",
                8000,
                null);

        byte[] payload = new byte[] {1, 2, 3, 4};
        route.output().write(payload);
        route.output().complete();

        byte[] bytes = Files.readAllBytes(captureFile);
        assertEquals(48, bytes.length);
        assertEquals("RIFF", new String(bytes, 0, 4, StandardCharsets.US_ASCII));
        assertEquals("WAVE", new String(bytes, 8, 4, StandardCharsets.US_ASCII));
        assertEquals("data", new String(bytes, 36, 4, StandardCharsets.US_ASCII));

        ByteBuffer header = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        assertEquals(40, header.getInt(4));
        assertEquals(1, header.getShort(20));
        assertEquals(1, header.getShort(22));
        assertEquals(8000, header.getInt(24));
        assertEquals(16000, header.getInt(28));
        assertEquals(2, header.getShort(32));
        assertEquals(16, header.getShort(34));
        assertEquals(4, header.getInt(40));
        assertArrayEquals(payload, java.util.Arrays.copyOfRange(bytes, 44, 48));
    }

    @Test
    void writesWaveHeaderForPcmuCapture() throws Exception {
        Path captureFile = tempDir.resolve("capture-pcmu.wav");
        BasicRtpCaptureFileWriter writer = new BasicRtpCaptureFileWriter();

        RtpCaptureRoute route = writer.openFileCapture(
                captureFile.toString(),
                "audio/wav",
                "PCMU/8000",
                8000,
                null);

        byte[] payload = new byte[] {0x11, 0x22, 0x33, 0x44};
        route.output().write(payload);
        route.output().complete();

        byte[] bytes = Files.readAllBytes(captureFile);
        assertEquals(48, bytes.length);

        ByteBuffer header = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        assertEquals(40, header.getInt(4));
        assertEquals(7, header.getShort(20));
        assertEquals(1, header.getShort(22));
        assertEquals(8000, header.getInt(24));
        assertEquals(8000, header.getInt(28));
        assertEquals(1, header.getShort(32));
        assertEquals(8, header.getShort(34));
        assertEquals(4, header.getInt(40));
        assertArrayEquals(payload, java.util.Arrays.copyOfRange(bytes, 44, 48));
    }
}