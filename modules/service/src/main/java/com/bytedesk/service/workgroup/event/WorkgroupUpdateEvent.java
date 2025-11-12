/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-30 14:07:21
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-30 14:07:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk  
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.workgroup.event;

import org.springframework.context.ApplicationEvent;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 工作组更新事件：最小载荷，避免实体序列化带来的懒加载问题。
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class WorkgroupUpdateEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private final String orgUid;
    private final String workgroupUid;
    private final String nickname; // 可选业务字段（名称变更等）

    public WorkgroupUpdateEvent(String orgUid, String workgroupUid, String nickname) {
        super(workgroupUid);
        this.orgUid = orgUid;
        this.workgroupUid = workgroupUid;
        this.nickname = nickname;
    }
}
