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
package com.bytedesk.core.rbac.organization.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

/**
 * 组织管理员转移事件。
 *
 * 发布时机：管理员转移事务内同步发布（不要走 @Async 的 BytedeskEventPublisher），
 * 消费方使用 @TransactionalEventListener(phase = AFTER_COMMIT) 在事务提交后处理，
 * 回滚时不会触发。
 *
 * 注意：不要复用 OrganizationCreateEvent，那会重放 category/faq/quickbutton/robot/workgroup
 * 等全部组织初始化监听器。
 */
@Getter
public class OrganizationAdminTransferEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private final String orgUid;

    /** 原管理员 userUid，可能为 null（组织此前没有管理员） */
    private final String oldAdminUserUid;

    /** 新管理员 userUid */
    private final String newAdminUserUid;

    public OrganizationAdminTransferEvent(Object source, String orgUid, String oldAdminUserUid,
            String newAdminUserUid) {
        super(source);
        this.orgUid = orgUid;
        this.oldAdminUserUid = oldAdminUserUid;
        this.newAdminUserUid = newAdminUserUid;
    }
}
