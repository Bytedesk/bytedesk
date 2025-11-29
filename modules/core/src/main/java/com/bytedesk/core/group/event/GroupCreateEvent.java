/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-04 10:41:37
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-13 08:54:45
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.group.event;

import com.bytedesk.core.group.GroupEntity;

public class GroupCreateEvent extends AbstractGroupEvent {
    
    private static final long serialVersionUID = 1L;

    public GroupCreateEvent(Object source, GroupEntity group) {
        super(source, group);
    }
}
