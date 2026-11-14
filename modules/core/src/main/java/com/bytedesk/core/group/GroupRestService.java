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
import java.util.Iterator;
import java.util.Optional;
import java.util.List;
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
        // 查找群组
        Optional<GroupEntity> groupOptional = findByUid(request.getUid());
        if (!groupOptional.isPresent()) {
            throw new RuntimeException("群组不存在: " + request.getUid());
        }

        GroupEntity group = groupOptional.get();
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
                    .map(member -> modelMapper.map(member, MemberProtobuf.class))
                    .collect(Collectors.toList());
            log.info("memberList content: {}", content);
        }

        return new org.springframework.data.domain.PageImpl<>(
                content, pageable, memberList.size());
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
        group.getAdmins().add(creator);
        //
        if (request.getMemberUids() != null && request.getMemberUids().size() > 0) {
            Iterator<String> it = request.getMemberUids().iterator();
            while (it.hasNext()) {
                String memberUid = it.next();
                Optional<MemberEntity> optionalMember = memberRestService.findByUid(memberUid);
                if (optionalMember.isPresent()) {
                    group.getMembers().add(optionalMember.get());
                } else {
                    throw new RuntimeException("Failed to find member by uid: " + memberUid);
                }
            }
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

    // join
    public GroupResponse join(GroupRequest request) {
        //
        Optional<GroupEntity> groupOptional = findByUid(request.getUid());
        if (groupOptional.isPresent()) {
            GroupEntity group = groupOptional.get();
            //
            if (request.getMemberUids() == null || request.getMemberUids().isEmpty()) {
                throw new RuntimeException("No members to join");
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
                throw new RuntimeException("Failed to join group");
            }
            return convertToResponse(saved);
        }
        //
        throw new RuntimeException("Failed to join group by uid: " + request.getUid());
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
                    .map(member -> modelMapper.map(member, MemberProtobuf.class))
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
