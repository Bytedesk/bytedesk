/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-23 08:51:27
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-14 16:39:47
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.thread.event;

import com.bytedesk.core.thread.ThreadEntity;

public class ThreadCloseEvent extends AbstractThreadEvent {
    
    private static final long serialVersionUID = 1L;

    public ThreadCloseEvent(Object source, ThreadEntity thread) {
        super(source, thread);
    }

}
