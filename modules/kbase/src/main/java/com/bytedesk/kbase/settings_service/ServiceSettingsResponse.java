/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-14 10:45:08
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-18 14:42:13
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
import java.util.Date;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;

import com.bytedesk.core.enums.LanguageEnum;
import com.bytedesk.kbase.llm_faq.FaqEntity;
import com.bytedesk.kbase.llm_faq.FaqResponse;
import com.bytedesk.kbase.quick_button.QuickButtonResponse;
import com.bytedesk.kbase.utils.KbaseConvertUtils;

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
public class ServiceSettingsResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private LanguageEnum language;

    private Boolean autoPopup;

    private Boolean showTopTip;

    private String topTip;

    private ZonedDateTime topTipStart;

    private ZonedDateTime topTipEnd;

    // 评价按钮显示控制已迁移至 ToolbarSettings.rate
    private Boolean autoInviteRate;

    private String inviteRateTip;

    private Integer rateMsgCount;

    private Boolean showPreForm;

    private Boolean preFormRequired;

    private String preForm;

    private Boolean showHistory;

    /**
     * 访客端是否显示消息状态（送达/已读等）
     */
    private Boolean showMessageStatus;

    /**
     * 是否允许访客端撤回消息
     */
    private Boolean allowVisitorRecall;

    /**
     * 访客端撤回消息时限（分钟）
     */
    private Integer visitorRecallMinutes;

    /**
     * 访客发起咨询是否需要登录
     */
    private Boolean requireLogin;

    // 输入联想开关
    // private Boolean showInputAssociation;

    private Boolean showCaptcha;

    private String welcomeTip;

    private List<FaqResponse> welcomeFaqs;

    private String welcomeKbUid;

    // 是否启用workflow
    @Builder.Default
    private Boolean enableWorkflow = false;

    // 工作流uid
    private String workflowUid;

    private String queueTip;

    private String leavemsgTip;

    /** auto close time in min - 默认自动关闭时间，单位分钟 */
    private Double autoCloseMin;

    private String autoCloseTip;

    private String agentCloseTip;

    // 桌面版聊天窗口右侧iframe
    private Boolean showRightIframe;

    // 桌面版聊天窗口右侧iframe地址
    private String rightIframeUrl;

    // 是否启用访客输入联想
    private Boolean inputAssociationEnabled;

    private String inputAssociationKbUid;

    // 访客输入预览：访客输入内容预览发送给客服（MESSAGE_TYPE_PREVIEW）
    private Boolean inputPreviewEnabled;

    // 输入预览是否一直显示（客服端预览框常驻）
    private Boolean inputPreviewAlwaysShow;

    // 输入预览显示时长（秒），仅当 inputPreviewAlwaysShow=false 时生效
    private Integer inputPreviewShowSeconds;

    private Boolean showFaqs;
    private List<FaqResponse> faqs;
    private String faqKbUid;

    private Boolean showQuickButtons;
    private List<QuickButtonResponse> quickButtons;

    // private Boolean showGuessFaqs;
    // private List<FaqResponse> guessFaqs;

    // private Boolean showHotFaqs;
    // private List<FaqResponse> hotFaqs;

    // private Boolean showShortcutFaqs;
    // private List<FaqResponse> shortcutFaqs;

    // 灰度发布配置
    // private GrayReleaseEntity grayReleaseConfig;

    private Boolean showLogo;

    // 有效日期
    private Date validateUntil;

    // 工具栏显示控制(固定字段,未设置则默认为显示)
    private ToolbarSettings toolbar;

    /**
     * 从 ServiceSettings 实体创建 ServiceSettingsResponse
     * @param settings ServiceSettings 实体
     * @return ServiceSettingsResponse 对象,如果 settings 为 null 则返回 null
     */
    public static ServiceSettingsResponse fromEntity(ServiceSettingsEntity settings) {
        if (settings == null) {
            return null;
        }
        return ServiceSettingsResponse.builder()
                .language(settings.getLanguage() != null ? LanguageEnum.valueOf(settings.getLanguage()) : null)
                .autoPopup(settings.getAutoPopup())
                .showTopTip(settings.getShowTopTip())
                .topTip(settings.getTopTip())
                .topTipStart(settings.getTopTipStart())
                .topTipEnd(settings.getTopTipEnd())
                .autoInviteRate(settings.getAutoInviteRate())
                .inviteRateTip(settings.getInviteRateTip())
                .rateMsgCount(settings.getRateMsgCount())
                .showPreForm(settings.getShowPreForm())
                .preFormRequired(settings.getPreFormRequired())
                .preForm(normalizeAdminPreFormValue(settings.getPreFormSchema()))
                .showHistory(settings.getShowHistory())
                .showMessageStatus(settings.getShowMessageStatus())
                .allowVisitorRecall(settings.getAllowVisitorRecall())
                .visitorRecallMinutes(settings.getVisitorRecallMinutes())
                .requireLogin(settings.getRequireLogin())
                // .showInputAssociation(settings.getShowInputAssociation())
                .showCaptcha(settings.getShowCaptcha())
                .welcomeTip(settings.getWelcomeTip())
                .welcomeFaqs(convertFaqResponses(settings.getWelcomeFaqs()))
                .welcomeKbUid(settings.getWelcomeKbUid())
                .autoCloseMin(settings.getAutoCloseMin())
                .autoCloseTip(settings.getAutoCloseTip())
                .agentCloseTip(settings.getAgentCloseTip())
                .showRightIframe(settings.getShowRightIframe())
                .rightIframeUrl(settings.getRightIframeUrl())
                .inputAssociationEnabled(settings.getInputAssociationEnabled())
                .inputAssociationKbUid(settings.getInputAssociationKbUid())
                .inputPreviewEnabled(settings.getInputPreviewEnabled())
                .inputPreviewAlwaysShow(settings.getInputPreviewAlwaysShow())
                .inputPreviewShowSeconds(settings.getInputPreviewShowSeconds())
                .showFaqs(settings.getShowFaqs())
                .faqs(convertFaqResponses(settings.getFaqs()))
                .faqKbUid(settings.getFaqKbUid())
                .showQuickButtons(settings.getShowQuickButtons())
                .quickButtons(QuickButtonResponse.fromEntities(settings.getQuickButtons()))
                // .showGuessFaqs(settings.getShowGuessFaqs())
                // .showHotFaqs(settings.getShowHotFaqs())
                // .showShortcutFaqs(settings.getShowShortcutFaqs())
                .showLogo(settings.getShowLogo())
                .validateUntil(settings.getValidateUntil())
                .toolbar(settings.getToolbar())
                .build();
    }

    private static List<FaqResponse> convertFaqResponses(List<FaqEntity> faqs) {
        if (faqs == null) {
            return null;
        }
        if (faqs.isEmpty()) {
            return Collections.emptyList();
        }
        return faqs.stream()
                .map(KbaseConvertUtils::convertToFaqResponse)
                .collect(Collectors.toList());
    }

    private static String normalizeAdminPreFormValue(String rawValue) {
        if (rawValue == null) {
            return null;
        }

        String trimmedValue = rawValue.trim();
        if (trimmedValue.isEmpty() || "{}".equals(trimmedValue) || "[]".equals(trimmedValue)) {
            return null;
        }

        if (trimmedValue.startsWith("{") || trimmedValue.startsWith("[")) {
            return null;
        }

        return trimmedValue;
    }

}
