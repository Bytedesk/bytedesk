/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-07 08:11:16
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-11 16:07:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.thread;

import java.io.Serializable;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
// @NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ThreadExtra implements Serializable {

    private static final long serialVersionUID = 1L;
    
    // private Boolean isOffline; // 标记此会话是否曾经处于离线状态
}
