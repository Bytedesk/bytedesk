/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-17 23:12:38
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor;

import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
// import org.springframework.util.SerializationUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.ai.robot.Robot;
import com.bytedesk.ai.robot.RobotService;
import com.bytedesk.ai.utils.ConvertAiUtils;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.event.BytedeskEventPublisher;
import com.bytedesk.core.ip.IpService;
import com.bytedesk.core.message.Message;
import com.bytedesk.core.message.MessageNotify;
import com.bytedesk.core.message.MessageResponse;
import com.bytedesk.core.message.MessageService;
import com.bytedesk.core.message.MessageStatusEnum;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.rbac.user.UserResponseSimple;
import com.bytedesk.core.thread.Thread;
import com.bytedesk.core.thread.ThreadService;
import com.bytedesk.core.thread.ThreadTypeEnum;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.service.agent.Agent;
import com.bytedesk.service.agent.AgentExceptionRobot;
import com.bytedesk.service.agent.AgentService;
import com.bytedesk.service.utils.ConvertServiceUtils;
import com.bytedesk.service.workgroup.Workgroup;
import com.bytedesk.service.workgroup.WorkgroupService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class VisitorService {

    private final VisitorRepository visitorRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final IpService ipService;

    private final ThreadService threadService;

    private final AgentService agentService;

    private final WorkgroupService workgroupService;

    private final RobotService robotService;

    private final MessageService messageService;

    private final BytedeskEventPublisher bytedeskEventPublisher;


    public VisitorResponse query(VisitorRequest visitorRequest) {

        Optional<Visitor> visitorOptional = findByUid(visitorRequest.getUid());
        if (!visitorOptional.isPresent()) {
            throw new RuntimeException("visitor not found");
        }

        return ConvertServiceUtils.convertToVisitorResponse(visitorOptional.get());
    }

    /**
     * create visitor record
     * 
     * @param visitorRequest
     * @return
     */
    public VisitorResponseSimple create(VisitorRequest visitorRequest, HttpServletRequest request) {

        String uid = visitorRequest.getUid();
        log.info("visitor init, uid: {}", uid);
        //
        Visitor visitor = findByUid(uid).orElse(null);
        if (visitor != null) {
            return ConvertServiceUtils.convertToVisitorResponseSimple(visitor);
        }
        //
        if (!StringUtils.hasText(visitorRequest.getNickname())) {
            visitorRequest.setNickname(ipService.createVisitorNickname(request));
        }
        if (!StringUtils.hasText(visitorRequest.getAvatar())) {
            visitorRequest.setAvatar(AvatarConsts.DEFAULT_VISITOR_AVATAR_URL);
        }
        //
        String ip = ipService.getIp(request);
        if (ip != null) {
            visitorRequest.setIp(ip);
            visitorRequest.setIpLocation(ipService.getIpLocation(ip));
        }
        // 
        visitor = modelMapper.map(visitorRequest, Visitor.class);
        visitor.setUid(uidUtils.getCacheSerialUid());
        visitor.setClient(ClientEnum.fromValue(visitorRequest.getClient()));
        // TODO: orgUid
        //
        return ConvertServiceUtils.convertToVisitorResponseSimple(save(visitor));
    }

    /** */
    public MessageResponse createCustomerServiceThread(VisitorRequest visitorRequest) {
        //
        String topic = visitorRequest.formatTopic();
        ThreadTypeEnum type = visitorRequest.formatType();
        String sid = visitorRequest.getSid();
        //
        if (type.equals(ThreadTypeEnum.APPOINTED)) {
            // 一对一客服
            return createAgentCsThread(visitorRequest, topic, type, sid);
            //
        } else if (type.equals(ThreadTypeEnum.WORKGROUP)) {
            // 技能组
            return createWorkgroupCsThread(visitorRequest, topic, type, sid);
            //
        } else if (type.equals(ThreadTypeEnum.ROBOT)) {
            // 机器人对话
            return createRobotCsThread(visitorRequest, topic, type, sid);
            //
        } else {
            throw new RuntimeException("Thread type " + type.name() + " not supported");
        }
    }

    //////////////////// Agent/////////////////////

    public MessageResponse createAgentCsThread(VisitorRequest visitorRequest, String topic, ThreadTypeEnum type,
            String sid) {

        Agent agent = agentService.findByUid(sid)
                .orElseThrow(() -> new RuntimeException("Agent uid " + sid + " not found"));
        if (agent.getServiceSettings().isDefaultRobot()) {
            // 默认转机器人优先接待
            Robot robot = agent.getServiceSettings().getRobot();
            if (robot != null) {
                //
                Thread thread = getRobotThread(visitorRequest, topic, type, robot);
                //
                return getRobotMessage(visitorRequest, thread, robot);
            } else {
                throw new AgentExceptionRobot("route " + sid + " to a robot");
            }
        } else if (agent.getServiceSettings().isOfflineRobot()) {
            // TODO: 人工离线期间转机器人
        } else if (agent.getServiceSettings().isNonWorktimeRobot()) {
            // TODO: 非工作时间转接机器人
        }
        //
        Thread thread = getAgentThread(visitorRequest, topic, type, agent);
        //
        MessageResponse messageResponse = getAgentMessage(visitorRequest, thread, agent);
        //
        if (agent.isConnected() && agent.isAvailable()) {
            // notify agent - 通知客服
            notifyAgent(thread, messageResponse);
        } else if (agent.isAvailable()) {
            // TODO: 断开连接，但是接待状态，判断是否有客服移动端token，有则发送通知
        }

        return messageResponse;
    }

    private Thread getAgentThread(VisitorRequest visitorRequest, String topic, ThreadTypeEnum type, Agent agent) {
        //
        Optional<Thread> threadOptional = threadService.findByTopic(topic);
        if (threadOptional.isPresent()) {
            return threadOptional.get();
        }
        //
        Thread thread = new Thread();
        thread.setUid(uidUtils.getCacheSerialUid());
        thread.setTopic(topic);
        thread.setType(type);
        thread.setClient(ClientEnum.fromValue(visitorRequest.getClient()));
        //
        VisitorResponseSimple visitor = ConvertServiceUtils.convertToVisitorResponseSimple(visitorRequest);
        thread.setUser(JSON.toJSONString(visitor));
        // thread.setUser(visitor);
        //
        if (!agent.isConnected() || !agent.isAvailable()) {
            thread.setContent(agent.getServiceSettings().getLeavemsgTip());
        } else {
            thread.setContent(agent.getServiceSettings().getWelcomeTip());
        }
        //
        thread.setOwner(agent.getMember().getUser());
        thread.setOrgUid(agent.getOrgUid());
        thread.setExtra(JSON.toJSONString(ConvertServiceUtils.convertToServiceSettingsResponseVisitor(agent.getServiceSettings())));
        thread.setAgent(JSON.toJSONString(ConvertServiceUtils.convertToAgentResponseSimple(agent)));
        // 
        return threadService.save(thread);
    }

    private MessageResponse getAgentMessage(VisitorRequest visitorRequest, Thread thread, Agent agent) {
        if (thread == null) {
            throw new RuntimeException("Thread cannot be null");
        }
        //
        if (!agent.isConnected() || !agent.isAvailable()) {
            thread.setContent(agent.getServiceSettings().getLeavemsgTip());
        } else {
            thread.setContent(agent.getServiceSettings().getWelcomeTip());
        }

        // if thread is closed, reopen it and then create a new message
        if (thread.isClosed()) {
            thread.setExtra(JSON.toJSONString(agent.getServiceSettings()));
            thread.setAgent(JSON.toJSONString(ConvertServiceUtils.convertToAgentResponseSimple(agent)));
            thread = threadService.reopen(thread);
            //
            Message message = createAgentMessage(thread, agent);
            message = messageService.save(message);

            return ConvertServiceUtils.convertToMessageResponse(message, thread);
        }

        // find the last message
        Optional<Message> messageOptional = messageService.findByThreadsUidInOrderByCreatedAtDesc(thread.getUid());
        if (messageOptional.isPresent()) {
            return ConvertServiceUtils.convertToMessageResponse(messageOptional.get(), thread);
        }
        // create new message
        Message message = createAgentMessage(thread, agent);
        message = messageService.save(message);

        return ConvertServiceUtils.convertToMessageResponse(message, thread);
    }

    private Message createAgentMessage(Thread thread, Agent agent) {
        UserResponseSimple user = modelMapper.map(agent, UserResponseSimple.class);

        Message message = Message.builder()
                .type(MessageTypeEnum.THREAD)
                .status(MessageStatusEnum.READ)
                .client(ClientEnum.SYSTEM)
                .user(JSON.toJSONString(user))
                .orgUid(thread.getOrgUid())
                .build();
        message.setUid(uidUtils.getCacheSerialUid());

        if (!agent.isConnected() || !agent.isAvailable()) {
            message.setContent(agent.getServiceSettings().getLeavemsgTip());
        } else {
            message.setContent(agent.getServiceSettings().getWelcomeTip());
        }
        message.getThreads().add(thread);

        return message;
    }

    ///////////////////// Workgroup///////////////////

    public MessageResponse createWorkgroupCsThread(VisitorRequest visitorRequest, String topic, ThreadTypeEnum type,
            String sid) {

        Workgroup workgroup = workgroupService.findByUid(sid)
                .orElseThrow(() -> new RuntimeException("Workgroup uid " + sid + " not found"));
        if (workgroup.getServiceSettings().isDefaultRobot()) {
            // 默认机器人优先接待
            Robot robot = workgroup.getServiceSettings().getRobot();
            if (robot != null) {
                Thread thread = getRobotThread(visitorRequest, topic, type, robot);
                return getRobotMessage(visitorRequest, thread, robot);
            } else {
                throw new AgentExceptionRobot("route " + sid + " to a robot");
            }
        } else if (workgroup.getServiceSettings().isOfflineRobot()) {
            // TODO: 人工离线期间转机器人
        } else if (workgroup.getServiceSettings().isNonWorktimeRobot()) {
            // TODO: 非工作时间转接机器人
        }
        if (workgroup.getAgents().isEmpty()) {
            throw new RuntimeException("No agents found in workgroup with uid " + sid);
        }
        Agent agent = workgroup.nextAgent();
        if (agent == null) {
            throw new RuntimeException("No available agent found in workgroup with uid " + sid);
        }
        //
        Thread thread = getWorkgroupThread(visitorRequest, topic, type, agent, workgroup);
        //
        MessageResponse messageResponse = getWorkgroupMessage(visitorRequest, thread, agent, workgroup);
        //
        if (agent.isConnected() && agent.isAvailable()) {
            log.info("agent is connected and available");
            // notify agent - 通知客服
            notifyAgent(thread, messageResponse);
        } else if (agent.isAvailable()) {
            // TODO: 断开连接，但是接待状态，判断是否有客服移动端token，有则发送通知
            log.info("agent is available");
        }

        return messageResponse;
    }

    private Thread getWorkgroupThread(
            VisitorRequest visitorRequest, String topic, ThreadTypeEnum type, Agent agent,
            Workgroup workgroup) {
        //
        Optional<Thread> threadOptional = threadService.findByTopic(topic);
        if (threadOptional.isPresent()) {
            return threadOptional.get();
        }
        //
        Thread thread = new Thread();
        thread.setUid(uidUtils.getCacheSerialUid());
        thread.setTopic(topic);
        thread.setType(type);
        thread.setClient(ClientEnum.fromValue(visitorRequest.getClient()));
        //
        VisitorResponseSimple visitor = ConvertServiceUtils.convertToVisitorResponseSimple(visitorRequest);
        thread.setUser(JSON.toJSONString(visitor));
        // thread.setUser(visitor);
        //
        if (!agent.isConnected() || !agent.isAvailable()) {
            thread.setContent(workgroup.getServiceSettings().getLeavemsgTip());
        } else {
            thread.setContent(workgroup.getServiceSettings().getWelcomeTip());
        }
        //
        thread.setOwner(agent.getMember().getUser());
        thread.setOrgUid(agent.getOrgUid());
        thread.setExtra(JSON.toJSONString(ConvertServiceUtils.convertToServiceSettingsResponseVisitor(workgroup.getServiceSettings())));
        thread.setAgent(JSON.toJSONString(ConvertServiceUtils.convertToWorkgroupResponseSimple(workgroup)));

        return threadService.save(thread);
    }

    private MessageResponse getWorkgroupMessage(VisitorRequest visitorRequest, Thread thread,
            Agent agent, Workgroup workgroup) {
        if (thread == null) {
            throw new RuntimeException("Thread cannot be null");
        }

        // if thread is closed, reopen it and then create a new message
        if (thread.isClosed()) {
            thread.setExtra(JSON.toJSONString(workgroup.getServiceSettings()));
            thread.setAgent(JSON.toJSONString(ConvertServiceUtils.convertToWorkgroupResponseSimple(workgroup)));
            thread.setContent(workgroup.getServiceSettings().getWelcomeTip());
            thread = threadService.reopen(thread);
            //
            Message message = createWorkgroupMessage(thread, agent, workgroup);

            message = messageService.save(message);

            return ConvertServiceUtils.convertToMessageResponse(message, thread);
        }

        // find the last message
        Optional<Message> messageOptional = messageService.findByThreadsUidInOrderByCreatedAtDesc(thread.getUid());
        if (messageOptional.isPresent()) {
            return ConvertServiceUtils.convertToMessageResponse(messageOptional.get(), thread);
        }
        // create new message
        Message message = createWorkgroupMessage(thread, agent, workgroup);

        message = messageService.save(message);

        return ConvertServiceUtils.convertToMessageResponse(message, thread);
    }

    private Message createWorkgroupMessage(Thread thread, Agent agent, Workgroup workgroup) {
        UserResponseSimple user = modelMapper.map(agent, UserResponseSimple.class);

        Message message = Message.builder()
                .type(MessageTypeEnum.THREAD)
                .status(MessageStatusEnum.READ)
                // .client(ClientConsts.CLIENT_SYSTEM)
                .client(ClientEnum.SYSTEM)
                .user(JSON.toJSONString(user))
                .orgUid(thread.getOrgUid())
                .build();
        message.setUid(uidUtils.getCacheSerialUid());

        if (!agent.isConnected() || !agent.isAvailable()) {
            message.setContent(workgroup.getServiceSettings().getLeavemsgTip());
        } else {
            message.setContent(workgroup.getServiceSettings().getWelcomeTip());
        }

        message.getThreads().add(thread);
        return message;
    }

    //////////////////// Robot/////////////////////////

    public MessageResponse createRobotCsThread(VisitorRequest visitorRequest, String topic, ThreadTypeEnum type,
            String sid) {
        Robot robot = robotService.findByUid(sid)
                .orElseThrow(() -> new RuntimeException("Robot uid " + sid + " not found"));
        //
        Thread thread = getRobotThread(visitorRequest, topic, type, robot);
        //
        return getRobotMessage(visitorRequest, thread, robot);
    }

    private Thread getRobotThread(VisitorRequest visitorRequest, String topic, ThreadTypeEnum type, Robot robot) {
        //
        Optional<Thread> threadOptional = threadService.findByTopic(topic);
        if (threadOptional.isPresent()) {
            return threadOptional.get();
        }
        //
        Thread thread = new Thread();
        thread.setUid(uidUtils.getCacheSerialUid());
        thread.setTopic(topic);
        thread.setType(type);
        thread.setClient(ClientEnum.fromValue(visitorRequest.getClient()));
        //
        VisitorResponseSimple visitor = ConvertServiceUtils.convertToVisitorResponseSimple(visitorRequest);
        thread.setUser(JSON.toJSONString(visitor));
        // thread.setUser(visitor);
        //
        // thread.setOwner(agent.getMember().getUser());
        thread.setOrgUid(robot.getOrgUid());
        thread.setExtra(JSON.toJSONString(ConvertAiUtils.convertToServiceSettingsResponseVisitor(
                robot.getServiceSettings())));
        thread.setAgent(JSON.toJSONString(ConvertAiUtils.convertToRobotResponseSimple(robot)));

        return threadService.save(thread);
    }

    private MessageResponse getRobotMessage(VisitorRequest visitorRequest, Thread thread, Robot robot) {

        if (thread == null) {
            throw new RuntimeException("Thread cannot be null");
        }

        thread.setContent(robot.getServiceSettings().getWelcomeTip());

        // if thread is closed, reopen it and then create a new message
        if (thread.isClosed()) {
            // 更新机器人配置+大模型相关信息
            thread.setExtra(JSON.toJSONString(robot.getServiceSettings()));
            thread.setAgent(JSON.toJSONString(ConvertAiUtils.convertToRobotResponseSimple(robot)));
            thread = threadService.reopen(thread);
            //
            Message message = createRobotMessage(thread, robot);
            //
            message = messageService.save(message);

            return ConvertServiceUtils.convertToMessageResponse(message, thread);
        }

        // find the last message
        Optional<Message> messageOptional = messageService.findByThreadsUidInOrderByCreatedAtDesc(thread.getUid());
        if (messageOptional.isPresent()) {
            return ConvertServiceUtils.convertToMessageResponse(messageOptional.get(), thread);
        }

        // create new message
        Message message = createRobotMessage(thread, robot);

        message = messageService.save(message);

        return ConvertServiceUtils.convertToMessageResponse(message, thread);
    }

    private Message createRobotMessage(Thread thread, Robot robot) {

        UserResponseSimple user = modelMapper.map(robot, UserResponseSimple.class);

        Message message = Message.builder()
                .type(MessageTypeEnum.THREAD)
                .content(robot.getServiceSettings().getWelcomeTip())
                .status(MessageStatusEnum.READ)
                // .client(ClientConsts.CLIENT_SYSTEM)
                .client(ClientEnum.SYSTEM)
                .user(JSON.toJSONString(user))
                .orgUid(thread.getOrgUid())
                .build();
        message.setUid(uidUtils.getCacheSerialUid());

        message.getThreads().add(thread);

        return message;
    }

    @Cacheable(value = "visitor", key = "#uid", unless = "#result == null")
    public Optional<Visitor> findByUid(String uid) {
        return visitorRepository.findByUidAndDeleted(uid, false);
    }

    @Caching(put = {
            @CachePut(value = "visitor", key = "#visitor.uid"),
    })
    private Visitor save(Visitor visitor) {
        return visitorRepository.save(visitor);
    }

    public void notifyAgent(Thread thread, MessageResponse messageResponse) {
        try {
            MessageNotify agentMessageResponse = modelMapper.map(messageResponse, MessageNotify.class);
            // 克隆MessageResponse对象
            // MessageNotify agentMessageResponse = SerializationUtils.clone(messageNotify);

            // 验证并解析extra为VisitorExtra对象
            String userJson = thread.getUser();
            if (StringUtils.hasText(userJson) && JSON.isValid(userJson)) {
                // 验证Visitor对象并转换
                VisitorResponseSimple visitor = JSON.parseObject(userJson, VisitorResponseSimple.class);
                if (visitor != null) {
                    // user替换成访客，否则客服端会显示客服自己的头像
                    UserResponseSimple user = ConvertServiceUtils.convertToUserResponseSimple(visitor);
                    agentMessageResponse.setUser(user);

                    // 发布消息事件
                    String json = JSON.toJSONString(agentMessageResponse);
                    bytedeskEventPublisher.publishMessageJsonEvent(json);
                } else {
                    // 处理visitor为空的情况
                }
            } else {
                // 处理extraJson为空或无效的情况
            }
        } catch (Exception e) {
            // 处理其他异常
        }
    }


    // TODO: 模拟压力测试：随机生成 10000 个访客，分配给1个技能组中10个客服账号，并随机分配给1个客服账号，每秒发送1条消息
    public void prepeareStressTest() {
        // String orgUid = UserConsts.DEFAULT_ORGANIZATION_UID;
        // // 随机生成10000个访客
        // List<Visitor> visitors = new ArrayList<>();
        // for (int i = 0; i < 10000; i++) {
        //     String uid = uidUtils.getCacheSerialUid();
        //     // visitors.add(new Visitor(uid, "visitor" + i));
        // }
        // 随机分配给1个技能组中10个客服账号
        // List<Robot> robots = robotService.findAllByOrgUidAndDeleted(orgUid, false);
        // if (robots == null || robots.isEmpty()) {
        //     return;
        // }
        // Random random = new Random();
        // for (Visitor visitor : visitors) {
        //     Robot robot = robots.get(random.nextInt(robots.size()));
        //     visitor.setAgentUid(robot.getUid());
        //     save(visitor);
        // }
    }


}
