/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-19 16:02:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-07 14:18:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.statistic_message;

import java.util.Date;

import com.bytedesk.core.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 记录客服回复访客消息时间戳
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_service_statistic_message")
public class StatisticMessageEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    // 所属会话uid
    private String threadUid;

    // 访客消息uid
    private String messageUid;

    private String status;

    // 消息创建时间，继承自BaseEntity
    // @Temporal(TemporalType.TIMESTAMP)
    // @Column(name = "created_at", updatable = false)

    // 客服回复消息uid
    private String responseMessageUid;
    // 客服回复消息回复时间
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "response_at")
    private Date responseAt;

    // 计算客服响应时长
    @Builder.Default
    @Column(name = "response_duration")
    private long responseDuration = 0;

    
}
