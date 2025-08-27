/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-28 09:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-28 09:30:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_webpage.mq;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 网页索引消息
 * 用于在Artemis队列中传递需要索引的网页信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebpageIndexMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 网页唯一标识
     */
    private String webpageUid;
    
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
    
    /**
     * 检查是否需要更新Elastic索引
     */
    public boolean isUpdateElasticIndex() {
        return updateElasticIndex != null && updateElasticIndex;
    }
    
    /**
     * 检查是否需要更新向量索引
     */
    public boolean isUpdateVectorIndex() {
        return updateVectorIndex != null && updateVectorIndex;
    }
}
