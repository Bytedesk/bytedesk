/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-30 07:13:59
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-03 17:51:36
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.category.event;

import org.springframework.context.ApplicationEvent;

import com.bytedesk.core.category.CategoryEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class CategoryCreateEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private CategoryEntity category;

    public CategoryCreateEvent(Object source, CategoryEntity category) {
        super(source);
        this.category = category;
    }
    
}
