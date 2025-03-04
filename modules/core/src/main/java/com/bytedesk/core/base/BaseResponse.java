/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-04 09:38:07
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.base;

import java.io.Serializable;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public abstract class BaseResponse implements Serializable {

    private String uid;

    // 导致报错： com.google.protobuf.InvalidProtocolBufferException: Cannot find field: orgUid
    // in message Thread
    // at com.bytedesk.core.utils.MessageConvertUtils.toProtoBean(MessageConvertUtils.java:19)
    // at com.bytedesk.core.message.MessageEventListener.onMessageJsonEvent(MessageEventListener.java:55)
    // private String orgUid;
}
