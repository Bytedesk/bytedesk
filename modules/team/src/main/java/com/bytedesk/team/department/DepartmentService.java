/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-25 09:35:29
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
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.config.BytedeskProperties;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.team.organization.Organization;
import com.bytedesk.team.organization.OrganizationService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class DepartmentService {

    private final ModelMapper modelMapper;

    private final BytedeskProperties properties;

    private final OrganizationService organizationService;

    private final DepartmentRepository departmentRepository;

    private final UidUtils uidUtils;

    public Page<DepartmentResponse> query(DepartmentRequest departmentRequest) {

        Pageable pageable = PageRequest.of(departmentRequest.getPageNumber(), departmentRequest.getPageSize(), Sort.Direction.ASC,
                "id");

        Page<Department> page = departmentRepository.findByOrgOidAndParent(departmentRequest.getOrgOid(), null, pageable);

        return page.map(this::convertToDepartmentResponse);
    }

    public Department create(DepartmentRequest departmentRequest) {

        Department department = modelMapper.map(departmentRequest, Department.class);
        department.setDid(uidUtils.getCacheSerialUid());

        if (StringUtils.hasLength(departmentRequest.getParentDid())) {
            log.debug("parent_did {}", departmentRequest.getParentDid());
            Optional<Department> parentOptional = departmentRepository.findByDid(departmentRequest.getParentDid());
            if (parentOptional.isPresent()) {
                parentOptional.get().addChild(department);
            }
        } else {
            log.debug("parent_did is null");
            department.setParent(null);
        }

        department.setOrgOid(departmentRequest.getOrgOid());

        return save(department);
    }

    @Cacheable(value = "department", key = "#name", unless = "#result == null")
    public Optional<Department> findByName(String name) {
        return departmentRepository.findByName(name);
    }

    @Cacheable(value = "department", key = "#did", unless = "#result == null")
    public Optional<Department> findByDid(String did) {
        return departmentRepository.findByDid(did);
    }

    public Department save(Department department) {
        return departmentRepository.save(department);
    }

    public DepartmentResponse convertToDepartmentResponse(Department department) {
        return modelMapper.map(department, DepartmentResponse.class);
    }

    public void initData() {

        if (departmentRepository.count() > 0) {
            log.debug("department already exist");
            return;
        }

        Optional<Organization> orgOptional = organizationService.findByName(properties.getCompany());
        if (orgOptional.isPresent()) {
            String orgOid = orgOptional.get().getOid();
            //
            Department[] departments = new Department[] {
                    Department.builder().name(TypeConsts.DEPT_HR).description(TypeConsts.DEPT_HR)
                            .orgOid(orgOid).type(TypeConsts.TYPE_SYSTEM).build(),
                    Department.builder().name(TypeConsts.DEPT_ORG).description(TypeConsts.DEPT_ORG)
                            .orgOid(orgOid).type(TypeConsts.TYPE_SYSTEM).build(),
                    Department.builder().name(TypeConsts.DEPT_IT).description(TypeConsts.DEPT_IT)
                            .orgOid(orgOid).type(TypeConsts.TYPE_SYSTEM).build(),
                    Department.builder().name(TypeConsts.DEPT_MONEY).description(TypeConsts.DEPT_MONEY)
                            .orgOid(orgOid).type(TypeConsts.TYPE_SYSTEM).build(),
                    Department.builder().name(TypeConsts.DEPT_MARKETING).description(TypeConsts.DEPT_MARKETING)
                            .orgOid(orgOid).type(TypeConsts.TYPE_SYSTEM).build(),
                    Department.builder().name(TypeConsts.DEPT_SALES).description(TypeConsts.DEPT_SALES)
                            .orgOid(orgOid).type(TypeConsts.TYPE_SYSTEM).build(),
                    Department.builder().name(TypeConsts.DEPT_CUSTOMER_SERVICE)
                            .description(TypeConsts.DEPT_CUSTOMER_SERVICE)
                            .orgOid(orgOid).type(TypeConsts.TYPE_SYSTEM).build()
            };

            Arrays.stream(departments).forEach((department) -> {
                Optional<Department> depOptional = departmentRepository.findByName(department.getName());
                if (!depOptional.isPresent()) {
                    department.setDid(uidUtils.getCacheSerialUid());
                    department.setOrgOid(orgOid);
                    // department.setOrganization(orgOptional.get());
                    // department.setUser(userService.getAdmin().get());
                    departmentRepository.save(department);
                }
            });
        }

    }

}
