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
package com.bytedesk.core.document;

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
 * 微语文档：
 * 支持文字、表格、多维表、白板、脑图等多种在线文档格式的协作，
 * 实现多人在线编辑，云端实时保存，帮助你提升工作效率
 * Document entity for content categorization and organization
 * Provides documentging functionality for various system entities
 * 
 * Database Table: bytedesk_core_document
 * Purpose: Stores document definitions, colors, and organization settings
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({DocumentEntityListener.class})
@Table(name = "bytedesk_core_document")
public class DocumentEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Name of the document
     */
    private String name;

    /**
     * Description of the document
     */
    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    /**
     * Type of document (DOCS、EXCEP、PPT, etc.)
     */
    @Builder.Default
    @Column(name = "document_type")
    private String type = DocumentTypeEnum.DOCS.name();

}
