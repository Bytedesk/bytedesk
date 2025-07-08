/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-18 11:49:43
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-08 18:55:39
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

import com.bytedesk.core.base.BaseExtra;
import com.bytedesk.core.message.MessageExtra;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class MessageLeaveExtra extends MessageExtra {

    private String uid;
    private String contact;
    private String content;

    @Builder.Default
    private List<String> images = new ArrayList<>();
    
    private String replyContent;

    @Builder.Default
    private List<String> replyImages = new ArrayList<>();

    private String status;

    public static MessageLeaveExtra fromJson(String json) {
        MessageLeaveExtra result = BaseExtra.fromJson(json, MessageLeaveExtra.class);
        return result != null ? result : MessageLeaveExtra.builder().build();
    }
}
