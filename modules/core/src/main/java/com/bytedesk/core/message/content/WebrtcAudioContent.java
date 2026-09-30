package com.bytedesk.core.message.content;

import com.bytedesk.core.base.BaseContent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 同事间/成员间音频通话记录消息内容（区别于 INVITE_AUDIO 信令）：
 * 通话结束后写入聊天记录的终态消息，携带通话双方、时长、方向与结果。
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class WebrtcAudioContent extends BaseContent {
    
    private static final long serialVersionUID = 1L;

    private String callUid;          // 通话唯一标识（WebrtcEntity.uid）
    private String callerUid;        // 主叫 user uid
    private String callerNickname;   // 主叫昵称
    private String callerAvatar;     // 主叫头像
    private String calleeUid;        // 被叫 user uid
    private String calleeNickname;   // 被叫昵称
    private String calleeAvatar;     // 被叫头像
    private String duration;         // 通话时长（秒）
    private String status;           // 终态：ENDED / REJECTED / CANCELED / TIMEOUT
    private String direction;        // INBOUND / OUTBOUND
    private String startedAt;        // 开始时间（ISO 字符串）
    private String endedAt;          // 结束时间（ISO 字符串）

    public static WebrtcAudioContent fromJson(String json) {
        return BaseContent.fromJson(json, WebrtcAudioContent.class);
    }
}
