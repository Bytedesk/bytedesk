/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-04 20:44:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-04 20:44:15
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message.event;

import com.bytedesk.core.message.MessageEntity;

public class MessageUpdateEvent extends AbstractMessageEvent {

    private static final long serialVersionUID = 1L;

    public MessageUpdateEvent(Object source, MessageEntity message) {
        super(source, message);
    }

}
