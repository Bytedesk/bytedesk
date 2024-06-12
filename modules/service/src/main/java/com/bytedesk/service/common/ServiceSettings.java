/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-29 13:57:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-12 10:39:28
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

import com.bytedesk.ai.robot.Robot;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.enums.LanguageEnum;
import com.bytedesk.core.quick_button.QuickButton;
import com.bytedesk.service.worktime.Worktime;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
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
public class ServiceSettings {
    
    @NotBlank
    @Builder.Default
    // private String language = I18Consts.ZH_CN;
    private LanguageEnum language = LanguageEnum.ZH_CN;
    
    /**
     * 是否自动弹出客服窗口
     */
    @Builder.Default
    @Column(name = "is_auto_pop")
    private boolean autoPop = false;

    /**
     * TODO: set different tips for different lang
     */
    @Builder.Default
    private boolean showTopTip = false;

    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String topTip = I18Consts.I18N_TOP_TIP;

    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String welcomeTip = I18Consts.I18N_WELCOME_TIP;

    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String leavemsgTip = I18Consts.I18N_LEAVEMSG_TIP;

    /** auto close time in min - 默认自动关闭时间，单位分钟 */
    @Builder.Default
    private Double autoCloseMin = Double.valueOf(25);

    /**
     * robot
     * 是否默认机器人接待
     */
    @Builder.Default
    private boolean defaultRobot = false;

    /** 无客服在线时，是否启用机器人接待 */
    @Builder.Default
    private boolean offlineRobot = false;

    /** 非工作时间段，是否启用机器人接待 */
    @Builder.Default
    private boolean nonWorktimeRobot = false;

     /** work time */
    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY)
    private List<Worktime> worktimes = new ArrayList<>();

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY)
    private List<QuickButton> quickButtons = new ArrayList<>();

    // TODO: 留言设置

    // TODO: 评价设置

    // TODO: 询前问卷
    

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Robot robot;

    // 访客对话底部页面显示logo
    @Builder.Default
    private boolean showLogo = true;

    // 有效日期
    private Date validateUntil;
}
