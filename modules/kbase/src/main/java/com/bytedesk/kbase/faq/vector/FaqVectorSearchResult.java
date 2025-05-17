/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-14 14:25:05
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-14 14:40:15
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.faq.vector;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * FAQ向量搜索结果实体类
 * 用于表示向量相似度检索的结果，参考FaqElasticSearchResult结构
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaqVectorSearchResult implements Serializable {

    private static final long serialVersionUID = 1L;
    
    // 包含FAQ向量数据
    private FaqVector faqVector;
    
    // 相似度分数
    private float score;
    
    // 存储带高亮标记的问题文本
    private String highlightedQuestion;
    
    // 向量相似度距离
    private float distance;
}
