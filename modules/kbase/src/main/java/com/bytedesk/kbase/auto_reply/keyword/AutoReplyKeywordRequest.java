/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-06 10:05:25
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-10 16:24:03
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.auto_reply.keyword;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.message.MessageTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AutoReplyKeywordRequest extends BaseRequest {
    
    // 可以存储多个关键词："关键词1|关键词2|关键词3"
    @Builder.Default
    private List<String> keywordList = new ArrayList<>();
    // private String keyword;

    // 多个关键词可以匹配到同一个回复
    @Builder.Default
    private List<String> replyList = new ArrayList<>();
    // private String reply;
    
    @Builder.Default
    private AutoReplyKeywordMatchEnum matchType = AutoReplyKeywordMatchEnum.EXACT;

    @Builder.Default
    private MessageTypeEnum contentType = MessageTypeEnum.TEXT;

    @Builder.Default
    private String tags = BytedeskConsts.EMPTY_ARRAY_STRING;

    // 回复次数
    @Builder.Default
    private Integer replyCount = 0;
    
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @Builder.Default
    private Boolean enabled = true;

    @Builder.Default
    private Boolean isTransfer = false; // 是否转人工
    
    private String categoryUid;
    
    private String kbUid; // 对应知识库

    private String userUid;
}
