/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-04 11:22:50
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-04 17:59:15
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.notification;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.member.MemberEntity;
import com.bytedesk.core.member.MemberRepository;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.role.RoleEntity;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserRepository;
import com.bytedesk.core.uid.UidUtils;

import lombok.RequiredArgsConstructor;

/**
 * Service for handling notification operations
 * 
 * @author jackning 270580156@qq.com
 * @since 2024-12-04
 */
@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final AuthService authService;
    private final UidUtils uidUtils;
    private final ModelMapper modelMapper;

    public NotificationDispatchResponse dispatchNotification(NotificationRequest request) {
        validateNotificationRequest(request);
        UserEntity operator = validateDispatchPermission(request);

        Set<UserEntity> recipients = resolveRecipients(request);
        List<NotificationEntity> savedNotifications = new ArrayList<>();
        for (UserEntity recipient : recipients) {
            savedNotifications.add(dispatchSingleUserNotification(request, normalizeType(request.getType()), recipient, operator));
        }

        return NotificationDispatchResponse.builder()
                .sentCount(savedNotifications.size())
                .level(normalizeLevel(request.getLevel()).name())
                .orgUid(request.getOrgUid())
                .deptUid(request.getDeptUid())
                .userUid(request.getUserUid())
                .build();
    }

    public NotificationEntity dispatchSystemNotificationToUser(NotificationRequest request) {
        validateNotificationRequest(request);
        Assert.hasText(request.getUserUid(), "Notification recipient UID cannot be empty");

        NotificationEntity entity = modelMapper.map(request, NotificationEntity.class);
        entity.setUid(uidUtils.getUid());
        entity.setType(normalizeType(request.getType()));
        entity.setStatus(NotificationStatusEnum.UNREAD.name());
        entity.setUserUid(request.getUserUid());
        entity.setCreatorUid(request.getCreatorUid());
        entity.setOrgUid(request.getOrgUid());
        entity.setLevel(normalizeLevel(request.getLevel()).name());
        entity.setExtra(resolveExtra(request));
        return notificationRepository.save(entity);
    }

    private NotificationEntity dispatchSingleUserNotification(NotificationRequest request, String type, UserEntity recipient, UserEntity operator) {
        NotificationEntity entity = modelMapper.map(request, NotificationEntity.class);
        entity.setUid(uidUtils.getUid());
        entity.setType(type);
        entity.setStatus(NotificationStatusEnum.UNREAD.name());
        entity.setUserUid(recipient.getUid());
        entity.setCreatorUid(operator.getUid());
        entity.setOrgUid(resolveRecipientOrgUid(request, recipient));
        entity.setLevel(normalizeLevel(request.getLevel()).name());
        entity.setExtra(resolveExtra(request));
        return notificationRepository.save(entity);
    }

    /**
     * Validate the notification request.
     * 
     * @param request the notification request to validate
     * @throws IllegalArgumentException if the request is invalid
     */
    private void validateNotificationRequest(NotificationRequest request) {
        Assert.notNull(request, "Notification request cannot be null");
        Assert.hasText(request.getTitle(), "Notification title cannot be null or empty");
        Assert.hasText(request.getContent(), "Notification content cannot be null or empty");
    }

    private UserEntity validateDispatchPermission(NotificationRequest request) {
        if (StringUtils.hasText(request.getCreatorUid())) {
            return userRepository.findByUid(request.getCreatorUid())
                    .orElseThrow(() -> new RuntimeException("Notification creator not found: " + request.getCreatorUid()));
        }

        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("Current user is required");
        }

        LevelEnum level = normalizeLevel(request.getLevel());
        if (LevelEnum.PLATFORM == level && !user.isSuperUser()) {
            throw new RuntimeException("Only super user can send platform notifications");
        }

        if (!user.isSuperUser() && !isAdmin(user)) {
            throw new RuntimeException("Only admin can send notifications");
        }

        return user;
    }

    private boolean isAdmin(UserEntity user) {
        if (user == null) {
            return false;
        }
        if (user.isSuperUser()) {
            return true;
        }
        if (user.getCurrentRoles() == null) {
            return false;
        }
        return user.getCurrentRoles().stream()
                .map(RoleEntity::getName)
                .anyMatch(ROLE_ADMIN::equals);
    }

    private Set<UserEntity> resolveRecipients(NotificationRequest request) {
        LevelEnum level = normalizeLevel(request.getLevel());
        return switch (level) {
            case PLATFORM -> resolvePlatformRecipients();
            case ORGANIZATION -> resolveOrganizationRecipients(request.getOrgUid());
            case DEPARTMENT -> resolveDepartmentRecipients(request.getDeptUid());
            case USER -> new LinkedHashSet<>(List.of(userRepository.findByUid(request.getUserUid())
                        .orElseThrow(() -> new RuntimeException("Notification recipient not found: " + request.getUserUid()))));
                    default -> throw new RuntimeException("Unsupported notification level: " + level.name());
        };
    }

    private Set<UserEntity> resolvePlatformRecipients() {
        return new LinkedHashSet<>(userRepository.findAll().stream()
                .filter(user -> !user.isDeleted())
                .toList());
    }

    private Set<UserEntity> resolveOrganizationRecipients(String orgUid) {
        Assert.hasText(orgUid, "Organization UID cannot be empty when sending organization notification");
        return new LinkedHashSet<>(memberRepository.findAll().stream()
                .filter(member -> !member.isDeleted())
                .filter(member -> orgUid.equals(member.getOrgUid()))
                .map(MemberEntity::getUser)
                .filter(java.util.Objects::nonNull)
                .toList());
    }

    private Set<UserEntity> resolveDepartmentRecipients(String deptUid) {
        Assert.hasText(deptUid, "Department UID cannot be empty when sending department notification");
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

    private String normalizeType(String type) {
        return NotificationTypeEnum.fromValue(type).name();
    }

    private String resolveRecipientOrgUid(NotificationRequest request, UserEntity recipient) {
        if (StringUtils.hasText(request.getOrgUid())) {
            return request.getOrgUid();
        }
        return recipient.getOrgUid();
    }

    private String resolveExtra(NotificationRequest request) {
        if (StringUtils.hasText(request.getExtra())) {
            return request.getExtra();
        }

        NotificationProtobuf notificationProtobuf = modelMapper.map(request, NotificationProtobuf.class);
        notificationProtobuf.setContent(request.getContent());
        return notificationProtobuf.toJson();
    }
}
