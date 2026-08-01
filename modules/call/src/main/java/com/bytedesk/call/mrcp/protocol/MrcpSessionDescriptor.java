package com.bytedesk.call.mrcp.protocol;

import java.util.ArrayList;
import java.util.List;

/**
 * Minimal MRCP session view extracted from SDP or other signaling inputs.
 */
public record MrcpSessionDescriptor(
        String resource,
        String audioHost,
        Integer audioPort,
        String mrcpHost,
        Integer mrcpPort,
        List<String> codecs) {

    public MrcpSessionDescriptor {
        codecs = codecs == null ? List.of() : List.copyOf(codecs);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String resource;
        private String audioHost;
        private Integer audioPort;
        private String mrcpHost;
        private Integer mrcpPort;
        private final List<String> codecs = new ArrayList<>();

        public Builder resource(String resource) {
            this.resource = resource;
            return this;
        }

        public Builder audioHost(String audioHost) {
            this.audioHost = audioHost;
            return this;
        }

        public Builder audioPort(Integer audioPort) {
            this.audioPort = audioPort;
            return this;
        }

        public Builder mrcpHost(String mrcpHost) {
            this.mrcpHost = mrcpHost;
            return this;
        }

        public Builder mrcpPort(Integer mrcpPort) {
            this.mrcpPort = mrcpPort;
            return this;
        }

        public Builder addCodec(String codec) {
            if (codec != null && !codec.isBlank()) {
                this.codecs.add(codec);
            }
            return this;
        }

        public Builder codecs(List<String> codecs) {
            this.codecs.clear();
            if (codecs != null) {
                codecs.stream().filter(codec -> codec != null && !codec.isBlank()).forEach(this.codecs::add);
            }
            return this;
        }

        public MrcpSessionDescriptor build() {
            return new MrcpSessionDescriptor(resource, audioHost, audioPort, mrcpHost, mrcpPort, codecs);
        }
    }
}
