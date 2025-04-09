/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:05:05
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-09 08:55:45
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.message_leave;

import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class MessageLeaveRequest extends BaseRequest {

    private String contact;

    private String content;

    @Builder.Default
    private List<String> images = new ArrayList<>();

    private String threadTopic;

    @Builder.Default
    private String status = MessageLeaveStatusEnum.PENDING.name();

    private String user;
}
