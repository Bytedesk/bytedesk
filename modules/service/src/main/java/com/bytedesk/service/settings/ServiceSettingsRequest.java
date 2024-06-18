/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-29 13:57:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-14 12:13:41
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

import com.bytedesk.core.service_settings.BaseServiceSettingsRequest;

import jakarta.persistence.Embeddable;
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
public class ServiceSettingsRequest extends BaseServiceSettingsRequest {
    
    // @Builder.Default
    // // private String language = I18Consts.ZH_CN;
    // private LanguageEnum language = LanguageEnum.ZH_CN;

    // @Builder.Default
    // @Column(name = "is_auto_pop")
    // private Boolean autoPop = false;

    // /**
    //  * TODO: set different tips for different lang
    //  */
    // @Builder.Default
    // private Boolean showTopTip = false;

    // @Builder.Default
    // @Column(length = 512)
    // private String topTip = I18Consts.I18N_TOP_TIP;

    // @Builder.Default
    // private String welcomeTip = I18Consts.I18N_WELCOME_TIP;

    // @Builder.Default
    // private String leavemsgTip = I18Consts.I18N_LEAVEMSG_TIP;

    // /** auto close time in min - 默认自动关闭时间，单位分钟 */
    // @Builder.Default
    // private Double autoCloseMin = Double.valueOf(25);

    // @Builder.Default
    // private List<String> worktimeUids = new ArrayList<>();

    // @Builder.Default
    // private List<String> quickButtonUids = new ArrayList<>();

    // @Builder.Default
    // private List<String> faqUids = new ArrayList<>();

    // // 访客对话底部页面显示logo
    // @Builder.Default
    // private Boolean showLogo = true;

    // // 有效日期
    // private Date validateUntil;

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

    private String robotUid;

    
}
