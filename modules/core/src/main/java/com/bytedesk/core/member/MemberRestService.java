/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-25 11:24:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.member;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.modelmapper.ModelMapper;
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

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.base.BaseRestServiceWithExcel;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.enums.PlatformEnum;
import com.bytedesk.core.exception.EmailExistsException;
import com.bytedesk.core.exception.MobileExistsException;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserRequest;
import com.bytedesk.core.rbac.user.UserResponse;
import com.bytedesk.core.rbac.user.UserService;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.department.DepartmentEntity;
import com.bytedesk.core.department.DepartmentRequest;
import com.bytedesk.core.department.DepartmentRestService;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.ThreadProcessStatusEnum;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class MemberRestService extends BaseRestServiceWithExcel<MemberEntity, MemberRequest, MemberResponse, MemberExcel> {

    private final UserService userService;

    private final MemberRepository memberRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    private final ThreadRestService threadRestService;

    private final DepartmentRestService departmentRestService;

    @Override
    public Page<MemberEntity> queryByOrgEntity(MemberRequest request) {
        Pageable pageable = request.getPageable();
        Specification<MemberEntity> spec = MemberSpecification.search(request);
        return memberRepository.findAll(spec, pageable);
    }

    @Override
    public Page<MemberResponse> queryByOrg(MemberRequest request) {
        Page<MemberEntity> memberPage = queryByOrgEntity(request);
        return memberPage.map(this::convertToResponse);
    }

    @Override
    public Page<MemberResponse> queryByUser(MemberRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            return null;
        }
        request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    public MemberResponse query(MemberRequest request) {
        UserEntity user = authService.getUser();
        Optional<MemberEntity> memberOptional = findByUserAndOrgUid(user, request.getOrgUid());
        if (!memberOptional.isPresent()) {
            return null;
        }
        return convertToResponse(memberOptional.get());
    }

    public MemberResponse queryByUserUid(MemberRequest request) {
        Optional<MemberEntity> memberOptional = findByUserUid(request.getUserUid());
        if (!memberOptional.isPresent()) {
            return null;
        }
        return convertToResponse(memberOptional.get());
    }

    public MemberResponse queryByUid(MemberRequest request) {
        Optional<MemberEntity> memberOptional = findByUid(request.getUid());
        if (!memberOptional.isPresent()) {
            throw new RuntimeException("Member not found");
        }
        return convertToResponse(memberOptional.get());
    }

    public Boolean existsByUid(String uid) {
        return memberRepository.existsByUid(uid);
    }

    @Transactional
    public MemberResponse initVisitor(MemberRequest request) {
        // 判断uid是否已存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
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
        if (StringUtils.hasText(request.getUid())) {
            member.setUid(request.getUid());
        } else {
            member.setUid(uidUtils.getUid());
        }
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
        member.setAvatar(request.getAvatar());
        member.setDescription(request.getDescription());
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


    // activate
    @Transactional
    public MemberResponse activate(MemberRequest request) {
        Optional<MemberEntity> memberOptional = findByUid(request.getUid());
        if (!memberOptional.isPresent()) {
            throw new RuntimeException("Failed to find member.");
        }
        //
        MemberEntity member = memberOptional.get();
        member.setStatus(MemberStatusEnum.ACTIVE.name());
        // 
        MemberEntity savedEntity = save(member);
        if (savedEntity == null) {
            throw new RuntimeException("Failed to save member.");
        }
        return convertToResponse(savedEntity);
    }

    public void clearDepartmentUid(String deptUid) {
        List<MemberEntity> members = memberRepository.findByDeptUidAndDeletedFalse(deptUid);
        for (MemberEntity member : members) {
            member.setDeptUid(null);
            save(member);
        }
    }

    @Cacheable(value = "member", key = "#uid", unless = "#result == null")
    public Optional<MemberEntity> findByUid(String uid) {
        Optional<MemberEntity> memberOptional = memberRepository.findByUid(uid);
        if (memberOptional.isPresent()) {
            MemberEntity member = memberOptional.get();
            // 预加载user，确保user数据被包含在缓存中
            if (member.getUser() != null) {
                member.getUser().getUid();
            }
        }
        return memberOptional;
    }

    @Cacheable(value = "member", key = "#uid", unless = "#result == null")
    public Optional<MemberEntity> findByUserUid(String uid) {
        Optional<MemberEntity> memberOptional = memberRepository.findByUser_UidAndDeletedFalse(uid);
        if (memberOptional.isPresent()) {
            MemberEntity member = memberOptional.get();
            // 预加载user，确保user数据被包含在缓存中
            if (member.getUser() != null) {
                member.getUser().getUid();
            }
        }
        return memberOptional;
    }

    @Cacheable(value = "member", key = "#mobile", unless = "#result == null")
    public Optional<MemberEntity> findByMobileAndOrgUid(String mobile, String orgUid) {
        Optional<MemberEntity> memberOptional = memberRepository.findByMobileAndOrgUidAndDeletedFalse(mobile, orgUid);
        if (memberOptional.isPresent()) {
            MemberEntity member = memberOptional.get();
            // 预加载user，确保user数据被包含在缓存中
            if (member.getUser() != null) {
                member.getUser().getUid();
            }
        }
        return memberOptional;
    }

    @Cacheable(value = "member", key = "#email", unless = "#result == null")
    public Optional<MemberEntity> findByEmailAndOrgUid(String email, String orgUid) {
        Optional<MemberEntity> memberOptional = memberRepository.findByEmailAndOrgUidAndDeletedFalse(email, orgUid);
        if (memberOptional.isPresent()) {
            MemberEntity member = memberOptional.get();
            // 预加载user，确保user数据被包含在缓存中
            if (member.getUser() != null) {
                member.getUser().getUid();
            }
        }
        return memberOptional;
    }

    @Cacheable(value = "member", key = "#user.uid", unless = "#result == null")
    public Optional<MemberEntity> findByUserAndOrgUid(UserEntity user, String orgUid) {
        Optional<MemberEntity> memberOptional = memberRepository.findByUserAndOrgUidAndDeletedFalse(user, orgUid);
        if (memberOptional.isPresent()) {
            MemberEntity member = memberOptional.get();
            // 预加载user，确保user数据被包含在缓存中
            if (member.getUser() != null) {
                member.getUser().getUid();
            }
        }
        return memberOptional;
    }

    public Boolean existsByEmailAndOrgUid(String email, String orgUid) {
        return memberRepository.existsByEmailAndOrgUidAndDeletedFalse(email, orgUid);
    }

    public Boolean existsByMobileAndOrgUid(String mobile, String orgUid) {
        return memberRepository.existsByMobileAndOrgUidAndDeletedFalse(mobile, orgUid);
    }

    @CachePut(value = "member", key = "#member.uid")
    @Override
    protected MemberEntity doSave(MemberEntity member) {
        return memberRepository.save(member);
    }

    @Override
    public MemberEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            MemberEntity entity) {
        // 乐观锁处理实现
        try {
            Optional<MemberEntity> latest = memberRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                MemberEntity latestEntity = latest.get();
                // 合并需要保留的数据
                // 这里可以根据业务需求合并实体
                return memberRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    public void save(List<MemberEntity> members) {
        memberRepository.saveAll(members);
    }

    @CacheEvict(value = "member", key = "#uid")
    public void deleteByUid(String uid) {
        Optional<MemberEntity> memberOptional = findByUid(uid);
        memberOptional.ifPresent(member -> {
            member.setDeleted(true);
            save(member);
        });
    }

    public MemberEntity convertExcelToMember(MemberExcel excel, String orgUid) {
        // 去重
        if (StringUtils.hasText(excel.getEmail()) && existsByEmailAndOrgUid(excel.getEmail(), orgUid)) {
            return null;
        }
        if (StringUtils.hasText(excel.getMobile()) && existsByMobileAndOrgUid(excel.getMobile(), orgUid)) {
            return null;
        }
        Set<String> roleUids = new HashSet<>(Arrays.asList(BytedeskConsts.DEFAULT_ROLE_MEMBER_UID));
        // 创建member
        MemberEntity member = modelMapper.map(excel, MemberEntity.class);
        member.setUid(uidUtils.getUid());
        member.setOrgUid(orgUid);
        member.setRoleUids(roleUids);
        // 
        Optional<DepartmentEntity> departmentOptional = departmentRestService.findByNameAndOrgUid(excel.getDepartmentName(), orgUid);
        if (departmentOptional.isPresent()) {
            member.setDeptUid(departmentOptional.get().getUid());
        } else {
            // 部门不存在，创建部门
            DepartmentRequest departmentRequest = DepartmentRequest.builder()
                    .uid(uidUtils.getUid())
                    .name(excel.getDepartmentName())
                    .description("Description for " + excel.getDepartmentName())
                    .orgUid(orgUid)
                    .build();
            departmentRestService.initVisitor(departmentRequest);
            member.setDeptUid(departmentRequest.getUid());
        }
        // 生成user
        // 尝试根据邮箱和平台查找用户
        UserEntity user = null;
        
        try {
            // 先尝试查找现有用户，避免创建重复用户
            if (StringUtils.hasText(excel.getMobile())) {
                Optional<UserEntity> existingUser = userService.findByMobileAndPlatform(
                        excel.getMobile(), PlatformEnum.BYTEDESK.name());
                if (existingUser.isPresent()) {
                    user = existingUser.get();
                }
            } else if (StringUtils.hasText(excel.getEmail())) {
                Optional<UserEntity> existingUser = userService.findByEmailAndPlatform(
                        excel.getEmail(), PlatformEnum.BYTEDESK.name());
                if (existingUser.isPresent()) {
                    user = existingUser.get();
                }
            }
            
            // 如果没有找到现有用户，则创建新用户
            if (user == null) {
                UserRequest userRequest = UserRequest.builder()
                    .avatar(AvatarConsts.getDefaultAvatarUrl())
                    .nickname(excel.getNickname())
                    .email(excel.getEmail())
                    .mobile(excel.getMobile())
                    .platform(PlatformEnum.BYTEDESK.name())
                    .orgUid(orgUid) // 确保设置组织UID，避免OrganizationRepository.findByUid接收null参数
                    .build();
                    
                if (!StringUtils.hasText(excel.getMobile()) && !StringUtils.hasText(excel.getEmail())) {
                    throw new RuntimeException("mobile and email should not be both null.");
                }
                
                // 直接创建用户，避免使用orElseGet回调方式
                user = userService.createUserFromMember(userRequest);
            }
            
            // 设置用户到成员对象中
            member.setUser(user);
            
            // 保存成员
            MemberEntity saveMember = save(member);
            if (saveMember == null) {
                throw new RuntimeException("Failed to save member.");
            }
            
            return saveMember;
            
        } catch (Exception e) {
            log.error("批量导入成员失败: {}", e.getMessage(), e);
            throw e;
        }
    }

     /** 同事私聊会话：org/member/{self_member_uid}/{other_member_uid} */
    public ThreadEntity createMemberReverseThread(ThreadEntity thread) {
        // 
        String reverseUid = new StringBuffer(thread.getUid()).reverse().toString();
        Optional<ThreadEntity> reverseThreadOptional = threadRestService.findByUid(reverseUid);
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
        reverseThread.setStatus(ThreadProcessStatusEnum.CHATTING.name());
        reverseThread.setTopic(reverseTopic);
        reverseThread.setUnreadCount(0);

        // 设置原始成员信息（如果存在）
        Optional<MemberEntity> originalMemberOptional = findByUid(originalMemberUid);
        if (originalMemberOptional.isPresent()) {
            UserProtobuf user = UserProtobuf.builder()
                    .uid(originalMemberOptional.get().getUser().getUid())
                    .nickname(originalMemberOptional.get().getNickname())
                    .avatar(originalMemberOptional.get().getAvatar())
                    .build();
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
        ThreadEntity savedThread = threadRestService.save(reverseThread);
        if (savedThread == null) {
            throw new RuntimeException("reverseThread save error");
        }
        return savedThread;
    }

    @Override
    public void delete(MemberRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public MemberResponse convertToResponse(MemberEntity entity) {
        MemberResponse response = modelMapper.map(entity, MemberResponse.class);
        if (entity.getUser() != null) {
            // 预加载user，确保user数据被包含在缓存中
            entity.getUser().getUid();
            response.setUser(modelMapper.map(entity.getUser(), UserResponse.class));
        }
        return response;
    }

    @Override
    public MemberExcel convertToExcel(MemberEntity entity) {
        MemberExcel excel = modelMapper.map(entity, MemberExcel.class);
        // 设置部门名称
        if (entity.getDeptUid() != null) {
            Optional<DepartmentEntity> departmentOptional = departmentRestService.findByUid(entity.getDeptUid());
            if (departmentOptional.isPresent()) {
                excel.setDepartmentName(departmentOptional.get().getName());
            }
        }
        return excel;
    }

}
