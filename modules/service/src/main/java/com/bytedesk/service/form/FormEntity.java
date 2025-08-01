/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-02 11:00:42
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.form;

import java.time.ZonedDateTime;
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
import lombok.Getter;
import lombok.Setter;

/**
 * Custom form entity for customer service interactions
 * Supports various form types like feedback forms, pre-inquiry forms, ticket forms, surveys, etc.
 * Used to collect user information and feedback
 * 
 * Database Table: bytedesk_service_form
 * Purpose: Stores form definitions, configurations, and submission tracking
 */
@Entity
@Getter
@Setter
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_service_form")
public class FormEntity extends BaseEntity {

    /**
     * Name or title of the form
     */
    private String name;

    /**
     * Description of the form's purpose
     */
    private String description;

    // @Builder.Default    
    // @Column(name = "form_type")    
    // private String type = FormTypeEnum.TICKET.name();        

    /**
     * Current status of the form (DRAFT, PUBLISHED, ARCHIVED, DISABLED)
     */
    @Builder.Default
    @Column(name = "form_status")
    private String status = FormStatusEnum.DRAFT.name();
    
    /**
     * Whether this form is a template for creating other forms
     */
    @Builder.Default
    @Column(name = "is_template")
    private Boolean template = false;
    
    /**
     * Form structure definition stored as JSON format
     */
    @Lob
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String formSchema;
    
    /**
     * Timestamp when the form was published
     */
    private ZonedDateTime publishTime;
    
    /**
     * Expiration date/time for the form
     */
    private ZonedDateTime expireLength;
    
    /**
     * Form layout type (SINGLE_COLUMN, TWO_COLUMN, RESPONSIVE, etc.)
     */
    @Builder.Default
    private String layoutType = "SINGLE_COLUMN";
    
    /**
     * Form styling configuration stored as JSON format
     */
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String styleConfig;
    
    /**
     * URL to redirect to after form submission
     */
    private String redirectUrl;
    
    /**
     * Message to display after successful form submission
     */
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String submitMessage;
    
    /**
     * Whether anonymous submissions are allowed
     */
    @Builder.Default
    private Boolean allowAnonymous = true;
    
    /**
     * Form access control settings stored as JSON format
     */
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String accessControl;
    
    /**
     * Maximum number of submissions allowed (0 means unlimited)
     */
    @Builder.Default
    private Integer submissionLimit = 0;
    
    /**
     * Total number of form submissions received
     */
    @Builder.Default
    private Integer submissionCount = 0;
    
    /**
     * Tags for form categorization and search
     */
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> tagList = new ArrayList<>();

}
