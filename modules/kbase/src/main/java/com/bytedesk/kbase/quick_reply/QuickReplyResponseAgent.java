/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-27 20:49:41
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-20 13:46:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.quick_reply;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Data;

// 客服端加载
@Data
@Builder
public class QuickReplyResponseAgent {

    private String key;

    private String title;

    private String content;

    private String type;

    private String level;

    private String platform;

    @Builder.Default
    private List<QuickReplyResponseAgent> children = new ArrayList<>();
}
