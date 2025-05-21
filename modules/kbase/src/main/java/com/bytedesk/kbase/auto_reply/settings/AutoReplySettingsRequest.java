/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-23 16:10:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-24 16:49:42
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.auto_reply.settings;

import java.io.Serializable;

import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.message.MessageTypeEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Embeddable
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class AutoReplySettingsRequest implements Serializable {
    
    // 自动回复开关
    @Builder.Default
    @Column(name = "is_autoreply_enabled")
    private Boolean autoReplyEnabled = false;

    // 自动回复类型
    @Builder.Default
    private String autoReplyType = AutoReplyTypeEnum.FIXED.name();

    // 固定回复类型所需要字段
    @Builder.Default
    private String autoReplyUid = BytedeskConsts.EMPTY_STRING;
    
    // 自动回复内容类型
    @Builder.Default
    private String autoReplyContentType = MessageTypeEnum.TEXT.name();

    // 自动回复内容
    @Builder.Default
    private String autoReplyContent = BytedeskConsts.EMPTY_STRING;

    // 关键词回复类型所需要字段
    // 大模型回复类型所需要字段
    private String kbUid;
}
