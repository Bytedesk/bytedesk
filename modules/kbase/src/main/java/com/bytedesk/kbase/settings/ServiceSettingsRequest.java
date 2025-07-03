/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-14 10:45:08
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-13 12:44:05
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
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.LanguageEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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
    private String preForm = BytedeskConsts.EMPTY_JSON_STRING;

    @Builder.Default
    private Boolean showHistory = false;

    // 输入联想开关
    @Builder.Default
    private Boolean showInputAssociation = false;

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

    @Builder.Default
    private String queueTip = I18Consts.I18N_QUEUE_TIP;

    @Builder.Default
    private String leavemsgTip = I18Consts.I18N_LEAVEMSG_TIP;

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

    // 是否显示预搜索
    @Builder.Default
    private Boolean showPreSearch = true;

    @Builder.Default
    private Boolean showFaqs = true;
    @Builder.Default
    private List<String> faqUids = new ArrayList<>();
    private String faqKbUid;

    @Builder.Default
    private Boolean showQuickFaqs = true;
    @Builder.Default
    private List<String> quickFaqUids = new ArrayList<>();
    // 快捷问题知识库
    private String quickFaqKbUid;

    @Builder.Default
    private Boolean showGuessFaqs = true;
    @Builder.Default
    private List<String> guessFaqUids = new ArrayList<>();
    
    @Builder.Default
    private Boolean showHotFaqs = true;
    @Builder.Default
    private List<String> hotFaqUids = new ArrayList<>();

    @Builder.Default
    private Boolean showShortcutFaqs = true;
    @Builder.Default
    private List<String> shortcutFaqUids = new ArrayList<>();

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

    // 主动触发设置
    @Builder.Default
    private Boolean enableProactiveTrigger = false;  // 是否启用主动触发

    @Builder.Default
    private Integer noResponseTimeout = 300;  // 访客无响应超时时间(秒)，默认5分钟

    @Builder.Default
    private String proactiveMessage = "您好，看起来您有一段时间没有互动了。请问还需要帮助吗？";  // 主动发送的消息内容

    @Builder.Default
    private Integer maxProactiveCount = 3;  // 最大主动触发次数，防止打扰用户

    @Builder.Default
    private Integer proactiveInterval = 600;  // 两次主动触发的最小间隔(秒)，默认10分钟

    @Builder.Default
    private String triggerConditions = BytedeskConsts.EMPTY_JSON_STRING;  // 触发条件配置，JSON格式

    @Builder.Default
    private List<String> proactiveFaqUids = new ArrayList<>();  // 主动推送的常见问题列表

    // 灰度发布配置
    // @Builder.Default
    // private GrayReleaseEntity grayReleaseConfig = new GrayReleaseConfig();

    // 访客对话底部页面显示logo
    @Builder.Default
    private Boolean showLogo = true;

    // 有效日期
    private Date validateUntil;
}
