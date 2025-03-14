/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-14 15:24:26
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-14 16:06:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.notice.extra;

import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class NoticeExtraLogin implements Serializable {

    private static final long serialVersionUID = 1L;

    private String loginIp;

    private String loginDevice;

    private String loginOs;

    private String loginBrowser;

    private String loginLocation;

    private String loginTime;
    
}
