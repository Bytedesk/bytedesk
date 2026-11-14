package com.bytedesk.ai.robot.group;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.event.EventListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.bytedesk.core.group.GroupEntity;
import com.bytedesk.core.group.GroupRestService;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageExtra;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.message.enums.MessageStatusEnum;
import com.bytedesk.core.message.enums.MessageTypeEnum;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadRestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 群机器人执行 worker。
 * 
 * 由 Quartz 每分钟事件驱动（与 RobotMessageEventListener 的五秒持久化模式一致，但拉长周期避免频繁扫描）：
 * 1. 归还过期 RUNNING 任务（实例崩溃恢复）
 * 2. 领取 PENDING 任务（CAS，多实例安全）
 * 3. 复查资格（机器人仍在群内、群未解散）
 * 4. 构建最近 20 条群聊上下文 + 当前问题
 * 5. 调用 RobotService 聚合流式入口（非流式完整答案）
 * 6. 以机器人身份发送群消息（复用既有广播链路，全群收到）
 * 7. 任务状态与回复在同一事务语义下落库（先消息、后任务；崩溃时任务回到 PENDING 重试，
 *    回复消息 uid 固定保证不产生重复回复）
 * 
 * 重试策略：attempt > 1（首次执行失败自动重试 1 次）不再领取，标记 FAILED。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GroupRobotExecutionWorker {

    private static final int MAX_ATTEMPTS = 2; // 首次 + 1 次自动重试
    private static final int CONTEXT_MESSAGE_COUNT = 20;
    private static final int BATCH_SIZE = 5;

    private final GroupRobotExecutionRepository executionRepository;

    private final RobotGroupService robotGroupService;

    private final GroupRestService groupRestService;

    private final ThreadRestService threadRestService;

    private final MessageRestService messageRestService;

    private final IMessageSendService messageSendService;

    // private final UidUtils uidUtils;

    @EventListener
    public void onQuartzOneMinEvent(com.bytedesk.core.quartz.event.QuartzOneMinEvent event) {
        // 回收失败不阻断本轮任务处理
        try {
            reclaimExpired();
        } catch (Exception e) {
            log.error("GroupRobotExecutionWorker reclaimExpired failed: {}", e.getMessage());
        }
        try {
            processBatch();
        } catch (Exception e) {
            log.error("GroupRobotExecutionWorker failed: {}", e.getMessage(), e);
        }
    }

    /**
     * 归还过期 RUNNING 任务（崩溃实例恢复）。
     */
    private void reclaimExpired() {
        int reclaimed = executionRepository.reclaimExpiredRunning(ZonedDateTime.now());
        if (reclaimed > 0) {
            log.info("reclaimed {} expired group robot executions", reclaimed);
        }
    }

    /**
     * 批量处理 PENDING 任务。
     */
    private void processBatch() {
        List<GroupRobotExecutionEntity> tasks = executionRepository
                .findClaimableTasks(PageRequest.of(0, BATCH_SIZE));
        for (GroupRobotExecutionEntity task : tasks) {
            if (task.getAttempt() >= MAX_ATTEMPTS) {
                // 超过重试上限的任务从未被本轮领取，直接以无租约方式标记失败
                markFailedUnclaimed(task);
                continue;
            }
            processOne(task);
        }
    }

    /**
     * 未领取（非 RUNNING）任务达到重试上限：置 FAILED（幂等，仅 PENDING 态生效）。
     */
    private void markFailedUnclaimed(GroupRobotExecutionEntity task) {
        executionRepository.markFailedIfPending(task.getUid(), "MAX_ATTEMPTS_EXCEEDED", ZonedDateTime.now());
    }

    private void processOne(GroupRobotExecutionEntity task) {
        String leaseToken = UUID.randomUUID().toString();
        ZonedDateTime leaseUntil = ZonedDateTime.now().plusMinutes(5);
        int claimed = executionRepository.claimTask(task.getUid(), leaseUntil, leaseToken, ZonedDateTime.now());
        if (claimed != 1) {
            return; // 已被其他实例领取
        }
        log.info("group robot execution claimed: executionUid: {}, robotUid: {}, attempt: {}",
                task.getUid(), task.getRobotUid(), task.getAttempt() + 1);
        //
        try {
            execute(task, leaseToken);
        } catch (Exception e) {
            log.warn("group robot execution {} failed: {}", task.getUid(), e.getMessage());
            markFailed(task, leaseToken, safeErrorCode(e));
        }
    }

    private void execute(GroupRobotExecutionEntity task, String leaseToken) {
        // 复查资格：群存在、未解散、机器人仍在群内
        GroupEntity group = groupRestService.findByUid(task.getGroupUid()).orElse(null);
        if (group == null) {
            markCancelled(task, leaseToken, "GROUP_NOT_FOUND");
            return;
        }
        if (group.getRobotUids() == null || !group.getRobotUids().contains(task.getRobotUid())) {
            markCancelled(task, leaseToken, "ROBOT_REMOVED");
            return;
        }
        // 源消息必须已落库（延迟持久化最多 5 秒，一分钟周期内必然可见；不可见则下次重试）
        MessageEntity sourceMessage = messageRestService.findByUid(task.getSourceMessageUid()).orElse(null);
        if (sourceMessage == null) {
            // 任务回到 PENDING 等待下一轮（不消耗失败计数）
            releaseToPending(task, leaseToken);
            return;
        }
        //
        String query = buildQueryWithContext(task);
        // 调用 ai 聚合入口：流式聚合为完整文本，失败自动回退同步
        String answer = robotGroupService.processGroupRobotQuery(
                task.getRobotUid(), task.getOrgUid(), query);
        if (!StringUtils.hasText(answer)) {
            throw new RuntimeException("EMPTY_ANSWER");
        }
        //
        sendRobotReply(task, sourceMessage, answer);
        markSucceeded(task, leaseToken);
        log.info("group robot execution {} succeeded, robotUid: {}", task.getUid(), task.getRobotUid());
    }

    /**
     * 构建带最近 20 条群聊上下文的提问。
     * 按 topic 查询全群消息（同一群所有成员共享 topic），排除回执/系统类，取最近 20 条按正序拼接。
     */
    private String buildQueryWithContext(GroupRobotExecutionEntity task) {
        List<MessageEntity> recent = messageRestService.getRecentMessages(task.getThreadTopic(),
                CONTEXT_MESSAGE_COUNT * 2); // 多取一倍，过滤后取 20
        //
        List<String> historyLines = recent.stream()
                .filter(msg -> isContextEligible(msg))
                .limit(CONTEXT_MESSAGE_COUNT)
                .map(msg -> describeMessage(msg))
                .collect(Collectors.toList());
        java.util.Collections.reverse(historyLines); // DB 取出为倒序，反转为正序
        //
        StringBuilder sb = new StringBuilder();
        if (!historyLines.isEmpty()) {
            sb.append("以下是群聊最近的对话记录（仅供理解上下文，不要重复回答）：\n");
            for (String line : historyLines) {
                sb.append(line).append("\n");
            }
            sb.append("\n");
        }
        sb.append("请回答刚才的问题。");
        return sb.toString();
    }

    /**
     * 上下文资格：可见的有效内容消息，排除通知/回执/机器人自身回复中的执行痕迹。
     */
    private boolean isContextEligible(MessageEntity msg) {
        if (msg == null || msg.getType() == null) {
            return false;
        }
        MessageTypeEnum type;
        try {
            type = MessageTypeEnum.valueOf(msg.getType());
        } catch (IllegalArgumentException e) {
            return false;
        }
        return MessageTypeEnum.TEXT.equals(type) || MessageTypeEnum.IMAGE.equals(type);
    }

    private String describeMessage(MessageEntity msg) {
        String nickname = "";
        try {
            UserProtobuf user = UserProtobuf.fromJson(msg.getUser());
            nickname = user != null ? user.getNickname() : "";
        } catch (Exception ignore) {
        }
        String preview = msg.getContent();
        if (preview != null && preview.length() > 200) {
            preview = preview.substring(0, 200) + "...";
        }
        return nickname + ": " + (preview == null ? "" : preview);
    }

    /**
     * 以机器人身份发送群回复。回复消息 uid 使用任务预分配值（幂等）。
     * 前缀使用发送者昵称（从源消息 user JSON 解析，展示友好）。
     */
    private void sendRobotReply(GroupRobotExecutionEntity task, MessageEntity sourceMessage, String answer) {
        var thread = threadRestService.findFirstByTopic(task.getThreadTopic()).orElse(null);
        if (thread == null) {
            throw new RuntimeException("THREAD_NOT_FOUND");
        }
        var robot = robotGroupService.findRobotProtobuf(task.getRobotUid());
        String senderNickname = task.getSenderUserUid();
        try {
            UserProtobuf sender = UserProtobuf.fromJson(sourceMessage.getUser());
            if (sender != null && StringUtils.hasText(sender.getNickname())) {
                senderNickname = sender.getNickname();
            }
        } catch (Exception ignore) {
            // 解析失败回退 uid
        }
        String content = "@" + senderNickname + " " + answer;
        //
        MessageExtra extra = MessageExtra.fromOrgUid(task.getOrgUid());
        MessageProtobuf reply = MessageProtobuf.builder()
                .uid(task.getReplyMessageUid())
                .type(MessageTypeEnum.TEXT)
                .content(content)
                .status(MessageStatusEnum.SUCCESS)
                .createdAt(ZonedDateTime.now())
                .channel(com.bytedesk.core.enums.ChannelEnum.SYSTEM)
                .thread(thread.toProtobuf())
                .user(robot.toUserProtobuf())
                .extra(extra.toJson())
                .build();
        messageSendService.sendProtobufMessage(reply);
    }

    private void markSucceeded(GroupRobotExecutionEntity task, String leaseToken) {
        executionRepository.completeWithLease(task.getUid(), leaseToken,
                GroupRobotExecutionEntity.STATUS_SUCCEEDED, task.getReplyMessageUid(), null,
                ZonedDateTime.now());
    }

    private void markFailed(GroupRobotExecutionEntity task, String leaseToken, String errorCode) {
        executionRepository.completeWithLease(task.getUid(), leaseToken,
                GroupRobotExecutionEntity.STATUS_FAILED, task.getReplyMessageUid(), safeErrorCodeText(errorCode),
                ZonedDateTime.now());
    }

    private void markCancelled(GroupRobotExecutionEntity task, String leaseToken, String reason) {
        executionRepository.completeWithLease(task.getUid(), leaseToken,
                GroupRobotExecutionEntity.STATUS_CANCELLED, task.getReplyMessageUid(), reason,
                ZonedDateTime.now());
    }

    /**
     * 释放回 PENDING（源消息尚未落库等临时情况，不消耗失败次数）。
     * 带租约校验的 UPDATE，防止过期 worker 误写。
     */
    private void releaseToPending(GroupRobotExecutionEntity task, String leaseToken) {
        executionRepository.releaseToPendingWithLease(task.getUid(), leaseToken, ZonedDateTime.now());
    }

    private String safeErrorCodeText(String raw) {
        if (!StringUtils.hasText(raw)) {
            return "UNKNOWN";
        }
        // 只保留短错误码，不透出 provider 细节
        return raw.length() > 60 ? raw.substring(0, 60) : raw;
    }

    private String safeErrorCode(Exception e) {
        return safeErrorCodeText(e.getMessage());
    }
}
