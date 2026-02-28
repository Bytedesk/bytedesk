package com.bytedesk.video.webrtc.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WebrtcActionRequest {

    @NotBlank
    private String callUid;

    @NotBlank
    private String actorUid;
}
