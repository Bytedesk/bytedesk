/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-05 12:50:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-05 12:50:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_file.mq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件Chunk处理消息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileChunkMessage {
    
    /**
     * 文件UID
     */
    private String fileUid;
    
    /**
     * 操作类型：process, retry, complete
     */
    private String operationType;
    
    /**
     * 文档索引（用于重试时定位具体文档）
     */
    private Integer documentIndex;
    
    /**
     * 错误消息（用于重试场景）
     */
    private String errorMessage;
    
    /**
     * 生成的chunk数量（用于完成通知）
     */
    private Integer chunkCount;
    
    /**
     * 批次号
     */
    private Integer batchNumber;
    
    /**
     * 重试次数
     */
    private Integer retryCount;
    
    /**
     * 消息创建时间
     */
    private Long createTime;
}
