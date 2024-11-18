package com.bytedesk.service.strategy;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.ai.robot.RobotRestService;
import com.bytedesk.ai.utils.ConvertAiUtils;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.ThreadTypeEnum;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.service.utils.ConvertServiceUtils;
import com.bytedesk.service.visitor.VisitorRequest;
import com.bytedesk.core.thread.ThreadEntity;

import lombok.AllArgsConstructor;

// 工单会话
@Component("ticketCsThreadStrategy")
@AllArgsConstructor
public class TicketCsThreadCreationStrategy implements CsThreadCreationStrategy {

    private final RobotRestService robotService;

    private final ThreadRestService threadService;

    private final UidUtils uidUtils;

    private final ModelMapper modelMapper;

    @Override
    public MessageProtobuf createCsThread(VisitorRequest visitorRequest) {
        return createTicketCsThread(visitorRequest);
    }

    public MessageProtobuf createTicketCsThread(VisitorRequest visitorRequest) {
        String agentAsistantRobotUid = visitorRequest.getSid();
        RobotEntity robot = robotService.findByUid(agentAsistantRobotUid)
                .orElseThrow(
                        () -> new RuntimeException("agentAsistantRobotUid " + agentAsistantRobotUid + " not found"));
        //
        ThreadEntity thread = getAgentassistantThread(visitorRequest, robot);
        //
        return getAgentassistantMessage(visitorRequest, thread, robot);
    }

    private ThreadEntity getAgentassistantThread(VisitorRequest visitorRequest, RobotEntity robot) {
        if (robot == null) {
            throw new RuntimeException("Robot cannot be null");
        }
        //
        String topic = TopicUtils.formatOrgRobotThreadTopic(robot.getUid(), visitorRequest.getUid());
        Optional<ThreadEntity> threadOptional = threadService.findByTopic(topic);
        if (threadOptional.isPresent()) {
            return threadOptional.get();
        }
        //
        ThreadEntity thread = ThreadEntity.builder().build();
        thread.setUid(uidUtils.getCacheSerialUid());
        thread.setTopic(topic);
        thread.setType(ThreadTypeEnum.TICKET.name());
        thread.setUnreadCount(0);
        thread.setClient(ClientEnum.fromValue(visitorRequest.getClient()).name());
        //
        UserProtobuf visitor = ConvertServiceUtils.convertToUserProtobuf(visitorRequest);
        thread.setUser(JSON.toJSONString(visitor));
        //
        thread.setOrgUid(robot.getOrgUid());
        thread.setExtra(JSON.toJSONString(ConvertAiUtils.convertToServiceSettingsResponseVisitor(
                robot.getServiceSettings())));
        thread.setAgent(JSON.toJSONString(ConvertAiUtils.convertToRobotProtobuf(robot)));
        //
        return thread;
    }

    private MessageProtobuf getAgentassistantMessage(VisitorRequest visitorRequest, ThreadEntity thread, RobotEntity robot) {
        if (thread == null) {
            throw new RuntimeException("Thread cannot be null");
        }
        if (robot == null) {
            throw new RuntimeException("Robot cannot be null");
        }
        thread.setContent(robot.getServiceSettings().getWelcomeTip());
        //
        // boolean isReenter = true;
        // if (thread.getState() == ThreadStateEnum.STARTED.name()) {
        //     isReenter = false;
        // }
        // 更新机器人配置+大模型相关信息
        thread.setExtra(JSON.toJSONString(ConvertAiUtils.convertToServiceSettingsResponseVisitor(
                robot.getServiceSettings())));
        thread.setAgent(JSON.toJSONString(ConvertAiUtils.convertToRobotProtobuf(robot)));
        thread.setContent(robot.getServiceSettings().getWelcomeTip());
        // if thread is closed, reopen it and then create a new message
        // if (thread.isClosed()) {
        //     isReenter = false;
        //     thread.setStatus(ThreadStateEnum.RESTART.name());
        // } else {
        //     thread.setStatus(isReenter ? ThreadStateEnum.CONTINUE.name() : ThreadStateEnum.STARTED.name());
        // }
        threadService.save(thread);
        //
        UserProtobuf user = modelMapper.map(robot, UserProtobuf.class);
        //
        JSONObject userExtra = new JSONObject();
        userExtra.put("llm", robot.getLlm().isEnabled());
        userExtra.put("defaultReply", robot.getDefaultReply());
        user.setExtra(JSON.toJSONString(userExtra));
        //
        return ThreadMessageUtil.getThreadWelcomeMessage(user, thread);
    }

}
