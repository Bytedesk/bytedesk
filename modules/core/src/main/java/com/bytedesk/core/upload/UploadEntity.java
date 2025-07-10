/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-16 10:46:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-08 16:20:56
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.upload;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.enums.ChannelEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * File upload entity for managing uploaded files and documents
 * Handles file metadata, storage information, and upload tracking
 * 
 * Database Table: bytedesk_core_upload
 * Purpose: Stores file upload records, metadata, and access information
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({ UploadEntityListener.class })
@Table(name = "bytedesk_core_upload")
public class UploadEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Original filename of the uploaded file
     */
    private String fileName;

    /**
     * Size of the uploaded file in bytes or human-readable format
     */
    private String fileSize;

    /**
     * URL or path to access the uploaded file
     */
    private String fileUrl;

    /**
     * MIME type or file extension of the uploaded file
     */
    private String fileType;

    /**
     * Client platform from which the file was uploaded (WEB, MOBILE, etc.)
     */
    @Builder.Default
    private String client = ClientEnum.WEB.name();

    /**
     * Type of upload (LLM, IMAGE, DOCUMENT, etc.)
     */
    @Builder.Default
    @Column(name ="upload_type")
    private String type = UploadTypeEnum.LLM.name();

    /**
     * Current status of the upload (UPLOADED, PROCESSING, FAILED, etc.)
     */
    @Builder.Default
    private String status = UploadStatusEnum.UPLOADED.name();

    /**
     * Associated category UID for file organization
     */
    private String categoryUid; // 所属分类

    /**
     * Associated knowledge base UID if file is part of a knowledge base
     */
    private String kbUid; // 所属知识库

    /**
     * Additional upload information stored as JSON format
     */
    @Builder.Default
    @Column(name = "upload_extra", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)   
    private String extra = BytedeskConsts.EMPTY_JSON_STRING;

    /**
     * User information who uploaded the file stored as JSON string
     */
    @Builder.Default
    @Column(name = "upload_user", length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    // @JdbcTypeCode(SqlTypes.JSON)
    private String user = BytedeskConsts.EMPTY_JSON_STRING;

    // vector store id
    // @Builder.Default
    // @Convert(converter = StringListConverter.class)
    // @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    // private List<String> docIdList = new ArrayList<>();
}
