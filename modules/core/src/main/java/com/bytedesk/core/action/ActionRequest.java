/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-25 15:40:29
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-20 18:11:58
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.action;

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.enums.PlatformEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class ActionRequest extends BaseRequest {
    
    private static final long serialVersionUID = 1L;
        
    private String title;

    private String action;

    private String description;

    private String ip;

    // according to ip address
    private String ipLocation;

    private String extra;
    
    // private ActionTypeEnum type;
    // private String orgUid;

    private String nickname;

    @Builder.Default
    private PlatformEnum platform = PlatformEnum.BYTEDESK;
}
