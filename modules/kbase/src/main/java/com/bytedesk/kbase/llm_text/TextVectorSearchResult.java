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
    
    // 包含Text向量数据
    private TextVector textVector;
    
    // 相似度分数
    @Builder.Default
    private float score = 0.0f;
    
    // 存储带高亮标记的内容文本
    private String highlightedContent;
    
    // 存储带高亮标记的标题
    private String highlightedTitle;
    
    // 向量相似度距离
    private float distance;
    
}
