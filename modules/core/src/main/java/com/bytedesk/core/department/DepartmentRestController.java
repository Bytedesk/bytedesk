/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-12 14:26:15
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
public class DepartmentRestController extends BaseRestController<DepartmentRequest> {

    private final DepartmentRestService departmentRestService;

    // @PreAuthorize("hasAuthority('MEMBER_READ')") // 暂时不加权限
    @Override
    public ResponseEntity<?> queryByOrg(DepartmentRequest request) {

        Page<DepartmentResponse> departmentPage = departmentRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(departmentPage));
    }

    // @PreAuthorize("hasAuthority('MEMBER_READ')") // 暂时不加权限
    @Override
    public ResponseEntity<?> queryByUser(DepartmentRequest request) {
        
        Page<DepartmentResponse> departmentPage = departmentRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(departmentPage));
    }

    // @PreAuthorize("hasAuthority('MEMBER_READ')") // 暂时不加权限
    @Override
    public ResponseEntity<?> queryByUid(DepartmentRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    @PreAuthorize("hasAuthority('MEMBER_CREATE')")
    @ActionAnnotation(title = "部门", action = "新建", description = "create department")
    public ResponseEntity<?> create(@RequestBody DepartmentRequest request) {

        DepartmentResponse department = departmentRestService.create(request);

        return ResponseEntity.ok().body(JsonResult.success(department));
    }

    @PreAuthorize("hasAuthority('MEMBER_UPDATE')")
    @ActionAnnotation(title = "部门", action = "更新", description = "update department")
    public ResponseEntity<?> update(@RequestBody DepartmentRequest request) {

        DepartmentResponse department = departmentRestService.update(request);
   
        return ResponseEntity.ok().body(JsonResult.success(department));
    }

    @PreAuthorize("hasAuthority('MEMBER_DELETE')")
    @ActionAnnotation(title = "部门", action = "删除", description = "delete department")
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody DepartmentRequest request) {

        departmentRestService.delete(request);

        return ResponseEntity.ok().body(JsonResult.success("delete dep success"));
    }

    @PreAuthorize("hasAuthority('MEMBER_EXPORT')")
    @Override
    public Object export(DepartmentRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    

    
}
