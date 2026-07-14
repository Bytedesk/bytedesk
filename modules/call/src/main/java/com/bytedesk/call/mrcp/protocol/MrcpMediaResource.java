package com.bytedesk.call.mrcp.protocol;

/**
 * Minimal media resource abstraction exposed from the call MRCP base layer.
 */
public record MrcpMediaResource(
        MediaResourceType type,
        String location,
        String contentType) {

    public static MrcpMediaResource file(String location, String contentType) {
        return new MrcpMediaResource(MediaResourceType.FILE, location, contentType);
    }

    public static MrcpMediaResource pipe(String location, String contentType) {
        return new MrcpMediaResource(MediaResourceType.PIPE, location, contentType);
    }

    public enum MediaResourceType {
        FILE,
        PIPE
    }
}
