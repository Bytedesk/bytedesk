/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-11 22:22:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.team.department;

import java.util.Arrays;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.constant.UserConsts;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class DepartmentService {

    private final ModelMapper modelMapper;

    private final DepartmentRepository departmentRepository;

    private final UidUtils uidUtils;

    // public List<DepartmentResponse> queryAll(DepartmentRequest departmentRequest)
    // {
    // List<Department> departments = departmentRepository
    // .findByOrgUidAndParentAndDeleted(departmentRequest.getOrgUid(), null, false);
    // return Arrays.asList(modelMapper.map(departments,
    // DepartmentResponse[].class));
    // }

    public Page<DepartmentResponse> queryByOrg(DepartmentRequest departmentRequest) {

        Pageable pageable = PageRequest.of(departmentRequest.getPageNumber(), departmentRequest.getPageSize(),
                Sort.Direction.ASC,
                "id");

        Specification<Department> specification = DepartmentSpecification.search(departmentRequest);
        Page<Department> page = departmentRepository.findAll(specification, pageable);
        // Page<Department> page = departmentRepository.findByOrgUidAndParentAndDeleted(departmentRequest.getOrgUid(),
        //         null, false,
        //         pageable);

        return page.map(this::convertToResponse);
    }

    public DepartmentResponse create(DepartmentRequest departmentRequest) {
        
        Department department = modelMapper.map(departmentRequest, Department.class);
        department.setUid(uidUtils.getCacheSerialUid());

        if (StringUtils.hasText(departmentRequest.getParentUid())) {
            log.debug("parent_uid {}", departmentRequest.getParentUid());
            Optional<Department> parentOptional = departmentRepository.findByUid(departmentRequest.getParentUid());
            if (parentOptional.isPresent()) {
                parentOptional.get().addChild(department);
            }
        } else {
            log.debug("parent_uid is null");
            department.setParent(null);
        }
        department.setOrgUid(departmentRequest.getOrgUid());

        Department createdDepartment = save(department);
        if (createdDepartment == null) {
            log.error("department not created");
            throw new RuntimeException("department not created");
        }

        return convertToResponse(createdDepartment);
    }

    public DepartmentResponse update(DepartmentRequest departmentRequest) {
        //
        Optional<Department> optional = findByUid(departmentRequest.getUid());
        if (optional.isPresent()) {
            Department department = optional.get();
            modelMapper.map(departmentRequest, Department.class);
            return convertToResponse(save(department));
        } else {
            log.error("department not found");
            throw new RuntimeException("department not found");
        }
    }

    public void deleteByUid(DepartmentRequest departmentRequest) {
        //
        Optional<Department> optional = findByUid(departmentRequest.getUid());
        if (optional.isPresent()) {
            Department department = optional.get();
            department.setDeleted(true);
            save(department);
        } else {
            log.error("department not found");
            throw new RuntimeException("department not found");
        }
    }

    @Cacheable(value = "department", key = "#name + '-' + #orgUid", unless = "#result == null")
    public Optional<Department> findByNameAndOrgUid(String name, String orgUid) {
        return departmentRepository.findByNameAndOrgUidAndDeleted(name, orgUid, false);
    }

    @Cacheable(value = "department", key = "#uid", unless = "#result == null")
    public Optional<Department> findByUid(String uid) {
        return departmentRepository.findByUid(uid);
    }

    public Department save(Department department) {
        try {
            return departmentRepository.save(department);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public DepartmentResponse convertToResponse(Department department) {
        DepartmentResponse departmentResponse = modelMapper.map(department, DepartmentResponse.class);
        if (department.getParent() != null) {
            departmentResponse.setParentUid(department.getParent().getUid());
        }
        return departmentResponse;
    }

    public void initData() {

        if (departmentRepository.count() > 0) {
            log.debug("department already exist");
            return;
        }
        //
        String orgUid = UserConsts.DEFAULT_ORGANIZATION_UID;
        //
        Department[] departments = new Department[] {
                Department.builder().name(I18Consts.I18N_PREFIX + TypeConsts.DEPT_ADMIN)
                        .description(TypeConsts.DEPT_ADMIN)
                        .orgUid(orgUid).type(TypeConsts.TYPE_SYSTEM)
                        .build(),
                Department.builder().name(I18Consts.I18N_PREFIX + TypeConsts.DEPT_HR)
                        .description(TypeConsts.DEPT_HR)
                        .orgUid(orgUid).type(TypeConsts.TYPE_SYSTEM)
                        .build(),
                Department.builder().name(I18Consts.I18N_PREFIX + TypeConsts.DEPT_ORG)
                        .description(TypeConsts.DEPT_ORG)
                        .orgUid(orgUid).type(TypeConsts.TYPE_SYSTEM)
                        .build(),
                Department.builder().name(I18Consts.I18N_PREFIX + TypeConsts.DEPT_IT)
                        .description(TypeConsts.DEPT_IT)
                        .orgUid(orgUid).type(TypeConsts.TYPE_SYSTEM)
                        .build(),
                Department.builder().name(I18Consts.I18N_PREFIX + TypeConsts.DEPT_MONEY)
                        .description(TypeConsts.DEPT_MONEY)
                        .orgUid(orgUid).type(TypeConsts.TYPE_SYSTEM)
                        .build(),
                Department.builder().name(I18Consts.I18N_PREFIX + TypeConsts.DEPT_MARKETING)
                        .description(TypeConsts.DEPT_MARKETING)
                        .orgUid(orgUid).type(TypeConsts.TYPE_SYSTEM)
                        .build(),
                Department.builder().name(I18Consts.I18N_PREFIX + TypeConsts.DEPT_SALES)
                        .description(TypeConsts.DEPT_SALES)
                        .orgUid(orgUid).type(TypeConsts.TYPE_SYSTEM)
                        .build(),
                Department.builder().name(I18Consts.I18N_PREFIX + TypeConsts.DEPT_CUSTOMER_SERVICE)
                        .description(TypeConsts.DEPT_CUSTOMER_SERVICE)
                        .orgUid(orgUid).type(TypeConsts.TYPE_SYSTEM)
                        .build()
        };

        Arrays.stream(departments).forEach((department) -> {
            Optional<Department> depOptional = findByNameAndOrgUid(department.getName(), orgUid);
            if (!depOptional.isPresent()) {
                department.setUid(uidUtils.getCacheSerialUid());
                departmentRepository.save(department);
            }
        });

    }

}
