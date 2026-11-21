/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 18:10:39
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.group;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.exception.CommonI18nExceptions;
import com.bytedesk.core.group.apply.GroupMemberApplyRequest;
import com.bytedesk.core.group.apply.GroupMemberApplyService;
import com.bytedesk.core.group.enums.GroupStatusEnum;
import com.bytedesk.core.group.enums.GroupTypeEnum;
import com.bytedesk.core.group.enums.GroupVisibilityEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.member.MemberEntity;
import com.bytedesk.core.member.MemberRestService;
import com.bytedesk.core.member.MemberProtobuf;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupRestService extends BaseRestServiceWithExport<GroupEntity, GroupRequest, GroupResponse, GroupExcel> {

    private final GroupRepository groupRepository;

    private final AuthService authService;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final MemberRestService memberRestService;

    private final ThreadRestService threadRestService;

    // 可选注入：ai 模块实现校验，starter 聚合时自动装配。
    // fail-closed：core 单独部署（无 ai 模块）时，机器人管理接口直接拒绝，而不是跳过校验。
    private final ObjectProvider<GroupRobotValidator> groupRobotValidatorProvider;

    // 群组统一授权（可见性/成员/管理员判定）
    private final GroupAccessService groupAccessService;

    // 入群申请（join 分流与失效联动）
    private final GroupMemberApplyService groupMemberApplyService;

    @Override
    protected Specification<GroupEntity> createSpecification(GroupRequest request) {
        return GroupSpecification.search(request, authService);
    }

    @Override
    protected Page<GroupEntity> executePageQuery(Specification<GroupEntity> spec, Pageable pageable) {
        return groupRepository.findAll(spec, pageable);
    }

    /**
     * 分页查询群组成员
     * 
     * @param request 包含群组uid和分页参数的请求
     * @return 成员列表的分页结果
     */
    public Page<MemberProtobuf> queryGroupMembers(GroupRequest request) {
        // 查找群组（私有群非成员 404，不暴露存在性）
        GroupEntity group = groupAccessService.requireReadableGroup(request.getUid());
        Pageable pageable = request.getPageable();

        // 使用Spring Data的分页功能，从集合中获取指定页的数据
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;

        log.info("pagination params - pageSize: {}, currentPage: {}, startItem: {}, totalSize: {}",
                pageSize, currentPage, startItem, group.getMembers().size());

        List<MemberEntity> memberList = group.getMembers();
        log.info("memberList before filter: {}", memberList);
        // 如果 request.searchText 不为空，则需要根据nickname过滤成员
        if (StringUtils.hasText(request.getSearchText())) {
            memberList = memberList.stream()
                    .filter(member -> member.getNickname() != null &&
                            member.getNickname().toLowerCase().contains(request.getSearchText().toLowerCase()))
                    .collect(Collectors.toList());
        }
        log.info("memberList after filter: {}", memberList);
        List<MemberProtobuf> content;

        if (memberList.size() < startItem) {
            content = List.of();
            log.info("memberList startItem is greater than memberList size: {}", content);
        } else {
            int toIndex = Math.min(startItem + pageSize, memberList.size());
            content = memberList.subList(startItem, toIndex).stream()
                    .map(this::convertToMemberProtobuf)
                    .collect(Collectors.toList());
            log.info("memberList content: {}", content);
        }

        return new org.springframework.data.domain.PageImpl<>(
                content, pageable, memberList.size());
    }

    /**
     * 成员 -> 提及候选 DTO：显式补充关联账号 UID（MemberEntity.user 为懒加载关联，
     * ModelMapper 默认映射不会带出 userUid）
     */
    private MemberProtobuf convertToMemberProtobuf(MemberEntity member) {
        MemberProtobuf protobuf = modelMapper.map(member, MemberProtobuf.class);
        if (member.getUser() != null) {
            protobuf.setUserUid(member.getUser().getUid());
        }
        return protobuf;
    }

    @Cacheable(value = "group", key = "#uid", unless = "#result == null")
    @Override
    public Optional<GroupEntity> findByUid(String uid) {
        return groupRepository.findByUid(uid);
    }

    @Override
    public GroupResponse create(GroupRequest request) {
        UserEntity creator = authService.getUser();
        if (creator == null) {
            throw new RuntimeException("group creator is null");
        }
        //
        GroupEntity group = GroupEntity.builder().build();
        if (StringUtils.hasText(request.getName())) {
            group.setName(request.getName());
        }
        if (StringUtils.hasText(request.getAvatar())) {
            group.setAvatar(request.getAvatar());
        }
        group.setUid(uidUtils.getUid());
        // 
        GroupTypeEnum groupType = GroupTypeEnum.NORMAL;
        if (StringUtils.hasText(request.getType())) {
            try {
                groupType = GroupTypeEnum.fromValue(request.getType());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid group type: {}, fallback to NORMAL", request.getType());
            }
        }
        group.setType(groupType.name());
        // 可见性：非法/未传回退 PRIVATE（fail-closed）；公开群可带 needApproval（默认 true）
        group.setVisibility(GroupVisibilityEnum.fromValue(request.getVisibility()).name());
        if (GroupVisibilityEnum.PUBLIC.name().equals(group.getVisibility())
                && request.getNeedApproval() != null) {
            group.setNeedApproval(request.getNeedApproval());
        }
        group.getAdmins().add(creator);
        //
        Set<String> addedMemberUids = new HashSet<>();
        if (request.getMemberUids() != null && request.getMemberUids().size() > 0) {
            Iterator<String> it = request.getMemberUids().iterator();
            while (it.hasNext()) {
                String memberUid = it.next();
                Optional<MemberEntity> optionalMember = memberRestService.findByUid(memberUid);
                if (optionalMember.isPresent()) {
                    MemberEntity member = optionalMember.get();
                    if (addedMemberUids.add(member.getUid())) {
                        group.getMembers().add(member);
                    }
                } else {
                    throw new RuntimeException("Failed to find member by uid: " + memberUid);
                }
            }
        }
        // 创建者必须作为实际成员入群（服务端绑定，不依赖前端成员列表）
        Optional<MemberEntity> creatorMember = memberRestService.findByUserUidAndOrgUid(creator.getUid(), creator.getOrgUid());
        if (creatorMember.isPresent()) {
            MemberEntity member = creatorMember.get();
            if (addedMemberUids.add(member.getUid())) {
                group.getMembers().add(member);
            }
        } else {
            throw new RuntimeException("group creator member not found in org: " + creator.getOrgUid());
        }
        //
        group.setCreator(creator);
        group.setUserUid(creator.getUid());
        group.setOrgUid(creator.getOrgUid());
        //
        // 处理创建时的机器人列表（fail-closed：ai 未装配或校验失败均拒绝创建）
        if (request.getRobotUids() != null && !request.getRobotUids().isEmpty()) {
            List<String> robotUids = normalizeRobotUids(request.getRobotUids(), "create");
            validateRobotUids(robotUids, creator.getOrgUid(), creator.getUid());
            robotUids.forEach(group::addRobot);
            validateRobotUidCapacity(group.getRobotUids());
        }
        //
        GroupEntity saved = save(group);
        if (saved == null) {
            throw new RuntimeException("Failed to create group");
        }
        return convertToResponse(saved);
    }

    @Override
    public GroupResponse update(GroupRequest request) {
        Optional<GroupEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            GroupEntity group = optional.get();
            //
            group.setName(request.getName());
            group.setAvatar(request.getAvatar());
            group.setDescription(request.getDescription());
            // group.setType(GroupTypeEnum.fromValue(request.getType()).name());
            // group.setStatus(GroupStatusEnum.fromValue(request.getStatus()).name());
            // group.setExternal(request.getIsExternal());
            //
            // if (request.getMemberUids() != null && request.getMemberUids().size() > 0) {
            // group.getMembers().clear();
            // Iterator<String> it = request.getMemberUids().iterator();
            // while (it.hasNext()) {
            // String memberUid = it.next();
            // Optional<MemberEntity> optionalMember = memberService.findByUid(memberUid);
            // if (optionalMember.isPresent()) {
            // group.getMembers().add(optionalMember.get());
            // } else {
            // throw new RuntimeException("Failed to find member by uid: " + memberUid);
            // }
            // }
            // }
            //
            GroupEntity saved = save(group);
            if (saved == null) {
                throw new RuntimeException("Failed to update group");
            }
            return convertToResponse(saved);
        }
        //
        throw new RuntimeException("Failed to update group by uid: " + request.getUid());
    }

    // update/name
    public GroupResponse updateGroupName(GroupRequest request) {
        //
        Optional<GroupEntity> groupOptional = findByUid(request.getUid());
        if (groupOptional.isPresent()) {
            GroupEntity group = groupOptional.get();
            //
            group.setName(request.getName());
            //
            GroupEntity saved = save(group);
            if (saved == null) {
                throw new RuntimeException("Failed to update group name");
            }
            //
            List<MemberEntity> members = group.getMembers();
            if (members != null && members.size() > 0) {
                for (MemberEntity member : members) {
                    String user = UserProtobuf.builder()
                            .uid(group.getUid())
                            .nickname(request.getName())
                            .avatar(group.getAvatar())
                            .build()
                            .toJson();
                    String topic = TopicUtils.TOPIC_ORG_GROUP_PREFIX + group.getUid();
                    log.info("update group member thread for user: {}, topic: {}", user, topic);
                    threadRestService.updateGroupMemberThread(user, topic, member.getUser());
                }
            }

            return convertToResponse(saved);
        }
        //
        throw new RuntimeException("Failed to update group name by uid: " + request.getUid());
    }

    // update/topTip
    public GroupResponse updateGroupTopTip(GroupRequest request) {
        //
        Optional<GroupEntity> groupOptional = findByUid(request.getUid());
        if (groupOptional.isPresent()) {
            GroupEntity group = groupOptional.get();
            //
            group.setShowTopTip(request.getShowTopTip());
            group.setTopTip(request.getTopTip());
            //
            GroupEntity saved = save(group);
            if (saved == null) {
                throw new RuntimeException("Failed to update group top tip");
            }
            return convertToResponse(saved);
        }
        //
        throw new RuntimeException("Failed to update group top tip by uid: " + request.getUid());
    }

    // 添加群机器人（仅群主/群管理员）
    @Transactional
    public GroupResponse addRobots(GroupRequest request) {
        UserEntity operator = requireGroupAdmin(request.getUid());
        GroupEntity group = findByUid(request.getUid())
                .orElseThrow(() -> new RuntimeException("Failed to find group by uid: " + request.getUid()));

        List<String> robotUids = normalizeRobotUids(request.getRobotUids(), "add");
        validateRobotUids(robotUids, group.getOrgUid(), operator.getUid());
        robotUids.forEach(group::addRobot);
        validateRobotUidCapacity(group.getRobotUids());

        GroupEntity saved = save(group);
        if (saved == null) {
            throw new RuntimeException("Failed to add robots to group");
        }
        return convertToResponse(saved);
    }

    // 移除群机器人（仅群主/群管理员），幂等：移除不存在的机器人返回当前状态
    @Transactional
    public GroupResponse removeRobots(GroupRequest request) {
        requireGroupAdmin(request.getUid());
        GroupEntity group = findByUid(request.getUid())
                .orElseThrow(() -> new RuntimeException("Failed to find group by uid: " + request.getUid()));

        List<String> robotUids = normalizeRobotUids(request.getRobotUids(), "remove");
        robotUids.forEach(group::removeRobot);

        GroupEntity saved = save(group);
        if (saved == null) {
            throw new RuntimeException("Failed to remove robots from group");
        }
        // TODO: 一期 A4 任务表落地后，此处同时取消该群未执行的机器人任务
        return convertToResponse(saved);
    }

    // 查询群机器人UID列表（群成员/管理员可见）
    public List<String> queryRobotUids(String groupUid) {
        GroupEntity group = findByUid(groupUid)
                .orElseThrow(() -> new RuntimeException("Failed to find group by uid: " + groupUid));
        List<String> robotUids = group.getRobotUids();
        return robotUids == null ? new ArrayList<>() : new ArrayList<>(robotUids);
    }

    /**
     * 校验操作者为群主或群管理员，返回操作者。群组不存在/已解散时抛 NotFound。
     */
    private UserEntity requireGroupAdmin(String groupUid) {
        UserEntity operator = authService.getUser();
        if (operator == null || !StringUtils.hasText(operator.getUid())) {
            throw CommonI18nExceptions.loginRequired();
        }
        GroupEntity group = findByUid(groupUid)
                .orElseThrow(() -> new RuntimeException("Failed to find group by uid: " + groupUid));
        boolean isAdmin = group.getCreator() != null && operator.getUid().equals(group.getCreator().getUid());
        if (!isAdmin) {
            isAdmin = group.getAdmins().stream()
                    .anyMatch(admin -> admin != null && operator.getUid().equals(admin.getUid()));
        }
        if (!isAdmin) {
            throw CommonI18nExceptions.forbidden("group.robot.admin.required");
        }
        return operator;
    }

    /**
     * 规范化机器人UID输入：trim、去空、去重，拒绝非法字符。
     */
    private List<String> normalizeRobotUids(List<String> robotUids, String action) {
        if (robotUids == null || robotUids.isEmpty()) {
            throw new RuntimeException("No robots to " + action);
        }
        // CSV 存储：UID 中不允许逗号/换行；系统 UID 为数字串，此处按白名单字符校验
        List<String> normalized = robotUids.stream()
                .filter(StringUtils::hasText)
                .map(uid -> uid.trim())
                .distinct()
                .collect(Collectors.toList());
        if (normalized.isEmpty()) {
            throw new RuntimeException("No robots to " + action);
        }
        for (String uid : normalized) {
            if (uid.length() > 128 || !uid.matches("[a-zA-Z0-9_-]+")) {
                throw new RuntimeException("Invalid robot uid: " + uid);
            }
        }
        return normalized;
    }

    /**
     * fail-closed 校验：ai 模块未装配或校验不通过时拒绝，绝不静默入库。
     */
    private void validateRobotUids(List<String> robotUids, String orgUid, String operatorUserUid) {
        GroupRobotValidator validator = groupRobotValidatorProvider.getIfAvailable();
        if (validator == null) {
            throw new RuntimeException("Group robot capability is not available on this deployment");
        }
        List<String> invalid = validator.findInvalidRobotUids(robotUids, orgUid, operatorUserUid);
        if (invalid != null && !invalid.isEmpty()) {
            throw new RuntimeException("Failed to find robots by uids: " + String.join(",", invalid));
        }
    }

    /**
     * 序列化容量校验：CSV 长度必须落在列宽内，避免数据库静默截断。
     */
    private void validateRobotUidCapacity(List<String> robotUids) {
        int maxTotalLength = BytedeskConsts.COLUMN_EXTRA_LENGTH; // 与列定义一致
        int total = robotUids.stream().mapToInt(uid -> uid.length()).sum() + Math.max(0, robotUids.size() - 1); // 逗号分隔符
        if (total > maxTotalLength) {
            throw new RuntimeException("Group robot uid capacity exceeded: " + total + " > " + maxTotalLength);
        }
    }

    // invite
    public GroupResponse invite(GroupRequest request) {
        //
        Optional<GroupEntity> groupOptional = findByUid(request.getUid());
        if (groupOptional.isPresent()) {
            GroupEntity group = groupOptional.get();
            //
            if (request.getMemberUids() == null || request.getMemberUids().isEmpty()) {
                throw new RuntimeException("No members to invite");
            }

            for (String memberUid : request.getMemberUids()) {
                Optional<MemberEntity> memberOptional = memberRestService.findByUid(memberUid);
                if (!memberOptional.isPresent()) {
                    throw new RuntimeException("Failed to find member by uid: " + memberUid);
                }
                group.getMembers().add(memberOptional.get());

                // 订阅topic
                String user = UserProtobuf.builder()
                        .uid(group.getUid())
                        .nickname(group.getName())
                        .avatar(group.getAvatar())
                        .build()
                        .toJson();
                String topic = TopicUtils.TOPIC_ORG_GROUP_PREFIX + group.getUid();
                String orgUid = group.getOrgUid();
                log.info("Creating group member thread for user: {}, topic: {}, orgUid: {}", user, topic, orgUid);
                threadRestService.createGroupMemberThread(user, topic, orgUid, memberOptional.get().getUser());
            }
            //
            GroupEntity saved = save(group);
            if (saved == null) {
                throw new RuntimeException("Failed to invite users to group");
            }
            //
            return convertToResponse(saved);
        }
        //
        throw new RuntimeException("Failed to invite users to group by uid: " + request.getUid());
    }

    // join：自助加入闸门（规划 §4.3/§4.4）
    // 1. 私有群 403（仅能邀请）；2. 已成员幂等；3. 满员拒绝；4. 免审批直接加入；5. 需审批分流到申请
    @Transactional
    public GroupResponse join(GroupRequest request) {
        UserEntity applicant = authService.getUser();
        if (applicant == null || !StringUtils.hasText(applicant.getUid())) {
            throw CommonI18nExceptions.loginRequired();
        }
        if (!StringUtils.hasText(request.getUid())) {
            throw new RuntimeException("group uid is required");
        }
        GroupEntity group = groupAccessService.findGroupOrNotFound(request.getUid());
        // 申请人必须与群同组织
        if (!applicant.getOrgUid().equals(group.getOrgUid())) {
            throw new RuntimeException("Failed to find group by uid: " + request.getUid());
        }
        // 私有群仅能通过邀请加入
        if (groupAccessService.isPrivate(group)) {
            throw GroupI18nExceptions.forbiddenJoinPrivate();
        }
        // 通过 userUid 解析申请人 Member（不信任客户端 memberUids）
        Optional<MemberEntity> memberOptional = memberRestService.findByUserUidAndOrgUid(applicant.getUid(), group.getOrgUid());
        if (memberOptional.isEmpty()) {
            throw new RuntimeException("member not found for user: " + applicant.getUid());
        }
        MemberEntity member = memberOptional.get();
        // 已是成员：幂等返回
        if (group.getMembers().stream().anyMatch(m -> m.getUid().equals(member.getUid()))) {
            return convertToResponse(group);
        }
        // 容量校验
        checkJoinCapacity(group, 1);
        // 需审批：分流到申请接口
        if (Boolean.TRUE.equals(group.getNeedApproval())) {
            GroupMemberApplyRequest applyRequest = GroupMemberApplyRequest.builder()
                    .groupUid(group.getUid())
                    .build();
            groupMemberApplyService.create(applyRequest);
            // 返回提示：已提交申请（复用响应体，带 pendingApply 标记）
            GroupResponse response = convertToResponse(group);
            response.setPendingApply(true);
            response.setJoined(false);
            return response;
        }
        // 免审批：直接加入
        addSelfToGroup(group, member, applicant);
        GroupEntity saved = save(group);
        if (saved == null) {
            throw new RuntimeException("Failed to join group");
        }
        GroupResponse response = convertToResponse(saved);
        response.setJoined(true);
        response.setPendingApply(false);
        return response;
    }

    /** 公开群搜索（规划 §4.2）：仅 PUBLIC+NORMAL+本组织，含 joined/pendingApply 标记 */
    @Transactional(readOnly = true)
    public Page<GroupResponse> queryPublic(GroupRequest request) {
        UserEntity user = authService.getUser();
        if (user == null || !StringUtils.hasText(user.getUid())) {
            throw CommonI18nExceptions.loginRequired();
        }
        // 强制服务端上下文：公开模式 + 当前用户 + 当前组织
        request.setPublicOnly(true);
        request.setUserUid(user.getUid());
        request.setOrgUid(user.getOrgUid());
        request.setLevel(null);
        request.setSuperUser(false);
        //
        Pageable pageable = request.getPageable();
        Specification<GroupEntity> spec = GroupSpecification.search(request, authService);
        Page<GroupEntity> page = groupRepository.findAll(spec, pageable);
        // 批量计算 pendingApply（避免 N+1）
        List<String> groupUids = page.getContent().stream().map(group -> group.getUid()).collect(Collectors.toList());
        Map<String, Boolean> pendingMap = groupMemberApplyService.mapPendingByGroupUids(user.getOrgUid(), user.getUid(), groupUids);
        //
        return page.map(group -> {
            GroupResponse response = convertToResponse(group);
            // 公开列表不含成员预览/机器人等敏感字段（摘要 DTO 语义）
            response.setMemberPreview(null);
            response.setRobotUids(null);
            // 规划 §4.2/D9：包含已加入群，joined 标记由成员/管理员身份计算
            response.setJoined(groupAccessService.isMemberOrAdmin(group, user.getUid()));
            response.setPendingApply(pendingMap.getOrDefault(group.getUid(), false));
            return response;
        });
    }

    /** 可见性切换（群主/管理员）：公开→私有时失效待审批申请 */
    @Transactional
    public GroupResponse updateVisibility(GroupRequest request) {
        GroupEntity group = groupAccessService.requireAdministerableGroup(request.getUid());
        GroupVisibilityEnum visibility = GroupVisibilityEnum.fromValue(request.getVisibility());
        if (GroupVisibilityEnum.PRIVATE.name().equals(visibility.name())
                && groupAccessService.isPublic(group)) {
            // 公开转私有：失效待审批申请（行锁串行化）
            groupMemberApplyService.invalidatePendingByGroupUid(group.getOrgUid(), group.getUid());
        }
        group.setVisibility(visibility.name());
        GroupEntity saved = save(group);
        if (saved == null) {
            throw new RuntimeException("Failed to update group visibility");
        }
        return convertToResponse(saved);
    }

    /** 加入策略开关（群主/管理员） */
    @Transactional
    public GroupResponse updateJoinPolicy(GroupRequest request) {
        GroupEntity group = groupAccessService.requireAdministerableGroup(request.getUid());
        if (request.getNeedApproval() != null) {
            group.setNeedApproval(request.getNeedApproval());
        }
        GroupEntity saved = save(group);
        if (saved == null) {
            throw new RuntimeException("Failed to update group join policy");
        }
        return convertToResponse(saved);
    }

    /** 私有群单群详情保护：非成员/非管理员 404（不暴露存在性） */
    @Transactional(readOnly = true)
    public GroupResponse queryGroupDetail(GroupRequest request) {
        GroupEntity group = groupAccessService.requireReadableGroup(request.getUid());
        return convertToResponse(group);
    }

    private void checkJoinCapacity(GroupEntity group, int additions) {
        int max = group.getMaxMembers() == null ? 500 : group.getMaxMembers();
        if (group.getMembers().size() + additions > max) {
            throw GroupI18nExceptions.groupFull();
        }
    }

    private void addSelfToGroup(GroupEntity group, MemberEntity member, UserEntity applicant) {
        if (group.getMembers().stream().noneMatch(m -> m.getUid().equals(member.getUid()))) {
            group.getMembers().add(member);
            // 订阅 topic：创建本人群会话 thread
            String user = UserProtobuf.builder()
                    .uid(group.getUid())
                    .nickname(group.getName())
                    .avatar(group.getAvatar())
                    .build()
                    .toJson();
            String topic = TopicUtils.TOPIC_ORG_GROUP_PREFIX + group.getUid();
            threadRestService.createGroupMemberThread(user, topic, group.getOrgUid(), applicant);
        }
    }

    // remove
    public GroupResponse remove(GroupRequest request) {
        //
        Optional<GroupEntity> groupOptional = findByUid(request.getUid());
        if (groupOptional.isPresent()) {
            GroupEntity group = groupOptional.get();
            //
            if (request.getMemberUids() == null || request.getMemberUids().isEmpty()) {
                throw new RuntimeException("No members to remove");
            }

            log.info("Before remove - group members size: {}, members: {}", 
                group.getMembers().size(), 
                group.getMembers().stream().map(member -> member.getUid()).collect(Collectors.toList()));

            for (String memberUid : request.getMemberUids()) {
                Optional<MemberEntity> memberOptional = memberRestService.findByUid(memberUid);
                if (!memberOptional.isPresent()) {
                    throw new RuntimeException("Failed to find member by uid: " + memberUid);
                }
                MemberEntity member = memberOptional.get();
                log.info("Attempting to remove member: {} from group: {}", member.getUid(), group.getUid());
                
                boolean removed = group.getMembers().removeIf(m -> m.getUid().equals(member.getUid()));
                if (removed) {
                    log.info("Successfully removed member: {} from group", member.getUid());
                    
                    // 删除订阅topic
                    String topic = TopicUtils.TOPIC_ORG_GROUP_PREFIX + group.getUid();
                    threadRestService.removeGroupMemberThread(topic, member.getUser());
                } else {
                    log.warn("Failed to remove member: {} from group - member not found in group", member.getUid());
                }
            }

            log.info("After remove - group members size: {}, remaining members: {}", 
                group.getMembers().size(),
                group.getMembers().stream().map(member -> member.getUid()).collect(Collectors.toList()));
            
            //
            GroupEntity saved = save(group);
            if (saved == null) {
                throw new RuntimeException("Failed to remove users from group");
            }
            log.info("After save - group members size: {}, saved members: {}", 
                saved.getMembers().size(),
                saved.getMembers().stream().map(member -> member.getUid()).collect(Collectors.toList()));
            //
            return convertToResponse(saved);
        }
        //
        throw new RuntimeException("Failed to remove users from group by uid: " + request.getUid());
    }

    // leave
    public GroupResponse leave(GroupRequest request) {
        //
        Optional<GroupEntity> groupOptional = findByUid(request.getUid());
        if (groupOptional.isPresent()) {
            GroupEntity group = groupOptional.get();
            //
            if (request.getMemberUids() == null || request.getMemberUids().isEmpty()) {
                throw new RuntimeException("No members to leave");
            }

            log.info("Before leave - group members size: {}, members: {}", 
                group.getMembers().size(), 
                group.getMembers().stream().map(member -> member.getUid()).collect(Collectors.toList()));

            for (String memberUid : request.getMemberUids()) {
                Optional<MemberEntity> memberOptional = memberRestService.findByUid(memberUid);
                if (!memberOptional.isPresent()) {
                    throw new RuntimeException("Failed to find member by uid: " + memberUid);
                }
                MemberEntity member = memberOptional.get();
                log.info("Attempting to remove member: {} from group: {}", member.getUid(), group.getUid());
                
                boolean removed = group.getMembers().removeIf(m -> m.getUid().equals(member.getUid()));
                if (removed) {
                    log.info("Successfully removed member: {} from group", member.getUid());
                    
                    // 删除订阅topic
                    String topic = TopicUtils.TOPIC_ORG_GROUP_PREFIX + group.getUid();
                    threadRestService.removeGroupMemberThread(topic, member.getUser());
                } else {
                    log.warn("Failed to remove member: {} from group - member not found in group", member.getUid());
                }
            }

            log.info("After leave - group members size: {}, remaining members: {}", 
                group.getMembers().size(),
                group.getMembers().stream().map(member -> member.getUid()).collect(Collectors.toList()));
            
            //
            GroupEntity saved = save(group);
            if (saved == null) {
                throw new RuntimeException("Failed to remove users from group");
            }
            log.info("After save - group members size: {}, saved members: {}", 
                saved.getMembers().size(),
                saved.getMembers().stream().map(member -> member.getUid()).collect(Collectors.toList()));
            //
            return convertToResponse(saved);
        }
        //
        throw new RuntimeException("Failed to leave group by uid: " + request.getUid());
    }

    public GroupResponse dismiss(GroupRequest request) {
        //
        Optional<GroupEntity> groupOptional = findByUid(request.getUid());
        if (groupOptional.isPresent()) {
            GroupEntity group = groupOptional.get();
            group.setStatus(GroupStatusEnum.DISMISSED.name());
            //
            GroupEntity saved = save(group);
            if (saved == null) {
                throw new RuntimeException("Failed to dismiss group");
            }
            // 解散时失效所有待审批申请
            try {
                groupMemberApplyService.invalidatePendingByGroupUid(group.getOrgUid(), group.getUid());
            } catch (Exception ex) {
                log.warn("invalidate pending applies on dismiss failed: groupUid={}, error={}", group.getUid(), ex.getMessage());
            }
            // 删除topic
            List<MemberEntity> members = group.getMembers();
            if (members != null && members.size() > 0) {
                for (MemberEntity member : members) {
                    String topic = TopicUtils.TOPIC_ORG_GROUP_PREFIX + group.getUid();
                    threadRestService.removeGroupMemberThread(topic, member.getUser());
                }
            }
            //
            return convertToResponse(saved);
        }
        //
        throw new RuntimeException("Failed to dismiss group by uid: " + request.getUid());
    }

    @CachePut(value = "group", key = "#entity.uid")
    @Override
    protected GroupEntity doSave(GroupEntity entity) {
        return groupRepository.save(entity);
    }

    @CacheEvict(value = "group", key = "#uid")
    @Override
    public void deleteByUid(String uid) {
        Optional<GroupEntity> optional = findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // groupRepository.delete(optional.get());
        } else {
            throw new RuntimeException("Failed to delete group by uid: " + uid);
        }
    }

    @Override
    public void delete(GroupRequest entity) {
        deleteByUid(entity.getUid());
    }

    @Override
    public GroupEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            GroupEntity entity) {
        // 乐观锁处理实现
        try {
            Optional<GroupEntity> latest = groupRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                GroupEntity latestEntity = latest.get();
                // 合并需要保留的数据
                // 这里可以根据业务需求合并实体
                return groupRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public GroupResponse convertToResponse(GroupEntity entity) {
        GroupResponse response = modelMapper.map(entity, GroupResponse.class);

        // 添加成员计数
        response.setMemberCount(entity.getMembers().size());

        // 可以选择返回前N个成员作为预览
        if (entity.getMembers() != null && !entity.getMembers().isEmpty()) {
            List<MemberProtobuf> memberPreview = entity.getMembers().stream()
                    .limit(10) // 只返回前10个成员作为预览
                    .map(this::convertToMemberProtobuf)
                    .collect(Collectors.toList());
            response.setMemberPreview(memberPreview);
        }

        return response;
    }

    /**
     * 将群组实体转换为Excel导出对象
     */
    @Override
    public GroupExcel convertToExcel(GroupEntity group) {
        GroupExcel excel = new GroupExcel();

        excel.setUid(group.getUid());
        excel.setName(group.getName());
        excel.setAvatar(group.getAvatar());
        excel.setDescription(group.getDescription());
        excel.setType(group.getType());
        excel.setVisibility(group.getVisibility());
        excel.setStatus(group.getStatus());
        excel.setIsExternal(group.getExternal());
        excel.setCreator(group.getCreator() != null ? group.getCreator().getNickname() : "");
        excel.setMemberCount(group.getMembers().size());
        excel.setAdminCount(group.getAdmins().size());
        excel.setMaxMembers(group.getMaxMembers());
        excel.setNeedApproval(group.getNeedApproval());
        excel.setAllowInvite(group.getAllowInvite());
        excel.setMuteAll(group.getMuteAll());
        excel.setShowTopTip(group.getShowTopTip());
        excel.setTopTip(group.getTopTip());
        excel.setCreatedAt(group.getCreatedAt().toString());
        excel.setUpdatedAt(group.getUpdatedAt().toString());

        return excel;
    }

}
