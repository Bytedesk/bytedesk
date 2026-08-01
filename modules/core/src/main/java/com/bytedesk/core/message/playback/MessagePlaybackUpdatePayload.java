package com.bytedesk.core.message.playback;

import com.alibaba.fastjson2.JSON;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessagePlaybackUpdatePayload {

    /** 被更新的原消息 uid */
    private String messageUid;

    /** 原消息更新后的 content */
    private String content;

    /** 最新播放状态 */
    private Boolean played;

    /** 本次操作用户 uid（游客端可能为空） */
    private String userUid;

    /** 服务端时间戳 */
    private Long serverTimestamp;

    public static MessagePlaybackUpdatePayload fromJson(String json) {
        return JSON.parseObject(json, MessagePlaybackUpdatePayload.class);
    }

    public String toJson() {
        return JSON.toJSONString(this);
    }
}
