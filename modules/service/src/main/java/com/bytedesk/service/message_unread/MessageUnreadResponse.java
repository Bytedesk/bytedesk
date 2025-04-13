/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-02 11:53:05
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-13 21:09:03
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.message_unread;

import com.bytedesk.core.base.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@AllArgsConstructor
public class MessageUnreadResponse extends BaseResponse {
    
    // private String userUid;
}
