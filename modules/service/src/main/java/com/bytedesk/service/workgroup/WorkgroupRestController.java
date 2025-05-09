/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:19:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-09 10:21:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.workgroup;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/workgroup")
public class WorkgroupRestController extends BaseRestController<WorkgroupRequest> {

    private final WorkgroupRestService workgroupRestService;

    // @PreAuthorize("hasAuthority('WORKGROUP_READ')") // 前端很多地方需要查询工作组列表，所以不需要权限
    public ResponseEntity<?> queryByOrg(WorkgroupRequest request) {

        Page<WorkgroupResponse> workgroups = workgroupRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(workgroups));
    }

    // @PreAuthorize("hasAuthority('WORKGROUP_READ')")
    @Override
    public ResponseEntity<?> queryByUser(WorkgroupRequest request) {
        
        Page<WorkgroupResponse> workgroups = workgroupRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(workgroups));
    }

    // @PreAuthorize("hasAuthority('WORKGROUP_READ')")
    @Override
    public ResponseEntity<?> queryByUid(WorkgroupRequest request) {
        
        WorkgroupResponse workgroup = workgroupRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(workgroup));
    }

    @PreAuthorize("hasAuthority('WORKGROUP_CREATE')")
    @ActionAnnotation(title = "工作组", action = "新建", description = "create workgroup")
    public ResponseEntity<?> create(@RequestBody WorkgroupRequest request) {

        WorkgroupResponse workgroup = workgroupRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(workgroup));
    }

    @PreAuthorize("hasAuthority('WORKGROUP_UPDATE')")
    @ActionAnnotation(title = "工作组", action = "更新", description = "update workgroup")
    public ResponseEntity<?> update(@RequestBody WorkgroupRequest request) {

        WorkgroupResponse workgroup = workgroupRestService.update(request);
        //
        return ResponseEntity.ok(JsonResult.success(workgroup));
    }

    // updateAvatar
    @PreAuthorize("hasAuthority('WORKGROUP_UPDATE')")
    @ActionAnnotation(title = "工作组", action = "updateAvatar", description = "update workgroup avatar")
    @PostMapping("/update/avatar")
    public ResponseEntity<?> updateAvatar(@RequestBody WorkgroupRequest request) {

        WorkgroupResponse workgroup = workgroupRestService.updateAvatar(request);

        return ResponseEntity.ok(JsonResult.success(workgroup));
    }

    // updateStatus
    @PreAuthorize("hasAuthority('WORKGROUP_UPDATE')")
    @ActionAnnotation(title = "工作组", action = "updateStatus", description = "update workgroup status")
    @PostMapping("/update/status")
    public ResponseEntity<?> updateStatus(@RequestBody WorkgroupRequest request) {

        WorkgroupResponse workgroup = workgroupRestService.updateStatus(request);
        //
        return ResponseEntity.ok(JsonResult.success(workgroup));
    }

    @PreAuthorize("hasAuthority('WORKGROUP_DELETE')")
    @ActionAnnotation(title = "工作组", action = "删除", description = "delete workgroup")
    public ResponseEntity<?> delete(@RequestBody WorkgroupRequest request) {

        workgroupRestService.deleteByUid(request.getUid());
        //
        return ResponseEntity.ok(JsonResult.success(request));
    }

    @PreAuthorize("hasAuthority('WORKGROUP_EXPORT')")
    @Override
    public Object export(WorkgroupRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    
    

    

}
