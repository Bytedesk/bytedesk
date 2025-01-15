/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-24 17:43:52
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-15 14:32:36
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.ip.black;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@Table(name = "bytedesk_core_ip_blacklist")
@AllArgsConstructor
@NoArgsConstructor
public class IpBlacklistEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "uid is required")
	@Column(name = "uuid", unique = true, nullable = false)
	private String uid;
    
    // 
    private String ip;
    private LocalDateTime blockedTime;
    private LocalDateTime expireTime;
    private String reason;
} 