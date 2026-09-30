package com.bytedesk.webrtc.webrtc;

import java.time.ZonedDateTime;

import com.bytedesk.core.base.BaseResponse;
import com.bytedesk.core.utils.BdDateUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class WebrtcResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    private String callUid;

    private String status;

    private String type;

    private String direction;

    private String threadUid;

    private String threadStatus;

    private Boolean offlineFallback;

    private Long roomId;

    /**
     * 服务房间容量（= 创建的 RoomEntity.maxParticipants）：发起方客户端创建 Janus 房间时使用；
     * 非音视频通话（未建档）为 null
     */
    private Integer publisherLimit;

    private Boolean record;

    private String recordFilename;

    private ZonedDateTime startedAt;

    private ZonedDateTime endedAt;

    private String callerUid;

    private String callerNickname;

    private String callerAvatar;

    private String calleeUid;

    private String calleeNickname;

    private String calleeAvatar;

    /**
     * 通话场景（VISITOR_SERVICE 访客客服 / MEMBER_CALL 同事通话，规划 §5.1）；
     * 存量数据未同列时为 VISITOR_SERVICE
     */
    private String scene;

    public String getStartedAt() {
        return BdDateUtils.formatDatetimeToString(startedAt);
    }

    public String getEndedAt() {
        return BdDateUtils.formatDatetimeToString(endedAt);
    }
}
