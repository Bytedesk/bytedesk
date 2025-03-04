/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-21 10:00:32
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-04 08:37:38
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message;

import com.bytedesk.core.base.BaseRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequest extends BaseRequest {
    
    // private String[] threads;
    private String threadTopic;
    
    private String userUid;

    private String nickname;

    private String type;

    // private String orgUid;

    // used for client query
    private String componentType;
}
