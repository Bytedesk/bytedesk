/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-14 17:57:08
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-06 14:59:40
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue_member;

import java.time.ZonedDateTime;

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.thread.ThreadEmotionTypeEnum;
import com.bytedesk.core.thread.ThreadIntentionTypeEnum;
import com.bytedesk.core.thread.ThreadQualityCheckResultEnum;
import com.bytedesk.core.thread.ThreadSummaryStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class QueueMemberRequest extends BaseRequest {

    private String queueUid;  // 关联队列

    private String queueNickname;  // 队列昵称

    private String visitorNickname; // 访客昵称

    private String robotNickname; //  机器人昵称

    private String agentNickname; // 客服昵称

    private String threadUid;  // 关联会话

    private String threadTopic;  // 会话主题，用于查询

    @Builder.Default
    private Integer beforeNumber = 0;  // 前面排队人数

    @Builder.Default
    private Integer waitLength = 0;  // 等待时间(秒)

    @Builder.Default
    private Integer queueNumber = 0;  // 排队号码

    @Builder.Default
    private ZonedDateTime visitorEnqueueAt = ZonedDateTime.now();  // 加入时间

    private ZonedDateTime visitorFirstMessageAt;  // 访客首次发送消息时间

    private ZonedDateTime visitorLastMessageAt;  // 访客最后发送消息时间

    private ZonedDateTime visitorLeavedAt;  // 离开时间

    private String agentAcceptType;  // 接入方式：自动、手动，不设置默认

    private ZonedDateTime agentAcceptedAt;  // 开始服务时间

    @Builder.Default
    private Boolean agentFirstResponse = false;  // 是否首次响应

    private ZonedDateTime agentFirstResponseAt;  // 首次人工响应时间

    private ZonedDateTime agentLastResponseAt;  // 最后响应时间

    private ZonedDateTime agentClosedAt;  // 结束时间

    @Builder.Default
    private Integer agentAvgResponseLength = 0;  // 平均响应时间(秒)
    
    @Builder.Default
    private Integer agentMaxResponseLength = 0;  // 最长响应时间(秒)

    @Builder.Default
    private Integer agentMessageCount = 0;  // 客服消息数量

    @Builder.Default
    private Integer visitorMessageCount = 0;  // 访客消息数量

    @Builder.Default
    private Boolean agentTimeout = false; // 是否超时

    // 机器人对话超时时间
    private ZonedDateTime robotTimeoutAt;

    // -----------------
    
    @Builder.Default
    private Integer systemMessageCount = 0;  // 系统消息数量

    // 人工对话超时时间
    private ZonedDateTime agentTimeoutAt;


    @Builder.Default
    private Integer visitorPriority = 0;  // 优先级(0-100)

    // 直接在评价表里面根据threadUid查询是否已经评价
    // 是否被评价
    @Builder.Default
    private Boolean rated = false;

    // 评分时间
    private ZonedDateTime rateAt;  // 评分时间

    // 是否已解决
    @Builder.Default
    private Boolean resolved = false;

    /// 是否留言
    @Builder.Default
    private Boolean messageLeave = false;

    private ZonedDateTime messageLeaveAt;  // 留言时间

    // 直接在小结表里面根据threadUid查询是否已经小结
    // 是否已经小结
    @Builder.Default
    private Boolean summarized = false;

    

    // 直接在质检表里面根据threadUid查询是否已经质检
    // 是否已经质检
    @Builder.Default
    private Boolean qualityChecked = false;

    // 重构到相应的表里面
    // 意图类型
    @Builder.Default
    private String intentionType = ThreadIntentionTypeEnum.OTHER.name();

    // 情绪类型
    @Builder.Default
    private String emotionType = ThreadEmotionTypeEnum.OTHER.name();

    // 质检结果
    @Builder.Default
    private String qualityCheckResult = ThreadQualityCheckResultEnum.OTHER.name();

    // 处理状态（待处理、已处理、已关闭等）
    @Builder.Default
    private String resolvedStatus = ThreadSummaryStatusEnum.PENDING.name();

    private String client;  // 客户来源渠道

    // 排队用户信息
    @Builder.Default
    private String visitor = BytedeskConsts.EMPTY_JSON_STRING;

    // 接待客服信息
    @Builder.Default
    private String agent = BytedeskConsts.EMPTY_JSON_STRING;

    // 接待工作组信息
    @Builder.Default
    private String workgroup = BytedeskConsts.EMPTY_JSON_STRING;

    // 机器人转人工
    @Builder.Default
    private Boolean robotToAgent = false;

    // 机器人转人工时间
    private ZonedDateTime robotToAgentAt;
}
