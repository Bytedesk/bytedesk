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
package com.bytedesk.kbase.settings_service;

import java.io.Serializable;
import java.util.List;

import com.bytedesk.core.enums.LanguageEnum;
import com.bytedesk.kbase.llm_faq.FaqResponseVisitor;
import com.bytedesk.kbase.quick_button.QuickButtonResponseVisitor;

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

    /**
     * 访客发起咨询是否需要登录
     */
    private Boolean requireLogin;

    // 输入联想开关
    // private Boolean showInputAssociation;

    private Boolean showCaptcha;

    /** auto close time in min - 默认自动关闭时间，单位分钟 */
    private Double autoCloseMin;

    // 桌面版聊天窗口右侧iframe
    private Boolean showRightIframe;

    // 桌面版聊天窗口右侧iframe地址
    private String rightIframeUrl;

    // 是否启用访客输入联想
    private Boolean inputAssociationEnabled;

    // 输入联想关联知识库
    private String inputAssociationKbUid;

    // 访客输入预览：访客输入内容预览发送给客服（MESSAGE_TYPE_PREVIEW）
    private Boolean inputPreviewEnabled;

    // 输入预览是否一直显示（客服端预览框常驻）
    private Boolean inputPreviewAlwaysShow;

    // 输入预览显示时长（秒），仅当 inputPreviewAlwaysShow=false 时生效
    private Integer inputPreviewShowSeconds;

    // 欢迎问题
    private List<FaqResponseVisitor> welcomeFaqs;
    private String welcomeKbUid;

    private Boolean showFaqs;
    private List<FaqResponseVisitor> faqs;
    private String faqKbUid;

    private Boolean showQuickButtons;

    private List<QuickButtonResponseVisitor> quickButtons;
    //
    // private Boolean showGuessFaqs;
    // // 猜你想问
    // private List<FaqResponseVisitor> guessFaqs;
    // //
    // private Boolean showHotFaqs;
    // // 热门问题
    // private List<FaqResponseVisitor> hotFaqs;
    // //
    // private Boolean showShortcutFaqs;
    // // 快捷功能
    // private List<FaqResponseVisitor> shortcutFaqs;

    private Boolean showLogo;

    // 是否允许访客主动转人工
    private Boolean allowVisitorManualTransfer;

    // 访客端转人工按钮文案
    private String manualTransferLabel;

    // 工具栏显示控制（固定字段，未设置则默认为显示）
    private ToolbarSettings toolbar;
}
