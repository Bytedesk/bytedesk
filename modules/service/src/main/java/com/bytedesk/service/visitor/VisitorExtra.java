/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-19 13:17:06
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-19 13:17:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor;

import java.io.Serializable;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
// @NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class VisitorExtra implements Serializable {

    private static final long serialVersionUID = 1L;
    
}
