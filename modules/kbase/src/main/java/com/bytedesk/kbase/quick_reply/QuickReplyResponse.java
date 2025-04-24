/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-24 15:12:22
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

import com.bytedesk.core.base.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class QuickReplyResponse extends BaseResponse {

    private String title;

    private String content;

    private String shortCut;

    private String type;

    // private String level;

    private List<String> tagList;

    private Boolean enabled;

    // 是否开启自动同步到llm_qa问答
    private Boolean autoSyncLlmQa;

    // 是否已经同步llm问答
    private Boolean llmQaSynced;

    // 同步到llm qa kbUid 
    private String llmQaKbUid;

    // 同步到llm qa uid
    private String llmQaUid;

    // 被点击次数
    private Integer clickCount;

    // 有效开始日期
    private LocalDateTime startDate;

    // 有效结束日期
    private LocalDateTime endDate;

    private String categoryUid;

    private String kbUid;

    private String agentUid;
    
}
