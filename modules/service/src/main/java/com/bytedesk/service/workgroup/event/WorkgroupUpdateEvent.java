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

import com.bytedesk.service.workgroup.WorkgroupEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class WorkgroupUpdateEvent extends ApplicationEvent {

    private final static long serialVersionUID = 1L;

    private WorkgroupEntity workgroup;

    public WorkgroupUpdateEvent(WorkgroupEntity workgroup) {
        super(workgroup);
        this.workgroup = workgroup;
    }
}
