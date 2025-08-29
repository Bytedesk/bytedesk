/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-29 09:41:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message;

import com.bytedesk.core.base.BaseExtra;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 消息扩展字段，用于存储额外的信息
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class MessageExtra extends BaseExtra {

    // 是否内部消息
    // 例如：企业内部员工之间的消息，true: 内部消息，false: 外部消息
    @Builder.Default
    private Boolean isInternal = false; // 设置默认值为false

    private String translatedText; // 翻译后的文本
    
    private String orgUid; // 组织UID

    public static MessageExtra fromJson(String json) {
        MessageExtra result = BaseExtra.fromJson(json, MessageExtra.class);
        return result != null ? result : MessageExtra.builder().build();
    }
}
