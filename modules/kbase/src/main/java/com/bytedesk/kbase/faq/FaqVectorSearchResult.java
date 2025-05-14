/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-14 14:25:05
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-14 14:25:05
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.faq;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * FAQ向量搜索结果实体类
 * 用于表示向量相似度检索的结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaqVectorSearchResult {
    
    // FAQ的唯一标识
    private String uid;
    
    // FAQ的问题
    private String question;
    
    // 向量文档内容（通常包含问题和答案）
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
    
    // 标签列表
    @Builder.Default
    private List<String> tagList = new ArrayList<>();
    
    // 获取答案（从content中提取，去除问题部分）
    public String getAnswer() {
        if (content == null || content.isEmpty() || !content.contains("\n")) {
            return "";
        }
        
        // 以第一个换行符分割，获取答案部分
        String[] parts = content.split("\n", 2);
        return parts.length > 1 ? parts[1] : "";
    }
}
