/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-29 16:46:42
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.settings_service;


import com.bytedesk.core.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({ServiceSettingsEntityListener.class})
@Table(name = "bytedesk_kbase_settings_service")
public class ServiceSettingsEntity extends BaseEntity {

    /**
     * 设置名称，用于区分不同的邀请设置模板
     */
    @Column(name = "settings_name")
    private String name;
    
    /**
     * 设置描述
     */
    @Column(name = "settings_description")
    private String description;

    /**
     * 是否为默认设置模板
     */
    @Builder.Default
    @Column(name = "is_default_template")
    private Boolean defaultTemplate = false;

    // @NotBlank
    // @Builder.Default
    // private String language = LanguageEnum.ZH_CN.name();

    // @NotBlank
    // @Builder.Default
    // private Boolean autoPopup = false;

    // // 顶部提示开关
    // @NotBlank
    // @Builder.Default
    // private Boolean showTopTip = false;
    
    // @NotBlank
    // @Builder.Default
    // private String topTip = I18Consts.I18N_TOP_TIP;

    // private ZonedDateTime topTipStart;

    // private ZonedDateTime topTipEnd;

    // // 满意度评价设置--------------------------------------------------
    // // show rate btn on chat toolbar
    // @NotBlank
    // @Builder.Default
    // private Boolean showRateBtn = false;

    // // 关闭会话时自动发送满意度评价
    // @NotBlank
    // @Builder.Default
    // private Boolean autoInviteRate = false;

    // // invite rate tip
    // @Builder.Default
    // private String inviteRateTip = I18Consts.I18N_INVITE_RATE_TIP;
    
    // // 自定义评价最低消息数量，未达到最低对话消息数，禁止评价
    // @NotBlank
    // @Builder.Default
    // private Integer rateMsgCount = 3;

    // //-----------------------------------------------------------------------------------

    // // 询前表单
    // // 是否显示询前表单
    // // TODO: 自定义询前表单字段
    // @NotBlank
    // @Builder.Default
    // private Boolean showPreForm = false;

    // @Builder.Default
    // @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    // private String preForm = BytedeskConsts.EMPTY_JSON_STRING;

    // //-----------------------------------------------------------------------------------

    // // show history message or not
    // @NotBlank
    // @Builder.Default
    // private Boolean showHistory = false;

    // // 输入联想开关
    // @NotBlank
    // @Builder.Default
    // private Boolean showInputAssociation = false;

    // // 防骚扰验证开关，TODO: 自定义验证规则: 1. 访问频率 2. 发消息时间间隔
    // @NotBlank
    // @Builder.Default
    // private Boolean showCaptcha = false;

    // @NotBlank
    // @Builder.Default
    // @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    // private String welcomeTip = I18Consts.I18N_WELCOME_TIP;

    // @Builder.Default
    // @ManyToMany(fetch = FetchType.LAZY)
    // private List<FaqEntity> welcomeFaqs = new ArrayList<>();

    // // 换一换-Faq常见问题知识库
    // private String welcomeKbUid;

    // //-----------------------------------------------------------------------------------

    // /** auto close time in minutes */
    // @NotBlank
    // @Builder.Default
    // private Double autoCloseMin = Double.valueOf(25);

    // @Builder.Default
    // private String autoCloseTip = I18Consts.I18N_AUTO_CLOSE_TIP;

    // @Builder.Default
    // private String agentCloseTip = I18Consts.I18N_AGENT_CLOSE_TIP;

    // //-----------------------------------------------------------------------------------

    // // 桌面版聊天窗口右侧iframe
    // @Builder.Default
    // private Boolean showRightIframe = false;

    // // 桌面版聊天窗口右侧iframe地址
    // private String rightIframeUrl;

    // // 是否显示预搜索
    // @Builder.Default
    // private Boolean showPreSearch = true;

    // // 常见问题
    // @Builder.Default
    // private Boolean showFaqs = false;
    // @Builder.Default
    // @ManyToMany(fetch = FetchType.LAZY)
    // private List<FaqEntity> faqs = new ArrayList<>();
    // // 常见问题知识库
    // private String faqKbUid;

    // // 快捷按钮
    // @Builder.Default
    // private Boolean showQuickFaqs = false;
    // @Builder.Default
    // @ManyToMany(fetch = FetchType.LAZY)
    // private List<FaqEntity> quickFaqs = new ArrayList<>();
    // // 快捷问题知识库
    // private String quickFaqKbUid;

    // // 猜你想问
    // @Builder.Default
    // private Boolean showGuessFaqs = false;
    // @Builder.Default
    // @ManyToMany(fetch = FetchType.LAZY)
    // private List<FaqEntity> guessFaqs = new ArrayList<>();

    // // 热门问题
    // @Builder.Default
    // private Boolean showHotFaqs = false;
    // @Builder.Default
    // @ManyToMany(fetch = FetchType.LAZY)
    // private List<FaqEntity> hotFaqs = new ArrayList<>();

    // // 快捷功能
    // @Builder.Default
    // private Boolean showShortcutFaqs = false;
    // @Builder.Default
    // @ManyToMany(fetch = FetchType.LAZY)
    // private List<FaqEntity> shortcutFaqs = new ArrayList<>();

    // // 未知答案固定回复
    // @Builder.Default
    // private Boolean showUnknownAnswer = false;

    // @Builder.Default
    // private String unknownAnswer = "抱歉，我暂时无法回答这个问题。";

    // // 满足一定触发条件下，机器人支持主动触发某个任务或者回复某些话术。
    // // 例如：1、长时间访客无消息，机器人主动发问或者触发任务，主动暖场；
    // // 2、满足一定条件，自动触发某个任务。

    // // 主动触发设置
    // @Builder.Default
    // private Boolean enableProactiveTrigger = false;  // 是否启用主动触发

    // @Builder.Default
    // private Integer noResponseTimeout = 300;  // 访客无响应超时时间(秒)，默认5分钟

    // @Builder.Default
    // @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    // private String proactiveMessage = "您好，看起来您有一段时间没有互动了。请问还需要帮助吗？";  // 主动发送的消息内容

    // @Builder.Default
    // private Integer maxProactiveCount = 3;  // 最大主动触发次数，防止打扰用户

    // @Builder.Default
    // private Integer proactiveInterval = 600;  // 两次主动触发的最小间隔(秒)，默认10分钟

    // /**
    //  * @see ServiceTrigger
    //  */
    // @Builder.Default
    // @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    // private String triggerConditions = BytedeskConsts.EMPTY_JSON_STRING;  // 触发条件配置，JSON格式

    // @Builder.Default
    // @ManyToMany(fetch = FetchType.LAZY)
    // private List<FaqEntity> proactiveFaqs = new ArrayList<>();  // 主动推送的常见问题列表

    // @NotBlank
    // @Builder.Default
    // private Boolean showLogo = true;

    // // validate until date, when expire the service will be disabled
    // private Date validateUntil;
    
}
