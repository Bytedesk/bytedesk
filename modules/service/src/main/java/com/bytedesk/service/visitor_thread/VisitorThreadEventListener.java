/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-29 13:00:33
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-19 11:09:12
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor_thread;

import java.util.Optional;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.thread.event.ThreadCloseEvent;
import com.bytedesk.core.thread.event.ThreadCreateEvent;
import com.bytedesk.core.thread.event.ThreadUpdateEvent;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.AgentRestService;
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
import com.bytedesk.core.thread.ThreadTypeEnum;

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

    // private final UidUtils uidUtils;

    private final IMessageSendService messageSendService;

    @EventListener
    public void onThreadCreateEvent(ThreadCreateEvent event) {
        ThreadEntity thread = event.getThread();
        log.info("visitor ThreadCreateEvent: {}, type {}", thread.getUid(), thread.getType());
        // 仅同步客服会话
        if (thread.isCustomerService()) {
            visitorThreadService.copyFromThread(event.getThread());
        } else {
            log.info("visitor ThreadCreateEvent not isCustomerService: {}, type {}", thread.getUid(), thread.getType());
        }
    }

    @EventListener
    public void onThreadUpdateEvent(ThreadUpdateEvent event) {
        ThreadEntity thread = event.getThread();
        log.info("visitor onThreadUpdateEvent: {}", thread.getUid());
        // 更新visitor_thread表
        if (thread.isCustomerService()) {
            visitorThreadService.update(event.getThread());
        }
    }

    @EventListener
    public void onThreadCloseEvent(ThreadCloseEvent event) {
        ThreadEntity thread = event.getThread();
        log.info("visitor onThreadCloseEvent: {}", thread.getUid());
        String topic = thread.getTopic();
        String content = "会话已结束";
        if (thread.isAutoClose()) {
            // 自动关闭，根据会话类型显示提示语
            if (thread.getType().equals(ThreadTypeEnum.WORKGROUP.name())) {
                String workgroupUid = TopicUtils.getWorkgroupUidFromThreadTopic(topic);
                Optional<WorkgroupEntity> workgroupOptional = workgroupRestService.findByUid(workgroupUid);
                if (workgroupOptional.isPresent()) {
                    WorkgroupEntity workgroup = workgroupOptional.get();
                    content = workgroup.getServiceSettings().getAutoCloseTip();
                } else {
                    content = I18Consts.I18N_AUTO_CLOSE_TIP;//"会话已结束，感谢您的咨询，祝您生活愉快！";
                }
            } else if (thread.getType().equals(ThreadTypeEnum.AGENT.name())) {
                String agentUid = TopicUtils.getAgentUidFromThreadTopic(topic);
                Optional<AgentEntity> agentOptional = agentRestService.findByUid(agentUid);
                if (agentOptional.isPresent()) {
                    AgentEntity agent = agentOptional.get();
                    content = agent.getServiceSettings().getAutoCloseTip();
                } else {
                    content = I18Consts.I18N_AUTO_CLOSE_TIP;//"会话已结束，感谢您的咨询，祝您生活愉快！";
                }
            } else if (thread.getType().equals(ThreadTypeEnum.ROBOT.name())) {
                String robotUid = TopicUtils.getRobotUidFromThreadTopic(topic);
                Optional<RobotEntity> robotOptional = robotRestService.findByUid(robotUid);
                if (robotOptional.isPresent()) {
                    RobotEntity robot = robotOptional.get();
                    content = robot.getServiceSettings().getAutoCloseTip();
                } else {
                    content = I18Consts.I18N_AUTO_CLOSE_TIP;//"会话已结束，感谢您的咨询，祝您生活愉快！";
                }
            }
        } else {
            // 非自动关闭，客服手动关闭，显示客服关闭提示语
            if (thread.getType().equals(ThreadTypeEnum.WORKGROUP.name())) {
                String workgroupUid = TopicUtils.getWorkgroupUidFromThreadTopic(topic);
                Optional<WorkgroupEntity> workgroupOptional = workgroupRestService.findByUid(workgroupUid);
                if (workgroupOptional.isPresent()) {
                    WorkgroupEntity workgroup = workgroupOptional.get();
                    content = workgroup.getServiceSettings().getAgentCloseTip();
                } else {
                    content = I18Consts.I18N_AGENT_CLOSE_TIP;//"会话已结束，感谢您的咨询，祝您生活愉快！";
                }
            } else if (thread.getType().equals(ThreadTypeEnum.AGENT.name())) {
                String agentUid = TopicUtils.getAgentUidFromThreadTopic(topic);
                Optional<AgentEntity> agentOptional = agentRestService.findByUid(agentUid);
                if (agentOptional.isPresent()) {
                    AgentEntity agent = agentOptional.get();
                    content = agent.getServiceSettings().getAgentCloseTip();
                } else {
                    content = I18Consts.I18N_AGENT_CLOSE_TIP;//"会话已结束，感谢您的咨询，祝您生活愉快！";
                }
            }
        }

        // 发送消息
        MessageProtobuf messageProtobuf = thread.isAutoClose() 
            ? MessageUtils.createAutoCloseMessage(thread,content) 
            : MessageUtils.createAgentCloseMessage(thread, content);
        messageSendService.sendProtobufMessage(messageProtobuf);
    }

    @EventListener
    public void onQuartzOneMinEvent(QuartzOneMinEvent event) {
        // log.info("visitor_thread quartz one min event: " + event);
        // auto close thread
        visitorThreadService.autoCloseThread();
        // TODO: 触发器逻辑
        
    }

}
