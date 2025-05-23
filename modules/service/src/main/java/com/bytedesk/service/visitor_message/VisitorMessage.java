/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-01 11:32:30
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-13 21:36:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor_message;

import com.bytedesk.core.message.AbstractMessageEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 分表存储客服消息
 * 同步message中客服消息，包括uid。用于在访客端显示消息，减少message表压力
 */
@Entity
@Data
@NoArgsConstructor // 添加无参构造函数
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Table(name = "bytedesk_service_visitor_message")
public class VisitorMessage extends AbstractMessageEntity {
    
    private static final long serialVersionUID = 1L;
    
    // 可以在这里添加 VisitorMessage 特有的字段（如果有的话）
}
