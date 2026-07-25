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

import com.bytedesk.core.thread.enums.ThreadProcessStatusEnum;
import com.bytedesk.core.utils.BdDateUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QueueMemberStatisticRow {

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
    private final Integer agentMessageCount;
    private final ZonedDateTime robotAcceptedAt;
    private final Integer robotMessageCount;
    private final ZonedDateTime systemClosedAt;
    private final Integer visitorMessageCount;
    private final Boolean messageLeave;
    private final Boolean systemClose;
    private final Boolean resolved;
    private final Boolean agentOffline;

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