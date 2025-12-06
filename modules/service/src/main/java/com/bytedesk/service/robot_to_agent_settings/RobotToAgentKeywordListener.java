package com.bytedesk.service.robot_to_agent_settings;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.message.MessageResponse;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserTypeEnum;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.enums.ThreadTypeEnum;
import com.bytedesk.core.thread.enums.ThreadTransferStatusEnum;
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
    private final WorkgroupRestService workgroupRestService;
    private final VisitorRestService visitorRestService;

    @EventListener
    @Transactional
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
        if (!isKeywordTriggerEnabled(robotToAgentSettings)) {
            log.debug("Robot keyword trigger disabled or empty for workgroup {}", workgroup.getUid());
            return;
        }

        if (!matchesKeyword(robotToAgentSettings.getTriggerKeywords(), message.getContent())) {
            log.debug("No keyword matched for thread {} content={} keywords={}", thread.getUid(), message.getContent(), robotToAgentSettings.getTriggerKeywords());
            return;
        }

        triggerForceAgentTransfer(thread, workgroup, message);
    }

    private boolean isVisitorTextMessage(MessageResponse message) {
        if (message == null) {
            return false;
        }
        if (!MessageTypeEnum.TEXT.name().equalsIgnoreCase(message.getType())) {
            return false;
        }
        UserProtobuf user = message.getUser();
        return user != null && UserTypeEnum.VISITOR.name().equalsIgnoreCase(user.getType());
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
            // markThreadTransferPending(thread);
            VisitorRequest visitorRequest = buildVisitorRequest(thread, workgroup, message);
            visitorRequest.setForceAgent(true);
            visitorRestService.requestThread(visitorRequest);
            log.info("Triggered robot-to-agent transfer for thread {} due to keyword match", thread.getUid());
        } catch (Exception ex) {
            log.error("Failed to trigger robot-to-agent transfer for thread {}", thread.getUid(), ex);
        }
    }

    // private void markThreadTransferPending(ThreadEntity thread) {
    //     if (thread == null) {
    //         return;
    //     }
    //     String currentStatus = thread.getTransferStatus();
    //     if (!StringUtils.hasText(currentStatus) || ThreadTransferStatusEnum.NONE.name().equals(currentStatus)) {
    //         log.debug("Marking thread {} transfer status as TRANSFER_PENDING", thread.getUid());
    //         thread.setTransferStatus(ThreadTransferStatusEnum.TRANSFER_PENDING.name());
    //         try {
    //             threadRestService.save(thread);
    //         } catch (Exception ex) {
    //             log.warn("Failed to update transfer status for thread {}", thread.getUid(), ex);
    //         }
    //     } else {
    //         log.debug("Thread {} transfer status already {}, skip update", thread.getUid(), currentStatus);
    //     }
    // }

    private VisitorRequest buildVisitorRequest(ThreadEntity thread, WorkgroupEntity workgroup, MessageResponse message) {
        UserProtobuf visitor = resolveVisitor(thread, message);
        VisitorRequest visitorRequest = VisitorRequest.builder()
            .uid(visitor != null ? visitor.getUid() : null)
            .userUid(visitor != null ? visitor.getUid() : null)
            .visitorUid(visitor != null ? visitor.getUid() : null)
            .nickname(visitor != null ? visitor.getNickname() : null)
            .avatar(visitor != null ? visitor.getAvatar() : null)
            .orgUid(thread.getOrgUid())
            .channel(thread.getChannel())
            .sid(workgroup.getUid())
            // .lang(visitor != null ? visitor.getLanguage() : null)
            .extra(thread.getExtra())
            .build();
        visitorRequest.setWorkgroupType();
        // visitorRequest.setIp(visitor != null ? visitor.getIp() : null);
        // visitorRequest.setIpLocation(visitor != null ? visitor.getIpLocation() : null);
        return visitorRequest;
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
}
