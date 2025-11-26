/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-18 07:51:39
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-16 16:58:37
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue_member;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.content.QueueContent;
import com.bytedesk.core.message.content.QueueNotification;
import com.bytedesk.core.message.event.MessageCreateEvent;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.event.ThreadAcceptEvent;
import com.bytedesk.core.thread.event.ThreadCloseEvent;
import com.bytedesk.core.quartz.event.QuartzOneMinEvent;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.presence.PresenceFacadeService;
import com.bytedesk.service.queue_settings.QueueSettingsEntity;
import com.bytedesk.service.queue_settings.QueueTipTemplateUtils;
import com.bytedesk.service.utils.ThreadMessageUtil;
import com.bytedesk.service.workgroup.WorkgroupEntity;
import com.bytedesk.service.workgroup.WorkgroupRestService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class QueueMemberEventListener {

    private final QueueMemberRestService queueMemberRestService;

    private final ThreadRestService threadRestService;

    private final IMessageSendService messageSendService;

    private final WorkgroupRestService workgroupRestService;

    private final PresenceFacadeService presenceFacadeService;

    @EventListener
    public void onThreadAcceptEvent(ThreadAcceptEvent event) {
        ThreadEntity thread = event.getThread();
        log.info("queue member onThreadAcceptEvent: {}", thread.getUid());
        Optional<QueueMemberEntity> memberOptional = queueMemberRestService.findByThreadUid(thread.getUid());
        if (memberOptional.isPresent()) {
            QueueMemberEntity member = memberOptional.get();
            member.manualAcceptThread();
            queueMemberRestService.save(member);
            // 
            handleQueueAcceptBroadcast(thread, member);
        } else {
            log.error("queue member onThreadAcceptEvent: member not found: {}", thread.getUid());
        }
    }

    /**
     * 会话关闭时(包含访客主动关闭/系统自动关闭/客服关闭)，刷新同队列其余排队成员位置
     */
    @EventListener
    public void onThreadCloseEvent(ThreadCloseEvent event) {
        ThreadEntity thread = event.getThread();
        if (thread == null || thread.getTopic() == null) {
            return;
        }

        // 当某个会话关闭，则检查当前客服队列或所在工作组队列中是否有排队成员，如果有，则自动接入最前面的排队成员

        
    }

    @EventListener
    public void onQuartzOneMinEvent(QuartzOneMinEvent event) {
        // int removed = queueMemberRestService.cleanupIdleQueueMembers();
        // if (removed > 0) {
        //     log.info("Idle queue members removed: {}", removed);
        //     // 广播所有前缀的队列位置刷新：这里简化处理，按当前活跃排队线程重新计算
        //     // 获取任意还在排队的线程列表，通过提取前缀分组刷新
        //     // 为避免复杂度，这里只刷新受影响的所有排队会话(全量刷新)
        //     // 查找所有排队中的线程(匹配已有查询方法前缀需要 topicPrefix, 此处使用简单遍历 prefix 集合)
        //     // 简化：不区分前缀，逐个线程重新发送其位置
        //     // 由于缺少批量查询接口，这里暂不实现全量广播以免性能问题，可后续优化
        //     // (占位注释)
        // }
    }

    /**
     * 1. 通知工作组内其他成员该排队会话已被接受 QUEUE_ACCEPT, 并在desktop端从队列中删除此排队会话
     * 2. 刷新同队列其余排队成员位置 QUEUE_UPDATE，并在visitor端更新排队位置显示
     * @param thread
     * @param acceptedMember
     */
    private void handleQueueAcceptBroadcast(ThreadEntity thread, QueueMemberEntity acceptedMember) {
        if (thread == null || acceptedMember == null) {
            return;
        }
        if (acceptedMember.getWorkgroupQueue() == null) {
            log.debug("queue accept broadcast skipped, no workgroup queue bound: threadUid={}", thread.getUid());
            return;
        }

        List<QueueMemberEntity> queueMembers = queueMemberRestService.findQueuingMembersByWorkgroupQueueUid(acceptedMember.getWorkgroupQueue().getUid());
        queueMembers.removeIf(member -> member.getThread() == null);
        queueMembers.removeIf(member -> thread.getUid().equals(member.getThread().getUid()));

        notifyAgentsQueueAccepted(thread, acceptedMember, queueMembers.size());
        if (queueMembers.isEmpty()) {
            return;
        }
        broadcastQueueUpdates(queueMembers);
    }

    private void notifyAgentsQueueAccepted(ThreadEntity thread, QueueMemberEntity acceptedMember, int remainingQueueSize) {
        Optional<WorkgroupEntity> workgroupOpt = resolveWorkgroupEntity(thread);
        if (!workgroupOpt.isPresent()) {
            log.debug("queue accept broadcast skipped, workgroup missing: threadUid={}", thread != null ? thread.getUid() : null);
            return;
        }

        List<AgentEntity> availableAgents = presenceFacadeService.getAvailableAgents(workgroupOpt.get());
        if (availableAgents == null || availableAgents.isEmpty()) {
            log.debug("queue accept broadcast skipped, no available agents: workgroupUid={}", workgroupOpt.get().getUid());
            return;
        }

        String acceptedAgentUid = resolveAgentUid(thread);
        List<AgentEntity> targetAgents = availableAgents.stream()
                .filter(agent -> !StringUtils.hasText(acceptedAgentUid) || !acceptedAgentUid.equals(agent.getUid()))
                .toList();

        if (targetAgents.isEmpty()) {
            log.debug("queue accept broadcast skipped, only accepting agent online: threadUid={} workgroupUid={}",
                    thread.getUid(), workgroupOpt.get().getUid());
            return;
        }

        QueueNotification payload = buildQueueNotification(thread, acceptedMember, null, remainingQueueSize, null);
        for (AgentEntity agent : targetAgents) {
            try {
                ThreadEntity agentQueueThread = queueMemberRestService.createAgentQueueThread(agent);
                MessageProtobuf message = ThreadMessageUtil.getAgentQueueAcceptMessage(payload, agentQueueThread);
                messageSendService.sendProtobufMessage(message);
            } catch (Exception e) {
                log.error("Failed to send QUEUE_ACCEPT notice: workgroupUid={}, agentUid={}, queueMemberUid={}, error={}",
                        workgroupOpt.get().getUid(), agent.getUid(), acceptedMember.getUid(), e.getMessage(), e);
            }
        }
    }

    private void broadcastQueueUpdates(List<QueueMemberEntity> queueMembers) {
        int totalCount = queueMembers.size();
        for (int i = 0; i < queueMembers.size(); i++) {
            QueueMemberEntity member = queueMembers.get(i);
            int position = i + 1;
            sendVisitorQueueUpdate(member, position, totalCount);
        }
    }

    private void sendVisitorQueueUpdate(QueueMemberEntity queueMember, int position, int totalCount) {
        ThreadEntity targetThread = queueMember.getThread();
        if (targetThread == null) {
            return;
        }
        QueueContent queueContent = buildQueueContent(queueMember, position, totalCount);
        try {
            targetThread.setContent(queueContent.toJson());
            threadRestService.save(targetThread);
        } catch (Exception e) {
            log.debug("queue update content persistence skipped: threadUid={}, error={}",
                    targetThread.getUid(), e.getMessage());
        }
        try {
            MessageProtobuf message = ThreadMessageUtil.getThreadQueueUpdateMessage(queueContent, targetThread);
            messageSendService.sendProtobufMessage(message);
        } catch (Exception e) {
            log.error("Failed to send visitor QUEUE_UPDATE: threadUid={}, queueMemberUid={}, error={}",
                    targetThread.getUid(), queueMember.getUid(), e.getMessage(), e);
        }
    }

    private QueueNotification buildQueueNotification(ThreadEntity thread, QueueMemberEntity queueMember,
            Integer position, int queueSize, Integer waitSeconds) {
        Long estimatedWaitMs = waitSeconds != null ? waitSeconds.longValue() * 1000L : null;
        return QueueNotification.builder()
                .queueMemberUid(queueMember.getUid())
                .threadUid(thread.getUid())
                .threadTopic(thread.getTopic())
                .position(position)
                .queueSize(queueSize)
                .estimatedWaitMs(estimatedWaitMs)
                .serverTimestamp(System.currentTimeMillis())
                .user(thread.getUser())
                .build();
    }

    private QueueContent buildQueueContent(QueueMemberEntity queueMember, int position, int totalCount) {
        int safePosition = Math.max(position, 1);
        int safeQueueSize = Math.max(totalCount, safePosition);

        QueueSettingsEntity queueSettings = resolveQueueSettings(queueMember);
        int avgWaitTimePerPerson = queueSettings != null && queueSettings.getAvgWaitTimePerPerson() != null
                ? queueSettings.getAvgWaitTimePerPerson()
                : QueueTipTemplateUtils.DEFAULT_AVG_WAIT_TIME_PER_PERSON;

        String displayText = QueueTipTemplateUtils.resolveTemplate(queueSettings, safePosition, safeQueueSize,
                avgWaitTimePerPerson);

        int waitSeconds = safePosition * avgWaitTimePerPerson;
        String estimatedWaitTime = QueueTipTemplateUtils.formatWaitTime(waitSeconds);

        return QueueContent.builder()
                .content(displayText)
                .position(safePosition)
                .queueSize(safeQueueSize)
                .waitSeconds(waitSeconds)
                .estimatedWaitTime(estimatedWaitTime)
                .serverTimestamp(System.currentTimeMillis())
                .build();
    }

    private QueueSettingsEntity resolveQueueSettings(QueueMemberEntity queueMember) {
        if (queueMember == null) {
            return null;
        }
        ThreadEntity thread = queueMember.getThread();
        if (thread == null) {
            return null;
        }

        Optional<WorkgroupEntity> workgroupOpt = resolveWorkgroupEntity(thread);
        return workgroupOpt.map(this::extractQueueSettings).orElse(null);
    }

    private QueueSettingsEntity extractQueueSettings(WorkgroupEntity workgroup) {
        if (workgroup == null || workgroup.getSettings() == null) {
            return null;
        }
        QueueSettingsEntity settings = workgroup.getSettings().getQueueSettings();
        if (settings == null) {
            settings = workgroup.getSettings().getDraftQueueSettings();
        }
        return settings;
    }

    private Optional<WorkgroupEntity> resolveWorkgroupEntity(ThreadEntity thread) {
        if (thread == null) {
            return Optional.empty();
        }

        UserProtobuf workgroupProtobuf = UserProtobuf.fromJson(thread.getWorkgroup());
        if (workgroupProtobuf == null || !StringUtils.hasText(workgroupProtobuf.getUid())) {
            return Optional.empty();
        }

        try {
            Optional<WorkgroupEntity> workgroupOpt = workgroupRestService.findByUid(workgroupProtobuf.getUid());
            if (!workgroupOpt.isPresent()) {
                log.debug("workgroup not found when resolving thread workgroup: {}", workgroupProtobuf.getUid());
            }
            return workgroupOpt;
        } catch (Exception e) {
            log.warn("Failed to resolve workgroup {}: {}", workgroupProtobuf.getUid(), e.getMessage());
            return Optional.empty();
        }
    }

    private String resolveAgentUid(ThreadEntity thread) {
        if (thread == null || !StringUtils.hasText(thread.getAgent())) {
            return null;
        }
        UserProtobuf agentProto = UserProtobuf.fromJson(thread.getAgent());
        return agentProto != null ? agentProto.getUid() : null;
    }

    @EventListener
    public void onMessageCreateEvent(MessageCreateEvent event) {
        MessageEntity message = event.getMessage();
        if (message == null) {
            return;
        }
        // log.debug("QueueMemberEventListener 接收到新消息事件: messageUid={}, threadUid={},
        // content={}",
        // message.getUid(), message.getThread().getUid(), message.getContent());

        // 获取消息对应的会话线程
        ThreadEntity thread = null;
        try {
            thread = threadRestService.findByUid(message.getThread().getUid()).orElse(null);
            if (thread == null) {
                log.warn("消息对应的会话不存在: messageUid={}, threadUid={}",
                        message.getUid(), message.getThread().getUid());
                return;
            }

            if (message.isFromVisitor()) {
                // 更新访客消息统计
                updateVisitorMessageStats(message, thread);
            } else if (message.isFromAgent()) {
                // 更新客服消息统计
                updateAgentMessageStats(message, thread);
            } else if (message.isFromRobot()) {
                // 处理机器人消息
                updateRobotMessageStats(message, thread);
            } else if (message.isFromSystem()) {
                // 处理系统消息
                updateSystemMessageStats(message, thread);
            }
        } catch (Exception e) {
            log.error("处理消息事件时出错: {}", e.getMessage(), e);
        }
    }

    /**
     * 更新访客消息统计
     * 
     * @param message 消息对象
     * @param thread  会话对象
     */
    private void updateVisitorMessageStats(MessageEntity message, ThreadEntity thread) {
        if (thread == null || message == null) {
            return;
        }
        if (!message.isFromVisitor()) {
            return;
        }

        try {
            // 查找关联的队列成员记录
            Optional<QueueMemberEntity> queueMemberOpt = queueMemberRestService.findByThreadUid(thread.getUid());
            if (queueMemberOpt.isEmpty()) {
                log.warn("未找到与会话关联的队列成员: threadUid={}", thread.getUid());
                return;
            }

            QueueMemberEntity queueMember = queueMemberOpt.get();
            ZonedDateTime now = BdDateUtils.now();

            // 更新首次消息时间（如果尚未设置）
            if (queueMember.getVisitorFirstMessageAt() == null) {
                queueMember.setVisitorFirstMessageAt(now);
            }

            // 更新最后一次访客消息时间
            queueMember.setVisitorLastMessageAt(now);

            // 更新访客消息计数
            queueMember.setVisitorMessageCount(queueMember.getVisitorMessageCount() + 1);

            // 保存更新 - 支持重试机制
            queueMemberRestService.save(queueMember);
            log.debug("已更新队列成员访客消息统计: threadUid={}, visitorMsgCount={}",
                    thread.getUid(), queueMember.getVisitorMessageCount());
        } catch (Exception e) {
            log.error("更新访客消息统计时出错: {}", e.getMessage(), e);
        }
    }

    /**
     * 更新客服消息统计
     * 
     * @param message 消息对象
     * @param thread  会话对象
     */
    private void updateAgentMessageStats(MessageEntity message, ThreadEntity thread) {
        if (thread == null || message == null) {
            return;
        }
        if (!message.isFromAgent()) {
            return;
        }

        try {
            // 查找关联的队列成员记录
            Optional<QueueMemberEntity> queueMemberOpt = queueMemberRestService.findByThreadUid(thread.getUid());
            if (queueMemberOpt.isEmpty()) {
                log.warn("未找到与会话关联的队列成员: threadUid={}", thread.getUid());
                return;
            }

            QueueMemberEntity queueMember = queueMemberOpt.get();
            ZonedDateTime now = BdDateUtils.now();

            // 更新客服消息计数
            queueMember.setAgentMessageCount(queueMember.getAgentMessageCount() + 1);

            // 如果是首次响应，记录首次响应时间
            if (!queueMember.getAgentFirstResponse() && queueMember.getVisitorLastMessageAt() != null) {
                queueMember.setAgentFirstResponse(true);
                queueMember.setAgentFirstResponseAt(now);

                // 计算首次响应时间（秒）
                long responseTimeInSeconds = Duration.between(queueMember.getVisitorLastMessageAt(), now).getSeconds();
                queueMember.setAgentMaxResponseLength((int) responseTimeInSeconds);
                queueMember.setAgentAvgResponseLength((int) responseTimeInSeconds);
            } else if (queueMember.getVisitorLastMessageAt() != null) {
                // 非首次响应，更新平均和最大响应时间
                long responseTimeInSeconds = Duration.between(queueMember.getVisitorLastMessageAt(), now).getSeconds();

                // 更新最大响应时间
                if (responseTimeInSeconds > queueMember.getAgentMaxResponseLength()) {
                    queueMember.setAgentMaxResponseLength((int) responseTimeInSeconds);
                }

                // 更新平均响应时间 - 使用累计平均计算方法
                // (currentAvg * (messageCount-1) + newValue) / messageCount
                int messageCount = queueMember.getAgentMessageCount();
                if (messageCount > 1) { // 避免除以零
                    int currentTotal = queueMember.getAgentAvgResponseLength() * (messageCount - 1);
                    queueMember.setAgentAvgResponseLength((currentTotal + (int) responseTimeInSeconds) / messageCount);
                }
            }

            // 更新最后响应时间
            queueMember.setAgentLastResponseAt(now);

            // 保存更新
            queueMemberRestService.save(queueMember);
            log.debug("已更新队列成员客服消息统计: threadUid={}, agentMsgCount={}, avgResponseTime={}s, maxResponseTime={}s",
                    thread.getUid(), queueMember.getAgentMessageCount(),
                    queueMember.getAgentAvgResponseLength(), queueMember.getAgentMaxResponseLength());
        } catch (Exception e) {
            log.error("更新客服消息统计时出错: {}", e.getMessage(), e);
        }
    }

    /**
     * 更新机器人消息统计
     *
     * @param message 消息对象
     * @param thread  会话对象
     */
    private void updateRobotMessageStats(MessageEntity message, ThreadEntity thread) {
        if (thread == null || message == null) {
            return;
        }
        if (!message.isFromRobot()) {
            return;
        }

        try {
            // 查找关联的队列成员记录
            Optional<QueueMemberEntity> queueMemberOpt = queueMemberRestService.findByThreadUid(thread.getUid());
            if (queueMemberOpt.isEmpty()) {
                log.warn("未找到与会话关联的队列成员: threadUid={}", thread.getUid());
                return;
            }

            QueueMemberEntity queueMember = queueMemberOpt.get();
            ZonedDateTime now = BdDateUtils.now();

            // 更新首次机器人消息时间（如果尚未设置）
            if (queueMember.getRobotFirstResponseAt() == null) {
                queueMember.setRobotFirstResponseAt(now);
            }

            // 更新最后一次机器人消息时间
            queueMember.setRobotLastResponseAt(now);

            // 更新机器人消息计数
            queueMember.setRobotMessageCount(queueMember.getRobotMessageCount() + 1);

            // 如果是访客提问后的机器人回复，计算响应时间
            if (queueMember.getVisitorLastMessageAt() != null) {
                long responseTimeInSeconds = Duration.between(queueMember.getVisitorLastMessageAt(), now).getSeconds();

                // 更新最大响应时间
                if (queueMember.getRobotMaxResponseLength() == 0 ||
                        responseTimeInSeconds > queueMember.getRobotMaxResponseLength()) {
                    queueMember.setRobotMaxResponseLength((int) responseTimeInSeconds);
                }

                // 更新平均响应时间
                int messageCount = queueMember.getRobotMessageCount();
                if (messageCount > 1) {
                    int currentTotal = queueMember.getRobotAvgResponseLength() * (messageCount - 1);
                    queueMember.setRobotAvgResponseLength((currentTotal + (int) responseTimeInSeconds) / messageCount);
                } else {
                    queueMember.setRobotAvgResponseLength((int) responseTimeInSeconds);
                }
            }

            // 保存更新
            queueMemberRestService.save(queueMember);
            log.debug("已更新队列成员机器人消息统计: threadUid={}, robotMsgCount={}, avgResponseTime={}s, maxResponseTime={}s",
                    thread.getUid(), queueMember.getRobotMessageCount(),
                    queueMember.getRobotAvgResponseLength(), queueMember.getRobotMaxResponseLength());
        } catch (Exception e) {
            log.error("更新机器人消息统计时出错: {}", e.getMessage(), e);
        }
    }

    /**
     * 更新系统消息统计
     *
     * @param message 消息对象
     * @param thread  会话对象
     */
    private void updateSystemMessageStats(MessageEntity message, ThreadEntity thread) {
        if (thread == null || message == null) {
            return;
        }
        if (!message.isFromSystem()) {
            return;
        }

        try {
            // 查找关联的队列成员记录
            Optional<QueueMemberEntity> queueMemberOpt = queueMemberRestService.findByThreadUid(thread.getUid());
            if (queueMemberOpt.isEmpty()) {
                log.warn("未找到与会话关联的队列成员: threadUid={}", thread.getUid());
                return;
            }

            QueueMemberEntity queueMember = queueMemberOpt.get();
            ZonedDateTime now = BdDateUtils.now();

            // 更新系统消息计数
            queueMember.setSystemMessageCount(queueMember.getSystemMessageCount() + 1);

            // 记录首次系统消息时间（如果尚未设置）
            if (queueMember.getSystemFirstResponseAt() == null) {
                queueMember.setSystemFirstResponseAt(now);
            }

            // 更新最后一次系统消息时间
            queueMember.setSystemLastResponseAt(now);

            // 保存更新
            queueMemberRestService.save(queueMember);
            log.debug("已更新队列成员系统消息统计: threadUid={}, systemMsgCount={}",
                    thread.getUid(), queueMember.getSystemMessageCount());
        } catch (Exception e) {
            log.error("更新系统消息统计时出错: {}", e.getMessage(), e);
        }
    }

    /**
     * 发送排队更新消息
     */
    // private void sendQueueUpdateMessage(ThreadEntity thread, int currentPosition, int totalCount) {
    //     try {
    //         // 构建用于展示的提示文本
    //         String displayText;
    //         if (currentPosition == 1) {
    //             displayText = "请稍后，下一个就是您";
    //         } else {
    //             int waitMinutes = (currentPosition - 1) * ThreadRoutingConstants.Timing.ESTIMATED_WAIT_TIME_PER_PERSON;
    //             displayText = "当前排队人数：" + totalCount + "，您的位置：" + currentPosition +
    //                     "，大约等待时间：" + waitMinutes + " 分钟";
    //         }

    //         // 计算等待时间（秒）与人性化描述
    //         int waitSeconds = currentPosition == 1 ? 0
    //                 : (currentPosition - 1) * ThreadRoutingConstants.Timing.ESTIMATED_WAIT_TIME_PER_PERSON * 60;
    //         String estimatedWaitTime = currentPosition == 1 ? "即将开始" : ("约" + (waitSeconds / 60) + "分钟");

    //         QueueContent queueContent = QueueContent.builder()
    //                 .content(displayText)
    //                 .position(currentPosition)
    //                 .queueSize(totalCount)
    //                 .waitSeconds(waitSeconds)
    //                 .estimatedWaitTime(estimatedWaitTime)
    //                 .serverTimestamp(System.currentTimeMillis())
    //                 .build();

    //         // 将结构化内容写入线程（保持与其它策略一致）
    //         thread.setContent(queueContent.toJson());
    //         threadRestService.save(thread);

    //         // 发送结构化排队消息
    //         MessageProtobuf messageProtobuf = ThreadMessageUtil.getThreadQueueMessage(queueContent, thread);
    //         messageSendService.sendProtobufMessage(messageProtobuf);

    //         log.debug("已发送排队更新消息(结构化): threadUid={}, position={}/{}, waitSeconds={}, content={}",
    //                 thread.getUid(), currentPosition, totalCount, waitSeconds, displayText);
    //     } catch (Exception e) {
    //         log.error("发送排队更新消息时出错: threadUid={}, error={}", thread.getUid(), e.getMessage(), e);
    //     }
    // }

    /**
     * 从topic中提取前三个部分作为搜索前缀
     * 例如：org/agent/{agent_uid}/{visitor_uid} -&gt; org/agent/{agent_uid}
     */
    // private String extractTopicPrefix(String topic) {
    //     if (topic == null || topic.isEmpty()) {
    //         return null;
    //     }

    //     String[] parts = topic.split("/");
    //     if (parts.length >= 3) {
    //         return parts[0] + "/" + parts[1] + "/" + parts[2];
    //     }
    //     return null;
    // }


}
