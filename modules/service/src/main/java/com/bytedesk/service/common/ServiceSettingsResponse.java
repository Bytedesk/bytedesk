/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-29 13:57:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-12 13:02:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.bytedesk.ai.robot.RobotResponseSimple;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.LanguageEnum;
import com.bytedesk.core.quick_button.QuickButtonResponse;
import com.bytedesk.service.worktime.WorktimeResponse;

import jakarta.persistence.Column;
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
public class ServiceSettingsResponse {
    
    @Builder.Default
    // private String language = I18Consts.ZH_CN;
    private LanguageEnum language = LanguageEnum.ZH_CN;

    @Builder.Default
    @Column(name = "is_auto_pop")
    private Boolean autoPop = false;

    /**
     * TODO: set different tips for different lang
     */
    @Builder.Default
    private Boolean showTopTip = false;

    @Builder.Default
    @Column(length = 512)
    private String topTip = I18Consts.I18N_TOP_TIP;

    @Builder.Default
    private String welcomeTip = I18Consts.I18N_WELCOME_TIP;

    @Builder.Default
    private String leavemsgTip = I18Consts.I18N_LEAVEMSG_TIP;

    /** auto close time in min - 默认自动关闭时间，单位分钟 */
    @Builder.Default
    private Double autoCloseMin = Double.valueOf(25);

    /**
     * robot
     * 是否默认机器人接待
     */
    @Builder.Default
    private Boolean defaultRobot = false;

    /** 无客服在线时，是否启用机器人接待 */
    @Builder.Default
    private Boolean offlineRobot = false;

    /** 非工作时间段，是否启用机器人接待 */
    @Builder.Default
    private Boolean nonWorktimeRobot = false;

    @Builder.Default
    private List<WorktimeResponse> worktimes = new ArrayList<>();

    @Builder.Default
    private List<QuickButtonResponse> quickButtons = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private RobotResponseSimple robot;

    //
    @Builder.Default
    private Boolean showLogo = true;

    // 有效日期
    private Date validateUntil;
}
