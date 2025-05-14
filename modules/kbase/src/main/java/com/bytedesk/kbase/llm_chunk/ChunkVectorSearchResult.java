/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-14 14:55:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-14 14:55:10
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_chunk;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Chunk向量搜索结果实体类
 * 用于表示向量相似度检索的结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChunkVectorSearchResult {
    
    // Chunk的唯一标识
    private String uid;
    
    // Chunk名称
    private String name;
    
    // 向量文档内容
    private String content;
    
    // 相似度分数
    @Builder.Default
    private double score = 0.0;
    
    // 知识库UID
    private String kbUid;
    
    // 分类UID
    private String categoryUid;
    
    // 组织UID
    private String orgUid;
    
    // Chunk类型
    private String type;
    
    // 文档ID
    private String docId;
    
    // 文件UID
    private String fileUid;
    
    // 标签列表
    @Builder.Default
    private List<String> tagList = new ArrayList<>();
    
    /**
     * 获取内容摘要（限制长度）
     * @param maxLength 摘要最大长度
     * @return 截取后的摘要
     */
    public String getContentSummary(int maxLength) {
        if (content == null || content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength) + "...";
    }
}
