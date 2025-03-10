/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:59:29
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-25 15:51:33
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.taboo_message.event;

import org.springframework.context.ApplicationEvent;

import com.bytedesk.kbase.taboo_message.TabooMessageEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class TabooMessageUpdateEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private TabooMessageEntity taboo_message;

    public TabooMessageUpdateEvent(TabooMessageEntity taboo_message) {
        super(taboo_message);
        this.taboo_message = taboo_message;
    }

}
