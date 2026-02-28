package com.bytedesk.core.message.reaction;

import com.alibaba.fastjson2.JSON;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageReactionUpdatePayload {

    /** 被更新的原消息 uid */
    private String messageUid;

    /** 原消息更新后的 content（JSON 或纯文本兼容后的 JSON） */
    private String content;

    /** 本次操作的 emoji */
    private String emoji;

    /** 本次操作的用户 uid */
    private String userUid;

    /** 服务端时间戳 */
    private Long serverTimestamp;

    public static MessageReactionUpdatePayload fromJson(String json) {
        return JSON.parseObject(json, MessageReactionUpdatePayload.class);
    }

    public String toJson() {
        return JSON.toJSONString(this);
    }
}
