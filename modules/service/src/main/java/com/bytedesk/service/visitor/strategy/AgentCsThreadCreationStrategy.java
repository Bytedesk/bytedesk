/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-15 15:58:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-11 08:50:53
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor.strategy;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.ai.robot.Robot;
import com.bytedesk.ai.utils.ConvertAiUtils;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageUtils;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadService;
import com.bytedesk.core.thread.ThreadStatusEnum;
import com.bytedesk.core.thread.ThreadTypeEnum;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.service.agent.Agent;
import com.bytedesk.service.agent.AgentService;
import com.bytedesk.service.utils.ConvertServiceUtils;
import com.bytedesk.service.visitor.VisitorRequest;

import jakarta.annotation.Nonnull;

import com.bytedesk.core.thread.Thread;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// 一对一客服对话
@Slf4j
@Component("agentCsThreadStrategy")
@AllArgsConstructor
public class AgentCsThreadCreationStrategy implements CsThreadCreationStrategy {

    private final AgentService agentService;

    private final ThreadService threadService;

    // private final MessageService messageService;

    private final UidUtils uidUtils;

    // private final ModelMapper modelMapper;

    @Override
    public MessageProtobuf createCsThread(VisitorRequest visitorRequest) {
        return createAgentCsThread(visitorRequest);
    }

    public MessageProtobuf createAgentCsThread(VisitorRequest visitorRequest) {
        //
        String agentUid = visitorRequest.getSid();
        //
        String topic = TopicUtils.formatOrgAgentThreadTopic(visitorRequest.getSid(), visitorRequest.getUid());
        // 是否已经存在进行中会话
        Thread thread = getProcessingThread(topic);
        if (thread != null && !visitorRequest.getForceAgent()) {
            log.info("Already have a processing thread " + JSON.toJSONString(thread));
            return getAgentProcessingMessage(visitorRequest, thread);
        }
        //
        Agent agent = agentService.findByUid(agentUid)
                .orElseThrow(() -> new RuntimeException("Agent uid " + agentUid + " not found"));
        //
        thread = getAgentThread(visitorRequest, agent, topic);

        // 未强制转人工的情况下，判断是否转机器人
        if (!visitorRequest.getForceAgent()) {
            // 判断是否需要转机器人
            Boolean isOffline = !agent.isConnected() || !agent.isAvailable();
            Boolean transferToRobot = agent.getServiceSettings().shouldTransferToRobot(isOffline);
            if (transferToRobot) {
                // 转机器人
                // TODO: 将robot设置为agent
                Robot robot = agent.getServiceSettings().getRobot();
                if (robot != null) {
                    // visitorRequest.setSid(robot.getUid());
                    // return robotCsThreadCreationStrategy.createCsThread(visitorRequest);
                    //
                    thread.setContent(robot.getServiceSettings().getWelcomeTip());
                    // 使用agent的serviceSettings配置
                    // thread.setExtra(JSON.toJSONString(ConvertAiUtils.convertToServiceSettingsResponseVisitor(
                    //         robot.getServiceSettings())));
                    UserProtobuf agenProtobuf = ConvertAiUtils.convertToUserProtobuf(robot);
                    // thread.setAgentProtobuf(agenProtobuf);
                    thread.setAgent(JSON.toJSONString(agenProtobuf));
                    // 
                    return getRobotMessage(visitorRequest, thread, agenProtobuf);
                    // 
                } else {
                    throw new RuntimeException("please set robot for " + agent.getNickname() + " in the admin panel first");
                }
            }
        }
        // TODO: 判断是否达到最大接待人数，如果达到则进入排队

        return getAgentMessage(visitorRequest, thread, agent);
    }

    // 是否存在未关闭的会话
    private Thread getProcessingThread(String topic) {
        // TODO: 到visitor thread表中拉取
        // 拉取未关闭会话
        Optional<Thread> threadOptional = threadService.findByTopicNotClosed(topic, "CLOSED");
        if (threadOptional.isPresent()) {
            return threadOptional.get();
        }
        return null;
    }

    private Thread getAgentThread(VisitorRequest visitorRequest, Agent agent, String topic) {
        // TODO: 到visitor thread表中拉取
        Thread thread = Thread.builder().build();
        Optional<Thread> threadOptional = threadService.findByTopic(topic);
        if (threadOptional.isPresent()) {
            // return threadOptional.get();
            thread = threadOptional.get();
        } else {
            //
            thread = Thread.builder().build();
            thread.setUid(uidUtils.getCacheSerialUid());
            thread.setTopic(topic);
            thread.setType(ThreadTypeEnum.AGENT.name());
            thread.setClient(ClientEnum.fromValue(visitorRequest.getClient()).name());
            //
            UserProtobuf visitor = ConvertServiceUtils.convertToUserProtobuf(visitorRequest);
            thread.setUser(JSON.toJSONString(visitor));
            //
            thread.setOwner(agent.getMember().getUser());
            thread.setOrgUid(agent.getOrgUid());
        }
        // 考虑到配置可能变化，更新配置
        thread.setExtra(JSON
                .toJSONString(
                        ConvertServiceUtils.convertToServiceSettingsResponseVisitor(agent.getServiceSettings())));
        // 考虑到客服信息发生变化，更新客服信息
        UserProtobuf agentProtobuf = ConvertServiceUtils.convertToUserProtobuf(agent);
        thread.setAgent(JSON.toJSONString(agentProtobuf));
        // 
        return thread;
    }

    private MessageProtobuf getAgentMessage(VisitorRequest visitorRequest, @Nonnull Thread thread, @Nonnull Agent agent) {
        //
        boolean isReenter = true;
        if (thread.getStatus() == ThreadStatusEnum.NORMAL.name()) {
            // 访客首次进入会话
            isReenter = false;
        }
        //
        if (!agent.isConnected() || !agent.isAvailable()) {
            // 离线状态永远显示离线提示语，不显示“继续会话”
            isReenter = false;
            // 客服离线 或 非接待状态
            thread.setContent(agent.getServiceSettings().getLeavemsgTip());
            thread.setStatus(ThreadStatusEnum.OFFLINE.name());
        } else {
            // 客服在线 且 接待状态
            thread.setUnreadCount(1);
            thread.setContent(agent.getServiceSettings().getWelcomeTip());
            // thread.setExtra(JSON.toJSONString(
            //         ConvertServiceUtils.convertToServiceSettingsResponseVisitor(agent.getServiceSettings())));
            // thread.setAgent(JSON.toJSONString(ConvertServiceUtils.convertToAgentResponseSimple(agent)));
            // if thread is closed, reopen it and then create a new message
            if (visitorRequest.getForceAgent()) {
                isReenter = false;
                thread.setStatus(ThreadStatusEnum.NORMAL.name());
            } else if (thread.isClosed()) {
                // 访客会话关闭之后，重新进入
                isReenter = false;
                thread.setStatus(ThreadStatusEnum.REOPEN.name());
            } else {
                thread.setStatus(isReenter ? ThreadStatusEnum.CONTINUE.name() : ThreadStatusEnum.NORMAL.name());
            }
        }
        threadService.save(thread);
        //
        // UserProtobuf user = modelMapper.map(agent, UserProtobuf.class);
        UserProtobuf user = ConvertServiceUtils.convertToUserProtobuf(agent);
        //
        MessageProtobuf messageProtobuf = ThreadMessageUtil.getThreadMessage(user, thread, isReenter);
        // 广播消息，由消息通道统一处理
        MessageUtils.notifyUser(messageProtobuf);

        return messageProtobuf;
    }

    private MessageProtobuf getRobotMessage(VisitorRequest visitorRequest, Thread thread, UserProtobuf user) {
        if (thread == null) {
            throw new RuntimeException("Thread cannot be null");
        }
        // 客服在线 且 接待状态
        thread.setUnreadCount(0);
        thread.setStatus(ThreadStatusEnum.NORMAL.name());
        threadService.save(thread);
        // log.info("getAgentProcessingMessage agent: {}", thread.getAgent());
        //
        MessageProtobuf messageProtobuf = ThreadMessageUtil.getThreadMessage(user, thread, false);
        // 广播消息，由消息通道统一处理
        // messageService.notifyUser(messageProtobuf);

        return messageProtobuf;
    }

    private MessageProtobuf getAgentProcessingMessage(VisitorRequest visitorRequest, Thread thread) {
        if (thread == null) {
            throw new RuntimeException("Thread cannot be null");
        }
        // 客服在线 且 接待状态
        thread.setUnreadCount(1);
        thread.setStatus(ThreadStatusEnum.CONTINUE.name());
        threadService.save(thread);
        //
        UserProtobuf user = JSON.parseObject(thread.getAgent(), UserProtobuf.class);
        //
        MessageProtobuf messageProtobuf = ThreadMessageUtil.getThreadMessage(user, thread, true);
        // 广播消息，由消息通道统一处理
        MessageUtils.notifyUser(messageProtobuf);

        return messageProtobuf;
    }

    // private MessageProtobuf notifyAgent(MessageProtobuf message) {
    // //
    // MessageProtobuf messageProtobuf = getAgentMessage(visitorRequest, thread,
    // agent);
    // // 广播消息，由消息通道统一处理
    // messageService.notifyUser(messageProtobuf);
    // //
    // // if (agent.isConnected() && agent.isAvailable()) {
    // // // notify agent - 通知客服
    // // notifyAgent(messageProtobuf);
    // // } else {
    // // // 离线状态
    // // }
    // // else if (agent.isAvailable()) {
    // // // TODO: 断开连接，但是接待状态，判断是否有客服移动端token，有则发送通知
    // // }

    // return messageProtobuf;
    // }

}