/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-18 11:00:54
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.form_result;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.service.form.FormTypeEnum;

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
 * FormResultEntity - 表单结果实体类
 * 用于存储用户填写表单后的结果数据
 * 
 * 数据库表：bytedesk_service_form_result
 * 用途：存储表单填写结果和用户提交的数据
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({FormResultEntityListener.class})
@Table(name = "bytedesk_service_form_result")
public class FormResultEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 关联的表单UID - 引用FormEntity的uid
     */
    @Column(name = "form_uid")
    private String formUid;

    /**
     * 表单结果类型
     */
    @Builder.Default
    @Column(name = "form_type")
    private String type = FormTypeEnum.GENERAL.name();

    /**
     * 提交者信息的JSON表示
     */
    @Builder.Default
    @Column(name = "form_user", length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String user = BytedeskConsts.EMPTY_JSON_STRING;

    /**
     * 表单数据 - 存储用户填写的表单数据，JSON格式
     */
    @Column(name = "form_data", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String formData;

    /**
     * 表单版本号 - 记录提交时表单的版本
     */
    @Column(name = "form_version")
    private Integer formVersion;

}
