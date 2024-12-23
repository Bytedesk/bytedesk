/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-29 13:57:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-23 13:39:37
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.settings;

import java.util.ArrayList;
import java.util.List;

import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.service.worktime.WorktimeResponse;

import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Embeddable
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class RobotSettingsResponse {

    // 是否默认机器人接待
    @Builder.Default
    private Boolean defaultRobot = false;

    /** 无客服在线时，是否启用机器人接待 */
    @Builder.Default
    private Boolean offlineRobot = false;

    /** 非工作时间段，是否启用机器人接待 */
    @Builder.Default
    private Boolean nonWorktimeRobot = false;

    @Builder.Default
    private Boolean queueRobot = false;
    
    @Builder.Default
    private Integer queueRobotCount = 100;

    @Builder.Default
    private List<WorktimeResponse> worktimes = new ArrayList<>();

    

    @ManyToOne(fetch = FetchType.LAZY)
    private RobotProtobuf robot;

}
