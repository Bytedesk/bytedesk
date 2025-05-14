/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-14 14:50:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-14 14:50:10
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_text;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文本向量搜索结果实体类
 * 用于表示向量相似度检索的结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TextVectorSearchResult {
    
    // 文本的唯一标识
    private String uid;
    
    // 文本标题
    private String title;
    
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
    
    // 文本类型
    private String type;
    
    // 标签列表
    @Builder.Default
    private List<String> tagList = new ArrayList<>();
    
    /**
     * 获取去除标题的纯内容部分
     * @return 内容部分（去除标题）
     */
    public String getPureContent() {
        if (content == null || content.isEmpty() || !content.contains("\n\n")) {
            return content;
        }
        
        // 以双换行符分割，获取内容部分（忽略标题）
        String[] parts = content.split("\n\n", 2);
        return parts.length > 1 ? parts[1] : content;
    }
    
    /**
     * 获取内容摘要（限制长度）
     * @param maxLength 摘要最大长度
     * @return 截取后的摘要
     */
    public String getContentSummary(int maxLength) {
        String pureContent = getPureContent();
        if (pureContent == null || pureContent.length() <= maxLength) {
            return pureContent;
        }
        return pureContent.substring(0, maxLength) + "...";
    }
}
