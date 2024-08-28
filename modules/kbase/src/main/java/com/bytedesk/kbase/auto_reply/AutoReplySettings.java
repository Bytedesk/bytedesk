/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-17 23:34:04
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-19 16:15:01
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.auto_reply;

import java.io.Serializable;

import com.bytedesk.core.constant.BdConstants;
import com.bytedesk.core.message.MessageTypeEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class AutoReplySettings implements Serializable {

    private static final long serialVersionUID = 1L;
    
    // 自动回复开关
    @Builder.Default
    @Column(name = "autoreply_enabled")
    private Boolean enabled = false;

    // 自动回复类型
    @Builder.Default
    // @Enumerated(EnumType.STRING)
    // private AutoReplyTypeEnum autoReplyType = AutoReplyTypeEnum.FIXED;
    private String autoReplyType = AutoReplyTypeEnum.FIXED.name();

    // 固定回复类型所需要字段
    @Builder.Default
    private String autoReplyUid = BdConstants.EMPTY_STRING;
    
    // 自动回复内容类型
    @Builder.Default
    // @Enumerated(EnumType.STRING)
    // private MessageTypeEnum autoReplyContentType = MessageTypeEnum.TEXT;
    private String autoReplyContentType = MessageTypeEnum.TEXT.name();

    // 自动回复内容
    @Builder.Default
    private String autoReplyContent = BdConstants.EMPTY_STRING;

    // 关键词回复类型所需要字段
    // 大模型回复类型所需要字段
    private String kbUid;

    // 

}
