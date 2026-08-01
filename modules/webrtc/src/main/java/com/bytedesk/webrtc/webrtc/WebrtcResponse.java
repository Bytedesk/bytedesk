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

    public String getStartedAt() {
        return BdDateUtils.formatDatetimeToString(startedAt);
    }

    public String getEndedAt() {
        return BdDateUtils.formatDatetimeToString(endedAt);
    }
}
