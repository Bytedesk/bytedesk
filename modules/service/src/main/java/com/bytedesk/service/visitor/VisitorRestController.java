/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-18 17:13:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.rbac.role.RolePermissions;
import com.bytedesk.core.utils.JsonResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/visitor")
public class VisitorRestController extends BaseRestController<VisitorRequest> {

    private final VisitorRestService visitorService;

    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(VisitorRequest request) {

        Page<VisitorResponse> page = visitorService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    // query visitor info by uid
    @GetMapping("/query")
    @Override
    public ResponseEntity<?> queryByUser(VisitorRequest visitorRequest) {
        //
        VisitorResponse visitorResponse = visitorService.query(visitorRequest);
        //
        return ResponseEntity.ok(JsonResult.success(visitorResponse));
    }

    @Override
    public ResponseEntity<?> create(VisitorRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody VisitorRequest visitorRequest) {
        //
        return ResponseEntity.ok(JsonResult.success("update success"));
    }

    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody VisitorRequest visitorRequest) {
        //
        return ResponseEntity.ok(JsonResult.success("delete success"));
    }

}
