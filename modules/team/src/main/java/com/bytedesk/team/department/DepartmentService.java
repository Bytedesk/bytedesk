/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-03-25 14:18:25
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
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.auth.AuthService;
import com.bytedesk.core.config.BytedeskProperties;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.rbac.user.UserService;
import com.bytedesk.core.utils.Utils;
import com.bytedesk.team.organization.Organization;
import com.bytedesk.team.organization.OrganizationService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class DepartmentService {

    private final AuthService authService;

    private final UserService userService;

    private final ModelMapper modelMapper;

    private final BytedeskProperties properties;

    private final OrganizationService organizationService;

    private final DepartmentRepository departmentRepository;

    public Department create(DepartmentRequest departmentRequest) {

        Department department = modelMapper.map(departmentRequest, Department.class);
        department.setDid(Utils.getUid());

        if (StringUtils.hasLength(departmentRequest.getParent_did())) {
            log.debug("TODO: parent_did {}", departmentRequest.getParent_did());
        } else {
            log.debug("parent_did is null");
            department.setParent(null);
        }

        Optional<Organization> orgOptional = organizationService.findByOid(departmentRequest.getOrg_oid());
        if (orgOptional.isPresent()) {
            log.debug("org_oid {} exist", departmentRequest.getOrg_oid());
            department.setOrganization(orgOptional.get());
        } else {
            log.debug("org_oid {} not exist", departmentRequest.getOrg_oid());
            return null;
        }

        department.setUser(authService.getCurrentUser());

        // FIXME: throw error ?
        // log.debug("department {}", department.toString());

        return departmentRepository.save(department);
    }

    public void initData() {

        if (departmentRepository.count() > 0) {
            log.debug("department already exist");
            return;
        }

        Optional<Organization> orgOptional = organizationService.findByName(properties.getCompany());
        if (orgOptional.isPresent()) {
            //
            Department[] departments = new Department[] {
                    Department.builder().name(TypeConsts.DEPT_HR).description(TypeConsts.DEPT_HR)
                            .organization(orgOptional.get()).type(TypeConsts.TYPE_SYSTEM).build(),
                    Department.builder().name(TypeConsts.DEPT_ORG).description(TypeConsts.DEPT_ORG)
                            .organization(orgOptional.get()).type(TypeConsts.TYPE_SYSTEM).build(),
                    Department.builder().name(TypeConsts.DEPT_IT).description(TypeConsts.DEPT_IT)
                            .organization(orgOptional.get()).type(TypeConsts.TYPE_SYSTEM).build(),
                    Department.builder().name(TypeConsts.DEPT_MONEY).description(TypeConsts.DEPT_MONEY)
                            .organization(orgOptional.get()).type(TypeConsts.TYPE_SYSTEM).build(),
                    Department.builder().name(TypeConsts.DEPT_MARKETING).description(TypeConsts.DEPT_MARKETING)
                            .organization(orgOptional.get()).type(TypeConsts.TYPE_SYSTEM).build(),
                    Department.builder().name(TypeConsts.DEPT_SALES).description(TypeConsts.DEPT_SALES)
                            .organization(orgOptional.get()).type(TypeConsts.TYPE_SYSTEM).build(),
                    Department.builder().name(TypeConsts.DEPT_CUSTOMER_SERVICE)
                            .description(TypeConsts.DEPT_CUSTOMER_SERVICE)
                            .organization(orgOptional.get()).type(TypeConsts.TYPE_SYSTEM).build()
            };

            Arrays.stream(departments).forEach((department) -> {
                Optional<Department> depOptional = departmentRepository.findByName(department.getName());
                if (!depOptional.isPresent()) {
                    String uuid = UUID.randomUUID().toString().replaceAll("-", "");
                    department.setDid(uuid);
                    // department.setOrganization(orgOptional.get());
                    department.setUser(userService.getAdmin().get());
                    departmentRepository.save(department);
                }
            });
        }

    }

}
