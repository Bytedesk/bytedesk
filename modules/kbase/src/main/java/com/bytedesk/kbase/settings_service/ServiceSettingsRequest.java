/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-14 10:45:08
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-18 14:38:07
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
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.LanguageEnum;
import com.bytedesk.kbase.settings.ToolbarSettings;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceSettingsRequest  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Builder.Default
    private LanguageEnum language = LanguageEnum.ZH_CN;

    @Builder.Default
    private Boolean autoPopup = false;

    @Builder.Default
    private Boolean showTopTip = false;

    @Builder.Default    
    private String topTip = I18Consts.I18N_TOP_TIP;

    private ZonedDateTime topTipStart;

    private ZonedDateTime topTipEnd;

    // show rate btn on chat toolbar
    @Builder.Default
    private Boolean showRateBtn = false;

    @Builder.Default
    private Boolean autoInviteRate = false;

    @Builder.Default
    private String inviteRateTip = I18Consts.I18N_INVITE_RATE_TIP;

    @Builder.Default
    private Integer rateMsgCount = 3;

    @Builder.Default
    private Boolean showPreForm = false;

    @Builder.Default
    private Boolean preFormRequired = false;

    @Builder.Default
    private String preForm = BytedeskConsts.EMPTY_JSON_STRING;

    @Builder.Default
    private Boolean showHistory = false;

    /**
     * 访客发起咨询是否需要登录（未登录则应先登录再创建会话）
     */
    @Builder.Default
    private Boolean requireLogin = false;

    // 输入联想开关
    // @Builder.Default
    // private Boolean showInputAssociation = false;

    @Builder.Default
    private Boolean showCaptcha = false;

    @Builder.Default
    private String welcomeTip = I18Consts.I18N_WELCOME_TIP;

    @Builder.Default
    private List<String> welcomeFaqUids = new ArrayList<>();

    private String welcomeKbUid;
    
    // 是否启用workflow
    @Builder.Default
    private Boolean enableWorkflow = false;

    // 工作流uid
    private String workflowUid;

    // 排队提示语模板，支持变量: {position}-排队位置, {queueSize}-队列总人数, {waitSeconds}-等待秒数, {waitMinutes}-等待分钟数, {waitTime}-格式化等待时间
    @Builder.Default
    private String queueTip = I18Consts.I18N_QUEUE_TIP_TEMPLATE;

    @Builder.Default
    private String leavemsgTip = I18Consts.I18N_MESSAGE_LEAVE_TIP;

    /** auto close time in min - 默认自动关闭时间，单位分钟 */
    @Builder.Default
    private Double autoCloseMin = Double.valueOf(25);

    @Builder.Default
    private String autoCloseTip = I18Consts.I18N_AUTO_CLOSE_TIP;

    @Builder.Default
    private String agentCloseTip = I18Consts.I18N_AGENT_CLOSE_TIP;

    // 桌面版聊天窗口右侧iframe
    @Builder.Default
    private Boolean showRightIframe = false;

    // 桌面版聊天窗口右侧iframe地址
    private String rightIframeUrl;

    // 是否启用访客输入联想
    @Builder.Default
    private Boolean inputAssociationEnabled = false;

    private String inputAssociationKbUid;

    // 访客输入预览：访客输入内容预览发送给客服（MESSAGE_TYPE_PREVIEW）
    @Builder.Default
    private Boolean inputPreviewEnabled = false;

    @Builder.Default
    private Boolean showFaqs = true;
    @Builder.Default
    private List<String> faqUids = new ArrayList<>();
    private String faqKbUid;

    @Builder.Default
    private Boolean showQuickButtons = true;
    @Builder.Default
    private List<String> quickButtonUids = new ArrayList<>();

    // @Builder.Default
    // private Boolean showGuessFaqs = true;
    // @Builder.Default
    // private List<String> guessFaqUids = new ArrayList<>();
    
    // @Builder.Default
    // private Boolean showHotFaqs = true;
    // @Builder.Default
    // private List<String> hotFaqUids = new ArrayList<>();

    // @Builder.Default
    // private Boolean showShortcutFaqs = true;
    // @Builder.Default
    // private List<String> shortcutFaqUids = new ArrayList<>();

    // 可自定义设置相似问题、关联问题的引导语
    // @Builder.Default    
    // private String similarQuestionGuide = "您是否想问：";

    @Builder.Default
    private String relatedQuestionGuide = "您是否想问：";

    // 未知答案固定回复
    // @Builder.Default
    // private Boolean showUnknownAnswer = false;

    // @Builder.Default
    // private String unknownAnswer = "抱歉，我暂时无法回答这个问题。";

    // 灰度发布配置
    // @Builder.Default
    // private GrayReleaseEntity grayReleaseConfig = new GrayReleaseConfig();

    // 访客对话底部页面显示logo
    @Builder.Default
    private Boolean showLogo = true;

    // 有效日期
    private Date validateUntil;

    // 工具栏显示控制（固定字段，未设置则默认为显示）
    @Builder.Default
    private ToolbarSettings toolbar = ToolbarSettings.builder().build();
}
