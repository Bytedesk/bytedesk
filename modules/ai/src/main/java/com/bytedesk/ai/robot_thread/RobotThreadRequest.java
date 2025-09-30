/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-29 13:09:44
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-19 09:49:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot_thread;

import com.bytedesk.core.base.BaseRequest;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class RobotThreadRequest extends BaseRequest {
    

    private static final long serialVersionUID = 1L;

    

    private String visitorUid;
}
