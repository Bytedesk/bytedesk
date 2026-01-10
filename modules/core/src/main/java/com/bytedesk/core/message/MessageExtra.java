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
    
    private static final long serialVersionUID = 1L;

    // 消息可见范围
    // public: 外部公开可见（默认）
    // internal: 企业内部可见
    // private: 仅我自己可见
    @Builder.Default
    private MessageVisibilityEnum visibility = MessageVisibilityEnum.PUBLIC;

    private String translatedText; // 翻译后的文本

    // 敏感词替换前的原始内容（用于审计/回溯）
    private String tabooOriginalContent;
    
    private String orgUid; // 组织UID

    public static MessageExtra fromJson(String json) {
        MessageExtra result = BaseExtra.fromJson(json, MessageExtra.class);
        return result != null ? result : MessageExtra.builder().build();
    }

    public static MessageExtra fromOrgUid(String orgUid) {
        return MessageExtra.builder().orgUid(orgUid).build();
    }

}
