/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-30 12:50:39
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.team.department;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.team.member.MemberEntity;
import com.bytedesk.team.member.MemberRepository;

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

    public Page<DepartmentResponse> queryByOrg(DepartmentRequest request) {
        Pageable pageable = request.getPageable();
        Specification<DepartmentEntity> specification = DepartmentSpecification.search(request);
        Page<DepartmentEntity> page = departmentRepository.findAll(specification, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<DepartmentResponse> queryByUser(DepartmentRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Cacheable(value = "department", key = "#name + '-' + #orgUid", unless = "#result == null")
    public Optional<DepartmentEntity> findByNameAndOrgUid(String name, String orgUid) {
        return departmentRepository.findByNameAndOrgUidAndDeletedFalse(name, orgUid);
    }

    @Cacheable(value = "department", key = "#uid", unless = "#result == null")
    public Optional<DepartmentEntity> findByUid(String uid) {
        return departmentRepository.findByUid(uid);
    }

    public Boolean existsByNameAndOrgUid(String name, String orgUid) {
        return departmentRepository.existsByNameAndOrgUidAndDeletedFalse(name, orgUid);
    }

    public Boolean existsByUid(String uid) {
        return departmentRepository.existsByUid(uid);
    }

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

    public DepartmentResponse update(DepartmentRequest request) {
        //
        Optional<DepartmentEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            DepartmentEntity department = optional.get();
            // modelMapper.map(departmentRequest, DepartmentEntity.class);
            if (request.getUid().equals(request.getParentUid())) {
                throw new RuntimeException("不能将当前部门设置为父部门");
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
                throw new RuntimeException("department update failed");
            }
            return convertToResponse(savedEntity);
        } else {
            throw new RuntimeException("department not found");
        }
    }

    @CachePut(value = "department", key = "#entity.uid")
    @Override
    protected DepartmentEntity doSave(DepartmentEntity entity) {
        return departmentRepository.save(entity);
    }

    @CacheEvict(value = "department", key = "#uid")
    @Override
    public void deleteByUid(String uid) {
        Optional<DepartmentEntity> optional = findByUid(uid);
        if (optional.isPresent()) {
            DepartmentEntity department = optional.get();
            // delete children departments
            for (DepartmentEntity child : department.getChildren()) {
                // child.setParent(null);
                child.setDeleted(true);
                save(child);
            }
            department.setDeleted(true);
            save(department);
            // remove department members
            // memberService.clearDepartmentUid(department.getUid());
             List<MemberEntity> members = memberRepository.findByDeptUidAndDeletedFalse(department.getUid());
            for (MemberEntity member : members) {
                member.setDeptUid(null);
                memberRepository.save(member);
            }
        } else {
            log.error("department not found");
            throw new RuntimeException("department not found");
        }
    }

    @Override
    public void delete(DepartmentRequest request) {
        deleteByUid(request.getUid());
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


}
