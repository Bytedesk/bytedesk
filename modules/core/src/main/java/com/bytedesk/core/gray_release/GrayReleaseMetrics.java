/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-07 11:14:04
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-07 11:25:18
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.gray_release;

import java.time.ZonedDateTime;

import com.bytedesk.core.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 灰度发布指标实体
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bytedesk_core_gray_release_metrics")
@EqualsAndHashCode(callSuper = true)
public class GrayReleaseMetrics extends BaseEntity {
    private static final long serialVersionUID = 1L;


    @Column(nullable = false)
    private String userUid;      // 用户ID

    @Column(nullable = false)
    private String feature;      // 功能代码

    @Column(nullable = false)
    private Boolean success;     // 是否成功

    @Column(nullable = false)
    private ZonedDateTime timestamp;  // 记录时间

    @Column(length = 512)
    private String errorMessage;  // 错误信息（如果有）
} 