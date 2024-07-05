/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-23 08:51:27
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-28 15:01:56
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.thread;

import org.springframework.context.ApplicationEvent;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ThreadStatusEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    public final String topic;

    public final ThreadStatusEnum status;

    public ThreadStatusEvent(Object source, String topic, ThreadStatusEnum status) {
        super(source);
        this.topic = topic;
        this.status = status;
    }

}
