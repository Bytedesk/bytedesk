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
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/department")
@Tag(name = "department - 部门", description = "department apis")
public class DepartmentRestController extends BaseRestController<DepartmentRequest, DepartmentRestService> {

    private final DepartmentRestService departmentRestService;

    @ActionAnnotation(title = "部门", action = "组织查询", description = "query department by org")
    @PreAuthorize(DepartmentPermissions.HAS_DEPARTMENT_READ_ANY_LEVEL)
    @Override
    public ResponseEntity<?> queryByOrg(DepartmentRequest request) {

        Page<DepartmentResponse> departmentPage = departmentRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(departmentPage));
    }

    @ActionAnnotation(title = "部门", action = "用户查询", description = "query department by user")
    @PreAuthorize(DepartmentPermissions.HAS_DEPARTMENT_READ_ANY_LEVEL)
    @Override
    public ResponseEntity<?> queryByUser(DepartmentRequest request) {
        
        Page<DepartmentResponse> departmentPage = departmentRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(departmentPage));
    }

    @ActionAnnotation(title = "部门", action = "查询详情", description = "query department by uid")
    @PreAuthorize(DepartmentPermissions.HAS_DEPARTMENT_READ_ANY_LEVEL)
    @Override
    public ResponseEntity<?> queryByUid(DepartmentRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    @PreAuthorize(DepartmentPermissions.HAS_DEPARTMENT_CREATE_ANY_LEVEL)
    @ActionAnnotation(title = "部门", action = "新建", description = "create department")
    public ResponseEntity<?> create(@RequestBody DepartmentRequest request) {

        DepartmentResponse department = departmentRestService.create(request);

        return ResponseEntity.ok().body(JsonResult.success(department));
    }

    @PreAuthorize(DepartmentPermissions.HAS_DEPARTMENT_UPDATE_ANY_LEVEL)
    @ActionAnnotation(title = "部门", action = "更新", description = "update department")
    public ResponseEntity<?> update(@RequestBody DepartmentRequest request) {

        DepartmentResponse department = departmentRestService.update(request);
   
        return ResponseEntity.ok().body(JsonResult.success(department));
    }

    @PreAuthorize(DepartmentPermissions.HAS_DEPARTMENT_DELETE_ANY_LEVEL)
    @ActionAnnotation(title = "部门", action = "删除", description = "delete department")
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody DepartmentRequest request) {

        departmentRestService.delete(request);

        return ResponseEntity.ok().body(JsonResult.success("delete dep success"));
    }

    @PreAuthorize(DepartmentPermissions.HAS_DEPARTMENT_EXPORT_ANY_LEVEL)
    @ActionAnnotation(title = "部门", action = "导出", description = "export department")
    @GetMapping("/export")
    @Override
    public Object export(DepartmentRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    

    
}
