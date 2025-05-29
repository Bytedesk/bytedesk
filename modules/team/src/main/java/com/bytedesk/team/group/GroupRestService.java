/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-29 14:57:34
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.team.group;

import java.util.Iterator;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestServiceWithExcel;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.team.member.MemberEntity;
import com.bytedesk.team.member.MemberRestService;
import com.bytedesk.team.member.MemberProtobuf;

import lombok.RequiredArgsConstructor;

// @Slf4j
@Service
@RequiredArgsConstructor
public class GroupRestService extends BaseRestServiceWithExcel<GroupEntity, GroupRequest, GroupResponse, GroupExcel> {

    private final GroupRepository groupRepository;

    private final AuthService authService;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final MemberRestService memberService;

    @Override
    public Page<GroupEntity> queryByOrgEntity(GroupRequest request) {
        Pageable pageable = request.getPageable();
        Specification<GroupEntity> specification = GroupSpecification.search(request);
        return groupRepository.findAll(specification, pageable);
    }

    @Override
    public Page<GroupResponse> queryByOrg(GroupRequest request) {
        Pageable pageable = request.getPageable();
        Specification<GroupEntity> specification = GroupSpecification.search(request);
        Page<GroupEntity> page = groupRepository.findAll(specification, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<GroupResponse> queryByUser(GroupRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("null");
        }
        request.setUserUid(user.getUid());
        request.setOrgUid(user.getOrgUid());
        //
        return queryByOrg(request);
    }

    public Page<GroupEntity> queryForExport(GroupRequest request) {
        Pageable pageable = request.getPageable();
        Specification<GroupEntity> specification = GroupSpecification.search(request);
        return groupRepository.findAll(specification, pageable);
    }

    public GroupResponse queryByUid(GroupRequest request) {
        Optional<GroupEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            return convertToResponse(optional.get());
        } else {
            throw new RuntimeException("Failed to query group by uid: " + request.getUid());
        }
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
        group.setName(request.getName());
        group.setUid(uidUtils.getUid());
        group.setType(GroupTypeEnum.fromValue(request.getType()).name());
        // 
        group.getAdmins().add(creator);
        // 
        if (request.getMemberUids() != null && request.getMemberUids().size() > 0) {
            Iterator<String> it = request.getMemberUids().iterator();
            while (it.hasNext()) {
                String memberUid = it.next();
                Optional<MemberEntity> optionalMember = memberService.findByUid(memberUid);
                if (optionalMember.isPresent()) {
                    group.getMembers().add(optionalMember.get());
                } else {
                    throw new RuntimeException("Failed to find member by uid: " + memberUid);
                }
            }
        }
        group.setCreator(creator);
        group.setOrgUid(creator.getOrgUid());
        // 
        GroupEntity saved = save(group);
        if (saved == null) {
            throw new RuntimeException("Failed to create group");
        }
        return convertToResponse(saved);
    }

    @Override
    public GroupResponse update(GroupRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    public void dismiss(GroupRequest request) {
        // 
        Optional<GroupEntity> groupOptional = findByUid(request.getUid());
        if (groupOptional.isPresent()) {
            GroupEntity group = groupOptional.get();
            group.setStatus(GroupStatusEnum.DISMISSED.name());
            //
            save(group);
            // 解散相关会话thread
            // threadService.dismissByTopic(TopicUtils.TOPIC_ORG_GROUP_PREFIX + group.getUid());
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
    public GroupEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, GroupEntity entity) {
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

    /**
     * 分页查询群组成员
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
        
        List<MemberEntity> memberList = group.getMembers();
        List<MemberProtobuf> content;
        
        if (memberList.size() < startItem) {
            content = List.of();
        } else {
            int toIndex = Math.min(startItem + pageSize, memberList.size());
            content = memberList.subList(startItem, toIndex).stream()
                    .map(member -> modelMapper.map(member, MemberProtobuf.class))
                    .collect(Collectors.toList());
        }
        
        return new org.springframework.data.domain.PageImpl<>(
                content, pageable, memberList.size());
    }

}
