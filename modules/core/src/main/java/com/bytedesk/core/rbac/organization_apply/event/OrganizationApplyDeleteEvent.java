/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 12:31:16
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-13 13:08:03
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.organization_apply.event;

import org.springframework.context.ApplicationEvent;

import com.bytedesk.core.rbac.organization_apply.OrganizationApplyEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class OrganizationApplyDeleteEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private OrganizationApplyEntity organizationApply;

    public OrganizationApplyDeleteEvent(OrganizationApplyEntity organizationApply) {
        super(organizationApply);
        this.organizationApply = organizationApply;
    }
}
