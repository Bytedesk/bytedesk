/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-15 15:57:29
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-03 13:48:19
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.routing_strategy;

import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.service.visitor.VisitorRequest;

public interface ThreadRoutingStrategy {
    
    MessageProtobuf createThread(VisitorRequest visitorRequest);
}