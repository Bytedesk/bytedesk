/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-17 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-17 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_faq.mq;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * FAQ索引消息
 * 用于在Artemis队列中传递需要索引的FAQ信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaqIndexMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * FAQ唯一标识
     */
    private String faqUid;
    
    /**
     * 操作类型：create, update, delete
     */
    private String operationType;
    
    /**
     * 是否需要更新全文索引
     */
    private Boolean updateElasticIndex;
    
    /**
     * 是否需要更新向量索引
     */
    private Boolean updateVectorIndex;
}
