/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-02 10:57:57
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.form;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.converter.StringListConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 支持客服发送自定义表单，
 * 例如留言表单、询前表单、工单表单、问卷调查等。
 * 收集用户信息
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_service_form")
public class FormEntity extends BaseEntity {

    private String name;

    private String description;

    // @Builder.Default    
    // @Column(name = "form_type")    
    // private String type = FormTypeEnum.TICKET.name();        

    // 表单状态：草稿、已发布、已归档、已禁用
    @Builder.Default
    @Column(name = "form_status")
    private String status = FormStatusEnum.DRAFT.name();
    
    // 是否为模板
    @Builder.Default
    @Column(name = "is_template")
    private Boolean template = false;
    
    // 表单结构定义，存储为JSON格式
    @Lob
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String formSchema;
    
    // 表单发布时间
    private LocalDateTime publishTime;
    
    // 表单过期时间
    private LocalDateTime expireLength;
    
    // 表单布局类型：单列、双列、响应式等
    @Builder.Default
    private String layoutType = "SINGLE_COLUMN";
    
    // 表单样式配置，JSON格式
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String styleConfig;
    
    // 提交后跳转URL
    private String redirectUrl;
    
    // 提交后显示的消息
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String submitMessage;
    
    // 是否允许匿名提交
    @Builder.Default
    private Boolean allowAnonymous = true;
    
    // 表单访问权限设置，JSON格式
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String accessControl;
    
    // 提交次数限制，0表示不限制
    @Builder.Default
    private Integer submissionLimit = 0;
    
    // 提交总数统计
    @Builder.Default
    private Integer submissionCount = 0;
    
    // 标签
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> tagList = new ArrayList<>();

}
