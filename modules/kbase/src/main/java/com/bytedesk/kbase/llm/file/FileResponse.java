/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:26:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-23 18:23:50
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm.file;

import java.time.LocalDateTime;
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

    private String fileName;

    private String content;

    private String fileUrl;

    // private String type;

    private String status;

    private List<String> tagList;

    // 有效开始日期
    private LocalDateTime startDate;

    // 有效结束日期
    private LocalDateTime endDate;

    private Boolean enabled;

    // 是否开启自动生成enable_llm_qa问答
    private Boolean autoGenerateLlmQa;

    // 是否已经生成llm问答
    private Boolean llmQaGenerated;

    // 是否开启自动删除llm问答
    private Boolean autoDeleteLlmQa;

    // 是否已经删除llm问答
    private Boolean llmQaDeleted;

    // 是否开启自动llm split切块
    private Boolean autoLlmSplit;

    // 是否已经自动llm split切块
    private Boolean llmSplitted;

    // 是否开启自动删除llm split切块
    private Boolean autoDeleteLlmSplit;

    // 是否已经删除llm split切块
    private Boolean llmSplittedDeleted;

    private String categoryUid; // 所属分类

    // private String kbUid; // 所属知识库
    private KbaseResponse kbase;

    // 对应 uploadEntity 的 uid
    // private String uploadUid;
    private UploadResponse upload;

    // 上传用户
    private String userUid;

    // private LocalDateTime createdAt;

    // private LocalDateTime updatedAt;
}
