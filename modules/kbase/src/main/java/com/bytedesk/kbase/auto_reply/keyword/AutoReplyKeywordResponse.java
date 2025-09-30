/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-06 10:05:38
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-03 13:34:49
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.auto_reply.keyword;

import java.time.ZonedDateTime;
import java.util.List;

import com.bytedesk.core.base.BaseResponse;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.utils.BdDateUtils;

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
public class AutoReplyKeywordResponse extends BaseResponse {
    private static final long serialVersionUID = 1L;

    
    // 可以存储多个关键词："关键词1|关键词2|关键词3"
    private List<String> keywordList;

    // 多个关键词可以匹配到同一个回复
    private List<String> replyList;
    
    private AutoReplyKeywordMatchEnum matchType;

    private MessageTypeEnum contentType;

    private List<String> tagList;

    // 回复次数
    private Integer replyCount;

    // 有效开始日期
    private ZonedDateTime startDate;

    // 有效结束日期
    private ZonedDateTime endDate;

    private Boolean enabled;

    private Boolean transfer;

    private String categoryUid;

    private String kbUid; // 对应知识库
    
    // 
    public String getStartDate() {
        return BdDateUtils.formatDatetimeToString(startDate);
    }

    public String getEndDate() {
        return BdDateUtils.formatDatetimeToString(endDate);
    }
}
