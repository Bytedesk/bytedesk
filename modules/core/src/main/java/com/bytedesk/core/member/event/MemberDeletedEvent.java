/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-20 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-09-20 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.core.member.event;

import com.bytedesk.core.member.MemberEntity;

/**
 * 成员被移除出组织事件（MemberRestService.removeUserFromOrg 事务内同步发布）。
 *
 * 消费方使用 @TransactionalEventListener(phase = AFTER_COMMIT) 在事务提交后处理：
 * modules/service 据此级联软删该组织下该用户的一对一客服并释放坐席。
 */
public class MemberDeletedEvent extends AbstractMemberEvent {

    private static final long serialVersionUID = 1L;

    public MemberDeletedEvent(Object source, MemberEntity member) {
        super(source, member);
    }
}
