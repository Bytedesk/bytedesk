/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-29 13:00:33
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-03 14:46:53
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor_thread;

import java.time.ZonedDateTime;
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
import com.bytedesk.core.thread.ThreadTypeEnum;
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
        String topic = thread.getTopic();
        String content = "会话已结束";
        if (thread.getAutoClose()) {
            // 自动关闭会话
            Optional<QueueMemberEntity> queueMemberOptional = queueMemberRestService.findByThreadUid(thread.getUid());
            if (queueMemberOptional.isPresent()) {
                QueueMemberEntity queueMember = queueMemberOptional.get();
                queueMember.setSystemCloseAt(ZonedDateTime.now());
                queueMember.setSystemClose(true);
                queueMemberRestService.save(queueMember);
                if (queueMember.getAgentOffline()) {
                    // 客服离线，自动关闭会话，不发送自动关闭消息
                    return;
                }
            }
            // 自动关闭，根据会话类型显示提示语
            if (thread.getType().equals(ThreadTypeEnum.WORKGROUP.name())) {
                String workgroupUid = TopicUtils.getWorkgroupUidFromThreadTopic(topic);
                Optional<WorkgroupEntity> workgroupOptional = workgroupRestService.findByUid(workgroupUid);
                if (workgroupOptional.isPresent()) {
                    WorkgroupEntity workgroup = workgroupOptional.get();
                    content = workgroup.getServiceSettings().getAutoCloseTip();
                } else {
                    content = I18Consts.I18N_AUTO_CLOSE_TIP;// "会话已结束，感谢您的咨询，祝您生活愉快！";
                }
            } else if (thread.getType().equals(ThreadTypeEnum.AGENT.name())) {
                String agentUid = TopicUtils.getAgentUidFromThreadTopic(topic);
                Optional<AgentEntity> agentOptional = agentRestService.findByUid(agentUid);
                if (agentOptional.isPresent()) {
                    AgentEntity agent = agentOptional.get();
                    content = agent.getServiceSettings().getAutoCloseTip();
                } else {
                    content = I18Consts.I18N_AUTO_CLOSE_TIP;// "会话已结束，感谢您的咨询，祝您生活愉快！";
                }
            } else if (thread.getType().equals(ThreadTypeEnum.ROBOT.name())) {
                String robotUid = TopicUtils.getRobotUidFromThreadTopic(topic);
                Optional<RobotEntity> robotOptional = robotRestService.findByUid(robotUid);
                if (robotOptional.isPresent()) {
                    RobotEntity robot = robotOptional.get();
                    content = robot.getServiceSettings().getAutoCloseTip();
                } else {
                    content = I18Consts.I18N_AUTO_CLOSE_TIP;// "会话已结束，感谢您的咨询，祝您生活愉快！";
                }
            }
        } else {
            // 手动关闭会话
            Optional<QueueMemberEntity> queueMemberOptional = queueMemberRestService.findByThreadUid(thread.getUid());
            if (queueMemberOptional.isPresent()) {
                QueueMemberEntity queueMember = queueMemberOptional.get();
                queueMember.setAgentClosedAt(ZonedDateTime.now());
                queueMember.setAgentClose(true);
                queueMemberRestService.save(queueMember);
            }
            // 非自动关闭，客服手动关闭，显示客服关闭提示语
            if (thread.getType().equals(ThreadTypeEnum.WORKGROUP.name())) {
                String workgroupUid = TopicUtils.getWorkgroupUidFromThreadTopic(topic);
                Optional<WorkgroupEntity> workgroupOptional = workgroupRestService.findByUid(workgroupUid);
                if (workgroupOptional.isPresent()) {
                    WorkgroupEntity workgroup = workgroupOptional.get();
                    content = workgroup.getServiceSettings().getAgentCloseTip();
                } else {
                    content = I18Consts.I18N_AGENT_CLOSE_TIP;// "会话已结束，感谢您的咨询，祝您生活愉快！";
                }
            } else if (thread.getType().equals(ThreadTypeEnum.AGENT.name())) {
                String agentUid = TopicUtils.getAgentUidFromThreadTopic(topic);
                Optional<AgentEntity> agentOptional = agentRestService.findByUid(agentUid);
                if (agentOptional.isPresent()) {
                    AgentEntity agent = agentOptional.get();
                    content = agent.getServiceSettings().getAgentCloseTip();
                } else {
                    content = I18Consts.I18N_AGENT_CLOSE_TIP;// "会话已结束，感谢您的咨询，祝您生活愉快！";
                }
            }
        }

        // 发送消息
        MessageProtobuf messageProtobuf = thread.getAutoClose()
                ? MessageUtils.createAutoCloseMessage(thread, content)
                : MessageUtils.createAgentCloseMessage(thread, content);
        messageSendService.sendProtobufMessage(messageProtobuf);
    }

    @EventListener
    public void onQuartzOneMinEvent(QuartzOneMinEvent event) {
        // log.info("visitor_thread quartz one min event: " + event);
        List<ThreadEntity> threads = threadRestService.findServiceThreadStateStarted();
        // auto close thread
        visitorThreadService.autoRemindAgentOrCloseThread(threads);
        // 触发器逻辑
        // 查找所有未关闭的会话，如果超过一定时间未回复，则判断是否触发自动回复
        threads.forEach(thread -> {
            // 使用BdDateUtils.toTimestamp确保时区一致性，都使用Asia/Shanghai时区
            long currentTimeMillis = BdDateUtils.toTimestamp(ZonedDateTime.now());
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
            String topic = thread.getTopic();
            // log.info("visitor_thread quartz one min event thread: " + thread.getUid());
            if (thread.getType().equals(ThreadTypeEnum.WORKGROUP.name())) {
                String workgroupUid = TopicUtils.getWorkgroupUidFromThreadTopic(topic);
                Optional<WorkgroupEntity> workgroupOptional = workgroupRestService.findByUid(workgroupUid);
                if (workgroupOptional.isPresent()) {
                    WorkgroupEntity workgroup = workgroupOptional.get();
                    if (workgroup.getServiceSettings().getEnableProactiveTrigger()
                            && diffInSeconds > workgroup.getServiceSettings().getNoResponseTimeout()) {
                        // 触发自动回复
                        log.info("visitor_thread quartz one min event thread: " + thread.getUid()
                                + " trigger workgroup proactive message");
                        MessageProtobuf messageProtobuf = MessageUtils.createSystemMessage(thread,
                                workgroup.getServiceSettings().getProactiveMessage());
                        messageSendService.sendProtobufMessage(messageProtobuf);
                    }
                }
            } else if (thread.getType().equals(ThreadTypeEnum.AGENT.name())) {
                String agentUid = TopicUtils.getAgentUidFromThreadTopic(topic);
                Optional<AgentEntity> agentOptional = agentRestService.findByUid(agentUid);
                if (agentOptional.isPresent()) {
                    AgentEntity agent = agentOptional.get();
                    if (agent.getServiceSettings().getEnableProactiveTrigger()
                            && diffInSeconds > agent.getServiceSettings().getNoResponseTimeout()) {
                        // 触发自动回复
                        log.info("visitor_thread quartz one min event thread: " + thread.getUid()
                                + " trigger agent proactive message");
                        MessageProtobuf messageProtobuf = MessageUtils.createSystemMessage(thread,
                                agent.getServiceSettings().getProactiveMessage());
                        messageSendService.sendProtobufMessage(messageProtobuf);
                    }
                }
            } else if (thread.getType().equals(ThreadTypeEnum.ROBOT.name())) {
                String robotUid = TopicUtils.getRobotUidFromThreadTopic(topic);
                Optional<RobotEntity> robotOptional = robotRestService.findByUid(robotUid);
                if (robotOptional.isPresent()) {
                    RobotEntity robot = robotOptional.get();
                    if (robot.getServiceSettings().getEnableProactiveTrigger()
                            && diffInSeconds > robot.getServiceSettings().getNoResponseTimeout()) {
                        // 触发自动回复
                        log.info("visitor_thread quartz one min event thread: " + thread.getUid()
                                + " trigger robot proactive message");
                        MessageProtobuf messageProtobuf = MessageUtils.createSystemMessage(thread,
                                robot.getServiceSettings().getProactiveMessage());
                        messageSendService.sendProtobufMessage(messageProtobuf);
                    }
                }
            }
        });
    }

}
