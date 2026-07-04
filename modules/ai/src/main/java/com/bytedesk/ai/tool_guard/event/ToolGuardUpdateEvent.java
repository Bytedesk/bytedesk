/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:59:29
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-25 10:01:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.tool_guard.event;

import com.bytedesk.ai.tool_guard.ToolGuardEntity;

/**
 * Event published when an existing tool_guard is updated.
 */
public class ToolGuardUpdateEvent extends AbstractToolGuardEvent {

    private static final long serialVersionUID = 1L;

    public ToolGuardUpdateEvent(ToolGuardEntity tool_guard) {
        super(tool_guard, tool_guard);
    }
}
