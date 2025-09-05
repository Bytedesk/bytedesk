/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-05 14:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-05 14:30:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_chunk.mq;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Chunk索引消息
 * 用于在Artemis队列中传递需要索引的Chunk信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChunkIndexMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Chunk唯一标识
     */
    private String chunkUid;
    
    /**
     * 操作类型：create, update, delete
     */
    private String operationType;
    
    /**
     * 是否需要更新全文索引
     */
    private Boolean updateElasticIndex;
    
    /**
     * 是否需要更新向量索引
     */
    private Boolean updateVectorIndex;
    
    /**
     * 文件UID（用于批处理时的分组）
     */
    private String fileUid;
    
    /**
     * 批处理ID（用于跟踪批处理状态）
     */
    private String batchId;
    
    /**
     * 批处理大小
     */
    private Integer batchSize;
    
    /**
     * 在批处理中的索引
     */
    private Integer batchIndex;
}
