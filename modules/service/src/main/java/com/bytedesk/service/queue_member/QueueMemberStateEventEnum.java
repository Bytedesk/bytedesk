/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-02 15:00:41
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-02 15:01:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue_member;

public enum QueueMemberStateEventEnum {
    AUTO_ACCEPT, // 自动接入
    MANUAL_ACCEPT, // 手动接入
    MANUAL_CLOSE, // 手动关闭
}
