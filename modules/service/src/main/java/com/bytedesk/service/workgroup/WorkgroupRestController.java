/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:19:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-05 16:34:26
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

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/workgroup")
public class WorkgroupRestController extends BaseRestController<WorkgroupRequest> {

    private final WorkgroupRestService workgroupService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN) // allow members to access this method
    public ResponseEntity<?> queryByOrg(WorkgroupRequest request) {

        Page<WorkgroupResponse> workgroups = workgroupService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(workgroups));
    }

    @Override
    public ResponseEntity<?> queryByUser(WorkgroupRequest request) {
        
        Page<WorkgroupResponse> workgroups = workgroupService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(workgroups));
    }

    @ActionAnnotation(title = "workgroup", action = "create", description = "create workgroup")
    public ResponseEntity<?> create(@RequestBody WorkgroupRequest request) {

        WorkgroupResponse workgroup = workgroupService.create(request);

        return ResponseEntity.ok(JsonResult.success(workgroup));
    }

    @ActionAnnotation(title = "workgroup", action = "update", description = "update workgroup")
    public ResponseEntity<?> update(@RequestBody WorkgroupRequest request) {

        WorkgroupResponse workgroup = workgroupService.update(request);
        //
        return ResponseEntity.ok(JsonResult.success(workgroup));
    }

    // updateAvatar
    @ActionAnnotation(title = "workgroup", action = "updateAvatar", description = "update workgroup avatar")
    @PostMapping("/update/avatar")
    public ResponseEntity<?> updateAvatar(@RequestBody WorkgroupRequest request) {

        WorkgroupResponse workgroup = workgroupService.updateAvatar(request);

        return ResponseEntity.ok(JsonResult.success(workgroup));
    }

    // updateStatus
    @ActionAnnotation(title = "workgroup", action = "updateStatus", description = "update workgroup status")
    @PostMapping("/update/status")
    public ResponseEntity<?> updateStatus(@RequestBody WorkgroupRequest request) {

        WorkgroupResponse workgroup = workgroupService.updateStatus(request);
        //
        return ResponseEntity.ok(JsonResult.success(workgroup));
    }

    @ActionAnnotation(title = "workgroup", action = "delete", description = "delete workgroup")
    public ResponseEntity<?> delete(@RequestBody WorkgroupRequest request) {

        workgroupService.deleteByUid(request.getUid());
        //
        return ResponseEntity.ok(JsonResult.success(request));
    }

    @Override
    public Object export(WorkgroupRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Override
    public ResponseEntity<?> queryByUid(WorkgroupRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    

    

}
