/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-26 09:15:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.team.member;

import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.enums.PlatformEnum;
import com.bytedesk.core.exception.EmailExistsException;
import com.bytedesk.core.exception.MobileExistsException;
// import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserRequest;
import com.bytedesk.core.rbac.user.UserService;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.ThreadStateEnum;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class MemberRestService extends BaseRestService<MemberEntity, MemberRequest, MemberResponse> {

    private final UserService userService;

    private final MemberRepository memberRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    private final ThreadRestService threadService;

    public Page<MemberResponse> queryByOrg(MemberRequest request) {
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.ASC, "id");
        Specification<MemberEntity> spec = MemberSpecification.search(request);
        Page<MemberEntity> memberPage = memberRepository.findAll(spec, pageable);
        return memberPage.map(this::convertToResponse);
    }

    public MemberResponse query(MemberRequest request) {
        UserEntity user = authService.getUser();
        Optional<MemberEntity> memberOptional = findByUserAndOrgUid(user, request.getOrgUid());
        if (!memberOptional.isPresent()) {
            throw new RuntimeException("Member does not exist."); // 抛出具体的异常
        }
        return convertToResponse(memberOptional.get());
    }

    public MemberResponse queryByUserUid(MemberRequest request) {
        Optional<MemberEntity> memberOptional = findByUserUid(request.getUid());
        if (!memberOptional.isPresent()) {
            throw new RuntimeException("Member does not exist."); // 抛出具体的异常
        }
        return convertToResponse(memberOptional.get());
    }

    @Transactional
    public MemberResponse create(MemberRequest request) {
        //
        if (StringUtils.hasText(request.getEmail())
                && existsByEmailAndOrgUid(request.getEmail(), request.getOrgUid())) {
            throw new EmailExistsException("Email " + request.getEmail() + " already exists..!!");
        }
        if (StringUtils.hasText(request.getMobile())
                && existsByMobileAndOrgUid(request.getMobile(), request.getOrgUid())) {
            throw new MobileExistsException("Mobile " + request.getMobile() + " already exists..!!");
        }
        //
        MemberEntity member = modelMapper.map(request, MemberEntity.class);
        member.setUid(uidUtils.getUid());
        member.setDeptUid(request.getDeptUid());
        member.setOrgUid(request.getOrgUid());
        member.setRoleUids(request.getRoleUids());
        // 尝试根据邮箱和平台查找用户
        UserRequest userRequest = modelMapper.map(request, UserRequest.class);
        userRequest.setAvatar(AvatarConsts.getDefaultUserAvatarUrl());
        userRequest.setPlatform(PlatformEnum.BYTEDESK.name());
        userRequest.setOrgUid(request.getOrgUid());
        //
        UserEntity user = null;
        if (StringUtils.hasText(request.getMobile())) {
            user = userService.findByMobileAndPlatform(request.getMobile(),
                    PlatformEnum.BYTEDESK.name())
                    .orElseGet(() -> userService.createUserFromMember(userRequest));
        } else if (StringUtils.hasText(request.getEmail())) {
            user = userService.findByEmailAndPlatform(request.getEmail(),
                    PlatformEnum.BYTEDESK.name())
                    .orElseGet(() -> userService.createUserFromMember(userRequest));
        } else {
            throw new RuntimeException("mobile and email should not be both null.");
        }
        // 设置用户到成员对象中
        member.setUser(user);
        //
        MemberEntity saveMember = save(member);
        if (saveMember == null) {
            throw new RuntimeException("Failed to save member.");
        }
        // TODO: 发送邀请通知：push推送、邮件、短信，只有用户登录并accept邀请后，才真正加入组织

        // 
        return convertToResponse(saveMember);
    }

    @Transactional
    public MemberResponse update(MemberRequest request) {
        //
        Optional<MemberEntity> memberOptional = findByUid(request.getUid());
        if (!memberOptional.isPresent()) {
            throw new RuntimeException("Failed to find member.");
        }
        //
        MemberEntity member = memberOptional.get();
        // modelMapper.map(memberRequest, member);
        member.setDeptUid(request.getDeptUid());
        member.setNickname(request.getNickname());
        member.setEmail(request.getEmail());
        member.setMobile(request.getMobile());
        member.setJobTitle(request.getJobTitle());
        member.setJobNo(request.getJobNo());
        member.setSeatNo(request.getSeatNo());
        member.setTelephone(request.getTelephone());
        member.setRoleUids(request.getRoleUids());
        // 
        UserEntity user = member.getUser();
        userService.updateUserRolesFromMember(user, request.getRoleUids());
        //
        MemberEntity savedMember = save(member);
        if (savedMember == null) {
            throw new RuntimeException("Failed to save member.");
        }
 
        return convertToResponse(savedMember);
    }

    public void clearDepartmentUid(String deptUid) {
        List<MemberEntity> members = memberRepository.findByDeptUidAndDeleted(deptUid, false);
        for (MemberEntity member : members) {
            member.setDeptUid(null);
            save(member);
        }
    }

    @Cacheable(value = "member", key = "#uid", unless = "#result == null")
    public Optional<MemberEntity> findByUid(String uid) {
        return memberRepository.findByUidAndDeleted(uid, false);
    }

    @Cacheable(value = "member", key = "#uid", unless = "#result == null")
    public Optional<MemberEntity> findByUserUid(String uid) {
        return memberRepository.findByUser_UidAndDeleted(uid, false);
    }

    @Cacheable(value = "member", key = "#mobile", unless = "#result == null")
    public Optional<MemberEntity> findByMobileAndOrgUid(String mobile, String orgUid) {
        return memberRepository.findByMobileAndOrgUidAndDeleted(mobile, orgUid, false);
    }

    @Cacheable(value = "member", key = "#email", unless = "#result == null")
    public Optional<MemberEntity> findByEmailAndOrgUid(String email, String orgUid) {
        return memberRepository.findByEmailAndOrgUidAndDeleted(email, orgUid, false);
    }

    @Cacheable(value = "member", key = "#user.uid", unless = "#result == null")
    public Optional<MemberEntity> findByUserAndOrgUid(UserEntity user, String orgUid) {
        return memberRepository.findByUserAndOrgUidAndDeleted(user, orgUid, false);
    }

    public Boolean existsByEmailAndOrgUid(String email, String orgUid) {
        return memberRepository.existsByEmailAndOrgUidAndDeleted(email, orgUid, false);
    }

    public Boolean existsByMobileAndOrgUid(String mobile, String orgUid) {
        return memberRepository.existsByMobileAndOrgUidAndDeleted(mobile, orgUid, false);
    }

    @Caching(put = {
            @CachePut(value = "member", key = "#member.uid", unless = "#member.uid == null"),
            @CachePut(value = "member", key = "#member.mobile", unless = "#member.mobile == null"),
            @CachePut(value = "member", key = "#member.email", unless = "#member.email == null")
    })
    @Override
    public MemberEntity save(MemberEntity member) {
        try {
            return memberRepository.save(member);
        } catch (ObjectOptimisticLockingFailureException e) {
            // TODO: handle exception
            handleOptimisticLockingFailureException(e, member);
        }
        return null;
    }

    public void save(List<MemberEntity> members) {
        memberRepository.saveAll(members);
    }

    public void deleteByUid(String uid) {
        Optional<MemberEntity> memberOptional = findByUid(uid);
        memberOptional.ifPresent(member -> {
            member.setDeleted(true);
            save(member);
        });
    }

    public MemberEntity convertExcelToMember(MemberExcel memberExcel, String orgUid) {
        // 去重
        if (StringUtils.hasText(memberExcel.getEmail()) && existsByEmailAndOrgUid(memberExcel.getEmail(), orgUid)) {
            return null;
        }
        if (StringUtils.hasText(memberExcel.getMobile()) && existsByMobileAndOrgUid(memberExcel.getMobile(), orgUid)) {
            return null;
        }
        // 创建member
        MemberEntity member = modelMapper.map(memberExcel, MemberEntity.class);
        member.setUid(uidUtils.getUid());
        member.setOrgUid(orgUid);
        // 生成user
        // 尝试根据邮箱和平台查找用户
        UserRequest userRequest = UserRequest.builder().build();
        userRequest.setAvatar(AvatarConsts.getDefaultAvatarUrl());
        userRequest.setNickname(memberExcel.getNickname());
        userRequest.setEmail(memberExcel.getEmail());
        userRequest.setMobile(memberExcel.getMobile());
        userRequest.setPlatform(PlatformEnum.BYTEDESK.name());
        userRequest.setOrgUid(orgUid);
        //
        UserEntity user = null;
        if (StringUtils.hasText(memberExcel.getMobile())) {
            user = userService.findByMobileAndPlatform(memberExcel.getMobile(),
                    PlatformEnum.BYTEDESK.name())
                    .orElseGet(() -> userService.createUserFromMember(userRequest));
        } else if (StringUtils.hasText(memberExcel.getEmail())) {
            user = userService.findByEmailAndPlatform(memberExcel.getEmail(),
                    PlatformEnum.BYTEDESK.name())
                    .orElseGet(() -> userService.createUserFromMember(userRequest));
        } else {
            throw new RuntimeException("mobile and email should not be both null.");
        }
        // 设置用户到成员对象中
        member.setUser(user);
        //
        MemberEntity saveMember = save(member);
        if (saveMember == null) {
            // 根据业务逻辑决定如何处理保存失败的情况
            // 例如，可以抛出一个异常或返回一个错误响应
            throw new RuntimeException("Failed to save member.");
        }
        return saveMember;
    }

    public MemberExcel convertToExcel(MemberResponse member) {
        return modelMapper.map(member, MemberExcel.class);
    }

     /** 同事私聊会话：org/member/{self_member_uid}/{other_member_uid} */
    public ThreadEntity createMemberReverseThread(ThreadEntity thread) {
        // 
        String reverseUid = new StringBuffer(thread.getUid()).reverse().toString();
        Optional<ThreadEntity> reverseThreadOptional = threadService.findByUid(reverseUid);
        if (reverseThreadOptional.isPresent()) {
            return reverseThreadOptional.get();
        }

        // 解析主题并获取成员UID
        String topic = thread.getTopic();
        String[] splits = topic.split("/");
        if (splits.length != 4) {
            throw new RuntimeException("reverse thread topic format error");
        }
        String originalMemberUid = splits[2];
        String reverseMemberUid = splits[3];
        // 构建反向主题
        String reverseTopic = TopicUtils.TOPIC_ORG_MEMBER_PREFIX + reverseMemberUid + "/" + originalMemberUid;
        // String reverseTopic = TopicUtils.getOrgMemberTopicReverse(topic);

        // 查找反向成员
        Optional<MemberEntity> reverseMemberOptional = findByUid(reverseMemberUid);
        if (!reverseMemberOptional.isPresent()) {
            throw new RuntimeException("getMemberReverseThread member not found");
        }

        // 创建反向线程对象
        ThreadEntity reverseThread = ThreadEntity.builder().build();
        reverseThread.setUid(reverseUid);
        reverseThread.setState(ThreadStateEnum.STARTED.name());
        reverseThread.setTopic(reverseTopic);
        reverseThread.setUnreadCount(0);

        // 设置原始成员信息（如果存在）
        Optional<MemberEntity> originalMemberOptional = findByUid(originalMemberUid);
        if (originalMemberOptional.isPresent()) {
            UserProtobuf user = UserProtobuf.builder()
                    .nickname(originalMemberOptional.get().getNickname())
                    .avatar(originalMemberOptional.get().getAvatar())
                    .build();
            user.setUid(originalMemberOptional.get().getUid());
            reverseThread.setUser(JSON.toJSONString(user));
        }
        
        // 设置其他线程属性
        reverseThread.setContent(thread.getContent());
        // reverseThread.setExtra(thread.getExtra());
        reverseThread.setType(thread.getType());
        // TODO: 同事私聊被动方默认不显示会话，直到收到一条消息
        // reverseThread.setHide(true);
        reverseThread.setClient(ClientEnum.SYSTEM.name());
        reverseThread.setOrgUid(thread.getOrgUid());
        reverseThread.setOwner(reverseMemberOptional.get().getUser());

        // 保存反向线程并返回结果
        ThreadEntity savedThread = threadService.save(reverseThread);
        if (savedThread == null) {
            throw new RuntimeException("reverseThread save error");
        }
        return savedThread;
    }

    @Override
    public Page<MemberResponse> queryByUser(MemberRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Override
    public void delete(MemberRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            MemberEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public MemberResponse convertToResponse(MemberEntity entity) {
        return modelMapper.map(entity, MemberResponse.class);
    }

    
}
