package com.bytedesk.core.message.biz;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class MessageBizUpdateRequest {

    /** 被更新的原消息 uid */
    private String messageUid;

    /** 业务消息类型，仅支持 GOODS / ORDER */
    private String bizType;

    /** 更新后的消息 content */
    private String content;
}