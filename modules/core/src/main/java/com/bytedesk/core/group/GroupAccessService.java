/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-11-16
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *   contact: 270580156@qq.com 
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.group;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.exception.NotFoundException;
import com.bytedesk.core.group.enums.GroupVisibilityEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 群组统一授权能力（规划 §3.2）
 * 
 * 身份一律来自服务端认证上下文，不信任客户端传入的 userUid/orgUid；
 * 私有群对无权用户抛与"群不存在"一致的 NotFoundException，不暴露存在性。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GroupAccessService {

    private final AuthService authService;

    private final GroupRepository groupRepository;

    /**
     * 校验当前登录用户可读取该群详情（私有群需为成员/群主/管理员）
     * 统一返回群实体，群不存在或无权时抛 NotFoundException
     */
    public GroupEntity requireReadableGroup(String groupUid) {
        UserEntity user = requireUser();
        GroupEntity group = findGroupOrNotFound(groupUid);
        if (isPrivate(group) && !isMemberOrAdmin(group, user.getUid())) {
            // 与不存在返回一致，避免探测
            throw new NotFoundException("group not found: " + groupUid);
        }
        return group;
    }

    /** 当前登录用户必须是群主或群管理员 */
    public GroupEntity requireAdministerableGroup(String groupUid) {
        UserEntity user = requireUser();
        GroupEntity group = findGroupOrNotFound(groupUid);
        if (!isGroupAdmin(group, user.getUid())) {
            throw GroupI18nExceptions.forbiddenAdminRequired();
        }
        return group;
    }

    /** 非管理端读取（如公开摘要转详情）：公开群放行，私有群按成员校验 */
    public boolean canView(GroupEntity group, String userUid) {
        if (!isPrivate(group)) {
            return true;
        }
        return isMemberOrAdmin(group, userUid);
    }

    public boolean isPrivate(GroupEntity group) {
        return group == null
                || !StringUtils.hasText(group.getVisibility())
                || GroupVisibilityEnum.PRIVATE.name().equalsIgnoreCase(group.getVisibility());
    }

    public boolean isPublic(GroupEntity group) {
        return !isPrivate(group);
    }

    public boolean isMemberOrAdmin(GroupEntity group, String userUid) {
        return isMember(group, userUid) || isGroupAdmin(group, userUid);
    }

    public boolean isMember(GroupEntity group, String userUid) {
        if (group == null || !StringUtils.hasText(userUid)) {
            return false;
        }
        return group.getMembers().stream()
                .anyMatch(m -> m != null && m.getUser() != null && userUid.equals(m.getUser().getUid()));
    }

    public boolean isGroupAdmin(GroupEntity group, String userUid) {
        if (group == null || !StringUtils.hasText(userUid)) {
            return false;
        }
        if (group.getCreator() != null && userUid.equals(group.getCreator().getUid())) {
            return true;
        }
        return group.getAdmins().stream().anyMatch(a -> a != null && userUid.equals(a.getUid()));
    }

    public GroupEntity findGroupOrNotFound(String groupUid) {
        if (!StringUtils.hasText(groupUid)) {
            throw new NotFoundException("group uid required");
        }
        Optional<GroupEntity> optional = groupRepository.findByUid(groupUid);
        if (optional.isEmpty() || optional.get().isDeleted()) {
            throw new NotFoundException("group not found: " + groupUid);
        }
        return optional.get();
    }

    public UserEntity requireUser() {
        UserEntity user = authService.getUser();
        if (user == null || !StringUtils.hasText(user.getUid())) {
            throw com.bytedesk.core.exception.CommonI18nExceptions.loginRequired();
        }
        return user;
    }
}
