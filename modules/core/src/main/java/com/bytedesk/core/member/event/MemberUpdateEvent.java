/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-04 10:41:37
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-05 11:22:15
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.member.event;

import com.bytedesk.core.member.MemberEntity;

public class MemberUpdateEvent extends AbstractMemberEvent {
    
    private static final long serialVersionUID = 1L;

    public MemberUpdateEvent(Object source, MemberEntity member) {
        super(source, member);
    }
}
