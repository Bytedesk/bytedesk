/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-07 08:11:16
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-09 13:13:39
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.thread;

import com.bytedesk.core.base.BaseExtra;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class ThreadExtra extends BaseExtra {

    private static final long serialVersionUID = 1L;

    public static ThreadExtra fromJson(String json) {
        ThreadExtra result = BaseExtra.fromJson(json, ThreadExtra.class);
        return result != null ? result : ThreadExtra.builder().build();
    }
    
}
