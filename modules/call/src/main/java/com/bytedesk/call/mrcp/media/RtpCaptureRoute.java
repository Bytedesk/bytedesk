package com.bytedesk.call.mrcp.media;

import com.bytedesk.call.mrcp.protocol.MrcpMediaResource;

/**
 * RTP capture route with a media resource and lifecycle control hook.
 */
public record RtpCaptureRoute(
        MrcpMediaResource mediaResource,
        RtpCaptureControl control,
        RtpCaptureOutput output) {
}