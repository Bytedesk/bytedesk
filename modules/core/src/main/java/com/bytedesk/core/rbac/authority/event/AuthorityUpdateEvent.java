/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-20 16:55:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-20 16:56:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.authority.event;

import com.bytedesk.core.rbac.authority.AuthorityEntity;

public class AuthorityUpdateEvent extends AbstractAuthorityEvent {

    private static final long serialVersionUID = 1L;

    public AuthorityUpdateEvent(AuthorityEntity authority) {
        super(authority, authority);
    }

}
