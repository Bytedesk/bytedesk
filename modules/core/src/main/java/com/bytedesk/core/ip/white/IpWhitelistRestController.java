/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-17 15:18:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-05 16:36:09
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.ip.white;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/ip/white")
@RequiredArgsConstructor
public class IpWhitelistRestController extends BaseRestController<IpWhitelistRequest> {
    
    private final IpWhitelistRestService ipWhitelistRestService;

    @Override
    public ResponseEntity<?> queryByOrg(IpWhitelistRequest request) {
        return ResponseEntity.ok(JsonResult.success(ipWhitelistRestService.queryByOrg(request)));
    }

    @Override
    public ResponseEntity<?> queryByUser(IpWhitelistRequest request) {
        return ResponseEntity.ok(JsonResult.success(ipWhitelistRestService.queryByUser(request)));
    }

    @Override
    public ResponseEntity<?> create(IpWhitelistRequest request) {
        return ResponseEntity.ok(JsonResult.success(ipWhitelistRestService.create(request)));
    }

    @Override
    public ResponseEntity<?> update(IpWhitelistRequest request) {
        return ResponseEntity.ok(JsonResult.success(ipWhitelistRestService.update(request)));
    }

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

    @Override
    public ResponseEntity<?> queryByUid(IpWhitelistRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    

}