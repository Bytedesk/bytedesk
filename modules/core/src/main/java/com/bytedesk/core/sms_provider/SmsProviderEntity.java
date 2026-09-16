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
package com.bytedesk.core.sms_provider;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.I18Consts;

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
 * SmsProvider entity for SMS provider configuration
 * Stores SMS provider credentials and configuration settings
 * 
 * Database Table: bytedesk_core_sms_provider
 * Purpose: Stores sms provider definitions and credentials
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({SmsProviderEntityListener.class})
@Table(name = "bytedesk_core_sms_provider")
public class SmsProviderEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Name of the sms_provider
     */
    private String name;

    /**
     * Description of the sms_provider
     */
    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    /**
     * Type of sms_provider (ALIYUN, TENCENT, HUAWEI, AWS, OTHER)
     */
    @Builder.Default
    @Column(name = "sms_provider_type")
    private String type = SmsProviderTypeEnum.ALIYUN.name();

    // ============ 短信服务商配置字段 ============

    /**
     * 短信服务商类型 (ALIYUN, TENCENT, HUAWEI等)
     */
    @Builder.Default
    @Column(name = "provider_type")
    private String providerType = "ALIYUN";

    /**
     * 短信服务访问密钥ID
     */
    private String accessKeyId;

    /**
     * 短信服务访问密钥Secret
     */
    private String accessKeySecret;

    /**
     * 短信服务区域/Region
     */
    private String region;

    /**
     * 短信服务端点/Endpoint
     */
    private String endpoint;

    /**
     * 配置是否启用
     */
    @Builder.Default
    @Column(name = "is_enabled")
    private Boolean enabled = true;

}
