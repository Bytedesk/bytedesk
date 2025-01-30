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

import com.bytedesk.service.workgroup.WorkgroupEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.context.ApplicationEvent;

@Data
@EqualsAndHashCode(callSuper = false)
public class WorkgroupCreateEvent extends ApplicationEvent {

    private final static long serialVersionUID = 1L;
    
    private WorkgroupEntity workgroup;
    
    public WorkgroupCreateEvent(WorkgroupEntity workgroup) {
        super(workgroup);
        this.workgroup = workgroup;
    }
    
}
