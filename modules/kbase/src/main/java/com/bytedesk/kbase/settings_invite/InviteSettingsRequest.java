/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:26:04
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-12 10:14:21
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.settings_invite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.constant.I18Consts;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class InviteSettingsRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;


    private String name;

    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    /**
     * 是否为默认设置模板
     */
    @Builder.Default
    private Boolean defaultTemplate = false;

    /**
     * 是否显示邀请
     */
    @Builder.Default
    private Boolean show = false;

    /**
     * 邀请文本
     */
    @Builder.Default
    private String text = "您好,请问有什么可以帮您?";

    /**
     * 邀请图标
     */
    @Builder.Default
    private String icon = "default";

    /**
     * 邀请延迟时间,单位:毫秒
     */
    @Builder.Default
    private Long delay = 3000L;

    /**
     * 是否循环
     */
    @Builder.Default
    private Boolean loop = false;

    /**
     * 循环延迟时间,单位:毫秒
     */
    @Builder.Default
    private Long loopDelay = 60000L;

    /**
     * 循环次数
     */
    @Builder.Default
    private Integer loopCount = 3;

    /**
     * 邀请消息列表
     */
    @Builder.Default
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
    private Boolean targetedInvite = false;

    /**
     * 定向邀请页面URL匹配模式
     */
    @Builder.Default
    private List<String> targetedInviteUrls = new ArrayList<>();

    /**
     * 智能触发邀请 - 基于用户行为
     */
    @Builder.Default
    private Boolean smartTrigger = false;

    /**
     * 页面停留时间触发，单位:秒
     */
    @Builder.Default
    private Integer pageStayTriggerSeconds = 30;

    /**
     * 页面滚动触发
     */
    @Builder.Default
    private Boolean scrollTrigger = false;

    /**
     * 页面滚动触发百分比 (0-100)
     * 注意：与后端实体字段保持一致命名
     */
    @Builder.Default
    private Integer scrollTriggerPerceninviteSettinge = 50;

    /**
     * 退出意图触发
     */
    @Builder.Default
    private Boolean exitIntentTrigger = false;

    /**
     * 访客来源触发
     */
    @Builder.Default
    private Boolean referrerTrigger = false;

    /**
     * 来源URL匹配模式
     */
    @Builder.Default
    private List<String> referrerPatterns = new ArrayList<>();

    /**
     * 访客设备类型触发 - 可选: desktop, mobile, tablet
     */
    @Builder.Default
    private List<String> deviceTypes = new ArrayList<>(Arrays.asList("desktop", "mobile", "tablet"));

    /**
     * 是否启用访客分段邀请
     */
    @Builder.Default
    private Boolean visitorSegmentation = false;

    /**
     * 新访客专属邀请消息
     */
    @Builder.Default
    private String newVisitorMessage = "欢迎首次访问我们的网站，有任何问题请随时咨询";

    /**
     * 回访访客专属邀请消息
     */
    @Builder.Default
    private String returningVisitorMessage = "欢迎回来，需要进一步帮助请告诉我们";

    /**
     * VIP访客专属邀请消息
     */
    @Builder.Default
    private String vipVisitorMessage = "尊敬的贵宾客户，很高兴为您提供专属服务";

    /**
     * 邀请样式设置: bubble, card, modal, banner
     */
    @Builder.Default
    private String inviteStyle = "bubble";

    /**
     * 邀请动画效果: fade, slide, bounce, none
     */
    @Builder.Default
    private String inviteAnimation = "fade";

    /**
     * 是否启用A/B测试
     */
    @Builder.Default
    private Boolean abTesting = false;

}
