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

    /**
     * 通话媒体类型（AUDIO/VIDEO，规划 §5.2 媒体类型契约）：MEMBER_CALL 同事通话以此为准，
     * 避免依赖 thread.extra 推导（extra 为空时会默认 VIDEO，音频会被误判）。
     * 访客客服链路不传，保持既有 resolveCallType 推导不变。
     */
    private String mediaType;

    /**
     * 可选场景（VISITOR_SERVICE/MEMBER_CALL）：服务端以 thread.type 推导为准，
     * 显式传入仅用于防御与测试（规划 §5.1）。
     */
    private String scene;

    // VIDEO mode: ONE_WAY/TWO_WAY
    private String videoMode;

    // INBOUND(visitor->agent) / OUTBOUND(agent->visitor)
    private WebrtcDirectionEnum direction;
}
