/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-28 09:44:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-28 09:44:22
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket.listener;

import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Component;

@Component
public class TicketTaskListener implements TaskListener {

    @Override
    public void notify(DelegateTask delegateTask) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notify'");
    }
    
}
