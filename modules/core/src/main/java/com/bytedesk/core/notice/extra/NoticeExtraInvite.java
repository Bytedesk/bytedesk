/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-05 08:06:43
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-14 15:23:36
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.notice.extra;

import com.bytedesk.core.thread.ThreadEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoticeExtraInvite {

    // 转接备注
    private String note;

    // 被转接会话
    private ThreadEntity thread;

    // 转接消息uid，用于invite_accept/invite_reject
    private String uid;

}
