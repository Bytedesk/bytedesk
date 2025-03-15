/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-05 08:06:43
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-15 09:46:09
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.notice.extra;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.base.BaseExtra;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadProtobuf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoticeExtraTransfer extends BaseExtra {

    private static final long serialVersionUID = 1L;

    // 转接备注
    private String note;

    // 被转接会话
    private ThreadProtobuf thread;

    private UserProtobuf agent;

    // 转接消息uid，用于transfer_accept/transfer_reject
    private String messageUid;

    public static NoticeExtraTransfer parseFromJson(String json) {
        return JSON.parseObject(json, NoticeExtraTransfer.class);
    }

}
