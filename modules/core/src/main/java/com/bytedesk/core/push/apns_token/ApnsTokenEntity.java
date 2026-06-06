/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-04 15:35:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.push.apns_token;

import com.bytedesk.core.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
// import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * APNs token entity for iOS offline push delivery.
 * 
 * Database Table: bytedesk_core_apns_token
 * Purpose: Stores APNs device tokens bound to the current user and organization
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({ApnsTokenEntityListener.class})
@Table(name = "bytedesk_core_apns_token")
public class ApnsTokenEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "device_token", length = 512, nullable = false)
    private String token;

    /**
     * Binds this token to a specific APNS p12 certificate uid.
     */
    @Column(name = "p12_uid")
    private String p12Uid;

    @Builder.Default
    @Column(name = "environment", length = 32)
    private String environment = ApnsTokenEnvironmentEnum.DEVELOPMENT.name();

    @Builder.Default
    @Column(name = "apns_token_type")
    private String type = ApnsTokenTypeEnum.IOS.name();
}
