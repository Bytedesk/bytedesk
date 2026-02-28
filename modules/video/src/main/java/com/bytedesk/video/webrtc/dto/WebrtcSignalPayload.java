package com.bytedesk.video.webrtc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebrtcSignalPayload {

    private String callUid;

    private String threadUid;

    private Long roomId;

    private Boolean record;

    private String recordFilename;

    private String callerUid;

    private String calleeUid;

    // TEXT/AUDIO/VIDEO/PHONE
    private String callType;

    // VIDEO mode: ONE_WAY/TWO_WAY
    private String videoMode;
}
