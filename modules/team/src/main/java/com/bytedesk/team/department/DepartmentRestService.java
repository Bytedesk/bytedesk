/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-03 23:08:22
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.team.department;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.team.member.MemberRestService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class DepartmentRestService extends BaseRestService<DepartmentEntity, DepartmentRequest, DepartmentResponse> {

    private final ModelMapper modelMapper;

    private final DepartmentRepository departmentRepository;

    private final UidUtils uidUtils;

    private final MemberRestService memberService;

    public Page<DepartmentResponse> queryByOrg(DepartmentRequest departmentRequest) {
        Pageable pageable = PageRequest.of(departmentRequest.getPageNumber(), departmentRequest.getPageSize(), Sort.Direction.ASC,"id");
        Specification<DepartmentEntity> specification = DepartmentSpecification.search(departmentRequest);
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
        return departmentRepository.findByNameAndOrgUidAndDeleted(name, orgUid, false);
    }

    @Cacheable(value = "department", key = "#uid", unless = "#result == null")
    public Optional<DepartmentEntity> findByUid(String uid) {
        return departmentRepository.findByUid(uid);
    }

    public Boolean existsByNameAndOrgUid(String name, String orgUid) {
        return departmentRepository.existsByNameAndOrgUid(name, orgUid);
    }

    public DepartmentResponse create(DepartmentRequest request) {
        // 
        if (existsByNameAndOrgUid(request.getName(), request.getOrgUid())) {
            log.error("department  " + request.getName() + " already exists");
            throw new RuntimeException("department " + request.getName() + " already exists");
        }

        DepartmentEntity department = modelMapper.map(request, DepartmentEntity.class);
        if (StringUtils.hasText(department.getUid())) {
            department.setUid(request.getUid());
        } else {
            department.setUid(uidUtils.getUid());
        }

        if (StringUtils.hasText(request.getParentUid())) {
            // log.debug("parent_uid {}", request.getParentUid());
            Optional<DepartmentEntity> parentOptional = departmentRepository.findByUid(request.getParentUid());
            if (parentOptional.isPresent()) {
                parentOptional.get().addChild(department);
            }
        } else {
            // log.debug("parent_uid is null");
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

    public DepartmentResponse update(DepartmentRequest departmentRequest) {
        //
        Optional<DepartmentEntity> optional = findByUid(departmentRequest.getUid());
        if (optional.isPresent()) {
            DepartmentEntity department = optional.get();
            // modelMapper.map(departmentRequest, DepartmentEntity.class);
            department.setName(departmentRequest.getName());
            department.setDescription(departmentRequest.getDescription());

            DepartmentEntity savedEntity = save(department);
            if (savedEntity == null) {
                throw new RuntimeException("department update failed");
            }
            return convertToResponse(savedEntity);
        } else {
            throw new RuntimeException("department not found");
        }
    }

    public DepartmentEntity save(DepartmentEntity department) {
        try {
            return departmentRepository.save(department);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

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
            memberService.clearDepartmentUid(department.getUid());
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
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            DepartmentEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    public DepartmentResponse convertToResponse(DepartmentEntity department) {
        // 过滤掉department.children中deleted为true的元素
        department.getChildren().removeIf(child -> child.isDeleted());
        DepartmentResponse departmentResponse = modelMapper.map(department, DepartmentResponse.class);
        if (department.getParent() != null) {
            departmentResponse.setParentUid(department.getParent().getUid());
        }
        return departmentResponse;
    }


}
