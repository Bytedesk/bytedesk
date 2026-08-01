package com.bytedesk.call.mrcp.sdp;

import java.util.ArrayList;
import java.util.List;

/**
 * Minimal SDP parser for extracting MRCP and audio media sections.
 */
public class BytedeskSdpParser {

    public ParsedSdp parse(String sdp) {
        if (sdp == null || sdp.isBlank()) {
            throw new IllegalArgumentException("sdp must not be blank");
        }

        String sessionHost = null;
        ParsedMediaDescription mrcpMedia = null;
        ParsedMediaDescription audioMedia = null;

        ParsedMediaDescription currentMedia = null;
        for (String rawLine : sdp.split("\\r?\\n")) {
            String line = rawLine == null ? "" : rawLine.trim();
            if (line.isEmpty()) {
                continue;
            }
            if (line.startsWith("c=")) {
                String host = parseConnectionHost(line);
                if (currentMedia == null) {
                    sessionHost = host;
                } else {
                    currentMedia.connectionHost(host);
                }
                continue;
            }
            if (line.startsWith("m=")) {
                currentMedia = parseMediaLine(line);
                if (currentMedia.isMrcp()) {
                    mrcpMedia = currentMedia;
                } else if (currentMedia.isAudio()) {
                    audioMedia = currentMedia;
                }
                continue;
            }
            if (currentMedia == null) {
                continue;
            }
            if (line.startsWith("a=rtpmap:")) {
                String codec = parseRtpmapCodec(line);
                if (codec != null) {
                    currentMedia.codecs().add(codec);
                }
                continue;
            }
            if (line.startsWith("a=resource:")) {
                currentMedia.resource(parseAttributeValue(line));
                continue;
            }
            if (line.startsWith("a=setup:")) {
                currentMedia.setup(parseAttributeValue(line));
                continue;
            }
            if (line.startsWith("a=channel:")) {
                currentMedia.channel(parseAttributeValue(line));
            }
        }

        applyDefaultHost(sessionHost, mrcpMedia);
        applyDefaultHost(sessionHost, audioMedia);
        return new ParsedSdp(sessionHost, mrcpMedia, audioMedia);
    }

    private ParsedMediaDescription parseMediaLine(String line) {
        String[] segments = line.substring(2).trim().split("\\s+");
        if (segments.length < 2) {
            throw new IllegalArgumentException("Invalid media line: " + line);
        }
        String mediaType = segments[0];
        Integer port = Integer.parseInt(segments[1]);
        List<String> payloads = new ArrayList<>();
        for (int i = 3; i < segments.length; i++) {
            payloads.add(segments[i]);
        }
        return new ParsedMediaDescription(mediaType, port, payloads);
    }

    private String parseConnectionHost(String line) {
        String[] segments = line.substring(2).trim().split("\\s+");
        return segments.length == 0 ? null : segments[segments.length - 1];
    }

    private String parseRtpmapCodec(String line) {
        int spaceIndex = line.indexOf(' ');
        if (spaceIndex < 0 || spaceIndex == line.length() - 1) {
            return null;
        }
        String codecSegment = line.substring(spaceIndex + 1);
        int slashIndex = codecSegment.indexOf('/');
        return slashIndex > 0 ? codecSegment.substring(0, slashIndex) : codecSegment;
    }

    private String parseAttributeValue(String line) {
        int colonIndex = line.indexOf(':');
        if (colonIndex < 0 || colonIndex == line.length() - 1) {
            return null;
        }
        return line.substring(colonIndex + 1).trim();
    }

    private void applyDefaultHost(String sessionHost, ParsedMediaDescription media) {
        if (media != null && media.connectionHost() == null) {
            media.connectionHost(sessionHost);
        }
    }

    public record ParsedSdp(String sessionHost, ParsedMediaDescription mrcpMedia, ParsedMediaDescription audioMedia) {
    }

    public static final class ParsedMediaDescription {
        private final String mediaType;
        private final Integer port;
        private final List<String> payloads;
        private final List<String> codecs = new ArrayList<>();
        private String connectionHost;
        private String resource;
        private String setup;
        private String channel;

        ParsedMediaDescription(String mediaType, Integer port, List<String> payloads) {
            this.mediaType = mediaType;
            this.port = port;
            this.payloads = payloads == null ? List.of() : List.copyOf(payloads);
        }

        public boolean isAudio() {
            return "audio".equalsIgnoreCase(mediaType);
        }

        public boolean isMrcp() {
            return "application".equalsIgnoreCase(mediaType);
        }

        public String mediaType() {
            return mediaType;
        }

        public Integer port() {
            return port;
        }

        public List<String> payloads() {
            return payloads;
        }

        public List<String> codecs() {
            return codecs;
        }

        public String connectionHost() {
            return connectionHost;
        }

        public void connectionHost(String connectionHost) {
            this.connectionHost = connectionHost;
        }

        public String resource() {
            return resource;
        }

        public void resource(String resource) {
            this.resource = resource;
        }

        public String setup() {
            return setup;
        }

        public void setup(String setup) {
            this.setup = setup;
        }

        public String channel() {
            return channel;
        }

        public void channel(String channel) {
            this.channel = channel;
        }
    }
}
