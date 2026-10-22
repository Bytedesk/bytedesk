/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-07-22 08:00:00
 * @LastEditors: GitHub Copilot
 * @LastEditTime: 2026-07-22 08:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.service.queue_member;

import java.time.Duration;
import java.time.ZonedDateTime;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserTypeEnum;
import com.bytedesk.core.thread.enums.ThreadProcessStatusEnum;
import com.bytedesk.core.utils.BdDateUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QueueMemberStatisticRow {

    private final ZonedDateTime createdAt;
    private final String agentJson;
    private final String robotJson;
    private final String threadUid;
    private final String threadStatus;
    private final ZonedDateTime visitorEnqueueAt;
    private final ZonedDateTime visitorFirstMessageAt;
    private final ZonedDateTime visitorLeavedAt;
    private final String agentAcceptType;
    private final ZonedDateTime agentAcceptedAt;
    private final ZonedDateTime agentFirstResponseAt;
    private final ZonedDateTime agentClosedAt;
    private final Integer agentMaxResponseLength;
    private final Integer agentAvgResponseLength;
    private final Integer agentMessageCount;
    private final ZonedDateTime robotAcceptedAt;
    private final Integer robotMessageCount;
    private final Boolean robotToAgent;
    private final ZonedDateTime robotToAgentAt;
    private final ZonedDateTime systemClosedAt;
    private final Integer visitorMessageCount;
    private final Boolean messageLeave;
    private final Boolean systemClose;
    private final Boolean resolved;
    private final Boolean agentOffline;

    /**
     * 从会话客服 JSON（thread.agent）解析客服 uid。
     * 统计投影不再使用队列 uid 作为客服 uid，因为队列 uid 是队列自身标识而非客服标识。
     */
    public String getAgentUid() {
        if (agentJson == null || agentJson.isBlank()) {
            return null;
        }
        try {
            UserProtobuf agent = JSON.parseObject(agentJson, UserProtobuf.class);
            return agent == null ? null : agent.getUid();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 机器人接待证据：thread.robot 快照存在且类型为 ROBOT。
     * 仅凭 robotAcceptedAt 非空不足以判定——工作流接待(workflowAutoAcceptThread)复用同一字段。
     */
    public boolean isRobotServed() {
        if (robotJson == null || robotJson.isBlank()) {
            return false;
        }
        try {
            UserProtobuf robot = JSON.parseObject(robotJson, UserProtobuf.class);
            return robot != null && UserTypeEnum.ROBOT.name().equals(robot.getType());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 机器人证据是否不可判定：
     * - 快照非法 JSON：数据损坏，无法分类；
     * - 快照为空但有自动接入时间：无法区分真实机器人与工作流复用（workflowAutoAcceptThread 复用同一字段）。
     * 快照为空且无自动接入时间 = 从未涉及机器人（正常直接人工/排队），是明确“非机器人”，不算不可判定。
     */
    public boolean isRobotEvidenceUnknown() {
        if (robotJson == null || robotJson.isBlank()) {
            return robotAcceptedAt != null;
        }
        try {
            JSON.parseObject(robotJson, UserProtobuf.class);
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    public Boolean isQueuing() {
        return ThreadProcessStatusEnum.QUEUING.name().equals(threadStatus);
    }

    public Boolean isClosed() {
        return ThreadProcessStatusEnum.CLOSED.name().equals(threadStatus);
    }

    public Boolean isOffline() {
        return ThreadProcessStatusEnum.OFFLINE.name().equals(threadStatus);
    }

    public long getWaitLength() {
        if (visitorEnqueueAt == null) {
            return 0L;
        }
        if (isOffline() || Boolean.TRUE.equals(agentOffline)) {
            return 0L;
        }
        if (robotAcceptedAt != null) {
            return Duration.between(visitorEnqueueAt, robotAcceptedAt).getSeconds();
        }
        ZonedDateTime endWaitLength = agentAcceptedAt != null ? agentAcceptedAt : BdDateUtils.now();
        return Duration.between(visitorEnqueueAt, endWaitLength).getSeconds();
    }
}