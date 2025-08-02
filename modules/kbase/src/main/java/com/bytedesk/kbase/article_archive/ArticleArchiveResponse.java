/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-02 08:22:50
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.article_archive;

import java.time.ZonedDateTime;
import java.util.List;

import com.bytedesk.core.base.BaseResponse;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.kbase.kbase.KbaseTypeEnum;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.experimental.Accessors;

@Getter
@Setter
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class ArticleArchiveResponse extends BaseResponse {

    private String title;

    private String summary;
    // private String coverImageUrl;

    private String contentMarkdown;

    private String contentHtml;

    private KbaseTypeEnum type;

    private List<String> tagList;

    // 有效开始日期
    private ZonedDateTime startDate;

    // 有效结束日期
    private ZonedDateTime endDate;

    private Boolean top;

    private Boolean published;

    private Boolean markdown;

    private Integer readCount;

    private Integer likeCount;

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

    private UserProtobuf user;

    // 
    public String getStartDate() {
        return BdDateUtils.formatDatetimeToString(startDate);
    }

    public String getEndDate() {
        return BdDateUtils.formatDatetimeToString(endDate);
    }
}
