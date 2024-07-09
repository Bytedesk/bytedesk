/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-14 10:45:08
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-06 18:47:34
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.service_settings;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.LanguageEnum;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@MappedSuperclass
public class BaseServiceSettingsRequest {

    // private String language = I18Consts.ZH_CN;
    private LanguageEnum language = LanguageEnum.ZH_CN;

    private Boolean autoPop = false;

    /**
     * TODO: set different tips for different lang
     */
    private Boolean showTopTip = false;

    // show rate btn on chat toolbar
    private Boolean showRateBtn = false;

    private String topTip = I18Consts.I18N_TOP_TIP;

    private String welcomeTip = I18Consts.I18N_WELCOME_TIP;

    private String leavemsgTip = I18Consts.I18N_LEAVEMSG_TIP;

    /** auto close time in min - 默认自动关闭时间，单位分钟 */
    private Double autoCloseMin = Double.valueOf(25);

    private List<String> worktimeUids = new ArrayList<>();

    private boolean showQuickButtons = true;

    private List<String> quickButtonUids = new ArrayList<>();

    private boolean showFaqs = true;

    private List<String> faqUids = new ArrayList<>();

    private boolean showGuessFaqs = true;

    private List<String> guessFaqUids = new ArrayList<>();
    
    private boolean showHotFaqs = true;

    private List<String> hotFaqUids = new ArrayList<>();

    private boolean showShortcutFaqs = true;
    
    private List<String> shortcutFaqUids = new ArrayList<>();

    // 访客对话底部页面显示logo
    private Boolean showLogo = true;

    // 有效日期
    private Date validateUntil;
}
