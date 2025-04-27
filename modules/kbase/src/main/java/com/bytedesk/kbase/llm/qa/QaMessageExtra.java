/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-18 18:09:59
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-27 13:16:49
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm.qa;

import com.bytedesk.core.message.MessageExtra;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class QaMessageExtra extends MessageExtra {

    private static final long serialVersionUID = 1L;
    
    private String uid;
    private String qaUid;
    private String rate;
    private String orgUid;
}
