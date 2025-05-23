/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:26:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-13 17:46:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_text;

import java.time.LocalDateTime;
import java.util.List;

import com.bytedesk.core.base.BaseResponse;

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
public class TextResponse extends BaseResponse {

    private String title;

    private String content;

    private List<String> tagList;

    private Boolean enabled;

    // 有效开始日期
    private LocalDateTime startDate;

    // 有效结束日期
    private LocalDateTime endDate;

    // // 是否开启自动生成enable_llm_qa问答
    // private Boolean autoGenerateLlmQa;

    // // 是否已经生成llm问答
    // private Boolean llmQaGenerated;

    // // 是否开启自动删除llm问答
    // private Boolean autoDeleteLlmQa;

    // // 是否已经删除llm问答
    // private Boolean llmQaDeleted;

    // // 是否开启自动llm Chunk切块
    // private Boolean autoLlmChunk;

    // // 是否已经自动llm Chunk切块
    // private Boolean llmChunked;

    // // 是否开启自动删除llm Chunk切块
    // private Boolean autoDeleteLlmChunk;

    // // 是否已经删除llm Chunk切块
    // private Boolean llmChunkedDeleted;

    private String status;

    private String vectorStatus;

    private String categoryUid; // 所属分类

     // used for auto-generate qa
     private String docId; // 对应文档

    private String kbUid; // 所属知识库

    private List<String> docIdList;
}
