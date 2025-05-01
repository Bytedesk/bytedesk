/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-29 16:16:32
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-29 17:22:01
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.settings_ratedown;


import java.util.List;

import com.bytedesk.core.base.BaseResponse;

import lombok.AllArgsConstructor;
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
public class RatedownSettingsResponse extends BaseResponse {

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
    private Boolean defaultTemplate;
    
    /**
     * 是否启用点踩功能
     */
    private Boolean enabled;

    /**
     * 点踩选项
     */
    private List<String> rateDownTagList;
    
    /**
     * 每次点踩允许选择的最大标签数量
     */
    private int maxTagSelection;
    
    /**
     * 是否允许用户输入自定义文本反馈
     */
    private Boolean allowCustomFeedback;
    
    /**
     * 自定义文本反馈的最大字符限制
     */
    private int maxFeedbackLength;
    
    /**
     * 是否需要客服后续跟进点踩反馈
     */
    private Boolean requireFollowup;
    
    /**
     * 点踩后是否显示感谢消息
     */
    private Boolean showThankYouMessage;
    
    /**
     * 感谢消息内容
     */
    private String thankYouMessage;
    
    /**
     * 点踩后是否自动触发满意度调查
     */
    private Boolean triggerSatisfactionSurvey;
    
    /**
     * 是否记录点踩的会话到质检系统
     */
    private Boolean markForQualityInspection;
    
    /**
     * 点踩后是否尝试转人工客服
     */
    private Boolean offerHumanAgent;

}
