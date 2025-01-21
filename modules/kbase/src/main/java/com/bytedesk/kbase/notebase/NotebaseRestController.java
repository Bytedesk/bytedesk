/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-21 10:43:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.notebase;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.rbac.role.RolePermissions;
import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/notebase")
@AllArgsConstructor
public class NotebaseRestController extends BaseRestController<NotebaseRequest> {

    private final NotebaseService NotebaseService;

    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(NotebaseRequest request) {

        Page<NotebaseResponse> page = NotebaseService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUser(NotebaseRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'query'");
    }

    @Override
    public ResponseEntity<?> create(@RequestBody NotebaseRequest request) {

        NotebaseResponse Notebase = NotebaseService.create(request);

        return ResponseEntity.ok(JsonResult.success(Notebase));
    }

    @Override
    public ResponseEntity<?> update(@RequestBody NotebaseRequest request) {

        NotebaseResponse Notebase = NotebaseService.update(request);

        return ResponseEntity.ok(JsonResult.success(Notebase));
    }

    @Override
    public ResponseEntity<?> delete(@RequestBody NotebaseRequest request) {

        NotebaseService.delete(request);

        return ResponseEntity.ok(JsonResult.success("delete success", request.getUid()));
    }

}
