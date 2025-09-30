/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-12 12:57:52
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-12 13:08:28
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message.content;

import java.time.ZonedDateTime;

import com.bytedesk.core.base.BaseContent;
import com.bytedesk.core.message.MessageTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 引用消息内容类
 * 类似微信的引用消息功能，包含被引用消息的信息和当前消息的内容
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class QuotationContent extends BaseContent {
    
    private static final long serialVersionUID = 1L;
    
    // 发送消息内容
    private String content;

    // 当前消息类型，暂时仅支持发送文本
    // private String type;
    
    /**
     * 被引用消息的类型
     * 对应 MessageTypeEnum
     */
    private MessageTypeEnum quotedMessageType;
    
    /**
     * 被引用消息的原始内容
     * 对于文本消息，直接存储文本内容
     * 对于其他类型消息，存储JSON格式的内容
     */
    private String quotedContent;
    
    // 被引用消息唯一标识
    private String quotedMessageUid;

    // 被引用消息发送人
    private String quotedSenderName;
    
    // 被引用消息发送人唯一标识
    private String quotedSenderUid;
    
    // 被引用消息发送时间
    private ZonedDateTime quotedCreatedAt;
    
}
