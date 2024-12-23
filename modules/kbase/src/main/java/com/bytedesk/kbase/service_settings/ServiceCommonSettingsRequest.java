/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-14 10:45:08
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-23 12:28:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.service_settings;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.LanguageEnum;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@MappedSuperclass
public class ServiceCommonSettingsRequest {

    // private String language = I18Consts.ZH_CN;
    private LanguageEnum language = LanguageEnum.ZH_CN;

    private Boolean autoPop = false;

    private Boolean showTopTip = false;

    private String topTip = I18Consts.I18N_TOP_TIP;

    private LocalDateTime topTipStart;

    private LocalDateTime topTipEnd;

    // show rate btn on chat toolbar
    private Boolean showRateBtn = false;

    private Integer rateMsgCount = 3;

    private Boolean showPreForm = false;

    private String preForm = BytedeskConsts.EMPTY_JSON_STRING;

    private Boolean showHistory = false;

    private boolean showCaptcha = false;

    private String welcomeTip = I18Consts.I18N_WELCOME_TIP;

    private String queueTip = I18Consts.I18N_QUEUE_TIP;

    private String leavemsgTip = I18Consts.I18N_LEAVEMSG_TIP;

    /** auto close time in min - 默认自动关闭时间，单位分钟 */
    private Double autoCloseMin = Double.valueOf(25);

    // private Boolean showQuickButtons = true;
    // private List<String> quickButtonUids = new ArrayList<>();

    private Boolean showFaqs = true;
    private List<String> faqUids = new ArrayList<>();

    private Boolean showQuickFaqs = true;
    private List<String> quickFaqUids = new ArrayList<>();

    private Boolean showGuessFaqs = true;
    private List<String> guessFaqUids = new ArrayList<>();
    
    private Boolean showHotFaqs = true;
    private List<String> hotFaqUids = new ArrayList<>();

    private Boolean showShortcutFaqs = true;
    private List<String> shortcutFaqUids = new ArrayList<>();

    // 输入联想开关
    private Boolean showInputAssociation = true;
    // 访客对话底部页面显示logo
    private Boolean showLogo = true;

    // 有效日期
    private Date validateUntil;
}
