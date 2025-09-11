/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-05 15:15:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-05 15:15:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_image.mq;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Image处理完成消息
 * 用于通知文件处理状态更新
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageCompleteMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Image唯一标识
     */
    private String imageUid;
    
    /**
     * 文件UID
     */
    private String fileUid;
    
    /**
     * 处理状态：success, error
     */
    private String status;
    
    /**
     * 错误信息（如果处理失败）
     */
    private String errorMessage;
    
    /**
     * 处理类型：elastic, vector
     */
    private String processType;
}
