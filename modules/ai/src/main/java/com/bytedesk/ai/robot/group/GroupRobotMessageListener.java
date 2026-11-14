package com.bytedesk.ai.robot.group;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bytedesk.core.group.GroupEntity;
import com.bytedesk.core.group.GroupRestService;
import com.bytedesk.core.message.MessageExtra;
import com.bytedesk.core.message.MessageMention;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.enums.MessageTypeEnum;
import com.bytedesk.core.message.event.MessageJsonEvent;
import com.bytedesk.core.rbac.user.UserTypeEnum;
import com.bytedesk.core.thread.enums.ThreadTypeEnum;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.uid.UidUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 群机器人任务登记监听器。
 * 
 * 仅做「校验 + 登记执行任务」，不在此调用 LLM：
 * - 只处理 GROUP 类型 thread 的 TEXT 消息
 * - 发送者必须是人类（排除 ROBOT/SYSTEM，防死循环）
 * - 消息 extra 中含 ROBOT 类型 mention，且机器人确实在该群 robotUids 中（防越权伪造）
 * - 同一源消息+机器人幂等（数据库唯一约束兜底）
 * 
 * 后台 worker（GroupRobotExecutionWorker）领取任务执行。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GroupRobotMessageListener {

    private final GroupRobotExecutionRepository executionRepository;

    private final GroupRestService groupRestService;

    private final UidUtils uidUtils;

    @EventListener
    public void onMessageJsonEvent(MessageJsonEvent event) {
        try {
            handle(event.getJson());
        } catch (Exception e) {
            // 监听器异常不影响消息主链路（持久化/广播已在 core 完成）
            log.warn("GroupRobotMessageListener failed: {}", e.getMessage());
        }
    }

    private void handle(String messageJson) {
        if (!StringUtils.hasText(messageJson)) {
            return;
        }
        MessageProtobuf message = MessageProtobuf.fromJson(messageJson);
        if (message == null || message.getThread() == null) {
            return;
        }
        // 仅群聊
        if (!ThreadTypeEnum.GROUP.equals(message.getThread().getType())) {
            return;
        }
        // 仅文本消息可触发问答
        if (!MessageTypeEnum.TEXT.equals(message.getType())) {
            return;
        }
        // 仅人类消息触发（ROBOT/SYSTEM 回复不再触发，防死循环）
        if (message.getUser() == null || !StringUtils.hasText(message.getUser().getUid())) {
            return;
        }
        String senderType = message.getUser().getType();
        if (UserTypeEnum.ROBOT.name().equals(senderType) || UserTypeEnum.SYSTEM.name().equals(senderType)) {
            return;
        }
        //
        String topic = message.getThread().getTopic();
        if (!StringUtils.hasText(topic) || !TopicUtils.isOrgGroupTopic(topic)) {
            return;
        }
        String groupUid = topic.split("/")[2];
        //
        MessageExtra extra = MessageExtra.fromJson(message.getExtra());
        if (extra == null || extra.getMentions() == null || extra.getMentions().isEmpty()) {
            log.info("group robot mention skip: no mentions in extra, messageUid: {}, extra: {}",
                    message.getUid(), message.getExtra());
            return;
        }
        // 解析被 @ 的机器人
        List<String> robotUids = new ArrayList<>();
        for (MessageMention mention : extra.getMentions()) {
            if (mention != null && mention.isRobot() && StringUtils.hasText(mention.getTargetUid())) {
                robotUids.add(mention.getTargetUid());
            }
        }
        if (robotUids.isEmpty()) {
            log.info("group robot mention skip: no ROBOT type mention, messageUid: {}, mentions: {}",
                    message.getUid(), extra.getMentions().size());
            return;
        }
        // 群校验：存在且机器人在群内（防越权：mention 伪造不在群内的机器人 uid 无效）
        GroupEntity group = groupRestService.findByUid(groupUid).orElse(null);
        if (group == null) {
            log.info("group robot mention skip: group not found, groupUid: {}", groupUid);
            return;
        }
        List<String> groupRobotUids = group.getRobotUids();
        if (groupRobotUids == null || groupRobotUids.isEmpty()) {
            log.info("group robot mention skip: group has no robots, groupUid: {}, robotUids: {}",
                    groupUid, robotUids);
            return;
        }
        //
        String senderUserUid = message.getUser().getUid();
        for (String robotUid : robotUids) {
            if (!groupRobotUids.contains(robotUid)) {
                log.info("group robot mention skip: robot {} not in group {}, groupRobots: {}",
                        robotUid, groupUid, groupRobotUids);
                continue;
            }
            registerExecution(group, topic, message, robotUid, senderUserUid);
        }
    }

    @Transactional
    protected void registerExecution(GroupEntity group, String topic, MessageProtobuf message,
            String robotUid, String senderUserUid) {
        String sourceMessageUid = message.getUid();
        // 幂等：已存在则跳过（并发由数据库唯一约束兜底）
        boolean exists = executionRepository
                .findByOrgUidAndGroupUidAndSourceMessageUidAndRobotUid(
                        group.getOrgUid(), group.getUid(), sourceMessageUid, robotUid)
                .isPresent();
        if (exists) {
            return;
        }
        //
        GroupRobotExecutionEntity execution = GroupRobotExecutionEntity.builder()
                .uid(uidUtils.getUid())
                .orgUid(group.getOrgUid())
                .userUid(senderUserUid)
                .senderUserUid(senderUserUid)
                .threadTopic(topic)
                .sourceMessageUid(sourceMessageUid)
                .robotUid(robotUid)
                .groupUid(group.getUid())
                .replyMessageUid(uidUtils.getUid()) // 预分配，保证回复消息幂等
                .status(GroupRobotExecutionEntity.STATUS_PENDING)
                .statusUpdatedAt(java.time.ZonedDateTime.now())
                .build();
        try {
            executionRepository.save(execution);
            log.info("group robot execution registered: executionUid: {}, robotUid: {}, sourceMessageUid: {}",
                    execution.getUid(), robotUid, sourceMessageUid);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // 并发重复登记，唯一约束兜底，忽略
            log.debug("duplicate group robot execution registered, sourceMessageUid: {}, robotUid: {}",
                    sourceMessageUid, robotUid);
        }
    }
}
