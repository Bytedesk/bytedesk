/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-18 11:49:43
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-27 14:39:08
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

import com.alibaba.fastjson2.JSON;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageLeaveExtra {
    // 
    private String contact;
    private String content;
    private String reply;
    private String status;
    // 
    @Builder.Default
    private List<String> images = new ArrayList<>();

    // replyImages
    @Builder.Default
    private List<String> replyImages = new ArrayList<>();

    // fromJson
    public static MessageLeaveExtra fromJson(String json) {
        return JSON.parseObject(json, MessageLeaveExtra.class);
    }

    public String toJson() {
        return JSON.toJSONString(this);
    }
}
