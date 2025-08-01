/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-17 15:18:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-01 16:58:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.ip.white;

import org.springframework.data.domain.Page;
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
@RequestMapping("/api/v1/ip/white")
@RequiredArgsConstructor
@Tag(name = "IP Whitelist Management", description = "IP whitelist management APIs for allowing specific IP addresses")
public class IpWhitelistRestController extends BaseRestController<IpWhitelistRequest> {
    
    private final IpWhitelistRestService ipWhitelistRestService;

    @Operation(summary = "Query IP Whitelist by Organization", description = "Retrieve IP whitelist for the current organization")
    @Override
    public ResponseEntity<?> queryByOrg(IpWhitelistRequest request) {

        Page<IpWhitelistResponse> page = ipWhitelistRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "Query IP Whitelist by User", description = "Retrieve IP whitelist for the current user")
    @Override
    public ResponseEntity<?> queryByUser(IpWhitelistRequest request) {

        Page<IpWhitelistResponse> page = ipWhitelistRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUid(IpWhitelistRequest request) {
        
        IpWhitelistResponse response = ipWhitelistRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Operation(summary = "Create IP Whitelist Entry", description = "Add an IP address to the whitelist")
    @Override
    public ResponseEntity<?> create(IpWhitelistRequest request) {

        IpWhitelistResponse response = ipWhitelistRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Operation(summary = "Update IP Whitelist Entry", description = "Update an existing IP whitelist entry")
    @Override
    public ResponseEntity<?> update(IpWhitelistRequest request) {

        IpWhitelistResponse response = ipWhitelistRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Operation(summary = "Delete IP Whitelist Entry", description = "Remove an IP address from the whitelist")
    @Override
    public ResponseEntity<?> delete(IpWhitelistRequest request) {
        
        ipWhitelistRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Override
    public Object export(IpWhitelistRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

}