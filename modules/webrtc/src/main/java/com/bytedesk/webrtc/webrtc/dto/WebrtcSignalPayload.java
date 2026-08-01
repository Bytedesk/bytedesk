package com.bytedesk.webrtc.webrtc.dto;

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

    private String callerNickname;

    private String callerAvatar;

    private String calleeUid;

    private String calleeNickname;

    private String calleeAvatar;

    // INBOUND/OUTBOUND
    private String direction;

    // TEXT/AUDIO/VIDEO/PHONE
    private String callType;

    // VIDEO mode: ONE_WAY/TWO_WAY
    private String videoMode;
}
