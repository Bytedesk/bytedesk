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

    /**
     * 服务房间容量（Janus videoroom publishers 上限，来自 RoomEntity.maxParticipants）：
     * 客户端创建 Janus 房间时使用，覆盖访客 + 接待客服 + 受邀客服；为空时客户端回退自身默认值
     */
    private Integer publisherLimit;

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

    /**
     * 通话场景（2026-09-29 规划）：VISITOR_SERVICE 访客客服 / MEMBER_CALL 同事通话。
     * 客户端据此区分录制责任端（同事通话仅主叫端录制）。
     */
    private String scene;
}
