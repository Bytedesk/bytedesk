/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-29 16:15:38
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-29 17:19:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.settings_ratedown;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.bytedesk.core.base.BaseRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class RatedownSettingsRequest extends BaseRequest {
    private static final long serialVersionUID = 1L;


    /**
     * 设置名称，用于区分不同的邀请设置模板
     */
    private String name;
    
    /**
     * 设置描述
     */
    private String description;

    /**
     * 是否为默认设置模板
     */
    @Builder.Default
    private Boolean defaultTemplate = false;
    
    /**
     * 是否启用点踩功能
     */
    @Builder.Default
    private Boolean enabled = true;

    /**
     * 点踩选项
     */
    @Builder.Default
    private List<String> rateDownTagList = new ArrayList<>(Arrays.asList(
        "回答不相关",
        "解释不清楚",
        "信息过时",
        "解决方案无效",
        "操作步骤错误",
        "态度不好",
        "回复太慢",
        "没有解决我的问题"
    ));
    
    /**
     * 每次点踩允许选择的最大标签数量
     */
    @Builder.Default
    private Integer maxTagSelection = 3;
    
    /**
     * 是否允许用户输入自定义文本反馈
     */
    @Builder.Default
    private Boolean allowCustomFeedback = true;
    
    /**
     * 自定义文本反馈的最大字符限制
     */
    @Builder.Default
    private Integer maxFeedbackLength = 200;
    
    /**
     * 是否需要客服后续跟进点踩反馈
     */
    @Builder.Default
    private Boolean requireFollowup = false;
    
    /**
     * 点踩后是否显示感谢消息
     */
    @Builder.Default
    private Boolean showThankYouMessage = true;
    
    /**
     * 感谢消息内容
     */
    @Builder.Default
    private String thankYouMessage = "感谢您的反馈，我们会努力改进服务质量！";
    
    /**
     * 点踩后是否自动触发满意度调查
     */
    @Builder.Default
    private Boolean triggerSatisfactionSurvey = false;
    
    /**
     * 是否记录点踩的会话到质检系统
     */
    @Builder.Default
    private Boolean markForQualityInspection = true;
    
    /**
     * 点踩后是否尝试转人工客服
     */
    @Builder.Default
    private Boolean offerHumanAgent = true;
}
