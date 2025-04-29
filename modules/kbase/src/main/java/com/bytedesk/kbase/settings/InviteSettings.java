/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-10 14:59:33
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-29 15:15:03
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.converter.StringListConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_kbase_invite_settings")
public class InviteSettings extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

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
    @Column(name = "is_default_template")
    private Boolean isDefaultTemplate = false;

    /**
     * 是否显示邀请
     */
    @Column(name = "is_invite_show")
    private Boolean show = false;
    
    /**
     * 邀请文本
     */
    @Column(name = "invite_text")
    private String text = "您好,请问有什么可以帮您?";
    
    /**
     * 邀请图标
     */
    @Builder.Default
    @Column(name = "invite_icon")
    private String icon = "default";
    
    /**
     * 邀请延迟时间,单位:毫秒
     */
    @Builder.Default
    @Column(name = "invite_delay")
    private Long delay = 3000L;
    
    /**
     * 是否循环
     */
    @Builder.Default
    @Column(name = "is_invite_loop")
    private Boolean loop = false;
    
    /**
     * 循环延迟时间,单位:毫秒 
     */
    @Builder.Default
    @Column(name = "invite_loop_delay")
    private Long loopDelay = 60000L;
    
    /**
     * 循环次数
     */
    @Builder.Default
    @Column(name = "invite_loop_count")
    private Integer loopCount = 3;

    /**
     * 邀请消息列表
     * 多条自动消息气泡消息内容
     */
    @Convert(converter = StringListConverter.class)
    @Column(name = "invite_message_list", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> messageList = new ArrayList<>(Arrays.asList(
        "您好，需要任何帮助请随时告诉我",
        "有什么可以帮您解答的问题吗？",
        "正在为您提供专业的在线咨询服务",
        "点击开始对话，我们将立即为您服务",
        "有疑问？我们的客服团队随时为您解答",
        "需要了解更多产品信息吗？立即咨询",
        "欢迎咨询，为您提供专业解答",
        "遇到问题了吗？点击这里获得帮助",
        "想了解更多优惠活动？点击咨询",
        "我们的专业团队在线等候您的提问"
    ));
    
    /**
     * 定向邀请设置 - 针对特定页面启用邀请
     */
    @Builder.Default
    @Column(name = "is_targeted_invite")
    private Boolean targetedInvite = false;
    
    /**
     * 定向邀请页面URL匹配模式
     * 支持精确匹配和通配符模式，如: /products/*, /checkout
     */
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(name = "targeted_invite_urls", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> targetedInviteUrls = new ArrayList<>();
    
    /**
     * 智能触发邀请 - 基于用户行为
     */
    @Builder.Default
    @Column(name = "is_smart_trigger")
    private Boolean smartTrigger = false;
    
    /**
     * 页面停留时间触发，单位:秒
     * 访客在页面停留超过指定时间后触发邀请
     */
    @Builder.Default
    @Column(name = "page_stay_trigger_seconds")
    private Integer pageStayTriggerSeconds = 30;
    
    /**
     * 页面滚动触发
     * 访客页面滚动超过一定比例后触发邀请
     */
    @Builder.Default
    @Column(name = "is_scroll_trigger")
    private Boolean scrollTrigger = false;
    
    /**
     * 页面滚动触发百分比 (0-100)
     */
    @Builder.Default
    @Column(name = "scroll_trigger_percentage")
    private Integer scrollTriggerPercentage = 50;
    
    /**
     * 退出意图触发
     * 当检测到访客可能离开页面时触发邀请
     */
    @Builder.Default
    @Column(name = "is_exit_intent_trigger")
    private Boolean exitIntentTrigger = false;
    
    /**
     * 访客来源触发 - 基于访客来源启用邀请
     */
    @Builder.Default
    @Column(name = "is_referrer_trigger")
    private Boolean referrerTrigger = false;
    
    /**
     * 来源URL匹配模式，如搜索引擎、社交媒体等
     */
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(name = "referrer_patterns", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> referrerPatterns = new ArrayList<>();
    
    /**
     * 访客设备类型触发 - 针对特定设备类型启用邀请
     * 可选值: desktop, mobile, tablet
     */
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(name = "device_types", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> deviceTypes = new ArrayList<>(Arrays.asList("desktop", "mobile", "tablet"));
    
    /**
     * 是否启用访客分段邀请
     * 可基于访客特征或行为设置不同邀请消息
     */
    @Builder.Default
    @Column(name = "is_visitor_segmentation")
    private Boolean visitorSegmentation = false;
    
    /**
     * 新访客专属邀请消息
     */
    @Builder.Default
    @Column(name = "new_visitor_message")
    private String newVisitorMessage = "欢迎首次访问我们的网站，有任何问题请随时咨询";
    
    /**
     * 回访访客专属邀请消息
     */
    @Builder.Default
    @Column(name = "returning_visitor_message")
    private String returningVisitorMessage = "欢迎回来，需要进一步帮助请告诉我们";
    
    /**
     * VIP访客专属邀请消息
     */
    @Builder.Default
    @Column(name = "vip_visitor_message")
    private String vipVisitorMessage = "尊敬的贵宾客户，很高兴为您提供专属服务";
    
    /**
     * 邀请样式设置
     * 可选值: bubble, card, modal, banner
     */
    @Builder.Default
    @Column(name = "invite_style")
    private String inviteStyle = "bubble";
    
    /**
     * 邀请动画效果
     * 可选值: fade, slide, bounce, none
     */
    @Builder.Default
    @Column(name = "invite_animation")
    private String inviteAnimation = "fade";
    
    /**
     * A/B测试功能
     * 是否启用A/B测试不同邀请方式的效果
     */
    @Builder.Default
    @Column(name = "is_ab_testing")
    private Boolean abTesting = false;
}
