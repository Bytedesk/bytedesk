/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-14 14:55:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-14 15:56:02
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_chunk.vector;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Chunk向量搜索结果实体类
 * 用于表示向量相似度检索的结果，参考FaqVectorSearchResult结构
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChunkVectorSearchResult implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // 包含ChunkVector对象
    private ChunkVector chunkVector;
    
    // 相似度分数
    private double score;
    
    // 存储带高亮标记的内容文本
    private String highlightedContent;
    
    // 存储带高亮标记的名称
    private String highlightedName;
    
    // 向量相似度距离
    private float distance;
    
    /**
     * 获取内容摘要（限制长度）
     * @param maxLength 摘要最大长度
     * @return 截取后的摘要
     */
    public String getContentSummary(int maxLength) {
        if (chunkVector == null || chunkVector.getContent() == null || 
            chunkVector.getContent().length() <= maxLength) {
            return chunkVector != null ? chunkVector.getContent() : null;
        }
        return chunkVector.getContent().substring(0, maxLength) + "...";
    }
}
