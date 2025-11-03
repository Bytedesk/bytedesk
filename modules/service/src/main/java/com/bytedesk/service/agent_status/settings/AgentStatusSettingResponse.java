/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:26:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-10 14:29:13
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent_status.settings;


import com.bytedesk.core.base.BaseResponse;

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
public class AgentStatusSettingResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;


    private String name;

    private String description;

    // 是否需要开启客服切换状态审核
    private Boolean needReview;

    // 审核时间段类型：ANY_TIME 或 CUSTOM_TIME
    private String reviewTimeType;

    // 自定义审核开始/结束时间，格式如 08:30 / 17:30
    private String reviewStartTime;
    private String reviewEndTime;

    // 审核方式：ALWAYS_MANUAL、AUTO_APPROVE、AUTO_REJECT
    private String reviewMethod;

    // 审核超时时间（分钟）
    private Integer reviewTimeoutMinutes;

    // private ZonedDateTime createdAt;
}
