package com.bytedesk.service.robot_to_agent_settings;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageResponse;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.message.enums.MessageTypeEnum;
import com.bytedesk.core.message.event.MessageCreateEvent;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserTypeEnum;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.enums.ThreadTypeEnum;
import com.bytedesk.core.thread.enums.ThreadTransferStatusEnum;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.service.visitor.VisitorProtobuf;
import com.bytedesk.service.visitor.VisitorRequest;
import com.bytedesk.service.visitor.VisitorRestService;
import com.bytedesk.service.workgroup.WorkgroupEntity;
import com.bytedesk.service.workgroup.WorkgroupRestService;
import com.bytedesk.service.robot_to_agent_settings.event.VisitorRobotMessageEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RobotToAgentKeywordListener {

    private static final String EMPTY_JSON = BytedeskConsts.EMPTY_JSON_STRING;

    private final ThreadRestService threadRestService;
    private final MessageRestService messageRestService;
    private final WorkgroupRestService workgroupRestService;
    private final VisitorRestService visitorRestService;

    private final Map<String, PendingKeywordReplyMark> pendingKeywordReplyMarks = new ConcurrentHashMap<>();

    /**
     * Synchronous listener invoked inline during {@code VisitorRestControllerVisitor.sendSseVisitorMessageInternal()}
     * BEFORE the async SSE executor starts. This guarantees that when keyword-triggered transfer occurs,
     * the thread state (ROBOTING→QUEUING) and transferStatus are persisted before
     * {@code robotService.processSseVisitorMessage()} checks {@code thread.isRoboting()}.
     * <p>
        * Do NOT make this method asynchronous — it would break the timing contract with the SSE pipeline.
        * Also avoid wrapping the whole listener in one transaction; RobotService runs immediately after this method
        * returns and must observe the saved thread state without waiting for an outer transaction to commit.
     */
    @EventListener
    public void onVisitorRobotMessageEvent(VisitorRobotMessageEvent event) {
        MessageResponse message;
        try {
            message = JSON.parseObject(event.getMessageJson(), MessageResponse.class);
        } catch (Exception ex) {
            log.debug("Failed to parse message json for robot keyword detection", ex);
            return;
        }

        if (log.isTraceEnabled()) {
            log.trace("Robot keyword listener received visitor SSE event: {}", event.getMessageJson());
        }
        if (!isVisitorTextMessage(message)) {
            log.debug("Skip non-visitor text message uid={}", message != null ? message.getUid() : null);
            return;
        }
        if (message.getThread() == null || !StringUtils.hasText(message.getThread().getUid())) {
            log.debug("Skip message uid={} due to missing thread info", message.getUid());
            return;
        }

        Optional<ThreadEntity> threadOptional = threadRestService.findByUid(message.getThread().getUid());
        if (threadOptional.isEmpty()) {
            log.debug("No thread found for uid={} when running robot keyword listener", message.getThread().getUid());
            return;
        }
        ThreadEntity thread = threadOptional.get();
        if (!ThreadTypeEnum.WORKGROUP.name().equals(thread.getType())) {
            log.debug("Thread {} is not workgroup type, skip robot keyword listener", thread.getUid());
            return;
        }
        if (!thread.isRoboting()) {
            log.debug("Thread {} is no longer in roboting state, skip keyword transfer", thread.getUid());
            return;
        }
        String transferStatus = thread.getTransferStatus();
        if (StringUtils.hasText(transferStatus) && !ThreadTransferStatusEnum.NONE.name().equals(transferStatus)) {
            log.debug("Thread {} already has transfer status {}, skip keyword transfer", thread.getUid(), transferStatus);
            return;
        }
        if (hasAgentAssigned(thread)) {
            log.debug("Thread {} already has agent assigned, skip keyword transfer", thread.getUid());
            return;
        }

        WorkgroupEntity workgroup = resolveWorkgroup(thread);
        if (workgroup == null) {
            log.debug("Unable to resolve workgroup for thread {}, skip keyword transfer", thread.getUid());
            return;
        }

        RobotToAgentSettingsEntity robotToAgentSettings = workgroup.getSettings() != null
                ? workgroup.getSettings().getRobotToAgentSettings()
                : null;
        if (robotToAgentSettings == null || !isKeywordTriggerEnabled(robotToAgentSettings)) {
            log.debug("Robot keyword trigger disabled or empty for workgroup {}", workgroup.getUid());
            return;
        }

        if (!matchesKeyword(robotToAgentSettings.getTriggerKeywords(), message.getContent())) {
            log.debug("No keyword matched for thread {} content={} keywords={}", thread.getUid(), message.getContent(), robotToAgentSettings.getTriggerKeywords());
            return;
        }

        triggerForceAgentTransfer(thread, workgroup, message);
    }

    @EventListener
    public void onMessageCreateEvent(MessageCreateEvent event) {
        if (event == null || event.getMessage() == null || !StringUtils.hasText(event.getMessage().getUid())) {
            return;
        }

        MessageEntity message = event.getMessage();
        PendingKeywordReplyMark pendingMark = pendingKeywordReplyMarks.remove(message.getUid());
        if (pendingMark == null) {
            return;
        }

        if (!message.isFromVisitor()) {
            log.debug("Skip pending keyword reply mark for non-visitor message uid={}", message.getUid());
            return;
        }
        if (message.getThread() == null || !StringUtils.hasText(message.getThread().getUid())) {
            log.debug("Skip pending keyword reply mark due to missing thread uid, messageUid={}", message.getUid());
            return;
        }
        if (!pendingMark.matchesThread(message.getThread().getUid())) {
            log.debug("Skip pending keyword reply mark due to thread mismatch, messageUid={}, expectedThreadUid={}, actualThreadUid={}",
                    message.getUid(), pendingMark.threadUid(), message.getThread().getUid());
            return;
        }

        markMessageAsReplied(message, pendingMark.repliedAt(), pendingMark.repliedByUid());
    }

    private boolean isVisitorTextMessage(MessageResponse message) {
        if (message == null) {
            return false;
        }
        if (!MessageTypeEnum.TEXT.name().equalsIgnoreCase(message.getType())) {
            return false;
        }
        UserProtobuf user = message.getUser();
        // 访客端 SSE 上行 payload 在极端情况下可能缺少 user.type（例如初始化尚未完成/字段裁剪）。
        // 这里仅对“明确不是访客”的情况做排除，避免关键词转人工概率性失效。
        if (user == null || !StringUtils.hasText(user.getType())) {
            return true;
        }
        return UserTypeEnum.VISITOR.name().equalsIgnoreCase(user.getType());
    }

    private boolean hasAgentAssigned(ThreadEntity thread) {
        String agentJson = thread.getAgent();
        return StringUtils.hasText(agentJson) && !EMPTY_JSON.equals(agentJson);
    }

    private WorkgroupEntity resolveWorkgroup(ThreadEntity thread) {
        String workgroupJson = thread.getWorkgroup();
        if (!StringUtils.hasText(workgroupJson) || EMPTY_JSON.equals(workgroupJson)) {
            return null;
        }
        try {
            UserProtobuf workgroupProto = JSON.parseObject(workgroupJson, UserProtobuf.class);
            if (workgroupProto == null || !StringUtils.hasText(workgroupProto.getUid())) {
                return null;
            }
            return workgroupRestService.findByUid(workgroupProto.getUid()).orElse(null);
        } catch (Exception ex) {
            log.warn("Failed to parse workgroup info for thread {}", thread.getUid(), ex);
            return null;
        }
    }

    private boolean isKeywordTriggerEnabled(RobotToAgentSettingsEntity settings) {
        return settings != null
                && Boolean.TRUE.equals(settings.getEnabled())
                && Boolean.TRUE.equals(settings.getKeywordTriggerEnabled())
                && !CollectionUtils.isEmpty(settings.getTriggerKeywords());
    }

    private boolean matchesKeyword(List<String> keywords, String content) {
        if (CollectionUtils.isEmpty(keywords) || !StringUtils.hasText(content)) {
            return false;
        }
        String normalizedContent = content.toLowerCase(Locale.ROOT);
        for (String keyword : keywords) {
            if (!StringUtils.hasText(keyword)) {
                continue;
            }
            if (normalizedContent.contains(keyword.trim().toLowerCase(Locale.ROOT))) {
                log.debug("Matched keyword '{}' for content '{}'", keyword, content);
                return true;
            }
        }
        return false;
    }

    protected void triggerForceAgentTransfer(ThreadEntity thread, WorkgroupEntity workgroup, MessageResponse message) {
        try {
            markThreadTransferPending(thread);
            scheduleKeywordTriggerMessageReplyMark(thread, message);
            VisitorRequest visitorRequest = buildVisitorRequest(thread, workgroup, message);
            visitorRequest.setForceAgent(true);
            visitorRestService.requestThread(visitorRequest);
            log.info("Triggered robot-to-agent transfer for thread {} due to keyword match", thread.getUid());
        } catch (Exception ex) {
            log.error("Failed to trigger robot-to-agent transfer for thread {}", thread.getUid(), ex);
            rollbackKeywordTransfer(thread, message);
        }
    }

    /**
     * 转人工失败后的补偿回滚：
     * - 会话若仍停留在 QUEUING（未真正入队）则回退 ROBOTING，transferStatus 还原 NONE，访客可继续与机器人对话并再次触发转人工；
     * - 回滚触发消息的 agentReplied 标记，避免访客消息被误标「已回复」；
     * - 清理内存态挂起标记，避免后续 MessageCreateEvent 再次补标。
     */
    private void rollbackKeywordTransfer(ThreadEntity thread, MessageResponse message) {
        if (thread == null || !StringUtils.hasText(thread.getUid())) {
            return;
        }
        try {
            threadRestService.findByUid(thread.getUid()).ifPresent(latest -> {
                boolean statusRolledBack = false;
                if (latest.isQueuing() && !hasAgentAssigned(latest)) {
                    latest.setRoboting();
                    latest.setTransferStatus(ThreadTransferStatusEnum.NONE.name());
                    threadRestService.save(latest);
                    statusRolledBack = true;
                    log.warn("Rolled back keyword transfer failure: thread {} restored to ROBOTING", latest.getUid());
                }
                if (!statusRolledBack) {
                    log.warn("Keyword transfer failed but thread {} state already advanced (status={}, agent assigned={}), skip rollback",
                            latest.getUid(), latest.getStatus(), hasAgentAssigned(latest));
                }
            });
        } catch (Exception rollbackEx) {
            log.warn("Failed to rollback thread state after keyword transfer failure, threadUid={}", thread.getUid(), rollbackEx);
        }

        if (message == null || !StringUtils.hasText(message.getUid())) {
            return;
        }
        pendingKeywordReplyMarks.remove(message.getUid());
        try {
            messageRestService.findByUid(message.getUid()).ifPresent(entity -> {
                if (Boolean.TRUE.equals(entity.getAgentReplied())) {
                    entity.setAgentReplied(false);
                    entity.setAgentRepliedAt(null);
                    entity.setAgentRepliedByUid(null);
                    messageRestService.save(entity);
                    log.debug("Rolled back agentReplied mark for keyword transfer message {}", entity.getUid());
                }
            });
        } catch (Exception markEx) {
            log.warn("Failed to rollback agentReplied mark for message {}", message.getUid(), markEx);
        }
    }

    private void scheduleKeywordTriggerMessageReplyMark(ThreadEntity thread, MessageResponse message) {
        if (thread == null || message == null || !StringUtils.hasText(message.getUid())) {
            return;
        }

        ZonedDateTime repliedAt = BdDateUtils.now();
        String repliedByUid = resolveKeywordReplyByUid(thread);
        if (tryMarkMessageAsReplied(message.getUid(), repliedAt, repliedByUid)) {
            return;
        }

        pendingKeywordReplyMarks.put(message.getUid(), new PendingKeywordReplyMark(thread.getUid(), repliedAt, repliedByUid));
        log.debug("Registered pending keyword-trigger reply mark for messageUid={}, threadUid={}", message.getUid(), thread.getUid());
    }

    private boolean tryMarkMessageAsReplied(String messageUid, ZonedDateTime repliedAt, String repliedByUid) {
        Optional<MessageEntity> messageOptional = messageRestService.findByUid(messageUid);
        if (messageOptional.isEmpty()) {
            return false;
        }
        markMessageAsReplied(messageOptional.get(), repliedAt, repliedByUid);
        return true;
    }

    private void markMessageAsReplied(MessageEntity message, ZonedDateTime repliedAt, String repliedByUid) {
        if (message == null || Boolean.TRUE.equals(message.getAgentReplied())) {
            return;
        }
        message.setAgentReplied(true);
        message.setAgentRepliedAt(repliedAt != null ? repliedAt : BdDateUtils.now());
        if (StringUtils.hasText(repliedByUid)) {
            message.setAgentRepliedByUid(repliedByUid);
        }
        messageRestService.save(message);
        log.debug("Marked keyword-trigger transfer message as replied, messageUid={}, threadUid={}, repliedByUid={}",
                message.getUid(),
                message.getThread() != null ? message.getThread().getUid() : null,
                repliedByUid);
    }

    private String resolveKeywordReplyByUid(ThreadEntity thread) {
        if (thread == null || !StringUtils.hasText(thread.getRobot())) {
            return null;
        }
        try {
            UserProtobuf robot = JSON.parseObject(thread.getRobot(), UserProtobuf.class);
            if (robot != null && StringUtils.hasText(robot.getUid())) {
                return robot.getUid();
            }
        } catch (Exception ex) {
            log.debug("Failed to parse robot uid for keyword-trigger reply mark, threadUid={}", thread.getUid(), ex);
        }
        return null;
    }

    private void markThreadTransferPending(ThreadEntity thread) {
        if (thread == null) {
            return;
        }
        String currentStatus = thread.getTransferStatus();
        boolean isNone = !StringUtils.hasText(currentStatus) || ThreadTransferStatusEnum.NONE.name().equals(currentStatus);

        // 关键：在 RobotService.processSseVisitorMessage() 内部仅通过 thread.isRoboting() 来决定是否跳过机器人回复。
        // 因此这里需要尽早把状态从 ROBOTING 切走，避免“已触发转人工但机器人仍继续回复”的竞态。
        if (thread.isRoboting()) {
            thread.setQueuing();
        }

        if (isNone) {
            thread.setTransferStatus(ThreadTransferStatusEnum.TRANSFER_PENDING.name());
        }

        if (thread.isRoboting() || isNone) {
            try {
                threadRestService.save(thread);
                log.debug("Marked thread {} transfer pending, status={}, transferStatus={}", thread.getUid(), thread.getStatus(), thread.getTransferStatus());
            } catch (Exception ex) {
                log.warn("Failed to update transfer pending flags for thread {}", thread.getUid(), ex);
            }
        }
    }

    private VisitorRequest buildVisitorRequest(ThreadEntity thread, WorkgroupEntity workgroup, MessageResponse message) {
        UserProtobuf visitor = resolveVisitor(thread, message);
        // 身份口径拆分：visitorUid 必须与首次建会话 resolveVisitorUidForThreadTopic 同源（外部稳定 ID），
        // 否则转人工会因 topic 后缀不同而新建重复会话，无法复用机器人会话。
        String visitorUid = resolveVisitorUidForTopic(thread, visitor);
        VisitorRequest.VisitorRequestBuilder<?, ?> builder = VisitorRequest.builder()
            .uid(visitor != null ? visitor.getUid() : null)
            .userUid(visitor != null ? visitor.getUid() : null)
            .visitorUid(visitorUid)
            .nickname(visitor != null ? visitor.getNickname() : null)
            .avatar(visitor != null ? visitor.getAvatar() : null)
            .orgUid(thread.getOrgUid())
            .channel(thread.getChannel())
            .sid(workgroup.getUid());
        // 仅社交渠道透传源会话 extra（下游 buildWorkgroupExtra 对社交渠道直接使用请求 extra）；
        // 普通渠道不透传，避免设置快照（含表单 schema 全文）灌进 thread_user/visitor.extra 造成超长与递归膨胀
        if (isSocialChannel(thread.getChannel())) {
            builder.extra(thread.getExtra());
        }
        VisitorRequest visitorRequest = builder.build();
        visitorRequest.setWorkgroupType();
        return visitorRequest;
    }

    /**
     * 解析与首次建会话同源的访客标识（用于 topic 匹配复用）。
     * 优先级：thread.user 中 VisitorProtobuf.visitorUid → topic 后缀 → 消息发送者 uid。
     */
    private String resolveVisitorUidForTopic(ThreadEntity thread, UserProtobuf fallbackVisitor) {
        String visitorJson = thread.getUser();
        if (StringUtils.hasText(visitorJson) && !EMPTY_JSON.equals(visitorJson)) {
            try {
                VisitorProtobuf visitorProto = VisitorProtobuf.fromJson(visitorJson);
                if (visitorProto != null && StringUtils.hasText(visitorProto.getVisitorUid())) {
                    return visitorProto.getVisitorUid();
                }
            } catch (Exception ex) {
                log.debug("Failed to parse visitorUid from thread.user for thread {}", thread.getUid(), ex);
            }
        }
        String topic = thread.getTopic();
        if (StringUtils.hasText(topic)) {
            int lastSlash = topic.lastIndexOf('/');
            if (lastSlash >= 0 && lastSlash + 1 < topic.length()) {
                return topic.substring(lastSlash + 1);
            }
        }
        return fallbackVisitor != null ? fallbackVisitor.getUid() : null;
    }

    private boolean isSocialChannel(String channel) {
        if (!StringUtils.hasText(channel)) {
            return false;
        }
        String lower = channel.toLowerCase(Locale.ROOT);
        return lower.contains("wechat") || lower.contains("messenger") || lower.contains("telegram")
                || lower.contains("whatsapp");
    }

    private UserProtobuf resolveVisitor(ThreadEntity thread, MessageResponse message) {
        if (message != null && message.getUser() != null) {
            return message.getUser();
        }
        String visitorJson = thread.getUser();
        if (!StringUtils.hasText(visitorJson) || EMPTY_JSON.equals(visitorJson)) {
            return null;
        }
        try {
            return JSON.parseObject(visitorJson, UserProtobuf.class);
        } catch (Exception ex) {
            log.warn("Failed to parse visitor info for thread {}", thread.getUid(), ex);
            return null;
        }
    }

    private record PendingKeywordReplyMark(String threadUid, ZonedDateTime repliedAt, String repliedByUid) {
        private boolean matchesThread(String actualThreadUid) {
            return StringUtils.hasText(threadUid) && threadUid.equals(actualThreadUid);
        }
    }
}
