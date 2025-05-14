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

import java.io.Serializable;
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
public class TextVectorSearchResult implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private TextVector textVector;
    
    // 相似度分数
    @Builder.Default
    private double score = 0.0;
    
    /**
     * 获取去除标题的纯内容部分
     * @return 内容部分（去除标题）
     */
    public String getPureContent() {
        if (textVector == null || textVector.getContent() == null || 
            textVector.getContent().isEmpty() || !textVector.getContent().contains("\n\n")) {
            return textVector != null ? textVector.getContent() : "";
        }
        
        // 以双换行符分割，获取内容部分（忽略标题）
        String[] parts = textVector.getContent().split("\n\n", 2);
        return parts.length > 1 ? parts[1] : textVector.getContent();
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
    
    /**
     * 便捷方法：获取文本标题
     */
    public String getTitle() {
        return textVector != null ? textVector.getTitle() : null;
    }
    
    /**
     * 便捷方法：获取文本内容
     */
    public String getContent() {
        return textVector != null ? textVector.getContent() : null;
    }
    
    /**
     * 便捷方法：获取文本唯一标识
     */
    public String getUid() {
        return textVector != null ? textVector.getUid() : null;
    }
    
    /**
     * 便捷方法：获取知识库UID
     */
    public String getKbUid() {
        return textVector != null ? textVector.getKbUid() : null;
    }
    
    /**
     * 便捷方法：获取分类UID
     */
    public String getCategoryUid() {
        return textVector != null ? textVector.getCategoryUid() : null;
    }
    
    /**
     * 便捷方法：获取组织UID
     */
    public String getOrgUid() {
        return textVector != null ? textVector.getOrgUid() : null;
    }
    
    /**
     * 便捷方法：获取文本类型
     */
    public String getType() {
        return textVector != null ? textVector.getType() : null;
    }
    
    /**
     * 便捷方法：获取标签列表
     */
    public List<String> getTagList() {
        return textVector != null ? textVector.getTagList() : new ArrayList<>();
    }
}
