/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-17 16:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-17 16:30:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.segment;

/**
 * 分词服务异常类
 * @author jackning
 */
public class SegmentException extends RuntimeException {
    
    public SegmentException(String message) {
        super(message);
    }
    
    public SegmentException(String message, Throwable cause) {
        super(message, cause);
    }
}