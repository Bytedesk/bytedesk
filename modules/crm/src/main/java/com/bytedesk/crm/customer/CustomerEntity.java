/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-22 16:52:52
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-25 08:32:12
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.crm.customer;

import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.converter.CustomFieldItemListConverter;
import com.bytedesk.core.converter.StringListConverter;
import com.bytedesk.core.model.CustomFieldItem;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Customer entity for lead management and customer relationship management
 * Represents customer information collected automatically or manually added
 * 
 * Database Table: bytedesk_crm_customer
 * Purpose: Stores customer contact information, preferences, and interaction history
 * 
 * @author jackning 270580156@qq.com
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_crm_customer")
public class CustomerEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Customer's display name
     */
    private String name;

    /**
     * Company name (plain text for now)
     */
    private String companyName;

    /**
     * Customer's email address for communication
     */
    private String email;

    /**
     * Customer's mobile phone number
     */
    private String mobile;

    /**
     * Customer status (string code)
     * @see CustomerStatusEnum
     */
    @Builder.Default
    @Column(name = "customer_status")
    private String status = CustomerStatusEnum.NEW.getCode();

    /**
     * Customer source (string code)
     * @see CustomerSourceEnum
     */
    @Builder.Default
    @Column(name = "customer_source")
    private String source = CustomerSourceEnum.WEB.getCode();

    /**
     * Customer description or notes
     */
    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;
    
    /**
     * Tags for customer categorization and search
     */
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> tagList = new ArrayList<>();

    /**
     * 用户自定义字段：字段昵称/字段key/字段值（JSON）
     */
    @Builder.Default
    @Convert(converter = CustomFieldItemListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<CustomFieldItem> customFieldList = new ArrayList<>();

    /**
     * Additional customer information stored as JSON format for extensibility
     */
	@Builder.Default
	@Column(name = "customer_extra", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String extra = BytedeskConsts.EMPTY_JSON_STRING;
    
    /**
     * Additional notes or comments about the customer
     */
    @Column(name = "customer_notes", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String notes; // 备注信息

    /**
     * 访客店铺信息（自由文本，便于后续扩展成结构化字段）
     */
    @Column(name = "shop_info", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String shopInfo;

    /**
     * 咨询内容（访客咨询/线索描述）
     */
    @Column(name = "consult_content", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String consultContent;

    /**
     * 是否需要跟进
     */
    @Builder.Default
    @Column(name = "need_follow_up")
    private Boolean needFollowUp = false;

    /**
     * Associated visitor UID for tracking customer journey
     * 关联的访客 visitorEntity.uid（系统 uid）
     */
    private String visitorUid;

    /**
     * Owner user uid
     */
    private String ownerUserUid;

}
