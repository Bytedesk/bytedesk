/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-19 10:44:15
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-19 15:27:05
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.thread;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ThreadFlowNode implements Serializable {

    private final static long serialVersionUID = 1L;

    private ThreadStateEnum status;

    private String content;

    private String createAt;

    private ThreadFlowNode children[];
}
