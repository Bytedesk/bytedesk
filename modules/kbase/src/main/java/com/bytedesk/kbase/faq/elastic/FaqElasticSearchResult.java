/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-28 21:31:59
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-29 09:21:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.faq.elastic;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaqElasticSearchResult implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private FaqElastic faqElastic;
   
    private float score;
    
    // 存储带高亮标记的问题文本
    private String highlightedQuestion;
}
