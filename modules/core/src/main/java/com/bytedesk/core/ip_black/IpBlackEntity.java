/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-24 17:43:52
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-01 21:39:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.ip_black;

import java.time.ZonedDateTime;

import com.bytedesk.core.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.bytedesk.core.utils.BdDateUtils;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@Entity
@Table(name = "bytedesk_core_ip_black")
@AllArgsConstructor
@NoArgsConstructor
public class IpBlackEntity extends BaseEntity {
    // 
    private String ip;
    private String ipLocation;

    // 开始时间
    @Builder.Default
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private ZonedDateTime startTime = BdDateUtils.now();

    // 结束时间
    @Builder.Default
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private ZonedDateTime endTime = BdDateUtils.now().plusHours(24);
    
    // 
    private String reason;
    
    // 
    private String blackUid;
    private String blackNickname;

    private String userUid;
    private String userNickname;
} 