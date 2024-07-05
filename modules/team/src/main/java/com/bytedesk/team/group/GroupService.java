/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-01 18:36:47
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

import com.bytedesk.core.base.BaseService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.User;
import com.bytedesk.core.thread.ThreadService;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.team.member.Member;
import com.bytedesk.team.member.MemberService;

import lombok.AllArgsConstructor;

// @Slf4j
@Service
@AllArgsConstructor
public class GroupService extends BaseService<Group, GroupRequest, GroupResponse> {

    private GroupRepository groupRepository;

    private AuthService authService;

    private ModelMapper modelMapper;

    private UidUtils uidUtils;

    private MemberService memberService;

    private ThreadService threadService;

    @Override
    public Page<GroupResponse> queryByOrg(GroupRequest request) {
        
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Direction.DESC, "updatedAt");

        Specification<Group> specification = GroupSpecification.search(request);
        Page<Group> page = groupRepository.findAll(specification, pageable);

        return page.map(this::convertToResponse);
    }

    @Override
    public Page<GroupResponse> queryByUser(GroupRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    public GroupResponse queryByUid(GroupRequest request) {
        Optional<Group> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            return convertToResponse(optional.get());
        } else {
            throw new RuntimeException("Failed to query group by uid: " + request.getUid());
        }
    }

    @Override
    public Optional<Group> findByUid(String uid) {
        return groupRepository.findByUid(uid);
    }

    @Override
    public GroupResponse create(GroupRequest request) {

        User creator = authService.getCurrentUser();
        // 
        Group group = Group.builder().build();
        group.setName(request.getName());
        group.setUid(uidUtils.getCacheSerialUid());
        group.setType(GroupTypeEnum.fromValue(request.getType()));
        // 
        group.getAdmins().add(creator);
        // 
        if (request.getMemberUids() != null && request.getMemberUids().size() > 0) {
            Iterator<String> it = request.getMemberUids().iterator();
            while (it.hasNext()) {
                String memberUid = it.next();
                Optional<Member> optionalMember = memberService.findByUid(memberUid);
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
        Group saved = save(group);
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
        Optional<Group> groupOptional = findByUid(request.getUid());
        if (groupOptional.isPresent()) {
            Group group = groupOptional.get();
            group.setStatus(GroupStatusEnum.DISMISSED);
            //
            save(group);
            // 解散相关会话thread
            threadService.dismissByTopic(TopicUtils.TOPIC_ORG_GROUP_PREFIX + group.getUid());
        }
        // 
        throw new RuntimeException("Failed to dismiss group by uid: " + request.getUid());
    }

    @Override
    public Group save(Group entity) {
        try {
            return groupRepository.save(entity);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteByUid'");
    }

    @Override
    public void delete(Group entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, Group entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public GroupResponse convertToResponse(Group entity) {
        return modelMapper.map(entity, GroupResponse.class);
    }

    

}
