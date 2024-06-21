/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-29 13:57:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-21 10:26:16
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
import com.bytedesk.core.service_settings.BaseServiceSettings;
import com.bytedesk.service.worktime.Worktime;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Embeddable;
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

    /**
     * default robot chat
     */
    @Builder.Default
    private boolean defaultRobot = false;

    @Builder.Default
    private boolean offlineRobot = false;

    @Builder.Default
    private boolean nonWorktimeRobot = false;

    /** work time */
    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY)
    private List<Worktime> worktimes = new ArrayList<>();

    // TODO: 留言设置

    // TODO: 评价设置

    // TODO: 询前问卷

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Robot robot;
}
