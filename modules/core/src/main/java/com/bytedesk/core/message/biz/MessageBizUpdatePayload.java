package com.bytedesk.core.message.biz;

import com.alibaba.fastjson2.JSON;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageBizUpdatePayload {

    /** 被更新的原消息 uid */
    private String messageUid;

    /** 原消息更新后的 content */
    private String content;

    /** 业务消息类型，仅支持 GOODS / ORDER */
    private String bizType;

    /** 服务端时间戳 */
    private Long serverTimestamp;

    public static MessageBizUpdatePayload fromJson(String json) {
        return JSON.parseObject(json, MessageBizUpdatePayload.class);
    }

    public String toJson() {
        return JSON.toJSONString(this);
    }
}