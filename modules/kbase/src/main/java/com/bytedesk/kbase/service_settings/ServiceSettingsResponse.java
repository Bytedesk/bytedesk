/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-14 10:45:08
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-10 10:59:07
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.service_settings;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import com.bytedesk.core.enums.LanguageEnum;
import com.bytedesk.kbase.faq.FaqResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceSettingsResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private LanguageEnum language;

    private Boolean autoPop;

    private Boolean showTopTip;

    private String topTip;

    private LocalDateTime topTipStart;

    private LocalDateTime topTipEnd;

    // show rate btn on chat toolbar
    private Boolean showRateBtn;

    private Integer rateMsgCount;

    private Boolean showPreForm;

    private String preForm;

    private Boolean showHistory;

    // 输入联想开关
    private Boolean showInputAssociation;

    private boolean showCaptcha;

    private String welcomeTip;

    private String queueTip;

    private String leavemsgTip;

    /** auto close time in min - 默认自动关闭时间，单位分钟 */
    private Double autoCloseMin;

    // 桌面版聊天窗口右侧iframe
    private Boolean showRightIframe;

    // 桌面版聊天窗口右侧iframe地址
    private String rightIframeUrl;

    // private Boolean showQuickButtons;
    // private List<QuickButtonResponse> quickButtons;

    private Boolean showFaqs;
    private List<FaqResponse> faqs;

    private Boolean showQuickFaqs;
    private List<FaqResponse> quickFaqs;

    private Boolean showGuessFaqs;
    private List<FaqResponse> guessFaqs;

    private Boolean showHotFaqs;
    private List<FaqResponse> hotFaqs;

    private Boolean showShortcutFaqs;
    private List<FaqResponse> shortcutFaqs;

    private Boolean showLogo;

    // 有效日期
    private Date validateUntil;

}
