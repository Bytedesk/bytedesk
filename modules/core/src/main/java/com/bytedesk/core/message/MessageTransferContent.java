/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-05 08:06:43
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-05 08:18:42
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message;

import com.bytedesk.core.thread.Thread;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageTransferContent {

    // 转接备注
    private String note;

    // 被转接会话
    private Thread thread;

    // 转接消息uid，用于transfer_accept/transfer_reject
    private String uid;

}
