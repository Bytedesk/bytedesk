/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-13 10:04:42
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-04 10:19:30
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.member.MemberEntity;
import com.bytedesk.core.member.MemberRepository;
import com.bytedesk.core.message.content.NoticeContent;
import com.bytedesk.core.message.enums.MessageNoticeTypeEnum;
import com.bytedesk.core.message.enums.MessageStatusEnum;
import com.bytedesk.core.message.utils.MessageUtils;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserRepository;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadResponse;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.BdDateUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessagePersistCache messagePersistCache;

    private final ThreadRestService threadRestService;

    private final IMessageSendService messageSendService;

    private final UidUtils uidUtils;

    private final UserRepository userRepository;

    private final MemberRepository memberRepository;

    public String processMessageJson(String messageJson, Boolean isRobot) {

        MessageProtobuf messageProtobuf = MessageProtobuf.fromJson(messageJson); 

        // 收到消息，更新消息状态为发送成功
        if (messageProtobuf.getStatus().equals(MessageStatusEnum.SENDING)) {
            messageProtobuf.setStatus(MessageStatusEnum.SUCCESS);
        } 
        
        // 机器人消息默认设置为已读
        if (isRobot) {
            messageProtobuf.setStatus(MessageStatusEnum.READ);
        }

        // 防止客户端时间错误，使用服务器时间戳，指定Asia/Shanghai时区
        messageProtobuf.setCreatedAt(BdDateUtils.now());

        // 
        String messageJsonResult = messageProtobuf.toJson();
        // 保存消息
        messagePersistCache.pushForPersist(messageJsonResult);

        return messageJsonResult;
    }

    public void sendForceLogoutMessage(UserEntity user, String orgUid, String sourceType, String sourceUid, String reason) {
        if (user == null || !StringUtils.hasText(user.getUid())) {
            return;
        }

        ThreadEntity thread = getOrCreateSystemThread(user);
        String content = JSON.toJSONString(Map.of(
            "reason", StringUtils.hasText(reason) ? reason : I18Consts.I18N_FORCE_LOGOUT_REASON,
                "sourceType", StringUtils.hasText(sourceType) ? sourceType : "UNKNOWN",
                "sourceUid", StringUtils.hasText(sourceUid) ? sourceUid : "",
                "action", "FORCE_LOGOUT"));

        MessageProtobuf message = MessageUtils.createKickoffMessage(
                uidUtils.getUid(),
                thread.toProtobuf(),
                orgUid,
                content);
        messageSendService.sendProtobufMessage(message);
    }

    public void sendSystemNotice(NoticeContent noticeContent) {
        validateNoticeContent(noticeContent);
        Set<UserEntity> recipients = resolveRecipients(noticeContent);
        for (UserEntity recipient : recipients) {
            sendSystemNoticeToRecipient(noticeContent, recipient, false);
        }
    }

    public void sendSystemLoginNotice(NoticeContent noticeContent) {
        validateNoticeContent(noticeContent);
        Set<UserEntity> recipients = resolveRecipients(noticeContent);
        for (UserEntity recipient : recipients) {
            sendSystemNoticeToRecipient(noticeContent, recipient, true);
        }
    }

    public String sendNoticeMessage(UserEntity user, String orgUid, String content) {
        if (user == null || !StringUtils.hasText(user.getUid())) {
            return null;
        }

        ThreadEntity thread = getOrCreateSystemThread(user);
        String messageUid = uidUtils.getUid();
        MessageProtobuf message = MessageUtils.createNoticeMessage(
                messageUid,
                thread.toProtobuf(),
                orgUid,
                content);
        messageSendService.sendProtobufMessage(message);
        return messageUid;
    }

    public String sendLoginNoticeMessage(UserEntity user, String orgUid, String content) {
        if (user == null || !StringUtils.hasText(user.getUid())) {
            return null;
        }

        ThreadEntity thread = getOrCreateSystemThread(user);
        String messageUid = uidUtils.getUid();
        MessageProtobuf message = MessageUtils.createLoginNoticeMessage(messageUid, thread.toProtobuf(), orgUid, content);
        messageSendService.sendProtobufMessage(message);
        return messageUid;
    }

    private void sendSystemNoticeToRecipient(NoticeContent sourceNoticeContent, UserEntity recipient, boolean loginNotice) {
        String orgUid = resolveRecipientOrgUid(sourceNoticeContent, recipient);
        NoticeContent noticeContent = NoticeContent.builder()
                .noticeUid(sourceNoticeContent.getNoticeUid())
                .title(sourceNoticeContent.getTitle())
                .content(sourceNoticeContent.getContent())
                .type(resolveType(sourceNoticeContent, loginNotice))
                .status(resolveStatus(sourceNoticeContent, loginNotice))
                .level(normalizeLevel(sourceNoticeContent.getLevel()).name())
                .orgUid(orgUid)
                .userUid(recipient.getUid())
                .deptUid(sourceNoticeContent.getDeptUid())
                .senderUid(sourceNoticeContent.getSenderUid())
                .senderNickname(sourceNoticeContent.getSenderNickname())
                .extra(resolveExtra(sourceNoticeContent))
                .build();

        if (loginNotice) {
            sendLoginNoticeMessage(recipient, orgUid, noticeContent.toJson());
            return;
        }
        sendNoticeMessage(recipient, orgUid, noticeContent.toJson());
    }

    private void validateNoticeContent(NoticeContent noticeContent) {
        Assert.notNull(noticeContent, "Notice content cannot be null");
        Assert.hasText(noticeContent.getTitle(), "Notice title cannot be null or empty");
        Assert.hasText(noticeContent.getContent(), "Notice content cannot be null or empty");
    }

    private Set<UserEntity> resolveRecipients(NoticeContent noticeContent) {
        LevelEnum level = normalizeLevel(noticeContent.getLevel());
        return switch (level) {
            case PLATFORM -> resolvePlatformRecipients();
            case ORGANIZATION -> resolveOrganizationRecipients(noticeContent.getOrgUid());
            case DEPARTMENT -> resolveDepartmentRecipients(noticeContent.getDeptUid());
            case USER -> new LinkedHashSet<>(List.of(userRepository.findByUid(noticeContent.getUserUid())
                    .orElseThrow(() -> new RuntimeException("Notice recipient not found: " + noticeContent.getUserUid()))));
            default -> throw new RuntimeException("Unsupported notice level: " + level.name());
        };
    }

    private Set<UserEntity> resolvePlatformRecipients() {
        return new LinkedHashSet<>(userRepository.findAll().stream()
                .filter(user -> !user.isDeleted())
                .toList());
    }

    private Set<UserEntity> resolveOrganizationRecipients(String orgUid) {
        Assert.hasText(orgUid, "Organization UID cannot be empty when sending organization notice");
        return new LinkedHashSet<>(memberRepository.findAll().stream()
                .filter(member -> !member.isDeleted())
                .filter(member -> orgUid.equals(member.getOrgUid()))
                .map(MemberEntity::getUser)
                .filter(java.util.Objects::nonNull)
                .toList());
    }

    private Set<UserEntity> resolveDepartmentRecipients(String deptUid) {
        Assert.hasText(deptUid, "Department UID cannot be empty when sending department notice");
        return new LinkedHashSet<>(memberRepository.findByDeptUidAndDeletedFalse(deptUid).stream()
                .map(MemberEntity::getUser)
                .filter(java.util.Objects::nonNull)
                .toList());
    }

    private LevelEnum normalizeLevel(String level) {
        if (!StringUtils.hasText(level)) {
            return LevelEnum.USER;
        }
        return LevelEnum.fromValue(level);
    }

    private String resolveType(NoticeContent noticeContent, boolean loginNotice) {
        if (loginNotice) {
            return MessageNoticeTypeEnum.LOGIN.name();
        }
        return StringUtils.hasText(noticeContent.getType()) ? noticeContent.getType() : MessageNoticeTypeEnum.GENERAL.name();
    }

    private String resolveStatus(NoticeContent noticeContent, boolean loginNotice) {
        if (StringUtils.hasText(noticeContent.getStatus())) {
            return noticeContent.getStatus();
        }
        return loginNotice ? MessageStatusEnum.READ.name() : MessageStatusEnum.SUCCESS.name();
    }

    private String resolveRecipientOrgUid(NoticeContent noticeContent, UserEntity recipient) {
        if (StringUtils.hasText(noticeContent.getOrgUid())) {
            return noticeContent.getOrgUid();
        }
        return recipient.getOrgUid();
    }

    private String resolveExtra(NoticeContent noticeContent) {
        return StringUtils.hasText(noticeContent.getExtra()) ? noticeContent.getExtra() : "{}";
    }

    private ThreadEntity getOrCreateSystemThread(UserEntity user) {
        String topic = TopicUtils.getSystemTopic(user.getUid());
        Optional<ThreadEntity> existing = threadRestService.findFirstByTopicAndOwner(topic, user);
        if (existing.isPresent()) {
            return existing.get();
        }

        ThreadResponse created = threadRestService.createSystemPublicAccountThread(user);
        return threadRestService.findByUid(created.getUid())
                .orElseThrow(() -> new RuntimeException("Failed to create system notice thread for user: " + user.getUid()));
    }


}
