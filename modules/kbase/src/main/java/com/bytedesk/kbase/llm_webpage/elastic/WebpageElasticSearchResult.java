/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-31 09:22:23
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-31 09:22:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_webpage.elastic;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 网页全文搜索结果类
 * 用于存储和传输搜索结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebpageElasticSearchResult implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private WebpageElastic webpageElastic;
   
    private float score;
    
    // 存储带高亮标记的标题文本
    private String highlightedTitle;
    
    // 存储带高亮标记的描述文本
    private String highlightedDescription;
    
    // 存储带高亮标记的内容文本(可能是摘要)
    private String highlightedContent;
}
