package com.bytedesk.video.webrtc.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WebrtcInviteRequest {

    @NotBlank
    private String threadUid;

    @NotBlank
    private String callerUid;

    @NotBlank
    private String calleeUid;

    // TEXT/AUDIO/VIDEO/PHONE
    private String callType;

    // VIDEO mode: ONE_WAY/TWO_WAY
    private String videoMode;
}
