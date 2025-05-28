/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-13 15:25:32
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-13 15:25:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_chunk;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChunkElasticSearchResult implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private ChunkElastic chunkElastic;
   
    private float score;
    
    // 存储带高亮标记的内容文本
    private String highlightedContent;
    
    // 存储带高亮标记的名称
    private String highlightedName;
}
