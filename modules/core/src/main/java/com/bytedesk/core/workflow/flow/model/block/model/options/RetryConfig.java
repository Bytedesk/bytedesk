/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-10 18:21:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-10 18:22:06
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  技术/商务联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.workflow.flow.model.block.model.options;


import lombok.Data;

@Data
public class RetryConfig {
     private Integer limit;
     private Integer interval;
 } 
