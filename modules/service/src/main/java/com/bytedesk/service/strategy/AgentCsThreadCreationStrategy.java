/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-15 15:58:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-23 17:47:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.strategy;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.ai.robot.Robot;
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
import com.bytedesk.service.route.IRouteService;
import com.bytedesk.service.utils.ConvertServiceUtils;
import com.bytedesk.service.visitor.VisitorRequest;

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

    private final IRouteService routeService;

    private final UidUtils uidUtils;

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
                Robot robot = agent.getServiceSettings().getRobot();
                // 
                return routeService.routeRobot(visitorRequest, thread, robot);
            }
        }
        // 
        return routeService.routeAgent(visitorRequest, thread, agent);
    }

    // 是否存在未关闭的会话
    private Thread getProcessingThread(String topic) {
        // TODO: 到visitor thread表中拉取
        // 拉取未关闭会话
        Optional<Thread> threadOptional = threadService.findByTopicNotClosed(topic);
        if (threadOptional.isPresent()) {
            return threadOptional.get();
        }
        return null;
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

    private Thread getAgentThread(VisitorRequest visitorRequest, Agent agent, String topic) {
        // TODO: 到visitor thread表中拉取
        Thread thread = Thread.builder().build();
        Optional<Thread> threadOptional = threadService.findByTopic(topic);
        if (threadOptional.isPresent()) {
            thread = threadOptional.get();
        } else {
            // thread.setUid(uidUtils.getCacheSerialUid());
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
        // 强制生成新会话uid，代表新会话。便于会话跟踪计数统计
        thread.setUid(uidUtils.getUid());
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

}