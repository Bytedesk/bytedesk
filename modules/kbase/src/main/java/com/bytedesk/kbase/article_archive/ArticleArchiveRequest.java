/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:48
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-07 09:34:22
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.article_archive;

import java.time.LocalDateTime;

import com.bytedesk.core.base.BaseRequest;

import com.bytedesk.core.constant.BytedeskConsts;
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
public class ArticleArchiveRequest extends BaseRequest {

    private String title;

    private String summary;
    // private String coverImageUrl;
    
    private String contentMarkdown;

    private String contentHtml;

    // search.html 搜索用
    private String content;

    // @Builder.Default
    // private MessageTypeEnum contentType = MessageTypeEnum.TEXT;

    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> tagList = new ArrayList<>();
    
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    
    @Builder.Default
    private Boolean top = false;

    @Builder.Default
    private Boolean published = false;

    @Builder.Default
    private Boolean markdown = false;

    @Builder.Default
    private Integer readCount = 0;

    @Builder.Default
    private Integer likeCount = 0;

    // status 状态
    @Builder.Default
    private String status = ArticleArchiveStatusEnum.DRAFT.name();

    // editor 编辑者
    @Builder.Default
    private String editor = BytedeskConsts.EMPTY_STRING;

    // 是否需要审核
    @Builder.Default
    private Boolean needAudit = false;

    // 审核状态
    @Builder.Default
    private String auditStatus = ArticleArchiveAuditStatusEnum.PENDING.name();

    // 审核意见
    @Builder.Default
    private String auditOpinion = BytedeskConsts.EMPTY_STRING;

    // 审核人
    @Builder.Default
    private String auditUser = BytedeskConsts.EMPTY_STRING;

    private String categoryUid;

    private String kbUid;

    private String userUid;
}
