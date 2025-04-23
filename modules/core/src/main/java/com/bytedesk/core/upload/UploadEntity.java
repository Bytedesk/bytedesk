/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-16 10:46:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-23 16:37:58
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
import com.bytedesk.core.enums.ClientEnum;
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

@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({ UploadEntityListener.class })
@Table(name = "bytedesk_core_upload")
public class UploadEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String fileName;

    private String fileSize;

    private String fileUrl;

    private String fileType;

    @Builder.Default
    private String client = ClientEnum.WEB.name();

    @Builder.Default
    @Column(name ="upload_type")
    private String type = UploadTypeEnum.LLM.name();

    @Builder.Default
    private String status = UploadStatusEnum.UPLOADED.name();

    private String categoryUid; // 所属分类

    private String kbUid; // 所属知识库

    // 额外附加信息
    @Builder.Default
    @Column(name = "upload_extra", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)   
    private String extra = BytedeskConsts.EMPTY_JSON_STRING;

    // 上传用户
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
