/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-15 15:58:23
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-11 08:50:05
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
import com.bytedesk.service.utils.ConvertServiceUtils;
import com.bytedesk.service.visitor.VisitorRequest;
import com.bytedesk.service.workgroup.Workgroup;
import com.bytedesk.service.workgroup.WorkgroupService;
import com.bytedesk.core.thread.Thread;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// 技能组会话
@Slf4j
@Component("workgroupCsThreadStrategy")
@AllArgsConstructor
public class WorkgroupCsThreadCreationStrategy implements CsThreadCreationStrategy {

    private final WorkgroupService workgroupService;

    private final ThreadService threadService;

    private final UidUtils uidUtils;

    @Override
    public MessageProtobuf createCsThread(VisitorRequest visitorRequest) {
        return createWorkgroupCsThread(visitorRequest);
    }

    public MessageProtobuf createWorkgroupCsThread(VisitorRequest visitorRequest) {
        //
        String workgroupUid = visitorRequest.getSid();
        //
        String topic = TopicUtils.formatOrgWorkgroupThreadTopic(workgroupUid, visitorRequest.getUid());
        // 是否已经存在进行中会话
        Thread thread = getProcessingThread(topic);
        if (thread != null && !visitorRequest.getForceAgent()) {
            log.info("Already have a processing thread " + JSON.toJSONString(thread));
            return getWorkgroupProcessingMessage(visitorRequest, thread);
        }
        //
        Workgroup workgroup = workgroupService.findByUid(workgroupUid)
                .orElseThrow(() -> new RuntimeException("Workgroup uid " + workgroupUid + " not found"));
        //
        thread = getWorkgroupThread(visitorRequest, workgroup, topic);

        // 未强制转人工的情况下，判断是否转机器人
        if (!visitorRequest.getForceAgent()) {
            Boolean isOffline = !workgroup.isConnected();
            Boolean transferToRobot = workgroup.getServiceSettings().shouldTransferToRobot(isOffline);
            if (transferToRobot) {
                // 转机器人
                // TODO: 将robot设置为agent
                Robot robot = workgroup.getServiceSettings().getRobot();
                if (robot != null) {
                    // 
                    thread.setContent(robot.getServiceSettings().getWelcomeTip());
                    // 
                    UserProtobuf agenProtobuf = ConvertAiUtils.convertToUserProtobuf(robot);
                    thread.setAgent(JSON.toJSONString(agenProtobuf));
                    //
                    return getRobotMessage(visitorRequest, thread, agenProtobuf);
                } else {
                    throw new RuntimeException("route " + workgroupUid + " to a robot");
                }
            }
        }
        // 下面人工接待
        // TODO: 所有客服都达到最大接待人数，则进入排队
        // TODO: 排队人数动态变化，随时通知访客端

        if (workgroup.getAgents().isEmpty()) {
            throw new RuntimeException("No agents found in workgroup with uid " + workgroupUid);
        }

        // TODO: 首先完善各个agent的统计数据，比如接待量、等待时长等
        Agent agent = workgroup.nextAgent();
        if (agent == null) {
            throw new RuntimeException("No available agent found in workgroup with uid " + workgroupUid);
        }
        // 
        thread.setOwner(agent.getMember().getUser());
        UserProtobuf agentProtobuf = ConvertServiceUtils.convertToUserProtobuf(agent);
        thread.setAgent(JSON.toJSONString(agentProtobuf));
        //
        return getWorkgroupMessage(visitorRequest, thread, agent, workgroup);
    }

    // 是否存在未关闭的会话
    private Thread getProcessingThread(String topic) {
        // TODO: 到visitor thread表中拉取
        // 拉取未关闭会话
        Optional<Thread> threadOptional = threadService.findByWgTopicNotClosed(topic);
        if (threadOptional.isPresent()) {
            return threadOptional.get();
        }
        return null;
    }

    private Thread getWorkgroupThread(VisitorRequest visitorRequest, Workgroup workgroup, String topic) {
        //
        Thread thread = Thread.builder().build();
        // TODO: 到visitor thread表中拉取
        Optional<Thread> threadOptional = threadService.findByTopic(topic);
        if (threadOptional.isPresent()) {
            thread = threadOptional.get();
        } else {
            thread.setUid(uidUtils.getCacheSerialUid());
            thread.setTopic(topic);
            thread.setType(ThreadTypeEnum.WORKGROUP.name());
            thread.setClient(ClientEnum.fromValue(visitorRequest.getClient()).name());
            thread.setOrgUid(workgroup.getOrgUid());
        }
        //
        UserProtobuf visitor = ConvertServiceUtils.convertToUserProtobuf(visitorRequest);
        thread.setUser(JSON.toJSONString(visitor));
        // 
        thread.setExtra(JSON.toJSONString(
                ConvertServiceUtils.convertToServiceSettingsResponseVisitor(workgroup.getServiceSettings())));
        //
        return thread;
    }

    private MessageProtobuf getWorkgroupMessage(VisitorRequest visitorRequest, Thread thread, Agent agent, Workgroup workgroup) {
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
            thread.setContent(workgroup.getServiceSettings().getLeavemsgTip());
            thread.setStatus(ThreadStatusEnum.OFFLINE.name());
        } else {
            // 客服在线 且 接待状态
            thread.setUnreadCount(1);
            thread.setContent(workgroup.getServiceSettings().getWelcomeTip());
            // thread.setAgent(JSON.toJSONString(ConvertServiceUtils.convertToWorkgroupResponseSimple(workgroup)));
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

    private MessageProtobuf getWorkgroupProcessingMessage(VisitorRequest visitorRequest, Thread thread) {
        if (thread == null) {
            throw new RuntimeException("Thread cannot be null");
        }
        //
        thread.setUnreadCount(1);
        thread.setStatus(ThreadStatusEnum.CONTINUE.name());
        threadService.save(thread);
        //
        UserProtobuf user = JSON.parseObject(thread.getAgent(), UserProtobuf.class);
        log.info("getWorkgroupProcessingMessage user: {}, agent {}", user.toString(), thread.getAgent());
        //
        MessageProtobuf messageProtobuf = ThreadMessageUtil.getThreadMessage(user, thread, true);
        // 广播消息，由消息通道统一处理
        MessageUtils.notifyUser(messageProtobuf);

        return messageProtobuf;
    }

    // // 广播消息，由消息通道统一处理
    // messageService.notifyUser(messageProtobuf);
    //
    // if (agent.isConnected() && agent.isAvailable()) {
    // log.info("agent is connected and available");
    // // notify agent - 通知客服
    // notifyAgent(messageProtobuf);
    // }
    // else if (agent.isAvailable()) {
    // // TODO: 断开连接，但是接待状态，判断是否有客服移动端token，有则发送通知
    // log.info("agent is available");
    // cacheService.pushForPersist(JSON.toJSONString(messageProtobuf));
    // } else {
    // cacheService.pushForPersist(JSON.toJSONString(messageProtobuf));
    // }
    //
    // return messageProtobuf;

}
