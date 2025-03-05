/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-05 16:45:35
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.team.department;

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


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/department")
@Tag(name = "department - 部门", description = "department apis")
public class DepartmentRestController extends BaseRestController<DepartmentRequest> {

    private final DepartmentRestService departmentService;

    @Override
    public ResponseEntity<?> queryByOrg(DepartmentRequest request) {

        Page<DepartmentResponse> departmentPage = departmentService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(departmentPage));
    }

    @Override
    public ResponseEntity<?> queryByUser(DepartmentRequest request) {
        
        Page<DepartmentResponse> departmentPage = departmentService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(departmentPage));
    }

    @ActionAnnotation(title = "department", action = "create", description = "create department")
    public ResponseEntity<?> create(@RequestBody DepartmentRequest departmentRequest) {

        DepartmentResponse department = departmentService.create(departmentRequest);

        return ResponseEntity.ok().body(JsonResult.success(department));
    }

    @ActionAnnotation(title = "department", action = "update", description = "update department")
    public ResponseEntity<?> update(@RequestBody DepartmentRequest departmentRequest) {

        DepartmentResponse department = departmentService.update(departmentRequest);
   
        return ResponseEntity.ok().body(JsonResult.success(department));
    }

    @ActionAnnotation(title = "department", action = "delete", description = "delete department")
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody DepartmentRequest departmentRequest) {

        departmentService.delete(departmentRequest);

        return ResponseEntity.ok().body(JsonResult.success("delete dep success"));
    }

    @Override
    public Object export(DepartmentRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Override
    public ResponseEntity<?> queryByUid(DepartmentRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    
}
