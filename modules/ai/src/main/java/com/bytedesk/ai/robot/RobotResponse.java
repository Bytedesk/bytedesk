/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 16:45:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-21 13:21:15
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot;

import com.bytedesk.core.base.BaseResponse;
import com.bytedesk.kbase.settings.ServiceSettingsResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RobotResponse extends BaseResponse {

    private String name;

    private String nickname;

    private String avatar;

    private String description;

    private String type;

    private ServiceSettingsResponse serviceSettings;

    // private InviteSettings inviteSettings;

    private RobotLlm llm;

    private String defaultReply;

    // 机器人分类
    private String categoryUid;

    // private Boolean published;

    private Boolean kbEnabled;

    private String kbUid; // 对应知识库

    // flow enabled
    private Boolean flowEnabled;

    // flow uid
    private String flowUid;

    private Boolean fromSystem;
}
