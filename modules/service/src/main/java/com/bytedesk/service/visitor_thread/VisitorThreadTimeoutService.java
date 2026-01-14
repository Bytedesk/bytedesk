/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-01-14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-01-14
 * @Description: visitor thread timeout handling service
 *
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.service.visitor_thread;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.message.utils.MessageUtils;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadContent;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.kbase.settings_service.ServiceSettingsResponseVisitor;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.AgentRestService;
import com.bytedesk.service.queue_member.QueueMemberEntity;
import com.bytedesk.service.queue_member.QueueMemberRestService;
import com.bytedesk.service.queue_settings.QueueSettingsEntity;
import com.bytedesk.service.utils.ServiceConvertUtils;
import com.bytedesk.service.utils.ThreadMessageUtil;
import com.bytedesk.service.workgroup.WorkgroupEntity;
import com.bytedesk.service.workgroup.WorkgroupRestService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class VisitorThreadTimeoutService {

    private final ThreadRestService threadRestService;

    private final AgentRestService agentRestService;

    private final WorkgroupRestService workgroupRestService;

    private final QueueMemberRestService queueMemberRestService;

    private final IMessageSendService messageSendService;

    private final MessageRestService messageRestService;

    /**
     * 自动提醒客服或关闭会话
     */
    @Async
    public void autoRemindAgentOrCloseThread(List<ThreadEntity> threads) {
        threads.forEach(this::processThreadTimeout);
    }

    /**
     * 处理单个Thread会话的超时逻辑
     */
    private void processThreadTimeout(ThreadEntity thread) {
        // 排队中的会话：只按“排队最大等待时长”处理，不能套用已接入会话的自动关闭时间。
        // 否则会出现：排队未接入会话按 autoClose(默认30分钟) 被提前关闭，导致 maxWaitTime 配置不生效。
        if (thread.isQueuing()) {
            handleQueueWaitTimeout(thread);
            return;
        }

        long diffInMinutes = calculateThreadTimeoutMinutes(thread);
        if (diffInMinutes < 0) {
            return; // 时间异常，跳过处理
        }

        // 处理自动关闭
        handleAutoClose(thread, diffInMinutes);

        // 处理超时提醒
        handleTimeoutReminder(thread, diffInMinutes);
    }

    /**
     * 计算从指定时间点到现在的分钟差
     */
    private long minutesSince(ZonedDateTime baseTime) {
        if (baseTime == null) {
            return -1;
        }
        long currentTimeMillis = BdDateUtils.toTimestamp(BdDateUtils.now());
        long baseMillis = BdDateUtils.toTimestamp(baseTime);
        long diffInMilliseconds = currentTimeMillis - baseMillis;
        if (diffInMilliseconds < 0) {
            return -1;
        }
        return TimeUnit.MILLISECONDS.toMinutes(diffInMilliseconds);
    }

    /**
     * 计算线程超时分钟数
     */
    private long calculateThreadTimeoutMinutes(ThreadEntity thread) {
        // 使用BdDateUtils.toTimestamp确保时区一致性，都使用Asia/Shanghai时区
        long currentTimeMillis = BdDateUtils.toTimestamp(BdDateUtils.now());
        long updatedAtMillis = BdDateUtils.toTimestamp(thread.getUpdatedAt());

        // 移除Math.abs()，确保时间顺序正确
        long diffInMilliseconds = currentTimeMillis - updatedAtMillis;

        // 如果updatedAt在未来，说明时间有问题，跳过处理
        if (diffInMilliseconds < 0) {
            log.warn("Thread {} updatedAt is in the future, skipping auto close check", thread.getUid());
            return -1;
        }

        // 转换为分钟
        return TimeUnit.MILLISECONDS.toMinutes(diffInMilliseconds);
    }

    /**
     * 处理自动关闭逻辑
     */
    private void handleAutoClose(ThreadEntity thread, long diffInMinutes) {
        // 需求：会话必须以客服的回复作为最后一条信息才能开始进入超时计时。
        // 若访客发送消息后客服一直不回复，则会话不会因 autoClose 自动结束。
        Optional<QueueMemberEntity> queueMemberOpt = queueMemberRestService.findByThreadUid(thread.getUid());
        if (queueMemberOpt.isPresent()) {
            QueueMemberEntity queueMember = queueMemberOpt.get();
            ZonedDateTime agentLastResponseAt = queueMember.getAgentLastResponseAt();
            ZonedDateTime visitorLastMessageAt = queueMember.getVisitorLastMessageAt();

            // 未有客服回复，不开启自动关闭计时
            if (agentLastResponseAt == null) {
                return;
            }
            // 访客最后消息晚于客服最后回复：说明客服尚未回复访客的最后一条消息，不应自动关闭
            if (visitorLastMessageAt != null && visitorLastMessageAt.isAfter(agentLastResponseAt)) {
                return;
            }

            long minutesSinceAgentReply = minutesSince(agentLastResponseAt);
            if (minutesSinceAgentReply < 0) {
                return;
            }
            diffInMinutes = minutesSinceAgentReply;
        }

        ServiceSettingsResponseVisitor settings = parseThreadSettings(thread);
        double autoCloseValue = getAutoCloseMinutes(settings);

        if (diffInMinutes > autoCloseValue) {
            threadRestService.autoClose(thread);
        }
    }

    /**
     * 解析线程设置
     */
    private ServiceSettingsResponseVisitor parseThreadSettings(ThreadEntity thread) {
        if (!StringUtils.hasText(thread.getExtra())) {
            return null;
        }

        try {
            return JSON.parseObject(thread.getExtra(), ServiceSettingsResponseVisitor.class);
        } catch (Exception e) {
            log.warn("Failed to parse thread extra JSON for thread {}: {}", thread.getUid(), e.getMessage());
            return null;
        }
    }

    /**
     * 获取自动关闭分钟数
     */
    private double getAutoCloseMinutes(ServiceSettingsResponseVisitor settings) {
        if (settings == null) {
            return 30.0; // 默认30分钟
        }

        Double autoCloseMinutes = settings.getAutoCloseMin();
        return (autoCloseMinutes != null) ? autoCloseMinutes : 30.0;
    }

    /**
     * 处理超时提醒逻辑
     */
    private void handleTimeoutReminder(ThreadEntity thread, long diffInMinutes) {
        UserProtobuf agentProtobuf = thread.getAgentProtobuf();
        if (agentProtobuf == null || !StringUtils.hasText(agentProtobuf.getUid())) {
            return;
        }

        Optional<AgentEntity> agentOpt = agentRestService.findByUid(agentProtobuf.getUid());
        if (!agentOpt.isPresent()) {
            return;
        }

        AgentEntity agent = agentOpt.get();
        // 需求：当访客发送消息后客服一直不回复，不自动结束会话，并对客服进行“回复超时提醒”。
        // 这里的超时基准应为“访客最后一条消息时间”，而不是 thread.updatedAt。
        Optional<QueueMemberEntity> queueMemberOpt = queueMemberRestService.findByThreadUid(thread.getUid());
        if (queueMemberOpt.isEmpty()) {
            return;
        }

        QueueMemberEntity queueMember = queueMemberOpt.get();
        // 已经标记超时则不重复发送提醒（前端可持续展示超时状态）
        if (queueMember.getAgentTimeout()) {
            return;
        }

        // 只有“访客最后消息需要客服回复”的场景才提醒
        if (!shouldSendTimeoutReminder(queueMember)) {
            return;
        }

        long pendingMinutes = minutesSince(queueMember.getVisitorLastMessageAt());
        if (pendingMinutes < 0) {
            return;
        }
        if (pendingMinutes <= agent.getTimeoutRemindTime()) {
            return;
        }

        sendRemindMessage(queueMember, thread, agent);
    }

    /**
     * 判断是否应该发送超时提醒
     */
    private boolean shouldSendTimeoutReminder(QueueMemberEntity queueMember) {
        // 访客最后发送消息时间为空，不需要提醒
        if (queueMember.getVisitorLastMessageAt() == null) {
            return false;
        }

        // 客服最后回复时间为空，需要提醒
        if (queueMember.getAgentLastResponseAt() == null) {
            return true;
        }

        // 访客最后发送消息时间 大于 客服最后回复时间，需要提醒
        return queueMember.getVisitorLastMessageAt().isAfter(queueMember.getAgentLastResponseAt());
    }

    private void sendRemindMessage(QueueMemberEntity queueMember, ThreadEntity thread, AgentEntity agent) {
        // 只设置首次超时时间，后续不再更新
        if (queueMember.getAgentTimeoutAt() == null) {
            queueMember.setAgentTimeoutAt(BdDateUtils.now());
            queueMember.setAgentTimeout(true);
        }
        // 更新超时次数
        queueMember.setAgentTimeoutCount(queueMember.getAgentTimeoutCount() + 1);
        // 保存队列成员信息
        queueMemberRestService.saveAsyncBestEffort(queueMember);
        // 发送会话超时提醒
        MessageProtobuf messageProtobuf = MessageUtils.createAgentReplyTimeoutMessage(thread,
                agent.getTimeoutRemindTip());
        messageSendService.sendProtobufMessage(messageProtobuf);
    }

    /**
     * 处理排队等待超时逻辑
     * @return true 如果会话已被处理（触发了离线留言），false 如果未处理
     */
    private boolean handleQueueWaitTimeout(ThreadEntity thread) {
        if (!thread.isQueuing()) {
            return false;
        }

        Optional<QueueMemberEntity> queueMemberOpt = queueMemberRestService.findByThreadUid(thread.getUid());
        if (!queueMemberOpt.isPresent()) {
            return false;
        }

        QueueMemberEntity queueMember = queueMemberOpt.get();
        if (Boolean.TRUE.equals(queueMember.getMessageLeave()) || queueMember.getVisitorEnqueueAt() == null) {
            return false;
        }

        QueueSettingsEntity queueSettings = resolveQueueSettings(thread);
        int maxWaitSeconds = resolveQueueMaxWaitSeconds(queueSettings);
        if (maxWaitSeconds <= 0) {
            return false;
        }

        long waitedSeconds = Duration.between(queueMember.getVisitorEnqueueAt(), BdDateUtils.now()).getSeconds();
        if (waitedSeconds < maxWaitSeconds) {
            return false;
        }

        triggerQueueLeaveMessage(thread, queueMember);
        return true;
    }

    public MessageProtobuf handleQueueOverflowLeaveMessage(ThreadEntity thread, QueueMemberEntity queueMember) {
        Assert.notNull(thread, "ThreadEntity must not be null");
        Assert.notNull(queueMember, "QueueMemberEntity must not be null");
        return triggerQueueLeaveMessage(thread, queueMember);
    }

    private QueueSettingsEntity resolveQueueSettings(ThreadEntity thread) {
        try {
            if (thread.isAgentType()) {
                String agentUid = TopicUtils.getAgentUidFromThreadTopic(thread.getTopic());
                return agentRestService.findByUid(agentUid)
                        .map(this::resolveQueueSettingsFromAgent)
                        .orElse(null);
            } else if (thread.isWorkgroupType()) {
                String workgroupUid = TopicUtils.getWorkgroupUidFromThreadTopic(thread.getTopic());
                return workgroupRestService.findByUid(workgroupUid)
                        .map(this::resolveQueueSettingsFromWorkgroup)
                        .orElse(null);
            }
        } catch (Exception e) {
            log.debug("Failed to resolve queue settings for thread {}: {}", thread.getUid(), e.getMessage());
        }
        return null;
    }

    private QueueSettingsEntity resolveQueueSettingsFromAgent(AgentEntity agent) {
        if (agent.getSettings() == null) {
            return null;
        }
        QueueSettingsEntity settings = agent.getSettings().getQueueSettings();
        if (settings == null) {
            settings = agent.getSettings().getDraftQueueSettings();
        }
        return settings;
    }

    private QueueSettingsEntity resolveQueueSettingsFromWorkgroup(WorkgroupEntity workgroup) {
        if (workgroup.getSettings() == null) {
            return null;
        }
        QueueSettingsEntity settings = workgroup.getSettings().getQueueSettings();
        if (settings == null) {
            settings = workgroup.getSettings().getDraftQueueSettings();
        }
        return settings;
    }

    private int resolveQueueMaxWaitSeconds(QueueSettingsEntity queueSettings) {
        if (queueSettings == null) {
            return QueueSettingsEntity.DEFAULT_MAX_WAIT_TIME_SECONDS;
        }
        return queueSettings.resolveMaxWaitTimeSeconds();
    }

    private MessageProtobuf triggerQueueLeaveMessage(ThreadEntity thread, QueueMemberEntity queueMember) {
        String leaveMessageTip = resolveLeaveMessageTip(thread);

        thread.setOffline().setContent(ThreadContent.of(MessageTypeEnum.LEAVE_MSG, leaveMessageTip, leaveMessageTip).toJson());
        ThreadEntity savedThread = threadRestService.save(thread);

        queueMember.setMessageLeave(true);
        queueMember.setMessageLeaveAt(BdDateUtils.now());
        queueMember.setVisitorLeavedAt(BdDateUtils.now());
        queueMemberRestService.saveAsyncBestEffort(queueMember);

        MessageEntity message = ThreadMessageUtil.getThreadOfflineMessage(leaveMessageTip, savedThread);
        messageRestService.save(message);
        MessageProtobuf protobuf = ServiceConvertUtils.convertToMessageProtobuf(message, savedThread);
        messageSendService.sendProtobufMessage(protobuf);
        return protobuf;
    }

    private String resolveLeaveMessageTip(ThreadEntity thread) {
        try {
            if (thread.isAgentType()) {
                String agentUid = TopicUtils.getAgentUidFromThreadTopic(thread.getTopic());
                return agentRestService.findByUid(agentUid)
                        .map(this::resolveLeaveMessageTip)
                        .orElse(I18Consts.I18N_MESSAGE_LEAVE_TIP);
            } else if (thread.isWorkgroupType()) {
                String workgroupUid = TopicUtils.getWorkgroupUidFromThreadTopic(thread.getTopic());
                return workgroupRestService.findByUid(workgroupUid)
                        .map(this::resolveLeaveMessageTip)
                        .orElse(I18Consts.I18N_MESSAGE_LEAVE_TIP);
            }
        } catch (Exception e) {
            log.debug("Failed to resolve leave message tip for thread {}: {}", thread.getUid(), e.getMessage());
        }
        return I18Consts.I18N_MESSAGE_LEAVE_TIP;
    }

    private String resolveLeaveMessageTip(AgentEntity agent) {
        if (agent.getSettings() != null
                && agent.getSettings().getMessageLeaveSettings() != null
                && StringUtils.hasText(agent.getSettings().getMessageLeaveSettings().getMessageLeaveTip())) {
            return agent.getSettings().getMessageLeaveSettings().getMessageLeaveTip();
        }
        return I18Consts.I18N_MESSAGE_LEAVE_TIP;
    }

    private String resolveLeaveMessageTip(WorkgroupEntity workgroup) {
        if (workgroup.getSettings() != null
                && workgroup.getSettings().getMessageLeaveSettings() != null
                && StringUtils.hasText(workgroup.getSettings().getMessageLeaveSettings().getMessageLeaveTip())) {
            return workgroup.getSettings().getMessageLeaveSettings().getMessageLeaveTip();
        }
        return I18Consts.I18N_MESSAGE_LEAVE_TIP;
    }
}
