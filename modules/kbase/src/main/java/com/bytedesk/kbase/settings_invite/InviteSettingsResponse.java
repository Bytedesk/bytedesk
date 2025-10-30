/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:26:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-10 14:29:13
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.settings_invite;


import com.bytedesk.core.base.BaseResponse;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class InviteSettingsResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;


    private String name;

    private String description;

    /**
     * 是否为默认设置模板
     */
    private Boolean defaultTemplate;

    /**
     * 是否显示邀请
     */
    private Boolean show;

    /**
     * 邀请文本
     */
    private String text;

    /**
     * 邀请图标
     */
    private String icon;

    /**
     * 邀请延迟时间,单位:毫秒
     */
    private Long delay;

    /**
     * 是否循环
     */
    private Boolean loop;

    /**
     * 循环延迟时间,单位:毫秒
     */
    private Long loopDelay;

    /**
     * 循环次数
     */
    private Integer loopCount;

    /**
     * 邀请消息列表
     */
    private List<String> messageList;

    /**
     * 定向邀请设置 - 针对特定页面启用邀请
     */
    private Boolean targetedInvite;

    /**
     * 定向邀请页面URL匹配模式
     */
    private List<String> targetedInviteUrls;

    /**
     * 智能触发邀请 - 基于用户行为
     */
    private Boolean smartTrigger;

    /**
     * 页面停留时间触发，单位:秒
     */
    private Integer pageStayTriggerSeconds;

    /**
     * 页面滚动触发
     */
    private Boolean scrollTrigger;

    /**
     * 页面滚动触发百分比 (0-100)
     * 注意：与后端实体字段保持一致命名
     */
    private Integer scrollTriggerPerceninviteSettinge;

    /**
     * 退出意图触发
     */
    private Boolean exitIntentTrigger;

    /**
     * 访客来源触发
     */
    private Boolean referrerTrigger;

    /**
     * 来源URL匹配模式
     */
    private List<String> referrerPatterns;

    /**
     * 访客设备类型触发 - 可选: desktop, mobile, tablet
     */
    private List<String> deviceTypes;

    /**
     * 是否启用访客分段邀请
     */
    private Boolean visitorSegmentation;

    /**
     * 新访客专属邀请消息
     */
    private String newVisitorMessage;

    /**
     * 回访访客专属邀请消息
     */
    private String returningVisitorMessage;

    /**
     * VIP访客专属邀请消息
     */
    private String vipVisitorMessage;

    /**
     * 邀请样式设置: bubble, card, modal, banner
     */
    private String inviteStyle;

    /**
     * 邀请动画效果: fade, slide, bounce, none
     */
    private String inviteAnimation;

    /**
     * 是否启用A/B测试
     */
    private Boolean abTesting;

    // private ZonedDateTime createdAt;
}
