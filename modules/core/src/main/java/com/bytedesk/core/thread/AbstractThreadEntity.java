/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-29 13:00:33
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-13 22:18:20
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.thread;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.utils.StringListConverter;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import com.bytedesk.core.rbac.user.UserEntity;

/**
 * 会话基类，包含ThreadEntity和VisitorThreadEntity的共同属性
 */
@MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractThreadEntity extends BaseEntity {
    
    private static final long serialVersionUID = 1L;

    /**
     * used to push message
     * topic format:
     * workgroup_wid + '/' + visitor_vid
     * agent_aid + '/' + visitor_vid
     * such as: wid/vid or aid/vid
     */
    @NotBlank
    @Column(name = "thread_topic", nullable = false)
    private String topic;

    @Column(nullable = true)
    private String content = BytedeskConsts.EMPTY_STRING;

    /**
     * @{ThreadTypeEnum}
     */
    @Column(name = "thread_type", nullable = false)
    private String type = ThreadTypeEnum.WORKGROUP.name();

    /** closed/open, agent closed/auto closed */
    @Column(name = "thread_state", nullable = false)
    private String state = ThreadStateEnum.QUEUING.name();

    // 置顶
    @Column(name = "is_top")
    private boolean top = false;

    // 未读
    @Column(name = "is_unread")
    private boolean unread = false;
    
    // 未读消息数
    private int unreadCount = 1;

    // 免打扰
    @Column(name = "is_mute")
    private boolean mute = false;

    // 不在会话列表显示
    @Column(name = "is_hide")
    private boolean hide = false;

    // 星标
    @Column(name = "thread_star")
    private int star = 0;

    // 类似微信折叠会话
    @Column(name = "is_folded")
    private boolean folded = false;
    
    private String client = ClientEnum.WEB.name();

    @Column(name = "thread_extra", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String extra = BytedeskConsts.EMPTY_JSON_STRING;

    // 意图类型
    private String intentionType = ThreadIntentionTypeEnum.OTHER.name();

    // 情绪类型
    private String emotionType = ThreadEmotionTypeEnum.OTHER.name();

    // 质检结果
    private String qualityCheckResult = ThreadQualityCheckResultEnum.OTHER.name();

    // 是否被评价
    @Column(name = "is_rated")
    private boolean rated = false;

    // 计数器编号，客服咨询首先需要取号，类似银行/医院排队系统
    private int queueNumber = 0;

    // 自动关闭
    @Column(name = "is_auto_close")
    private boolean autoClose = false;

    // 机器人
    @Column(name = "is_robot")
    private boolean robot = false;

    // 备注
    @Column(name = "thread_note")
    private String note;

    // 标签
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> tagList = new ArrayList<>();

    /**
     * 在客服会话中，存储访客信息
     * 在同事会话中，存储同事信息
     * 在用户私聊中，存储对方用户信息
     * 机器人会话中，存储访客信息
     * 群组会话中，存储群组信息
     * 注意：h2 db 不能使用 user, 所以重定义为 thread_user
     */
    @Column(name = "thread_user", length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String user = BytedeskConsts.EMPTY_JSON_STRING;

    /**
     * 一对一客服对话中，存储客服信息
     * 技能组客服对话中，存储技能组信息
     * 机器人对话中，存储机器人信息
     * 用户私聊、群聊、同事会话中，无需存储，使用owner字段信息
     */
    @Column(name = "thread_agent", length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String agent = BytedeskConsts.EMPTY_JSON_STRING;
    
    // multi agent assistants: monitoring agent、quality check agent、robot agent
    @Column(length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String multiAgents = BytedeskConsts.EMPTY_JSON_STRING;
    
    // belongs to user - 将owner关系移动到抽象类
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity owner;
    

}
