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
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.constant.TypeConsts;
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

    /**
     * 结果标题或名称
     */
    private String name;

    /**
     * 结果描述
     */
    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    /**
     * 表单结果类型（THREAD, CUSTOMER, TICKET）
     */
    @Builder.Default
    @Column(name = "form_type")
    private String type = FormResultTypeEnum.CUSTOMER.name();

    /**
     * 关联的表单UID - 引用FormEntity的uid
     */
    @Column(name = "form_uid")
    private String formUid;

    /**
     * 提交者信息 - 可以是访客、客户或用户
     */
    @Column(name = "submitter_uid")
    private String submitterUid;

    /**
     * 提交者姓名
     */
    @Column(name = "submitter_name")
    private String submitterName;

    /**
     * 提交者邮箱
     */
    @Column(name = "submitter_email")
    private String submitterEmail;

    /**
     * 提交者手机号
     */
    @Column(name = "submitter_mobile")
    private String submitterMobile;

    /**
     * 提交者IP地址
     */
    @Column(name = "submitter_ip")
    private String submitterIp;

    /**
     * 表单数据 - 存储用户填写的表单数据，JSON格式
     */
    @Column(name = "form_data", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String formData;

    /**
     * 关联的业务对象UID（如工单UID、会话UID等）
     */
    @Column(name = "related_uid")
    private String relatedUid;

    /**
     * 关联的业务对象类型（TICKET, THREAD, CUSTOMER等）
     */
    @Column(name = "related_type")
    private String relatedType;

    /**
     * 提交状态：DRAFT（草稿）、SUBMITTED（已提交）、PROCESSED（已处理）
     */
    @Builder.Default
    @Column(name = "submit_status")
    private String status = FormResultStatusEnum.SUBMITTED.name();

    /**
     * 处理结果或备注
     */
    @Column(name = "process_result", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String processResult;

    /**
     * 处理人UID
     */
    @Column(name = "processor_uid")
    private String processorUid;

    /**
     * 表单版本号 - 记录提交时表单的版本
     */
    @Column(name = "form_version")
    private Integer formVersion;

    /**
     * 附件信息 - 存储上传的文件信息，JSON格式
     */
    @Column(name = "attachments", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String attachments;

}
