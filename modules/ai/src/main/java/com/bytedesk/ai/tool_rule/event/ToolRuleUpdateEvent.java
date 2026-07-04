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
package com.bytedesk.ai.tool_rule.event;

import com.bytedesk.ai.tool_rule.ToolRuleEntity;

/**
 * Event published when an existing tool_rule is updated.
 */
public class ToolRuleUpdateEvent extends AbstractToolRuleEvent {

    private static final long serialVersionUID = 1L;

    public ToolRuleUpdateEvent(ToolRuleEntity tool_rule) {
        super(tool_rule, tool_rule);
    }
}
