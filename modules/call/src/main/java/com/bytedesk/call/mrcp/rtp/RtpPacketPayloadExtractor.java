package com.bytedesk.call.mrcp.rtp;

/**
 * Minimal RTP packet parser that exposes the payload slice.
 */
public class RtpPacketPayloadExtractor {

    private static final int RTP_MIN_HEADER_SIZE = 12;
    private static final int RTP_VERSION = 2;

    public PayloadSlice extract(byte[] packet) {
        if (packet == null) {
            throw new IllegalArgumentException("packet must not be null");
        }
        return extract(packet, 0, packet.length);
    }

    public PayloadSlice extract(byte[] packet, int offset, int length) {
        if (packet == null) {
            throw new IllegalArgumentException("packet must not be null");
        }
        if (offset < 0 || length < 0 || offset + length > packet.length) {
            throw new IllegalArgumentException("Invalid RTP packet bounds");
        }
        if (length < RTP_MIN_HEADER_SIZE) {
            throw new IllegalArgumentException("RTP packet too short");
        }

        int firstByte = packet[offset] & 0xFF;
        int version = (firstByte >> 6) & 0x03;
        if (version != RTP_VERSION) {
            throw new IllegalArgumentException("Unsupported RTP version: " + version);
        }

        int payloadType = packet[offset + 1] & 0x7F;
        boolean hasPadding = (firstByte & 0x20) != 0;
        boolean hasExtension = (firstByte & 0x10) != 0;
        int csrcCount = firstByte & 0x0F;

        long ssrc = ((packet[offset + 8] & 0xFFL) << 24)
                | ((packet[offset + 9] & 0xFFL) << 16)
                | ((packet[offset + 10] & 0xFFL) << 8)
                | (packet[offset + 11] & 0xFFL);

        int payloadOffset = offset + RTP_MIN_HEADER_SIZE + (csrcCount * 4);
        if (payloadOffset > offset + length) {
            throw new IllegalArgumentException("Invalid RTP CSRC header length");
        }

        if (hasExtension) {
            if (payloadOffset + 4 > offset + length) {
                throw new IllegalArgumentException("Invalid RTP extension header");
            }
            int extensionLengthWords = ((packet[payloadOffset + 2] & 0xFF) << 8)
                    | (packet[payloadOffset + 3] & 0xFF);
            payloadOffset += 4 + (extensionLengthWords * 4);
            if (payloadOffset > offset + length) {
                throw new IllegalArgumentException("Invalid RTP extension payload length");
            }
        }

        int payloadLength = (offset + length) - payloadOffset;
        if (hasPadding) {
            int paddingLength = packet[offset + length - 1] & 0xFF;
            payloadLength -= paddingLength;
        }
        if (payloadLength < 0) {
            throw new IllegalArgumentException("Invalid RTP payload length");
        }

        return new PayloadSlice(packet, payloadOffset, payloadLength, ssrc, payloadType);
    }

    public record PayloadSlice(byte[] packet, int offset, int length, long ssrc, int payloadType) {
    }
}