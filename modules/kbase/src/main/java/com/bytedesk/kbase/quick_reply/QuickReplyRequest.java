/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:48
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-24 15:12:10
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.quick_reply;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.enums.LevelEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class QuickReplyRequest extends BaseRequest {

    private String title;

    private String content;

    private String shortCut;

    // @Builder.Default
    // private String type;
    // private MessageTypeEnum type = MessageTypeEnum.TEXT;

    @Builder.Default
    private String level = LevelEnum.ORGANIZATION.name();

    @Builder.Default
    private List<String> tagList = new ArrayList<>();

    @Builder.Default
    private Boolean enabled = true;

    // 是否开启自动同步到llm_qa问答
    @Builder.Default
    private Boolean autoSyncLlmQa = false;

    // 是否已经同步llm问答
    @Builder.Default
    private Boolean llmQaSynced = false;

    // 同步到llm qa kbUid 
    private String llmQaKbUid;

    // 同步到llm qa uid
    private String llmQaUid;


    // 被点击次数
    @Builder.Default
    private Integer clickCount = 0;
    
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String categoryUid;

    private String kbUid; // 对应知识库的uid

    private String orgUid;

    private String agentUid;
}
