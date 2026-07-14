package com.bytedesk.call.mrcp.rtp;

import com.bytedesk.call.mrcp.protocol.MrcpSessionDescriptor;

/**
 * Creates minimal RTP session metadata from the internal session descriptor.
 */
public class BytedeskRtpSessionFactory {

    public RtpSession create(MrcpSessionDescriptor descriptor) {
        if (descriptor == null) {
            throw new IllegalArgumentException("descriptor must not be null");
        }
        if (descriptor.audioHost() == null || descriptor.audioPort() == null) {
            throw new IllegalArgumentException("audio host and audio port are required");
        }
        return new RtpSession(descriptor.audioHost(), descriptor.audioPort(), descriptor.codecs());
    }

    public record RtpSession(String host, Integer port, java.util.List<String> codecs) {
    }
}
