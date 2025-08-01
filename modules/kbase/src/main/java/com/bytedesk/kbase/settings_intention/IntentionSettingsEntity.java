/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-29 16:07:47
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.settings_intention;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.converter.StringListConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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

@Getter
@Setter
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({IntentionSettingsEntityListener.class})
@Entity
@Table(name = "bytedesk_kbase_settings_intention")
public class IntentionSettingsEntity extends BaseEntity {

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

    /**
     * 意图分类选项
     */
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(name = "intention_list", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> intentionList = new ArrayList<>(Arrays.asList(
        "产品咨询",
        "价格查询",
        "订单查询",
        "投诉反馈",
        "售后服务",
        "退换货申请",
        "支付问题",
        "账号问题",
        "技术支持",
        "功能建议",
        "物流查询",
        "优惠活动",
        "其他"
    ));
    
    /**
     * 意图信心度阈值，低于该阈值的意图将被认为是不确定的
     * 范围：0-1，例如0.75表示75%的置信度
     */
    @Builder.Default
    @Column(name = "confidence_threshold")
    private Double confidenceThreshold = 0.7;

    /**
     * 子意图分类映射，支持多级意图分类结构
     * 格式：{"主意图1": ["子意图1", "子意图2"], "主意图2": ["子意图3", "子意图4"]}
     */
    @Builder.Default
    @Column(name = "sub_intention_mapping", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String subIntentionMapping = BytedeskConsts.EMPTY_JSON_STRING;

    /**
     * 意图预设回复，针对不同意图提供默认回复模板
     * 格式：{"意图1": "预设回复1", "意图2": "预设回复2"}
     */
    @Builder.Default
    @Column(name = "intention_responses", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String intentionResponses = BytedeskConsts.EMPTY_JSON_STRING;

    /**
     * 未识别意图的处理方式
     * TRANSFER_HUMAN: 转人工
     * DEFAULT_ANSWER: 使用默认回复
     * CLARIFY: 请求用户澄清
     */
    @Builder.Default
    @Column(name = "unknown_intention_handling")
    private String unknownIntentionHandling = "CLARIFY";

    /**
     * 未识别意图的默认回复
     */
    @Builder.Default
    @Column(name = "unknown_intention_response", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String unknownIntentionResponse = "非常抱歉，我没能理解您的问题。请您能否更详细地描述一下您的需求，以便我为您提供更准确的帮助？";

    /**
     * 意图标签，用于对意图进行分组和归类
     * 格式：{"意图1": ["标签1", "标签2"], "意图2": ["标签3", "标签4"]}
     */
    @Builder.Default
    @Column(name = "intention_intentionSettingss", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String intentionTags = BytedeskConsts.EMPTY_JSON_STRING;

    /**
     * 意图优先级配置，数字越小优先级越高
     * 格式：{"意图1": 1, "意图2": 2, "意图3": 3}
     */
    @Builder.Default
    @Column(name = "intention_priorities", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String intentionPriorities = BytedeskConsts.EMPTY_JSON_STRING;
    
    /**
     * 是否启用意图跟踪，跟踪用户意图的变化
     */
    @Builder.Default
    @Column(name = "enable_intention_tracking")
    private Boolean enableIntentionTracking = true;
    
    /**
     * 意图转换阈值，当新意图的置信度超过此阈值时才会转换当前意图
     */
    @Builder.Default
    @Column(name = "intention_switch_threshold")
    private Double intentionSwitchThreshold = 0.8;
}
