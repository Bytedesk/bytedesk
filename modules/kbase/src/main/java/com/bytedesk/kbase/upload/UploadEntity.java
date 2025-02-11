/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-16 10:46:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-11 17:02:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.upload;

import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.utils.StringListConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({ UploadEntityListener.class })
@Table(name = "bytedesk_kbase_upload")
public class UploadEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String fileName;

    private String fileSize;

    private String fileUrl;

    private String fileType;

    // @Enumerated(EnumType.STRING)
    @Builder.Default
    // private ClientEnum client = ClientEnum.WEB;
    private String client = ClientEnum.WEB.name();

    // @Enumerated(EnumType.STRING)
    @Builder.Default
    // private boolean isLlm = false;
    @Column(name ="upload_type")
    // private UploadTypeEnum type = UploadTypeEnum.LLM;
    private String type = UploadTypeEnum.LLM.name();

    @Builder.Default
    // @Enumerated(EnumType.STRING)
    // private UploadStatusEnum status = UploadStatusEnum.UPLOADED;
    private String status = UploadStatusEnum.UPLOADED.name();

    private String categoryUid; // 所属分类

    private String kbUid; // 所属知识库

    // 上传用户
    @Builder.Default
    @Column(name = "upload_user", length = 512)
    // @JdbcTypeCode(SqlTypes.JSON)
    private String user = BytedeskConsts.EMPTY_JSON_STRING;

    // vector store id
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> docIdList = new ArrayList<>();
}
