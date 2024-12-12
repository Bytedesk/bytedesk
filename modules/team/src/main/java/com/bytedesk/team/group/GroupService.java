/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-23 18:21:39
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.team.group;

import java.util.Iterator;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.team.member.MemberEntity;
import com.bytedesk.team.member.MemberRestService;

import lombok.AllArgsConstructor;

// @Slf4j
@Service
@AllArgsConstructor
public class GroupService extends BaseRestService<GroupEntity, GroupRequest, GroupResponse> {

    private GroupRepository groupRepository;

    private AuthService authService;

    private ModelMapper modelMapper;

    private UidUtils uidUtils;

    private MemberRestService memberService;

    // private ThreadService threadService;

    @Override
    public Page<GroupResponse> queryByOrg(GroupRequest request) {
        
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Direction.DESC, "updatedAt");

        Specification<GroupEntity> specification = GroupSpecification.search(request);
        Page<GroupEntity> page = groupRepository.findAll(specification, pageable);

        return page.map(this::convertToResponse);
    }

    @Override
    public Page<GroupResponse> queryByUser(GroupRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    public GroupResponse queryByUid(GroupRequest request) {
        Optional<GroupEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            return convertToResponse(optional.get());
        } else {
            throw new RuntimeException("Failed to query group by uid: " + request.getUid());
        }
    }

    @Override
    public Optional<GroupEntity> findByUid(String uid) {
        return groupRepository.findByUid(uid);
    }

    @Override
    public GroupResponse create(GroupRequest request) {

        UserEntity creator = authService.getUser();
        // 
        GroupEntity group = GroupEntity.builder().build();
        group.setName(request.getName());
        group.setUid(uidUtils.getCacheSerialUid());
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

    @Override
    public GroupEntity save(GroupEntity entity) {
        try {
            return groupRepository.save(entity);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;
    }

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
        // delete(entity.getUid());
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, GroupEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public GroupResponse convertToResponse(GroupEntity entity) {
        return modelMapper.map(entity, GroupResponse.class);
    }

    

}
