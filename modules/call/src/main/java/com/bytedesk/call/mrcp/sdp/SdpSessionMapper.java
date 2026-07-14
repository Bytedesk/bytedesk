package com.bytedesk.call.mrcp.sdp;

import com.bytedesk.call.mrcp.protocol.MrcpSessionDescriptor;

/**
 * Maps parsed SDP information into the internal MRCP session descriptor.
 */
public class SdpSessionMapper {

    public MrcpSessionDescriptor map(BytedeskSdpParser.ParsedSdp parsedSdp) {
        if (parsedSdp == null) {
            throw new IllegalArgumentException("parsedSdp must not be null");
        }
        BytedeskSdpParser.ParsedMediaDescription mrcpMedia = parsedSdp.mrcpMedia();
        BytedeskSdpParser.ParsedMediaDescription audioMedia = parsedSdp.audioMedia();
        if (mrcpMedia == null && audioMedia == null) {
            throw new IllegalArgumentException("parsedSdp must contain at least one media description");
        }

        MrcpSessionDescriptor.Builder builder = MrcpSessionDescriptor.builder();
        if (mrcpMedia != null) {
            builder.resource(mrcpMedia.resource())
                    .mrcpHost(mrcpMedia.connectionHost())
                    .mrcpPort(mrcpMedia.port());
        }
        if (audioMedia != null) {
            builder.audioHost(audioMedia.connectionHost())
                    .audioPort(audioMedia.port())
                    .codecs(audioMedia.codecs());
        }
        return builder.build();
    }
}
