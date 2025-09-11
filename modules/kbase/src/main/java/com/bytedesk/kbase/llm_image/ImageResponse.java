/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:26:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-03 13:32:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_image;

import java.time.ZonedDateTime;
import java.util.List;

import com.bytedesk.core.base.BaseResponse;
import com.bytedesk.core.utils.BdDateUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class ImageResponse extends BaseResponse {

    private String name;

    private String content;

    private String type;

    // 对应 拆分 document 的 id
    private String docId;

    private String elasticStatus;

    private String vectorStatus;

    private List<String> tagList;

    // 有效开始日期
    private ZonedDateTime startDate;

    // 有效结束日期
    private ZonedDateTime endDate;

    private Boolean enabled;

    // 所属分类
    private String categoryUid;

    // kbaseEntity 的 uid
    private String kbUid;

    // fileEntity 的 uid
    private String fileUid;

    // 
    public String getStartDate() {
        return BdDateUtils.formatDatetimeToString(startDate);
    }

    public String getEndDate() {
        return BdDateUtils.formatDatetimeToString(endDate);
    }

}
