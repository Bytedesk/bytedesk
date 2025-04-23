/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-23 17:12:58
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.article;

import java.time.LocalDateTime;
import java.util.List;

import com.bytedesk.core.base.BaseResponse;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.kbase.kbase.KbaseTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class ArticleResponse extends BaseResponse {

    private String title;

    private String summary;
    // private String coverImageUrl;

    private String contentMarkdown;

    private String contentHtml;

    private KbaseTypeEnum type;

    private List<String> tagList;

    // 有效开始日期
    private LocalDateTime startDate;

    // 有效结束日期
    private LocalDateTime endDate;

    private Boolean top;

    private Boolean published;

    // 是否开启自动生成enable_llm_qa问答
    private Boolean autoGenerateLlmQa;

    // 是否已经生成llm问答
    private Boolean llmQaGenerated;

    // 是否开启自动删除llm问答
    private Boolean autoDeleteLlmQa;

    // 是否已经删除llm问答
    private Boolean llmQaDeleted;

    // 是否开启自动llm split切块
    private Boolean autoLlmSplit;

    // 是否已经自动llm split切块
    private Boolean llmSplitted;

    // 是否开启自动删除llm split切块
    private Boolean autoDeleteLlmSplit;

    // 是否已经删除llm split切块
    private Boolean llmSplittedDeleted;

    private Boolean markdown;

    private Integer readCount;

    private Integer likeCount = 0;

    // status 状态
    private String status;

    // editor 编辑者
    private String editor;

    // 是否需要审核
    private Boolean needAudit;

    // 审核状态
    private String auditStatus;

    // 审核意见
    private String auditOpinion;

    // 审核人
    private String auditUser;

    private String categoryUid;

    private String kbUid;

    // private String orgUid;

    // private LocalDateTime createdAt;

    // private LocalDateTime updatedAt;

    private UserProtobuf user;

    // public String getCreatedAt() {
    //     return BdDateUtils.formatDatetimeToString(createdAt);
    // }

    // public String getUpdatedAt() {
    //     return BdDateUtils.formatDatetimeToString(updatedAt);
    // }
}
