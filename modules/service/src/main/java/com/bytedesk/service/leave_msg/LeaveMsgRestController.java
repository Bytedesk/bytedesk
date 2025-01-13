/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:04:34
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-18 17:12:45
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.leave_msg;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.rbac.role.RolePermissions;
import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/leavemsg")
@AllArgsConstructor
public class LeaveMsgRestController extends BaseRestController<LeaveMsgRequest> {

    private final LeaveMsgRestService LeaveMsgService;

    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(LeaveMsgRequest request) {

        Page<LeaveMsgResponse> page = LeaveMsgService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUser(LeaveMsgRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'query'");
    }

    @PostMapping("/create")
    @Override
    public ResponseEntity<?> create(@RequestBody LeaveMsgRequest request) {

        LeaveMsgResponse response = LeaveMsgService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    public ResponseEntity<?> update(LeaveMsgRequest request) {

        LeaveMsgResponse response = LeaveMsgService.update(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    public ResponseEntity<?> delete(LeaveMsgRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

}
