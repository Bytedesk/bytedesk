/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-26 09:39:05
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-02 09:44:29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class VisitorDevice {

    // device info
    private String browser;

    private String os;

    private String device;
    
}
