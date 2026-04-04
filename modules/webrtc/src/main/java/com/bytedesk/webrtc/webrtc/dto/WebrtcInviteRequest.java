package com.bytedesk.webrtc.webrtc.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import com.bytedesk.core.enums.VisitorCallTypeEnum;
import com.bytedesk.webrtc.webrtc.WebrtcDirectionEnum;

@Data
public class WebrtcInviteRequest {

    @NotBlank
    private String threadUid;

    @NotBlank
    private String callerUid;

    @NotBlank
    private String calleeUid;

    // TEXT/WEBRTC/PHONE
    private VisitorCallTypeEnum callType;

    // VIDEO mode: ONE_WAY/TWO_WAY
    private String videoMode;

    // INBOUND(visitor->agent) / OUTBOUND(agent->visitor)
    private WebrtcDirectionEnum direction;
}
