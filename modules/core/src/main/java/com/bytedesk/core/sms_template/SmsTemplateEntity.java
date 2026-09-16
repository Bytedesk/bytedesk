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
package com.bytedesk.core.sms_template;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.constant.TypeConsts;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
 * SmsTemplate entity for content categorization and organization
 * Provides sms_templateging functionality for various system entities
 * 
 * Database Table: bytedesk_core_sms_template
 * Purpose: Stores sms_template definitions, colors, and organization settings
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({SmsTemplateEntityListener.class})
@Table(name = "bytedesk_core_sms_template")
public class SmsTemplateEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Name of the sms_template
     */
    private String name;

    /**
     * Description of the sms_template
     */
    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    /**
     * Type of sms_template (TICKET, VERIFY_CODE, etc.)
     */
    @Builder.Default
    @Column(name = "sms_template_type")
    private String type = SmsTemplateTypeEnum.CUSTOMER.name();

    // ============ 短信模板配置字段（从 SmsProviderEntity 迁移）============

    /**
     * 短信签名
     * 例如：微语、阿里云等
     */
    private String signName;

    /**
     * 短信模板编码
     * 例如：SMS_85365092
     */
    private String templateCode;

    /**
     * 短信模板内容
     * 支持占位符变量，如：您的验证码为${code},十分钟内有效
     */
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String content;

    /**
     * 模板变量名列表，如：["name","ticketNumber","status"]
     * 发送时按此列表从上下文填充对应的模板变量。
     */
    @Builder.Default
    @Convert(converter = com.bytedesk.core.converter.StringListConverter.class)
    @Column(name = "variable_names", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private java.util.List<String> variableNames = java.util.List.of("name");

    /**
     * 关联的短信服务商UID
     * 用于指定此模板通过哪个服务商发送
     */
    @Column(name = "provider_uid")
    private String providerUid;

}
