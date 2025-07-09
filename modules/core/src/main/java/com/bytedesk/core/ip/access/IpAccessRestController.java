/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-17 15:18:23
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-05 16:35:57
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.ip.access;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/ip/access")
@RequiredArgsConstructor
@Tag(name = "IP Access Management", description = "IP access control management APIs for managing IP access permissions")
public class IpAccessRestController extends BaseRestController<IpAccessRequest> {

    private final IpAccessRestService ipAccessRestService;

    @Operation(summary = "Query IP Access by Organization", description = "Retrieve IP access records for the current organization")
    @Override
    public ResponseEntity<?> queryByOrg(IpAccessRequest request) {
        return ResponseEntity.ok(JsonResult.success(ipAccessRestService.queryByOrg(request)));
    }

    @Operation(summary = "Query IP Access by User", description = "Retrieve IP access records for the current user")
    @Override
    public ResponseEntity<?> queryByUser(IpAccessRequest request) {
        return ResponseEntity.ok(JsonResult.success(ipAccessRestService.queryByUser(request)));
    }

    @Operation(summary = "Create IP Access Record", description = "Create a new IP access record")
    @Override
    public ResponseEntity<?> create(IpAccessRequest request) {
        return ResponseEntity.ok(JsonResult.success(ipAccessRestService.create(request)));
    }

    @Operation(summary = "Update IP Access Record", description = "Update an existing IP access record")
    @Override
    public ResponseEntity<?> update(IpAccessRequest request) {
        return ResponseEntity.ok(JsonResult.success(ipAccessRestService.update(request)));
    }

    @Operation(summary = "Delete IP Access Record", description = "Delete an IP access record")
    @Override
    public ResponseEntity<?> delete(IpAccessRequest request) {
        ipAccessRestService.delete(request);
        return ResponseEntity.ok(JsonResult.success());
    }

    @Override
    public Object export(IpAccessRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Override
    public ResponseEntity<?> queryByUid(IpAccessRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
  


}
