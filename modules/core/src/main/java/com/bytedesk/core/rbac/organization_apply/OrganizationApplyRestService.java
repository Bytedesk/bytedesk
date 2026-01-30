/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 11:41:43
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.organization_apply;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.modelmapper.ModelMapper;
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
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.exception.NotFoundException;
import com.bytedesk.core.member.MemberEntity;
import com.bytedesk.core.member.MemberRepository;
import com.bytedesk.core.member.MemberRequest;
import com.bytedesk.core.member.MemberRestService;
import com.bytedesk.core.member.MemberStatusEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.organization.OrganizationRepository;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserRepository;
import com.bytedesk.core.rbac.user.UserResponseContact;
import com.bytedesk.core.rbac.user.UserService;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class OrganizationApplyRestService extends BaseRestServiceWithExport<OrganizationApplyEntity, OrganizationApplyRequest, OrganizationApplyResponse, OrganizationApplyExcel> {

    private final OrganizationApplyRepository organizationApplyRepository;

    private final OrganizationRepository organizationRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    private final UserRepository userRepository;

    private final UserService userService;

    private final MemberRestService memberRestService;

    private final MemberRepository memberRepository;

    @Override
    public Page<OrganizationApplyEntity> queryByOrgEntity(OrganizationApplyRequest request) {
        Pageable pageable = request.getPageable();
        Specification<OrganizationApplyEntity> spec = OrganizationApplySpecification.search(request, authService);
        return organizationApplyRepository.findAll(spec, pageable);
    }

    @Override
    public Page<OrganizationApplyResponse> queryByOrg(OrganizationApplyRequest request) {
        Page<OrganizationApplyEntity> page = queryByOrgEntity(request);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<OrganizationApplyResponse> queryByUser(OrganizationApplyRequest request) {
        UserEntity user = authService.getUser();
        
        request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    @Override
    public OrganizationApplyResponse queryByUid(OrganizationApplyRequest request) {
        Optional<OrganizationApplyEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            return convertToResponse(optional.get());
        }
        throw new NotFoundException("OrganizationApply not found");
    }

    @Cacheable(value = "organizationApply", key = "#uid", unless="#result==null")
    @Override
    public Optional<OrganizationApplyEntity> findByUid(String uid) {
        return organizationApplyRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return organizationApplyRepository.existsByUid(uid);
    }

    /**
     * 用户提交“申请加入组织”
     */
    public OrganizationApplyResponse applyJoin(OrganizationApplyRequest request) {
        if (!StringUtils.hasText(request.getOrgUid())) {
            throw new IllegalArgumentException("orgUid is required");
        }

        UserEntity user = authService.getUser();
        if (user == null) {
            throw new IllegalArgumentException("login required");
        }
        String userUid = user.getUid();

        OrganizationEntity org = organizationRepository.findByUid(request.getOrgUid())
                .orElseThrow(() -> new NotFoundException("Organization not found"));
        if (Boolean.FALSE.equals(org.getEnabled())) {
            throw new IllegalArgumentException("Organization is disabled");
        }

        Optional<OrganizationApplyEntity> existed = organizationApplyRepository
                .findFirstByOrgUidAndUserUidAndStatusAndDeletedFalse(
                        request.getOrgUid(),
                        userUid,
                        OrganizationApplyStatusEnum.PENDING.name());
        if (existed.isPresent()) {
            return convertToResponse(existed.get());
        }

        OrganizationApplyEntity entity = OrganizationApplyEntity.builder()
                .uid(uidUtils.getUid())
                .orgUid(request.getOrgUid())
                .userUid(userUid)
                // name 用于存储组织名称快照，便于前端列表展示
                .name(StringUtils.hasText(request.getName()) ? request.getName() : org.getName())
                // description 用于存储申请备注
                .description(StringUtils.hasText(request.getDescription()) ? request.getDescription() : I18Consts.I18N_DESCRIPTION)
                .type(StringUtils.hasText(request.getType()) ? request.getType() : OrganizationApplyTypeEnum.CUSTOMER.name())
                .status(OrganizationApplyStatusEnum.PENDING.name())
                .build();

        OrganizationApplyEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create organization apply failed");
        }
        return convertToResponse(savedEntity);
    }

    /**
     * 组织管理员审批通过申请
     * - 申请状态更新为 APPROVED
     * - 将申请人默认组织切换为该组织
     * - 将该组织写入用户的 userOrganizationRoles，并授予默认 ROLE_USER
     * - 为用户在该组织下创建/激活 Member
     */
    @Transactional
    public OrganizationApplyResponse approve(OrganizationApplyRequest request) {
        if (!StringUtils.hasText(request.getUid())) {
            throw new IllegalArgumentException("uid is required");
        }

        OrganizationApplyEntity apply = organizationApplyRepository.findByUid(request.getUid())
                .orElseThrow(() -> new NotFoundException("OrganizationApply not found"));
        if (apply.isDeleted()) {
            throw new NotFoundException("OrganizationApply not found");
        }

        if (!StringUtils.hasText(apply.getOrgUid())) {
            throw new IllegalArgumentException("orgUid is required");
        }
        if (!StringUtils.hasText(apply.getUserUid())) {
            throw new IllegalArgumentException("userUid is required");
        }

        UserEntity operator = authService.getUser();
        if (operator == null) {
            throw new IllegalArgumentException("login required");
        }

        OrganizationEntity org = organizationRepository.findByUid(apply.getOrgUid())
                .orElseThrow(() -> new NotFoundException("Organization not found"));

        // 仅允许组织 owner 审批（与 OrganizationEntity.user 语义一致）
        if (org.getUser() == null || !StringUtils.hasText(org.getUser().getUid())
                || !org.getUser().getUid().equals(operator.getUid())) {
            throw new IllegalArgumentException("permission denied");
        }

        // 若已处理，直接返回
        String currentStatus = StringUtils.hasText(apply.getStatus()) ? apply.getStatus() : OrganizationApplyStatusEnum.PENDING.name();
        if (!OrganizationApplyStatusEnum.PENDING.name().equalsIgnoreCase(currentStatus)) {
            return convertToResponse(apply);
        }

        apply.setStatus(OrganizationApplyStatusEnum.APPROVED.name());
        apply.setRejectReason(null);
        apply.setHandledByUid(operator.getUid());
        apply.setHandledAt(ZonedDateTime.now());
        OrganizationApplyEntity saved = save(apply);

        UserEntity applicant = userRepository.findByUid(apply.getUserUid())
                .orElseThrow(() -> new NotFoundException("User not found"));

        // 1) 切换默认组织 + 写入组织角色（默认 ROLE_USER）
        userService.ensureCurrentOrganization(applicant, org.getUid());
        userService.updateUserRoles(applicant, new HashSet<>(Arrays.asList(BytedeskConsts.DEFAULT_ROLE_USER_UID)));

        // 2) 创建/激活 Member
        Optional<MemberEntity> existedMember = memberRepository.findByUserAndOrgUidAndDeletedFalse(applicant, org.getUid());
        if (existedMember.isPresent()) {
            MemberEntity member = existedMember.get();
            if (!MemberStatusEnum.ACTIVE.name().equalsIgnoreCase(member.getStatus())) {
                member.setStatus(MemberStatusEnum.ACTIVE.name());
                memberRepository.save(member);
            }
        } else {
            // MemberRestService.create 需要 email 或 mobile；若都没有则直接落库
            if (StringUtils.hasText(applicant.getMobile()) || StringUtils.hasText(applicant.getEmail())) {
                Set<String> roleUids = new HashSet<>(Arrays.asList(BytedeskConsts.DEFAULT_ROLE_USER_UID));
                MemberRequest memberRequest = MemberRequest.builder()
                        .nickname(StringUtils.hasText(applicant.getNickname()) ? applicant.getNickname() : applicant.getUsername())
                        .email(applicant.getEmail())
                        .mobile(applicant.getMobile())
                        .status(MemberStatusEnum.ACTIVE.name())
                        .roleUids(roleUids)
                        .orgUid(org.getUid())
                        .build();
                memberRestService.create(memberRequest);
            } else {
                MemberEntity member = MemberEntity.builder()
                        .uid(uidUtils.getUid())
                        .nickname(StringUtils.hasText(applicant.getNickname()) ? applicant.getNickname() : applicant.getUsername())
                        .email(applicant.getEmail())
                        .mobile(applicant.getMobile())
                        .status(MemberStatusEnum.ACTIVE.name())
                        .orgUid(org.getUid())
                        .user(applicant)
                        .build();
                memberRepository.save(member);
            }
        }

        return convertToResponse(saved);
    }

    /**
     * 组织管理员审批拒绝申请
     */
    @Transactional
    public OrganizationApplyResponse reject(OrganizationApplyRequest request) {
        if (!StringUtils.hasText(request.getUid())) {
            throw new IllegalArgumentException("uid is required");
        }

        OrganizationApplyEntity apply = organizationApplyRepository.findByUid(request.getUid())
                .orElseThrow(() -> new NotFoundException("OrganizationApply not found"));
        if (apply.isDeleted()) {
            throw new NotFoundException("OrganizationApply not found");
        }

        UserEntity operator = authService.getUser();
        if (operator == null) {
            throw new IllegalArgumentException("login required");
        }

        OrganizationEntity org = organizationRepository.findByUid(apply.getOrgUid())
                .orElseThrow(() -> new NotFoundException("Organization not found"));

        if (org.getUser() == null || !StringUtils.hasText(org.getUser().getUid())
                || !org.getUser().getUid().equals(operator.getUid())) {
            throw new IllegalArgumentException("permission denied");
        }

        String currentStatus = StringUtils.hasText(apply.getStatus()) ? apply.getStatus() : OrganizationApplyStatusEnum.PENDING.name();
        if (!OrganizationApplyStatusEnum.PENDING.name().equalsIgnoreCase(currentStatus)) {
            return convertToResponse(apply);
        }

        apply.setStatus(OrganizationApplyStatusEnum.REJECTED.name());
        if (StringUtils.hasText(request.getRejectReason())) {
            apply.setRejectReason(request.getRejectReason());
        }
        apply.setHandledByUid(operator.getUid());
        apply.setHandledAt(ZonedDateTime.now());

        OrganizationApplyEntity saved = save(apply);
        return convertToResponse(saved);
    }

    @Override
    public OrganizationApplyResponse create(OrganizationApplyRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        OrganizationApplyEntity entity = modelMapper.map(request, OrganizationApplyEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        OrganizationApplyEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create organizationApply failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public OrganizationApplyResponse update(OrganizationApplyRequest request) {
        Optional<OrganizationApplyEntity> optional = organizationApplyRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            OrganizationApplyEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            OrganizationApplyEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update organizationApply failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("OrganizationApply not found");
        }
    }

    @Override
    protected OrganizationApplyEntity doSave(OrganizationApplyEntity entity) {
        return organizationApplyRepository.save(entity);
    }

    @Override
    public OrganizationApplyEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, OrganizationApplyEntity entity) {
        try {
            Optional<OrganizationApplyEntity> latest = organizationApplyRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                OrganizationApplyEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                latestEntity.setDescription(entity.getDescription());
                latestEntity.setStatus(entity.getStatus());
                latestEntity.setRejectReason(entity.getRejectReason());
                latestEntity.setHandledByUid(entity.getHandledByUid());
                latestEntity.setHandledAt(entity.getHandledAt());
                return organizationApplyRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突: {}", ex.getMessage(), ex);
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<OrganizationApplyEntity> optional = organizationApplyRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // tagRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("OrganizationApply not found");
        }
    }

    @Override
    public void delete(OrganizationApplyRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public OrganizationApplyResponse convertToResponse(OrganizationApplyEntity entity) {
        OrganizationApplyResponse response = modelMapper.map(entity, OrganizationApplyResponse.class);
        if (entity != null && StringUtils.hasText(entity.getUserUid())) {
            userRepository.findByUid(entity.getUserUid()).ifPresent(user -> {
                UserResponseContact userContact = modelMapper.map(user, UserResponseContact.class);
                response.setUser(userContact);
            });
        }
        return response;
    }

    @Override
    public OrganizationApplyExcel convertToExcel(OrganizationApplyEntity entity) {
        return modelMapper.map(entity, OrganizationApplyExcel.class);
    }

    @Override
    protected Specification<OrganizationApplyEntity> createSpecification(OrganizationApplyRequest request) {
        return OrganizationApplySpecification.search(request, authService);
    }

    @Override
    protected Page<OrganizationApplyEntity> executePageQuery(Specification<OrganizationApplyEntity> spec, Pageable pageable) {
        return organizationApplyRepository.findAll(spec, pageable);
    }
    
    
}
