/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-01 15:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-01 15:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.member.mq;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Member批量导入消息
 * 用于在消息队列中传递Member批量导入信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberBatchMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 批次唯一标识
     */
    private String batchUid;
    
    /**
     * 操作类型：batch_import
     */
    private String operationType;
    
    /**
     * Member Excel数据（JSON格式）
     */
    private String memberExcelJson;
    
    /**
     * 组织唯一标识
     */
    private String orgUid;
    
    /**
     * 批次索引（第几个Member）
     */
    private Integer batchIndex;
    
    /**
     * 批次总数
     */
    private Integer batchTotal;
    
    /**
     * 是否为最后一个批次
     */
    private Boolean isLastBatch;
    
    /**
     * 重试次数
     */
    private Integer retryCount;
    
    /**
     * 创建时间戳
     */
    private Long createTimestamp;
}
