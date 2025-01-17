/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-27 12:21:29
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-17 10:25:12
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.black;

import java.time.LocalDateTime;

import com.bytedesk.core.base.BaseResponse;

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
public class BlackResponse extends BaseResponse {

    private String reason;

    // AI: 考虑到黑名单功能主要用于用户访问控制,需要频繁查询和过滤,我建议使用单独字段存储的方式:
    // 黑名单用户uid
    private String blackUid;

    // 黑名单用户昵称
    private String blackNickname;

    // 黑名单用户头像
    private String blackAvatar;

    // 执行拉黑的用户uid
    private String userUid;

    // 执行拉黑的用户昵称
    private String userNickname;

    // 执行拉黑的用户头像
    private String userAvatar;

    // 开始时间
    private LocalDateTime startTime;

    // 结束时间
    private LocalDateTime endTime;

    // 会话主题
    private String threadTopic;
}
