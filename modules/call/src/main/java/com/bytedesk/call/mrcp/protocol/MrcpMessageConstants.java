package com.bytedesk.call.mrcp.protocol;

/**
 * Minimal MRCP constants shared by the internal protocol layer.
 */
public final class MrcpMessageConstants {

    public static final String RESOURCE_HEADER = "Channel-Identifier";
    public static final String CONTENT_TYPE_HEADER = "Content-Type";
    public static final String REQUEST_STATE_IN_PROGRESS = "IN-PROGRESS";
    public static final String REQUEST_STATE_COMPLETE = "COMPLETE";
    public static final String EVENT_SPEAK_COMPLETE = "SPEAK-COMPLETE";
    public static final String EVENT_RECOGNITION_COMPLETE = "RECOGNITION-COMPLETE";

    private MrcpMessageConstants() {
    }
}