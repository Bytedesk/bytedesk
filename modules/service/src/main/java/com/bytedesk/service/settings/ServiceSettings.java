/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-29 13:57:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-03 10:37:12
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

import com.bytedesk.ai.robot.Robot;
import com.bytedesk.kbase.service_settings.BaseServiceSettings;
import com.bytedesk.service.leave_msg.LeaveMsgSettings;
import com.bytedesk.service.worktime.Worktime;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@Embeddable
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class ServiceSettings extends BaseServiceSettings {

    // 开启机器人之后，robot字段为必填
    @Builder.Default
    private boolean defaultRobot = false;
    @Builder.Default
    private boolean offlineRobot = false;
    @Builder.Default
    private boolean nonWorktimeRobot = false;

    // TODO: 当排队人数超过指定值时，自动分配机器人
    // @Builder.Default
    // private boolean queueRobot = false;
    // @Builder.Default
    // private int queueRobotCount = 100;

    /** work time */
    @Builder.Default
    @OneToMany(fetch = FetchType.EAGER)
    private List<Worktime> worktimes = new ArrayList<>();

    // 留言设置
    @Embedded
    @Builder.Default
    private LeaveMsgSettings leaveMsgSettings = new LeaveMsgSettings();

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Robot robot;

    //
    public boolean isWorkTime() {
        if (this.worktimes == null || this.worktimes.isEmpty()) {
            return true;
        }
        return this.worktimes.stream().anyMatch(w -> w.isWorkTime());
    }

    //
    public Boolean shouldTransferToRobot(Boolean isOffline) {
        if (this.defaultRobot) {
            // 默认机器人优先接待
            return true;
        } else if (isOffline && this.offlineRobot) {
            // 所有客服离线，且设置机器人离线优先接待
            return true;
        } else if (this.nonWorktimeRobot && !isWorkTime()) {
            // 设置非工作时间机器人接待，且当前非工作时间，转机器人
            return true;
        }
        return false;
    }
}
