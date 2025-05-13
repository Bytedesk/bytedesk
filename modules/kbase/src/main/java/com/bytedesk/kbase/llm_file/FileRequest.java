/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:26:04
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-13 17:46:40
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_file;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.kbase.llm_chunk.ChunkStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class FileRequest extends BaseRequest {

    private String fileName;

    private String fileUrl;


    @Builder.Default
    private List<String> tagList = new ArrayList<>();
    
    // 是否启用，状态：启用/禁用
    @Builder.Default
    private Boolean enabled = true;

    // 有效开始日期
    @Builder.Default
    private LocalDateTime startDate = LocalDateTime.now();

    // 有效结束日期
    // 当前 + 100 年
    @Builder.Default
    private LocalDateTime endDate = LocalDateTime.now().plusYears(100);

    // 是否开启自动生成enable_llm_qa问答
    @Builder.Default
    private Boolean autoGenerateLlmQa = false;

    // 是否已经生成llm问答
    @Builder.Default
    private Boolean llmQaGenerated = false;

    // 是否开启自动删除llm问答
    @Builder.Default
    private Boolean autoDeleteLlmQa = false;

    // 是否已经删除llm问答
    @Builder.Default
    private Boolean llmQaDeleted = false;

    // 是否开启自动llm Chunk切块
    @Builder.Default
    private Boolean autoLlmChunk = false;

    // 是否已经自动llm Chunk切块
    @Builder.Default
    private Boolean llmChunked = false;

    // 是否开启自动删除llm Chunk切块
    @Builder.Default
    private Boolean autoDeleteLlmChunk = false;

    // 是否已经删除llm Chunk切块
    @Builder.Default
    private Boolean llmChunkDeleted = false;

    @Builder.Default
    private String status = ChunkStatusEnum.NEW.name();

    @Builder.Default
    private String vectorStatus = ChunkStatusEnum.NEW.name();

    // 所属分类
    private String categoryUid;

    // 所属知识库
    private String kbUid;

    // 对应 uploadEntity 的 uid
    private String uploadUid;

}
