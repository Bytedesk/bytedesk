/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-15 15:44:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-22 15:57:36
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.process;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProcessDefinitionResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String key;
    private String name;
    private String description;
    private Integer version;
    private String deploymentId;
    private String tenantId;
}
