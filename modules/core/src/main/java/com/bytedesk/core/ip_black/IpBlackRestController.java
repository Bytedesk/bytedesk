/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-17 15:17:39
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-01 16:56:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.ip_black;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/ip/black")
@AllArgsConstructor
@Tag(name = "IP Black Management", description = "IP blacklist management APIs for blocking specific IP addresses")
public class IpBlackRestController extends BaseRestController<IpBlackRequest> {

    private final IpBlackRestService ipBlackRestService;

    @Operation(summary = "Query IP Black by Organization", description = "Retrieve IP blacklist for the current organization")
    @Override
    public ResponseEntity<?> queryByOrg(IpBlackRequest request) {

        Page<IpBlackResponse> page = ipBlackRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "Query IP Black by User", description = "Retrieve IP blacklist for the current user")
    @Override
    public ResponseEntity<?> queryByUser(IpBlackRequest request) {

        Page<IpBlackResponse> page = ipBlackRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUid(IpBlackRequest request) {
        
        IpBlackResponse response = ipBlackRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Operation(summary = "Create IP Black Entry", description = "Add an IP address to the blacklist")
    @Override
    public ResponseEntity<?> create(IpBlackRequest request) {

        IpBlackResponse response = ipBlackRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Operation(summary = "Update IP Black Entry", description = "Update an existing IP blacklist entry")
    @Override
    public ResponseEntity<?> update(IpBlackRequest request) {

        IpBlackResponse response = ipBlackRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Operation(summary = "Delete IP Black Entry", description = "Remove an IP address from the blacklist")
    @Override
    public ResponseEntity<?> delete(IpBlackRequest request) {

        ipBlackRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Operation(summary = "Export IP Black", description = "Export IP blacklist to Excel format")
    @Override
    public Object export(IpBlackRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            ipBlackRestService,
            IpBlackExcel.class,
            "黑名单Ip",
            "blackIp"
        );
    }

    


}
