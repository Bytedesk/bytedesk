/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-14 10:45:08
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-18 14:41:46
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.settings;

import java.io.Serializable;
import java.util.List;

import com.bytedesk.core.enums.LanguageEnum;
import com.bytedesk.kbase.llm_faq.FaqResponseVisitor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class ServiceSettingsResponseVisitor implements Serializable {

    private static final long serialVersionUID = 1L;

    private LanguageEnum language;

    private Boolean autoPopup;

    private Boolean showTopTip;

    private String topTip;

    private Boolean showRateBtn;

    private Boolean autoInviteRate;

    private String inviteRateTip;

    private Integer rateMsgCount;

    private Boolean showPreForm;

    private Boolean preFormRequired;

    private String preForm;

    private Boolean showHistory;

    // 输入联想开关
    private Boolean showInputAssociation;

    private Boolean showCaptcha;

    /** auto close time in min - 默认自动关闭时间，单位分钟 */
    private Double autoCloseMin;

    // 桌面版聊天窗口右侧iframe
    private Boolean showRightIframe;

    // 桌面版聊天窗口右侧iframe地址
    private String rightIframeUrl;

    // 是否显示预搜索
    private Boolean showPreSearch;

    // 欢迎问题
    private List<FaqResponseVisitor> welcomeFaqs;
    private String welcomeKbUid;

    private Boolean showFaqs;
    private List<FaqResponseVisitor> faqs;
    private String faqKbUid;

    private Boolean showQuickFaqs;
    // 快捷问题
    private List<FaqResponseVisitor> quickFaqs;
    // 快捷问题知识库
    private String quickFaqKbUid;
    //
    private Boolean showGuessFaqs;
    // 猜你想问
    private List<FaqResponseVisitor> guessFaqs;
    //
    private Boolean showHotFaqs;
    // 热门问题
    private List<FaqResponseVisitor> hotFaqs;
    //
    private Boolean showShortcutFaqs;
    // 快捷功能
    private List<FaqResponseVisitor> shortcutFaqs;

    private Boolean showLogo;

    // 工具栏显示控制（固定字段，未设置则默认为显示）
    private ToolbarSettings toolbar;
}
