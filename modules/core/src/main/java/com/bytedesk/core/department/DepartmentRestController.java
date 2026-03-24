/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 17:17:02
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.department;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/department")
@Tag(name = "Department Management", description = "Department management APIs")
public class DepartmentRestController extends BaseRestController<DepartmentRequest, DepartmentRestService> {

    private final DepartmentRestService departmentRestService;

    @ActionAnnotation(title = I18Consts.I18N_DEPARTMENT, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query department by org")
    @PreAuthorize(DepartmentPermissions.HAS_DEPARTMENT_READ)
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(DepartmentRequest request) {

        Page<DepartmentResponse> departmentPage = departmentRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(departmentPage));
    }

    @ActionAnnotation(title = I18Consts.I18N_DEPARTMENT, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query department by user")
    @PreAuthorize(DepartmentPermissions.HAS_DEPARTMENT_READ)
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(DepartmentRequest request) {
        
        Page<DepartmentResponse> departmentPage = departmentRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(departmentPage));
    }

    @ActionAnnotation(title = I18Consts.I18N_DEPARTMENT, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query department by uid")
    @PreAuthorize(DepartmentPermissions.HAS_DEPARTMENT_READ_OR_TICKET_READ)
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(DepartmentRequest request) {

        DepartmentResponse department = departmentRestService.queryByUid(request);
        
        return ResponseEntity.ok(JsonResult.success(department));
    }

    @PreAuthorize(DepartmentPermissions.HAS_DEPARTMENT_CREATE)
    @ActionAnnotation(title = I18Consts.I18N_DEPARTMENT, action = I18Consts.I18N_ACTION_CREATE, description = "create department")
    @Override
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody DepartmentRequest request) {

        DepartmentResponse department = departmentRestService.create(request);

        return ResponseEntity.ok().body(JsonResult.success(department));
    }

    @PreAuthorize(DepartmentPermissions.HAS_DEPARTMENT_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_DEPARTMENT, action = I18Consts.I18N_ACTION_UPDATE, description = "update department")
    @Override
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody DepartmentRequest request) {

        DepartmentResponse department = departmentRestService.update(request);
   
        return ResponseEntity.ok().body(JsonResult.success(department));
    }

    @PreAuthorize(DepartmentPermissions.HAS_DEPARTMENT_DELETE)
    @ActionAnnotation(title = I18Consts.I18N_DEPARTMENT, action = I18Consts.I18N_ACTION_DELETE, description = "delete department")
    @Override
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody DepartmentRequest request) {

        departmentRestService.delete(request);

        return ResponseEntity.ok().body(JsonResult.success("delete dep success"));
    }

    @PreAuthorize(DepartmentPermissions.HAS_DEPARTMENT_EXPORT)
    @ActionAnnotation(title = I18Consts.I18N_DEPARTMENT, action = I18Consts.I18N_ACTION_EXPORT, description = "export department")
    @GetMapping("/export")
    @Override
    public Object export(DepartmentRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    

    
}
