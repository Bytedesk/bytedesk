/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-01 10:17:57
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-15 17:36:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.thread.enums;

public enum ThreadSummaryStatusEnum {
    NONE,
    // 待处理
    PENDING,
    // 处理中
    PROCESSING,
    // 已解决
    RESOLVED,
    // 已关闭
    CLOSED;
}
