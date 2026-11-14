/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 15:53:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.department;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collections;

import org.modelmapper.ModelMapper;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.exception.CommonI18nExceptions;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.member.MemberEntity;
import com.bytedesk.core.member.MemberRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class DepartmentRestService extends BaseRestService<DepartmentEntity, DepartmentRequest, DepartmentResponse> {

    private final ModelMapper modelMapper;

    private final DepartmentRepository departmentRepository;

    private final UidUtils uidUtils;

    private final MemberRepository memberRepository;

    private final CacheManager cacheManager;

    @Override
    public DepartmentResponse queryByUid(DepartmentRequest request) {
        DepartmentEntity department = findByUid(request.getUid())
                .orElseThrow(() -> new RuntimeException("department not found with uid: " + request.getUid()));

        // 安全兜底：若请求携带 orgUid，则强制校验资源归属组织，避免凭 UID 跨组织探测
        if (StringUtils.hasText(request.getOrgUid()) && !request.getOrgUid().equals(department.getOrgUid())) {
            throw new RuntimeException("department org mismatch");
        }

        return convertToResponse(department);
    }

    public Page<DepartmentResponse> queryByOrg(DepartmentRequest request) {
        Pageable pageable = request.getPageable();
        Specification<DepartmentEntity> specification = DepartmentSpecification.search(request, authService);
        Page<DepartmentEntity> page = departmentRepository.findAll(specification, pageable);
        return page.map(this::convertToResponse);
    }

    // 注意：不要缓存“查不到”的结果，否则会出现“部门刚创建但仍查不到”的负缓存问题
    // Spring Cache 对 Optional 返回值可能会做解包（empty -> null / present -> entity），因此这里只做 null 判断，避免对实体误调用 isEmpty()
    public Optional<DepartmentEntity> findByNameAndOrgUid(String name, String orgUid) {
        return departmentRepository.findByNameAndOrgUidAndDeletedFalse(name, orgUid);
    }

    public Optional<DepartmentEntity> findByUid(String uid) {
        return departmentRepository.findByUid(uid);
    }

    public Boolean existsByNameAndOrgUid(String name, String orgUid) {
        return departmentRepository.existsByNameAndOrgUidAndDeletedFalse(name, orgUid);
    }

    public Boolean existsByUid(String uid) {
        return departmentRepository.existsByUid(uid);
    }

    @CacheEvict(value = "department", allEntries = true)
    public DepartmentResponse create(DepartmentRequest request) {
        // 判断uid是否存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 
        if (existsByNameAndOrgUid(request.getName(), request.getOrgUid())) {
            log.error("department  " + request.getName() + " already exists");
            throw new RuntimeException("部门 " + request.getName() + " 已经存在，请修改部门名称");
        }

        DepartmentEntity department = modelMapper.map(request, DepartmentEntity.class);
        if (StringUtils.hasText(department.getUid())) {
            department.setUid(request.getUid());
        } else {
            department.setUid(uidUtils.getUid());
        }

        if (StringUtils.hasText(request.getParentUid())) {
            Optional<DepartmentEntity> parentOptional = departmentRepository.findByUid(request.getParentUid());
            if (parentOptional.isPresent()) {
                parentOptional.get().addChild(department);
            }
        } else {
            department.setParent(null);
        }
        department.setOrgUid(request.getOrgUid());

        DepartmentEntity createdDepartment = save(department);
        if (createdDepartment == null) {
            log.error("department not created");
            throw new RuntimeException("department not created");
        }

        return convertToResponse(createdDepartment);
    }

    @CacheEvict(value = "department", allEntries = true)
    public DepartmentResponse update(DepartmentRequest request) {
        //
        Optional<DepartmentEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            DepartmentEntity department = optional.get();
            // modelMapper.map(departmentRequest, DepartmentEntity.class);
            if (request.getUid().equals(request.getParentUid())) {
                throw new RuntimeException(I18Consts.I18N_DEPARTMENT_PARENT_SELF_NOT_ALLOWED);
            }
            department.setName(request.getName());
            department.setDescription(request.getDescription());
            // 判断parentUid是否跟当前部门uid相同
            if (StringUtils.hasText(request.getParentUid())) {
                Optional<DepartmentEntity> parentOptional = departmentRepository.findByUid(request.getParentUid());
                if (parentOptional.isPresent()) {
                    parentOptional.get().addChild(department);
                }
            }
            // 
            DepartmentEntity savedEntity = save(department);
            if (savedEntity == null) {
                throw new RuntimeException(I18Consts.I18N_UPDATE_FAILED);
            }
            return convertToResponse(savedEntity);
        } else {
            throw new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND);
        }
    }

    @Override
    protected DepartmentEntity doSave(DepartmentEntity entity) {
        return departmentRepository.save(entity);
    }

    @Override
    @Transactional
    public void deleteByUid(String uid) {
        deleteSubtree(uid, null);
    }

    @Override
    @Transactional
    public void delete(DepartmentRequest request) {
        if (request == null || !StringUtils.hasText(request.getUid())) {
            throw new IllegalArgumentException("department uid is required");
        }
        deleteSubtree(request.getUid(), request.getOrgUid());
    }

    private void deleteSubtree(String uid, String requestedOrgUid) {
        DepartmentEntity root = departmentRepository.findByUid(uid)
                .filter(department -> !department.isDeleted())
                .orElseThrow(() -> new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND));

        assertDeleteScope(root, requestedOrgUid);

        List<DepartmentEntity> activeDepartments = new ArrayList<>();
        Set<String> visitedUids = new HashSet<>();
        Set<String> activePathUids = new HashSet<>();
        collectDepartmentSubtree(root, root.getOrgUid(), visitedUids, activePathUids, activeDepartments);

        Set<String> departmentUids = new HashSet<>();
        for (DepartmentEntity department : activeDepartments) {
            departmentUids.add(department.getUid());
            department.setDeleted(true);
        }

        List<MemberEntity> members = memberRepository
                .findByDeptUidInAndOrgUidAndDeletedFalse(departmentUids, root.getOrgUid());
        Set<String> memberUids = new HashSet<>();
        for (MemberEntity member : members) {
            if (!StringUtils.hasText(member.getUid())) {
                throw new IllegalStateException("active member is missing uid");
            }
            member.setDeptUid(null);
            memberUids.add(member.getUid());
        }

        departmentRepository.saveAll(activeDepartments);
        memberRepository.saveAll(members);
        departmentRepository.flush();
        memberRepository.flush();
        registerCacheEvictionAfterCommit(memberUids);
    }

    private void assertDeleteScope(DepartmentEntity department, String requestedOrgUid) {
        if (!StringUtils.hasText(department.getOrgUid())
                || (StringUtils.hasText(requestedOrgUid) && !requestedOrgUid.equals(department.getOrgUid()))) {
            throw new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND);
        }

        UserEntity user = authService.getUser();
        if (user == null) {
            throw CommonI18nExceptions.loginRequired();
        }
        boolean platformOrganizationUser = BytedeskConsts.DEFAULT_ORGANIZATION_UID.equals(user.getOrgUid());
        if (!user.isSuperUser()
                && !platformOrganizationUser
                && !department.getOrgUid().equals(user.getOrgUid())) {
            throw new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND);
        }
    }

    private void collectDepartmentSubtree(
            DepartmentEntity department,
            String orgUid,
            Set<String> visitedUids,
            Set<String> activePathUids,
            List<DepartmentEntity> activeDepartments) {
        if (department == null || !StringUtils.hasText(department.getUid())) {
            throw new IllegalStateException("department subtree contains an invalid node");
        }
        if (!orgUid.equals(department.getOrgUid())) {
            throw new IllegalStateException("department subtree crosses organization boundary");
        }
        if (activePathUids.contains(department.getUid())) {
            throw new IllegalStateException("department subtree contains a cycle");
        }
        if (!visitedUids.add(department.getUid())) {
            return;
        }

        activePathUids.add(department.getUid());
        if (!department.isDeleted()) {
            activeDepartments.add(department);
        }
        for (DepartmentEntity child : department.getChildren()) {
            collectDepartmentSubtree(child, orgUid, visitedUids, activePathUids, activeDepartments);
        }
        activePathUids.remove(department.getUid());
    }

    private void registerCacheEvictionAfterCommit(Set<String> memberUids) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            throw new IllegalStateException("department deletion requires an active transaction");
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                RuntimeException failure = null;
                failure = evictCache("member", memberUids, failure);
                failure = evictCache("department", Collections.emptySet(), failure);
                if (failure != null) {
                    log.error("Department deletion committed but cache eviction failed", failure);
                    throw new DepartmentDeletionCommittedException(
                            "Department deleted, but cache refresh failed", failure);
                }
            }
        });
    }

    private RuntimeException evictCache(
            String cacheName,
            Set<String> keys,
            RuntimeException previousFailure) {
        try {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache == null) {
                throw new IllegalStateException("cache not configured: " + cacheName);
            }
            if (keys.isEmpty()) {
                cache.clear();
            } else {
                keys.forEach(cache::evict);
            }
        } catch (RuntimeException exception) {
            if (previousFailure == null) {
                return exception;
            }
            previousFailure.addSuppressed(exception);
        }
        return previousFailure;
    }

    @Override
    public DepartmentEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            DepartmentEntity entity) {
        // 乐观锁处理实现
        try {
            Optional<DepartmentEntity> latest = departmentRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                DepartmentEntity latestEntity = latest.get();
                // 合并需要保留的数据
                // 这里可以根据业务需求合并实体
                return departmentRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    public DepartmentResponse convertToResponse(DepartmentEntity department) {
        // 过滤掉department.children中deleted为true的元素
        department.getChildren().removeIf(child -> child.isDeleted());
        DepartmentResponse departmentResponse = modelMapper.map(department, DepartmentResponse.class);
        if (department.getParent() != null) {
            departmentResponse.setParentUid(department.getParent().getUid());
        }
        
        // 计算当前部门的直接成员数
        List<MemberEntity> directMembers = memberRepository.findByDeptUidAndDeletedFalse(department.getUid());
        int totalMemberCount = directMembers.size();
        
        // 递归计算所有子部门的成员数，并设置子部门的成员数量
        Set<DepartmentResponse> childResponses = new HashSet<>();
        for (DepartmentEntity child : department.getChildren()) {
            if (!child.isDeleted()) {
                DepartmentResponse childResponse = convertToResponse(child);
                totalMemberCount += childResponse.getMemberCount();
                childResponses.add(childResponse);
            }
        }
        
        departmentResponse.setChildren(childResponses);
        departmentResponse.setMemberCount(totalMemberCount);
        return departmentResponse;
    }

    @Override
    protected Specification<DepartmentEntity> createSpecification(DepartmentRequest request) {
        return DepartmentSpecification.search(request, authService);
    }

    @Override
    protected Page<DepartmentEntity> executePageQuery(Specification<DepartmentEntity> spec, Pageable pageable) {
        return departmentRepository.findAll(spec, pageable);
    }

}
