/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-12-22
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.settings_emotion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.modelmapper.ModelMapper;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.converter.StringListConverter;
import com.bytedesk.kbase.settings_trigger.ExecutionTimingConsts;
import com.bytedesk.kbase.settings_trigger.TriggerScopeConsts;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Entity
@Table(name = "bytedesk_kbase_settings_emotion")
public class EmotionSettingEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 设置名称，用于区分不同的情绪识别设置模板
     */
    @Column(name = "settings_name")
    private String name;
    
    /**
     * 设置描述
     */
    @Column(name = "settings_description")
    private String description;

    /**
     * 关联robot entity uid，用于调用LLM大模型进行情绪识别
     */
    @Column(name = "robot_uid")
    private String robotUid;

    /**
     * 是否为默认设置模板
     */
    @Builder.Default
    @Column(name = "is_default_template")
    private Boolean defaultTemplate = false;

    /**
     * 是否启用情绪识别
     */
    @Builder.Default
    @Column(name = "is_enabled")
    private Boolean enabled = false;

    /**
     * 执行时机
     * - ON_MESSAGE: 收到新消息时
     * - THREAD_END: 会话结束时
     */
    @Builder.Default
    @Column(name = "execution_timing")
    private String executionTiming = ExecutionTimingConsts.ON_MESSAGE;

    /**
     * 触发范围
     * - VISITOR_ONLY: 仅访客消息
     * - AGENT_ONLY: 仅客服消息
     * - ALL: 所有非系统消息
     */
    @Builder.Default
    @Column(name = "trigger_scope")
    private String triggerScope = TriggerScopeConsts.VISITOR_ONLY;

    /**
     * 触发频率：每隔 N 条消息触发一次
     * - 1 表示每条消息都触发
     */
    @Builder.Default
    @Column(name = "trigger_every_n_messages")
    private Integer triggerEveryNMessages = 1;

    /**
     * 触发冷却时间（秒）：上次触发后，冷却未结束则跳过
     */
    @Builder.Default
    @Column(name = "trigger_cooldown_seconds")
    private Integer triggerCooldownSeconds = 0;

    /**
     * 是否仅按时间冷却触发（忽略 triggerEveryNMessages）
     */
    @Builder.Default
    @Column(name = "is_trigger_cooldown_only")
    private Boolean triggerCooldownOnly = false;

    /**
     * 情绪分类选项
     */
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(name = "emotion_list", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> emotionList = new ArrayList<>(Arrays.asList(
        "满意",
        "一般",
        "不满意",
        "愤怒",
        "焦虑",
        "开心",
        "悲伤",
        "中立"
    ));
    
    /**
     * 情绪信心度阈值，低于该阈值的情绪识别结果将被认为是不确定的
     * 范围：0-1，例如0.75表示75%的置信度
     */
    @Builder.Default
    @Column(name = "confidence_threshold")
    private Double confidenceThreshold = 0.7;

    /**
     * 情绪识别触发条件
     * 格式：{"触发条件1": "值1", "触发条件2": "值2"}
     * 例如：{"message_length": "10", "keyword_match": "true"}
     */
    @Builder.Default
    @Column(name = "emotion_triggers", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String emotionTriggers = BytedeskConsts.EMPTY_JSON_STRING;

    /**
     * 情绪对应的处理策略
     * 格式：{"情绪1": "处理策略1", "情绪2": "处理策略2"}
     * 例如：{"满意": "normal", "不满意": "escalate"}
     */
    @Builder.Default
    @Column(name = "emotion_handlers", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String emotionHandlers = BytedeskConsts.EMPTY_JSON_STRING;

    /**
     * 负面情绪的处理方式
     * TRANSFER_HUMAN: 转人工
     * ESCALATE: 升级处理
     * OFFER_HELP: 主动提供帮助
     */
    @Builder.Default
    @Column(name = "negative_emotion_handling")
    private String negativeEmotionHandling = "ESCALATE";

    /**
     * 负面情绪检测到时的提示消息
     */
    @Builder.Default
    @Column(name = "negative_emotion_tip", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String negativeEmotionTip = "我注意到您可能感到不满意，我已经为您安排了专业的客服人员来帮助您解决问题。";

    /**
     * 情绪标签，用于对情绪进行分组和归类
     * 格式：{"情绪1": ["标签1", "标签2"], "情绪2": ["标签3", "标签4"]}
     */
    @Builder.Default
    @Column(name = "emotion_tags", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String emotionTags = BytedeskConsts.EMPTY_JSON_STRING;

    /**
     * 情绪权重配置，用于计算综合情绪分数
     * 格式：{"情绪1": 1.0, "情绪2": 0.8, "情绪3": 0.6}
     */
    @Builder.Default
    @Column(name = "emotion_weights", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String emotionWeights = BytedeskConsts.EMPTY_JSON_STRING;
    
    /**
     * 是否启用情绪跟踪，跟踪用户情绪的变化趋势
     */
    @Builder.Default
    @Column(name = "enable_emotion_tracking")
    private Boolean enableEmotionTracking = true;
    
    /**
     * 情绪转换阈值，当新情绪的置信度超过此阈值时才会转换当前情绪
     */
    @Builder.Default
    @Column(name = "emotion_switch_threshold")
    private Double emotionSwitchThreshold = 0.8;

    /**
     * 从 EmotionSettingRequest 创建 EmotionSetting 实体
     * 如果 request 为 null，返回默认构建的实体
     *
     * @param request EmotionSettingRequest 对象，可以为 null
     * @param modelMapper ModelMapper 实例用于字段映射
     * @return EmotionSetting 实体，永远不为 null
     */
    public static EmotionSettingEntity fromRequest(EmotionSettingRequest request, ModelMapper modelMapper) {
        if (request == null || modelMapper == null) {
            return EmotionSettingEntity.builder().build();
        }
        return modelMapper.map(request, EmotionSettingEntity.class);
    }
}
