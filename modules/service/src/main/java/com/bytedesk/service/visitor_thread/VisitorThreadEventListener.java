/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-29 13:00:33
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-01 13:47:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor_thread;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.thread.event.ThreadCloseEvent;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.AgentRestService;
import com.bytedesk.service.queue_member.QueueMemberEntity;
import com.bytedesk.service.queue_member.QueueMemberRestService;
import com.bytedesk.service.workgroup.WorkgroupEntity;
import com.bytedesk.service.workgroup.WorkgroupRestService;
import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.ai.robot.RobotRestService;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageUtils;
import com.bytedesk.core.quartz.event.QuartzOneMinEvent;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.enums.ThreadTypeEnum;
import com.bytedesk.core.utils.BdDateUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class VisitorThreadEventListener {

    private final VisitorThreadService visitorThreadService;

    private final WorkgroupRestService workgroupRestService;

    private final AgentRestService agentRestService;

    private final RobotRestService robotRestService;

    private final IMessageSendService messageSendService;

    private final ThreadRestService threadRestService;

    private final QueueMemberRestService queueMemberRestService;

    @EventListener
    public void onThreadCloseEvent(ThreadCloseEvent event) {
        ThreadEntity thread = event.getThread();
        log.info("visitor onThreadCloseEvent: {}", thread.getUid());
        
        boolean autoClose = thread.getAutoClose();
        
        // 更新队列成员状态
        updateQueueMemberOnClose(thread, autoClose);
        
        // 如果是自动关闭且客服离线，不发送消息
        if (autoClose && isAgentOffline(thread)) {
            return;
        }
        
        // 获取关闭提示语
        String content = getCloseTip(thread, autoClose);
        
        // 发送消息
        MessageProtobuf messageProtobuf = autoClose
                ? MessageUtils.createAutoCloseMessage(thread, content)
                : MessageUtils.createAgentCloseMessage(thread, content);
        messageSendService.sendProtobufMessage(messageProtobuf);
    }

    @EventListener
    public void onQuartzOneMinEvent(QuartzOneMinEvent event) {
        // log.info("visitor_thread quartz one min event: " + event);
        List<ThreadEntity> threads = threadRestService.findServiceThreadStateStarted();
        
        // 自动关闭线程
        visitorThreadService.autoRemindAgentOrCloseThread(threads);
        
        // 处理主动触发逻辑
        threads.forEach(this::processProactiveTrigger);
    }

    /**
     * 处理单个线程的主动触发逻辑
     */
    private void processProactiveTrigger(ThreadEntity thread) {
        // 使用BdDateUtils.toTimestamp确保时区一致性，都使用Asia/Shanghai时区
        long currentTimeMillis = BdDateUtils.toTimestamp(BdDateUtils.now());
        long updatedAtMillis = BdDateUtils.toTimestamp(thread.getUpdatedAt());
        
        // 移除Math.abs()，确保时间顺序正确
        long diffInMilliseconds = currentTimeMillis - updatedAtMillis;
        
        // 如果updatedAt在未来，说明时间有问题，跳过处理
        if (diffInMilliseconds < 0) {
            log.warn("Thread {} updatedAt is in the future, skipping proactive trigger check", thread.getUid());
            return;
        }
        
        // Convert to seconds
        long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(diffInMilliseconds);
        
        // 处理主动触发
        handleProactiveTrigger(thread, diffInSeconds);
    }
    

    /**
     * 更新队列成员状态
     */
    private void updateQueueMemberOnClose(ThreadEntity thread, boolean autoClose) {
        Optional<QueueMemberEntity> queueMemberOptional = queueMemberRestService.findByThreadUid(thread.getUid());
        if (queueMemberOptional.isPresent()) {
            QueueMemberEntity queueMember = queueMemberOptional.get();
            if (autoClose) {
                queueMember.setSystemClosedAt(BdDateUtils.now());
                queueMember.setSystemClose(true);
            } else {
                queueMember.setAgentClosedAt(BdDateUtils.now());
                queueMember.setAgentClose(true);
            }
            queueMemberRestService.save(queueMember);
        }
    }

    /**
     * 检查客服是否离线
     */
    private boolean isAgentOffline(ThreadEntity thread) {
        Optional<QueueMemberEntity> queueMemberOptional = queueMemberRestService.findByThreadUid(thread.getUid());
        return queueMemberOptional.isPresent() && queueMemberOptional.get().getAgentOffline();
    }

    /**
     * 获取关闭提示语
     */
    private String getCloseTip(ThreadEntity thread, boolean autoClose) {
        String topic = thread.getTopic();
        
        if (thread.getType().equals(ThreadTypeEnum.WORKGROUP.name())) {
            return getWorkgroupCloseTip(topic, autoClose);
        } else if (thread.getType().equals(ThreadTypeEnum.AGENT.name())) {
            return getAgentCloseTip(topic, autoClose);
        } else if (thread.getType().equals(ThreadTypeEnum.ROBOT.name())) {
            return getRobotCloseTip(topic);
        }
        
        return "会话已结束";
    }

    /**
     * 获取工作组关闭提示语
     */
    private String getWorkgroupCloseTip(String topic, boolean autoClose) {
        String workgroupUid = TopicUtils.getWorkgroupUidFromThreadTopic(topic);
        Optional<WorkgroupEntity> workgroupOptional = workgroupRestService.findByUid(workgroupUid);
        
        if (workgroupOptional.isPresent()) {
            WorkgroupEntity workgroup = workgroupOptional.get();
            if (workgroup.getSettings() != null && workgroup.getSettings().getServiceSettings() != null) {
                return autoClose 
                    ? workgroup.getSettings().getServiceSettings().getAutoCloseTip()
                    : workgroup.getSettings().getServiceSettings().getAgentCloseTip();
            }
        }
        
        return autoClose ? I18Consts.I18N_AUTO_CLOSE_TIP : I18Consts.I18N_AGENT_CLOSE_TIP;
    }

    /**
     * 获取客服关闭提示语
     */
    private String getAgentCloseTip(String topic, boolean autoClose) {
        String agentUid = TopicUtils.getAgentUidFromThreadTopic(topic);
        Optional<AgentEntity> agentOptional = agentRestService.findByUid(agentUid);
        
        if (agentOptional.isPresent()) {
            AgentEntity agent = agentOptional.get();
            if (agent.getSettings() != null && agent.getSettings().getServiceSettings() != null) {
                return autoClose 
                    ? agent.getSettings().getServiceSettings().getAutoCloseTip()
                    : agent.getSettings().getServiceSettings().getAgentCloseTip();
            }
        }
        
        return autoClose ? I18Consts.I18N_AUTO_CLOSE_TIP : I18Consts.I18N_AGENT_CLOSE_TIP;
    }

    /**
     * 获取机器人关闭提示语
     */
    private String getRobotCloseTip(String topic) {
        String robotUid = TopicUtils.getRobotUidFromThreadTopic(topic);
        Optional<RobotEntity> robotOptional = robotRestService.findByUid(robotUid);
        
        if (robotOptional.isPresent()) {
            RobotEntity robot = robotOptional.get();
            if (robot.getSettings() != null && robot.getSettings().getServiceSettings() != null) {
                return robot.getSettings().getServiceSettings().getAutoCloseTip();
            }
        }
        
        return I18Consts.I18N_AUTO_CLOSE_TIP;
    }

    /**
     * 处理主动触发消息
     */
    private void handleProactiveTrigger(ThreadEntity thread, long diffInSeconds) {
        String topic = thread.getTopic();
        
        if (thread.getType().equals(ThreadTypeEnum.WORKGROUP.name())) {
            handleWorkgroupProactiveTrigger(thread, topic, diffInSeconds);
        } else if (thread.getType().equals(ThreadTypeEnum.AGENT.name())) {
            handleAgentProactiveTrigger(thread, topic, diffInSeconds);
        } else if (thread.getType().equals(ThreadTypeEnum.ROBOT.name())) {
            handleRobotProactiveTrigger(thread, topic, diffInSeconds);
        }
    }

    /**
     * 处理工作组主动触发
     */
    private void handleWorkgroupProactiveTrigger(ThreadEntity thread, String topic, long diffInSeconds) {
        String workgroupUid = TopicUtils.getWorkgroupUidFromThreadTopic(topic);
        Optional<WorkgroupEntity> workgroupOptional = workgroupRestService.findByUid(workgroupUid);
        
        if (workgroupOptional.isPresent()) {
            WorkgroupEntity workgroup = workgroupOptional.get();
            if (workgroup.getSettings() != null && workgroup.getSettings().getServiceSettings() != null) {
                if (workgroup.getSettings().getServiceSettings().getEnableProactiveTrigger()
                        && diffInSeconds > workgroup.getSettings().getServiceSettings().getNoResponseTimeout()) {
                    log.info("visitor_thread quartz one min event thread: {} trigger workgroup proactive message", 
                        thread.getUid());
                    MessageProtobuf messageProtobuf = MessageUtils.createSystemMessage(thread,
                            workgroup.getSettings().getServiceSettings().getProactiveMessage());
                    messageSendService.sendProtobufMessage(messageProtobuf);
                }
            }
        }
    }

    /**
     * 处理客服主动触发
     */
    private void handleAgentProactiveTrigger(ThreadEntity thread, String topic, long diffInSeconds) {
        String agentUid = TopicUtils.getAgentUidFromThreadTopic(topic);
        Optional<AgentEntity> agentOptional = agentRestService.findByUid(agentUid);
        
        if (agentOptional.isPresent()) {
            AgentEntity agent = agentOptional.get();
            if (agent.getSettings() != null && agent.getSettings().getServiceSettings() != null) {
                if (agent.getSettings().getServiceSettings().getEnableProactiveTrigger()
                        && diffInSeconds > agent.getSettings().getServiceSettings().getNoResponseTimeout()) {
                    log.info("visitor_thread quartz one min event thread: {} trigger agent proactive message", 
                        thread.getUid());
                    MessageProtobuf messageProtobuf = MessageUtils.createSystemMessage(thread,
                            agent.getSettings().getServiceSettings().getProactiveMessage());
                    messageSendService.sendProtobufMessage(messageProtobuf);
                }
            }
        }
    }

    /**
     * 处理机器人主动触发
     */
    private void handleRobotProactiveTrigger(ThreadEntity thread, String topic, long diffInSeconds) {
        String robotUid = TopicUtils.getRobotUidFromThreadTopic(topic);
        Optional<RobotEntity> robotOptional = robotRestService.findByUid(robotUid);
        
        if (robotOptional.isPresent()) {
            RobotEntity robot = robotOptional.get();
            if (robot.getSettings() != null && robot.getSettings().getServiceSettings() != null) {
                if (robot.getSettings().getServiceSettings().getEnableProactiveTrigger()
                        && diffInSeconds > robot.getSettings().getServiceSettings().getNoResponseTimeout()) {
                    log.info("visitor_thread quartz one min event thread: {} trigger robot proactive message", 
                        thread.getUid());
                    MessageProtobuf messageProtobuf = MessageUtils.createSystemMessage(thread,
                            robot.getSettings().getServiceSettings().getProactiveMessage());
                    messageSendService.sendProtobufMessage(messageProtobuf);
                }
            }
        }
    }

}
