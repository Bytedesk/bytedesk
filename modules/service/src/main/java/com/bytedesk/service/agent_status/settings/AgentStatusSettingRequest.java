/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:26:04
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-12 10:15:30
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent_status.settings;

import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.constant.I18Consts;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class AgentStatusSettingRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    private String name;

    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    // 是否支持音频会话状态
    @Builder.Default
    private Boolean availableAudioEnabled = false;

    // 是否支持视频会话状态
    @Builder.Default
    private Boolean availableVideoEnabled = false;

    // 是否支持电话会话状态
    @Builder.Default
    private Boolean availablePhoneEnabled = false;

    // 是否支持休息状态
    @Builder.Default
    private Boolean restEnabled = true;

    // 休息状态可选原因列表
    @Builder.Default
    private List<String> restReasons = new ArrayList<>();

    // 是否需要开启客服切换状态审核
    @Builder.Default
    private Boolean needReview = false;

    // 审核时间段类型：ANY_TIME(任意时间)或CUSTOM_TIME(自定义时间)
    @Builder.Default
    private String reviewTimeType = "ANY_TIME";

    // 自定义审核开始时间，格式如：08:30
    private String reviewStartTime;

    // 自定义审核结束时间，格式如：17:30
    private String reviewEndTime;

    // 审核方式：ALWAYS_MANUAL、AUTO_APPROVE、AUTO_REJECT
    @Builder.Default
    private String reviewMethod = "ALWAYS_MANUAL";

    // 审核超时时间（分钟）
    @Builder.Default
    private Integer reviewTimeoutMinutes = 10;

}
