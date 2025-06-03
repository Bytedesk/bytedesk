/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:32
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-27 18:04:45
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.quick_reply.mq;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 快捷回复索引消息
 * 用于在Artemis队列中传递需要索引的快捷回复信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuickReplyIndexMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 快捷回复唯一标识
     */
    private String quickReplyUid;
    
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