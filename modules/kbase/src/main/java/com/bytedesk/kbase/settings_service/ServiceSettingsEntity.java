/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-14 10:45:08
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-18 10:55:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.settings_service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.enums.LanguageEnum;
import com.bytedesk.kbase.llm_faq.FaqEntity;
import com.bytedesk.kbase.quick_button.QuickButtonEntity;
import com.bytedesk.kbase.settings.ToolbarSettings;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Embedded;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(
    name = "bytedesk_kbase_service_settings",
    indexes = {
        @Index(name = "idx_service_settings_uid", columnList = "uuid")
    }
)
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class ServiceSettingsEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @NotBlank
    @Builder.Default
    private String language = LanguageEnum.ZH_CN.name();

    @NotNull
    @Builder.Default
    private Boolean autoPopup = false;

    // 顶部提示开关
    @NotNull
    @Builder.Default
    private Boolean showTopTip = false;
    
    @NotBlank
    @Builder.Default
    private String topTip = I18Consts.I18N_TOP_TIP;

    private ZonedDateTime topTipStart;

    private ZonedDateTime topTipEnd;

    // 满意度评价设置--------------------------------------------------
    // show rate btn on chat toolbar
    @NotNull
    @Builder.Default
    private Boolean showRateBtn = false;

    // 关闭会话时自动发送满意度评价
    @NotNull
    @Builder.Default
    private Boolean autoInviteRate = false;

    // invite rate tip
    @Builder.Default
    private String inviteRateTip = I18Consts.I18N_INVITE_RATE_TIP;
    
    // 自定义评价最低消息数量，未达到最低对话消息数，禁止评价
    @NotNull
    @Builder.Default
    private Integer rateMsgCount = 3;

    // 5星评分、10星评分
    // @Builder.Default
    // private Integer rateStarCount = 5;

    // 评价选择按钮文字
    // @Builder.Default
    // @Convert(converter = StringListConverter.class)
    // @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    // private List<String> rateTagList = new ArrayList<>();

    //-----------------------------------------------------------------------------------

    // 询前表单
    // 是否显示询前表单
    @NotNull
    @Builder.Default
    private Boolean showPreForm = false;

    // 是否强制填写
    @NotNull
    @Builder.Default
    private Boolean preFormRequired = false;

    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String preFormSchema = BytedeskConsts.EMPTY_JSON_STRING;

    //-----------------------------------------------------------------------------------

    // show history message or not
    @NotNull
    @Builder.Default
    private Boolean showHistory = false;

    // 防骚扰验证开关，TODO: 自定义验证规则: 1. 访问频率 2. 发消息时间间隔
    @NotNull
    @Builder.Default
    private Boolean showCaptcha = false;

    @NotBlank
    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String welcomeTip = I18Consts.I18N_WELCOME_TIP;

    // 换一换-Faq常见问题知识库
    private String welcomeKbUid;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "bytedesk_kbase_service_settings_welcome_faqs",
        joinColumns = @JoinColumn(name = "service_settings_id"),
        inverseJoinColumns = @JoinColumn(name = "welcome_faqs_id")
    )
    private List<FaqEntity> welcomeFaqs = new ArrayList<>();

    //-----------------------------------------------------------------------------------

    /** auto close time in minutes */
    @NotNull
    @Builder.Default
    private Double autoCloseMin = Double.valueOf(25);

    @Builder.Default
    private String autoCloseTip = I18Consts.I18N_AUTO_CLOSE_TIP;

    @Builder.Default
    private String agentCloseTip = I18Consts.I18N_AGENT_CLOSE_TIP;

    //-----------------------------------------------------------------------------------

    // 桌面版聊天窗口右侧iframe
    @Builder.Default
    private Boolean showRightIframe = false;

    // 桌面版聊天窗口右侧iframe地址
    private String rightIframeUrl;

    // 是否启用访客输入联想
    @Builder.Default
    private Boolean inputAssociationEnabled = false;

    // 输入联想关联知识库
    private String inputAssociationKbUid;

    // 访客输入预览：访客输入内容预览发送给客服（MESSAGE_TYPE_PREVIEW）
    @Builder.Default
    private Boolean inputPreviewEnabled = false;
    
    // 常见问题
    @Builder.Default
    private Boolean showFaqs = false;
    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "bytedesk_kbase_service_settings_faqs",
        joinColumns = @JoinColumn(name = "service_settings_id"),
        inverseJoinColumns = @JoinColumn(name = "faqs_id")
    )
    private List<FaqEntity> faqs = new ArrayList<>();
    // 常见问题知识库
    private String faqKbUid;

    // 快捷按钮
    @Builder.Default
    private Boolean showQuickButtons = false;
    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "bytedesk_kbase_service_settings_quick_buttons",
        joinColumns = @JoinColumn(name = "service_settings_id"),
        inverseJoinColumns = @JoinColumn(name = "quick_button_id")
    )
    private List<QuickButtonEntity> quickButtons = new ArrayList<>();

    // 猜你想问
    // @Builder.Default
    // private Boolean showGuessFaqs = false;
    // @Builder.Default
    // @ManyToMany(fetch = FetchType.LAZY)
    // @JoinTable(
    //     name = "bytedesk_kbase_service_settings_guess_faqs",
    //     joinColumns = @JoinColumn(name = "service_settings_id"),
    //     inverseJoinColumns = @JoinColumn(name = "guess_faqs_id")
    // )
    // private List<FaqEntity> guessFaqs = new ArrayList<>();

    // 热门问题
    // @Builder.Default
    // private Boolean showHotFaqs = false;
    // @Builder.Default
    // @ManyToMany(fetch = FetchType.LAZY)
    // @JoinTable(
    //     name = "bytedesk_kbase_service_settings_hot_faqs",
    //     joinColumns = @JoinColumn(name = "service_settings_id"),
    //     inverseJoinColumns = @JoinColumn(name = "hot_faqs_id")
    // )
    // private List<FaqEntity> hotFaqs = new ArrayList<>();

    // 快捷功能
    // @Builder.Default
    // private Boolean showShortcutFaqs = false;
    // @Builder.Default
    // @ManyToMany(fetch = FetchType.LAZY)
    // @JoinTable(
    //     name = "bytedesk_kbase_service_settings_shortcut_faqs",
    //     joinColumns = @JoinColumn(name = "service_settings_id"),
    //     inverseJoinColumns = @JoinColumn(name = "shortcut_faqs_id")
    // )
    // private List<FaqEntity> shortcutFaqs = new ArrayList<>();

    @NotNull
    @Builder.Default
    private Boolean showLogo = true;

    // validate until date, when expire the service will be disabled
    private Date validateUntil;
    
    //-----------------------------------------------------------------------------------

    // 工具栏显示控制(固定字段,未设置则默认为显示)
    @Builder.Default
    @Embedded
    private ToolbarSettings toolbar = new ToolbarSettings();
    
    /**
     * 从 ServiceSettingsRequest 创建 ServiceSettings 实体
     * 如果 request 为 null，返回默认构建的实体
     * 
     * @param request ServiceSettingsRequest 对象，可以为 null
     * @param modelMapper ModelMapper 实例用于字段映射
     * @return ServiceSettings 实体，永远不为 null
     */
    public static ServiceSettingsEntity fromRequest(ServiceSettingsRequest request, ModelMapper modelMapper) {
        // 如果 request 为 null，返回默认值
        if (request == null || modelMapper == null) {
            return ServiceSettingsEntity.builder().build();
        }
        
        // 使用 ModelMapper 自动映射大部分字段
        ServiceSettingsEntity settings = modelMapper.map(request, ServiceSettingsEntity.class);
        
        // 处理特殊字段: LanguageEnum 需要转换为 String
        if (request.getLanguage() != null) {
            settings.setLanguage(request.getLanguage().name());
        }
        
        return settings;
    }
    
}
