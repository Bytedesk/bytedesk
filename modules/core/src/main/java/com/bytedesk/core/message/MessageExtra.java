/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-10 18:00:09
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

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.base.BaseExtra;
import com.bytedesk.core.constant.BytedeskConsts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

// 消息扩展字段
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class MessageExtra extends BaseExtra {

    // 有帮助、没帮助
    @Builder.Default
    private String helpful = MessageHelpfulEnum.OTHER.name();

    /**
     * 没有帮助的情况下，支持用户输入反馈内容 feedback
     * @{MessageFeedback}
     */
    @Builder.Default
    private String feedback = BytedeskConsts.EMPTY_JSON_STRING;
    
    // 自动回复
    @Builder.Default
    private Boolean isAutoReply = false;
    
    private String autoReplyType;

    // 机器人回复
    
    // 翻译
    private String translation;

    // 引用
    private String quotation;
    
    // 企业id
    private String orgUid;

    public static MessageExtra fromJson(String json) {
        return JSON.parseObject(json, MessageExtra.class);
    }
}
