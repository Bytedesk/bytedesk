/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-04 07:45:29
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

import java.util.Date;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
// import org.springframework.util.SerializationUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.ai.robot.Robot;
import com.bytedesk.ai.robot.RobotService;
import com.bytedesk.ai.utils.ConvertAiUtils;
import com.bytedesk.core.base.BaseService;
import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.ip.IpService;
import com.bytedesk.core.message.Message;
import com.bytedesk.core.message.MessageExtra;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageStatusEnum;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.Thread;
import com.bytedesk.core.thread.ThreadService;
import com.bytedesk.core.thread.ThreadStatusEnum;
import com.bytedesk.core.thread.ThreadTypeEnum;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.service.agent.Agent;
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
public class VisitorService extends BaseService<Visitor, VisitorRequest, VisitorResponse> {

    private final VisitorRepository visitorRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final IpService ipService;

    private final ThreadService threadService;

    private final AgentService agentService;

    private final WorkgroupService workgroupService;

    private final RobotService robotService;

    private final BytedeskEventPublisher bytedeskEventPublisher;

    @Override
    public Page<VisitorResponse> queryByOrg(VisitorRequest request) {

        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.DESC,
                "updatedAt");
        Specification<Visitor> spec = VisitorSpecification.search(request);
        Page<Visitor> page = visitorRepository.findAll(spec, pageable);

        return page.map(ConvertServiceUtils::convertToVisitorResponse);
    }

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
    public VisitorProtobuf create(VisitorRequest visitorRequest, HttpServletRequest request) {
        //
        String uid = visitorRequest.getUid();
        log.info("visitor init, uid: {}", uid);
        //
        Visitor visitor = findByUid(uid).orElse(null);
        if (visitor != null) {
            return ConvertServiceUtils.convertToVisitorProtobuf(visitor);
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
        log.info("visitorRequest {}", visitorRequest);
        visitor = modelMapper.map(visitorRequest, Visitor.class);
        visitor.setUid(uidUtils.getCacheSerialUid());
        visitor.setClient(ClientEnum.fromValue(visitorRequest.getClient()));
        visitor.setOrgUid(visitorRequest.getOrgUid());
        //
        VisitorDevice device = modelMapper.map(visitorRequest, VisitorDevice.class);
        visitor.setDevice(device);
        //
        Visitor savedVisitor = save(visitor);
        if (savedVisitor == null) {
            throw new RuntimeException("visitor not saved");
        }
        //
        return ConvertServiceUtils.convertToVisitorProtobuf(savedVisitor);
    }

    // private Map<ThreadTypeEnum, CsThreadCreationStrategy> strategyMap = new
    // HashMap<>();

    // public VisitorService() {
    // // 在构造函数中初始化策略映射
    // strategyMap.put(ThreadTypeEnum.AGENT, new AgentCsThreadCreationStrategy());
    // strategyMap.put(ThreadTypeEnum.WORKGROUP, new
    // WorkgroupCsThreadCreationStrategy());
    // strategyMap.put(ThreadTypeEnum.ROBOT, new RobotCsThreadCreationStrategy());
    // }

    // public MessageProtobuf createCsThread(VisitorRequest visitorRequest) {
    // ThreadTypeEnum type = visitorRequest.formatType();
    // CsThreadCreationStrategy strategy = strategyMap.get(type);
    // if (strategy == null) {
    // throw new RuntimeException("Thread type " + type.name() + " not supported");
    // }
    // return strategy.createCsThread(visitorRequest);
    // }

    /** TODO: 重构策略模式？ */
    public MessageProtobuf createCsThread(VisitorRequest visitorRequest) {
        //
        ThreadTypeEnum type = visitorRequest.formatType();
        //
        if (type.equals(ThreadTypeEnum.AGENT)) {
            // 一对一客服
            return createAgentCsThread(visitorRequest);
            //
        } else if (type.equals(ThreadTypeEnum.WORKGROUP)) {
            // 技能组
            return createWorkgroupCsThread(visitorRequest);
            //
        } else if (type.equals(ThreadTypeEnum.ROBOT)) {
            // 机器人对话
            return createRobotCsThread(visitorRequest);
            //
        } else {
            throw new RuntimeException("Thread type " + type.name() + " not supported");
        }
    }

    ////////////////// Agent/////////////////////

    public MessageProtobuf createAgentCsThread(VisitorRequest visitorRequest) {
        //
        String agentUid = visitorRequest.getSid();
        Agent agent = agentService.findByUid(agentUid)
                .orElseThrow(() -> new RuntimeException("Agent uid " + agentUid + " not found"));
        //
        boolean transferToRobot = false;
        if (agent.getServiceSettings().isDefaultRobot()) {
            // 默认转机器人优先接待
            transferToRobot = true;
        } else if (agent.getServiceSettings().isOfflineRobot()) {
            // 设置客服离线机器人，且当前客服离线，转机器人
            if (!agent.isConnected() || !agent.isAvailable()) {
                // 离线转机器人
                transferToRobot = true;
            }
        } else if (!agent.getServiceSettings().isWorkTime()
                && agent.getServiceSettings().isNonWorktimeRobot()) {
            // 当前非工作时间，且设置非工作时间转机器人，转机器人
            transferToRobot = true;
        }
        //
        if (transferToRobot) {
            // 转机器人
            Robot robot = agent.getServiceSettings().getRobot();
            if (robot != null) {
                Thread thread = getRobotThread(visitorRequest, robot);
                return getRobotMessage(visitorRequest, thread, robot);
            } else {
                throw new RuntimeException("route " + agentUid + " to a robot");
            }
        }
        // 下面进入人工接待

        // TODO: 判断是否达到最大接待人数，如果达到则进入排队

        Thread thread = getAgentThread(visitorRequest, agent);
        //
        MessageProtobuf messageProtobuf = getAgentMessage(visitorRequest, thread, agent);
        // 广播消息，由消息通道统一处理
        notifyAgent(messageProtobuf);
        // if (agent.isConnected() && agent.isAvailable()) {
        // // notify agent - 通知客服
        // notifyAgent(messageProtobuf);
        // } else {
        // // 离线状态
        // }
        // else if (agent.isAvailable()) {
        // // TODO: 断开连接，但是接待状态，判断是否有客服移动端token，有则发送通知
        // }

        return messageProtobuf;
    }

    private Thread getAgentThread(VisitorRequest visitorRequest, Agent agent) {
        //
        String topic = TopicUtils.formatOrgAgentThreadTopic(visitorRequest.getSid(), visitorRequest.getUid());
        // TODO: 到visitor thread表中拉取
        Optional<Thread> threadOptional = threadService.findByTopic(topic);
        if (threadOptional.isPresent()) {
            return threadOptional.get();
        }
        //
        Thread thread = Thread.builder().build();
        thread.setUid(uidUtils.getCacheSerialUid());
        thread.setTopic(topic);
        thread.setType(ThreadTypeEnum.AGENT);
        thread.setClient(ClientEnum.fromValue(visitorRequest.getClient()));
        //
        VisitorProtobuf visitor = ConvertServiceUtils.convertToVisitorProtobuf(visitorRequest);
        thread.setUser(JSON.toJSONString(visitor));
        //
        thread.setOwner(agent.getMember().getUser());
        thread.setOrgUid(agent.getOrgUid());
        thread.setExtra(JSON
                .toJSONString(ConvertServiceUtils.convertToServiceSettingsResponseVisitor(agent.getServiceSettings())));
        thread.setAgent(JSON.toJSONString(ConvertServiceUtils.convertToAgentResponseSimple(agent)));
        //
        return thread;
    }

    private MessageProtobuf getAgentMessage(VisitorRequest visitorRequest, Thread thread, Agent agent) {
        //
        if (thread == null) {
            throw new RuntimeException("Thread cannot be null");
        }
        if (agent == null) {
            throw new RuntimeException("Agent cannot be null");
        }
        //
        boolean isReenter = true;
        if (thread.isInit()) {
            // 访客首次进入会话
            isReenter = false;
        }
        //
        if (!agent.isConnected() || !agent.isAvailable()) {
            // 离线状态永远显示离线提示语，不显示“继续会话”
            isReenter = false;
            // 客服离线 或 非接待状态
            thread.setContent(agent.getServiceSettings().getLeavemsgTip());
            thread.setStatus(ThreadStatusEnum.OFFLINE);
        } else {
            // 客服在线 且 接待状态
            thread.setUnreadCount(1);
            thread.setContent(agent.getServiceSettings().getWelcomeTip());
            thread.setExtra(JSON.toJSONString(
                    ConvertServiceUtils.convertToServiceSettingsResponseVisitor(agent.getServiceSettings())));
            thread.setAgent(JSON.toJSONString(ConvertServiceUtils.convertToAgentResponseSimple(agent)));
            // if thread is closed, reopen it and then create a new message
            if (thread.isClosed()) {
                // 访客会话关闭之后，重新进入
                isReenter = false;
                thread.setStatus(ThreadStatusEnum.REOPEN);
            } else {
                thread.setStatus(isReenter ? ThreadStatusEnum.CONTINUE : ThreadStatusEnum.NORMAL);
            }
        }
        threadService.save(thread);
        //
        UserProtobuf user = modelMapper.map(agent, UserProtobuf.class);
        //
        return getThreadMessage(user, thread, isReenter);
    }

    ///////////////////// Workgroup///////////////////

    public MessageProtobuf createWorkgroupCsThread(VisitorRequest visitorRequest) {
        //
        String workgroupUid = visitorRequest.getSid();
        Workgroup workgroup = workgroupService.findByUid(workgroupUid)
                .orElseThrow(() -> new RuntimeException("Workgroup uid " + workgroupUid + " not found"));
        //
        boolean transferToRobot = false;
        if (workgroup.getServiceSettings().isDefaultRobot()) {
            // 默认机器人优先接待
            transferToRobot = true;
        } else if (!workgroup.isConnected()
                && workgroup.getServiceSettings().isOfflineRobot()) {
            // 所有客服离线，且设置机器人离线优先接待
            transferToRobot = true;
        } else if (!workgroup.getServiceSettings().isWorkTime()
                && workgroup.getServiceSettings().isNonWorktimeRobot()) {
            // 设置非工作时间机器人接待，且当前非工作时间，转机器人
            transferToRobot = true;
        }
        //
        if (transferToRobot) {
            // 转机器人
            Robot robot = workgroup.getServiceSettings().getRobot();
            if (robot != null) {
                Thread thread = getRobotThread(visitorRequest, robot);
                return getRobotMessage(visitorRequest, thread, robot);
            } else {
                throw new RuntimeException("route " + workgroupUid + " to a robot");
            }
        }
        // 下面人工接待
        // TODO: 所有客服都达到最大接待人数，则进入排队

        if (workgroup.getAgents().isEmpty()) {
            throw new RuntimeException("No agents found in workgroup with uid " + workgroupUid);
        }
        //
        Agent agent = workgroup.nextAgent();
        if (agent == null) {
            throw new RuntimeException("No available agent found in workgroup with uid " + workgroupUid);
        }
        //
        Thread thread = getWorkgroupThread(visitorRequest, agent, workgroup);
        //
        MessageProtobuf messageProtobuf = getWorkgroupMessage(visitorRequest, thread, agent, workgroup);
        // 广播消息，由消息通道统一处理
        notifyAgent(messageProtobuf);
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
        return messageProtobuf;
    }

    private Thread getWorkgroupThread(VisitorRequest visitorRequest, Agent agent, Workgroup workgroup) {
        //
        String topic = TopicUtils.formatOrgWorkgroupThreadTopic(workgroup.getUid(), agent.getUid(),
                visitorRequest.getUid());
        // TODO: 到visitor thread表中拉取
        Optional<Thread> threadOptional = threadService.findByTopic(topic);
        if (threadOptional.isPresent()) {
            return threadOptional.get();
        }
        //
        Thread thread = Thread.builder().build();
        thread.setUid(uidUtils.getCacheSerialUid());
        thread.setTopic(topic);
        thread.setType(ThreadTypeEnum.WORKGROUP);
        thread.setClient(ClientEnum.fromValue(visitorRequest.getClient()));
        //
        VisitorProtobuf visitor = ConvertServiceUtils.convertToVisitorProtobuf(visitorRequest);
        thread.setUser(JSON.toJSONString(visitor));
        //
        thread.setOwner(agent.getMember().getUser());
        thread.setOrgUid(agent.getOrgUid());
        thread.setExtra(JSON.toJSONString(
                ConvertServiceUtils.convertToServiceSettingsResponseVisitor(workgroup.getServiceSettings())));
        thread.setAgent(JSON.toJSONString(ConvertServiceUtils.convertToWorkgroupResponseSimple(workgroup)));
        //
        return thread;

        // Thread savedThread = threadService.save(thread);
        // if (savedThread == null) {
        // throw new RuntimeException("crate workgroup thread error, topic " + topic);
        // }
        // return savedThread;
    }

    private MessageProtobuf getWorkgroupMessage(VisitorRequest visitorRequest, Thread thread, Agent agent,
            Workgroup workgroup) {
        if (thread == null) {
            throw new RuntimeException("Thread cannot be null");
        }
        if (agent == null) {
            throw new RuntimeException("Agent cannot be null");
        }
        if (workgroup == null) {
            throw new RuntimeException("Workgroup cannot be null");
        }
        //
        boolean isReenter = true;
        if (thread.isInit()) {
            // 访客首次进入会话
            isReenter = false;
        }
        //
        if (!agent.isConnected() || !agent.isAvailable()) {
            // 离线状态永远显示离线提示语，不显示“继续会话”
            isReenter = false;
            // 客服离线 或 非接待状态
            thread.setContent(workgroup.getServiceSettings().getLeavemsgTip());
            thread.setStatus(ThreadStatusEnum.OFFLINE);
        } else {
            // 客服在线 且 接待状态
            thread.setUnreadCount(1);
            thread.setContent(workgroup.getServiceSettings().getWelcomeTip());
            thread.setExtra(JSON.toJSONString(
                    ConvertServiceUtils.convertToServiceSettingsResponseVisitor(workgroup.getServiceSettings())));
            thread.setAgent(JSON.toJSONString(ConvertServiceUtils.convertToWorkgroupResponseSimple(workgroup)));
            // if thread is closed, reopen it and then create a new message
            if (thread.isClosed()) {
                // 访客会话关闭之后，重新进入
                isReenter = false;
                thread.setStatus(ThreadStatusEnum.REOPEN);
            } else {
                thread.setStatus(isReenter ? ThreadStatusEnum.CONTINUE : ThreadStatusEnum.NORMAL);
            }
        }
        threadService.save(thread);
        //
        UserProtobuf user = modelMapper.map(agent, UserProtobuf.class);
        //
        return getThreadMessage(user, thread, isReenter);
    }

    //////////////////// Robot/////////////////////////

    public MessageProtobuf createRobotCsThread(VisitorRequest visitorRequest) {
        //
        String robotUid = visitorRequest.getSid();
        Robot robot = robotService.findByUid(robotUid)
                .orElseThrow(() -> new RuntimeException("Robot uid " + robotUid + " not found"));
        //
        Thread thread = getRobotThread(visitorRequest, robot);
        //
        return getRobotMessage(visitorRequest, thread, robot);
    }

    private Thread getRobotThread(VisitorRequest visitorRequest, Robot robot) {
        if (robot == null) {
            throw new RuntimeException("Robot cannot be null");
        }
        //
        String topic = TopicUtils.formatOrgRobotThreadTopic(robot.getUid(), visitorRequest.getUid());
        // TODO: 到visitor thread表中拉取
        Optional<Thread> threadOptional = threadService.findByTopic(topic);
        if (threadOptional.isPresent()) {
            return threadOptional.get();
        }
        //
        Thread thread = Thread.builder().build();
        thread.setUid(uidUtils.getCacheSerialUid());
        thread.setTopic(topic);
        thread.setType(ThreadTypeEnum.ROBOT);
        thread.setUnreadCount(0);
        thread.setClient(ClientEnum.fromValue(visitorRequest.getClient()));
        //
        VisitorProtobuf visitor = ConvertServiceUtils.convertToVisitorProtobuf(visitorRequest);
        thread.setUser(JSON.toJSONString(visitor));
        //
        thread.setOrgUid(robot.getOrgUid());
        thread.setExtra(JSON.toJSONString(ConvertAiUtils.convertToServiceSettingsResponseVisitor(
                robot.getServiceSettings())));
        thread.setAgent(JSON.toJSONString(ConvertAiUtils.convertToRobotProtobuf(robot)));
        //
        return thread;
    }

    private MessageProtobuf getRobotMessage(VisitorRequest visitorRequest, Thread thread, Robot robot) {
        if (thread == null) {
            throw new RuntimeException("Thread cannot be null");
        }
        if (robot == null) {
            throw new RuntimeException("Robot cannot be null");
        }
        thread.setContent(robot.getServiceSettings().getWelcomeTip());
        //
        boolean isReenter = true;
        if (thread.isInit()) {
            isReenter = false;
        }
        // 更新机器人配置+大模型相关信息
        thread.setExtra(JSON.toJSONString(ConvertAiUtils.convertToServiceSettingsResponseVisitor(
                robot.getServiceSettings())));
        thread.setAgent(JSON.toJSONString(ConvertAiUtils.convertToRobotProtobuf(robot)));
        thread.setContent(robot.getServiceSettings().getWelcomeTip());
        // if thread is closed, reopen it and then create a new message
        if (thread.isClosed()) {
            isReenter = false;
            thread.setStatus(ThreadStatusEnum.REOPEN);
        } else {
            thread.setStatus(isReenter ? ThreadStatusEnum.CONTINUE : ThreadStatusEnum.NORMAL);
        }
        threadService.save(thread);
        //
        UserProtobuf user = modelMapper.map(robot, UserProtobuf.class);
        //
        JSONObject userExtra = new JSONObject();
        userExtra.put("llm", robot.getLlm().isEnabled());
        userExtra.put("defaultReply", robot.getDefaultReply());
        user.setExtra(JSON.toJSONString(userExtra));
        //
        return getThreadMessage(user, thread, isReenter);
    }

    //////////////////// Common /////////////////////////

    // thread
    private MessageProtobuf getThreadMessage(UserProtobuf user, Thread thread, boolean isReenter) {
        //
        Message message = Message.builder()
                .content(isReenter ? I18Consts.I18N_REENTER_TIP : thread.getContent())
                .type(isReenter ? MessageTypeEnum.CONTINUE : MessageTypeEnum.WELCOME)
                .status(MessageStatusEnum.READ)
                .client(ClientEnum.SYSTEM)
                .user(JSON.toJSONString(user))
                .build();
        message.setUid(uidUtils.getCacheSerialUid());
        message.setOrgUid(thread.getOrgUid());
        message.setCreatedAt(new Date());
        message.setUpdatedAt(new Date());
        //
        if (thread.getStatus().equals(ThreadStatusEnum.OFFLINE)) {
            message.setType(MessageTypeEnum.LEAVE_MSG);
        }
        // message.getThreads().add(thread);
        message.setThreadTopic(thread.getTopic());
        //
        MessageExtra extraObject = MessageExtra.builder().orgUid(thread.getOrgUid()).build();
        message.setExtra(JSON.toJSONString(extraObject));
        //
        return ConvertServiceUtils.convertToMessageProtobuf(message, thread);
    }

    @Cacheable(value = "visitor", key = "#uid", unless = "#result == null")
    public Optional<Visitor> findByUid(String uid) {
        return visitorRepository.findByUidAndDeleted(uid, false);
    }

    @Caching(put = {
            @CachePut(value = "visitor", key = "#visitor.uid"),
    })
    @Override
    public Visitor save(Visitor visitor) {
        try {
            return visitorRepository.save(visitor);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return null;
    }

    public void notifyAgent(MessageProtobuf messageProtobuf) {
        //
        String json = JSON.toJSONString(messageProtobuf);
        bytedeskEventPublisher.publishMessageJsonEvent(json);
    }

    // TODO: 模拟压力测试：随机生成 10000 个访客，分配给1个技能组中10个客服账号，并随机分配给1个客服账号，每秒发送1条消息
    public void prepeareStressTest() {
        // String orgUid = BdConstants.DEFAULT_ORGANIZATION_UID;
        // // 随机生成10000个访客
        // List<Visitor> visitors = new ArrayList<>();
        // for (int i = 0; i < 10000; i++) {
        // String uid = uidUtils.getCacheSerialUid();
        // // visitors.add(new Visitor(uid, "visitor" + i));
        // }
        // 随机分配给1个技能组中10个客服账号
        // List<Robot> robots = robotService.findAllByOrgUidAndDeleted(orgUid, false);
        // if (robots == null || robots.isEmpty()) {
        // return;
        // }
        // Random random = new Random();
        // for (Visitor visitor : visitors) {
        // Robot robot = robots.get(random.nextInt(robots.size()));
        // visitor.setAgentUid(robot.getUid());
        // save(visitor);
        // }
    }

    @Override
    public Page<VisitorResponse> queryByUser(VisitorRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Override
    public VisitorResponse create(VisitorRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public VisitorResponse update(VisitorRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void deleteByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteByUid'");
    }

    @Override
    public void delete(Visitor entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, Visitor entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public VisitorResponse convertToResponse(Visitor entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'convertToResponse'");
    }

}
