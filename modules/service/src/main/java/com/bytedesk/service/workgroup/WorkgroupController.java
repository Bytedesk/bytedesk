/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:19:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-18 17:14:50
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.workgroup;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.action.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.rbac.role.RolePermissions;
import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/workgroup")
public class WorkgroupController extends BaseRestController<WorkgroupRequest> {

    private final WorkgroupService workgroupService;

    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    public ResponseEntity<?> queryByOrg(WorkgroupRequest workgroupRequest) {

        Page<WorkgroupResponse> workgroups = workgroupService.queryByOrg(workgroupRequest);

        return ResponseEntity.ok(JsonResult.success(workgroups));
    }

    @Override
    public ResponseEntity<?> queryByUser(WorkgroupRequest arg0) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @ActionAnnotation(title = "workgroup", action = "create", description = "create workgroup")
    public ResponseEntity<?> create(@RequestBody WorkgroupRequest workgroupRequest) {

        WorkgroupResponse workgroup = workgroupService.create(workgroupRequest);
        if (workgroup == null) {
            return ResponseEntity.ok(JsonResult.error("create workgroup failed", -1));
        }
        return ResponseEntity.ok(JsonResult.success(workgroup));
    }

    @ActionAnnotation(title = "workgroup", action = "update", description = "update workgroup")
    public ResponseEntity<?> update(@RequestBody WorkgroupRequest workgroupRequest) {

        WorkgroupResponse workgroup = workgroupService.update(workgroupRequest);
        if (workgroup == null) {
            return ResponseEntity.ok(JsonResult.error("update failed", -1));
        }
        //
        return ResponseEntity.ok(JsonResult.success(workgroup));
    }

    @ActionAnnotation(title = "workgroup", action = "delete", description = "delete workgroup")
    public ResponseEntity<?> delete(@RequestBody WorkgroupRequest workgroupRequest) {

        workgroupService.deleteByUid(workgroupRequest.getUid());
        //
        return ResponseEntity.ok(JsonResult.success(workgroupRequest));
    }
    

    

}
