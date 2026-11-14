package com.bytedesk.ai.robot.group;

import org.springframework.stereotype.Service;
import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.ai.robot.RobotRestService;
import com.bytedesk.ai.robot.RobotService;
import com.bytedesk.ai.utils.ConvertAiUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 群机器人查询服务：封装 LLM 调用与机器人信息获取。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RobotGroupService {

    private final RobotRestService robotRestService;

    private final RobotService robotService;

    /**
     * 群机器人问答：复用聚合流式入口（超时自动回退同步），返回完整答案文本。
     * searchKnowledgeBase=false：群聊首期不接知识库，仅用群上下文。
     */
    public String processGroupRobotQuery(String robotUid, String orgUid, String query) {
        return robotService.processStreamTextByRobotUid(robotUid, orgUid, query, false);
    }

    /**
     * 获取机器人 protobuf 快照（用于以机器人身份发消息）。
     */
    public RobotProtobuf findRobotProtobuf(String robotUid) {
        RobotEntity robot = robotRestService.findByUid(robotUid)
                .orElseThrow(() -> new RuntimeException("robot not found: " + robotUid));
        return ConvertAiUtils.convertToRobotProtobuf(robot);
    }
}
