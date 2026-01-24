/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:26:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-05 15:50:09
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_file;

import java.time.ZonedDateTime;
import java.util.List;

import com.bytedesk.core.base.BaseResponse;
import com.bytedesk.core.upload.UploadResponse;
import com.bytedesk.kbase.kbase.KbaseResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class FileResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;


    private String fileName;

    private String content;

    private String fileUrl;

    private String fileType;

    // 切块策略：TOKEN / CHARACTER / PARAGRAPH
    private String chunkingStrategy;

    // 切块大小（字符数）
    private Integer chunkSize;

    // 重叠大小（字符数）
    private Integer chunkOverlap;

    private String elasticStatus;

    private String vectorStatus;

    private List<String> tagList;

    // 有效开始日期
    private ZonedDateTime startDate;

    // 有效结束日期
    private ZonedDateTime endDate;

    private Boolean enabled;

    private String categoryUid; // 所属分类

    // private String kbUid; // 所属知识库
    private KbaseResponse kbase;

    // 对应 uploadEntity 的 uid
    // private String uploadUid;
    private UploadResponse upload;

    // 上传用户
    private String userUid;
}
