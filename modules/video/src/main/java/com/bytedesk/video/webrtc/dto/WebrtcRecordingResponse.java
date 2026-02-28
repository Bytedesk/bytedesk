package com.bytedesk.video.webrtc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebrtcRecordingResponse {

    private String callUid;

    private String actorUid;

    /**
     * Stored file name on server.
     */
    private String filename;

    /**
     * Relative path under record dir (uses '/' separators).
     */
    private String path;

    private Long size;

    private String contentType;
}
