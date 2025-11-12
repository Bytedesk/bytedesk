/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-30 14:05:44
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-30 14:06:52
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
 * 工作组创建事件：仅携带最小必要信息，避免实体序列化/懒加载问题。
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class WorkgroupCreateEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private final String orgUid;
    private final String workgroupUid;

    public WorkgroupCreateEvent(String orgUid, String workgroupUid) {
        super(workgroupUid); // 以 workgroupUid 作为事件源
        this.orgUid = orgUid;
        this.workgroupUid = workgroupUid;
    }
}
