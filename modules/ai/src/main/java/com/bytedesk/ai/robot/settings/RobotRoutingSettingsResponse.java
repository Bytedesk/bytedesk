/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-29 13:57:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-10-24 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot.settings;

import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.core.base.BaseResponse;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * Response DTO for robot routing settings
 */
@Data
@SuperBuilder
@Embeddable
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RobotRoutingSettingsResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    // 是否默认机器人接待
    @Builder.Default
    private Boolean defaultRobot = false;

    /** 无客服在线时，是否启用机器人接待 */
    @Builder.Default
    private Boolean offlineRobot = false;

    /** 非工作时间段，是否启用机器人接待 */
    @Builder.Default
    private Boolean nonWorktimeRobot = false;

    @ManyToOne(fetch = FetchType.LAZY)
    private RobotProtobuf robot;

}
