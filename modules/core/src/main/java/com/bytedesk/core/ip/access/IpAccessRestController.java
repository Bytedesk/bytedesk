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

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/ip/access")
@RequiredArgsConstructor
public class IpAccessRestController extends BaseRestController<IpAccessRequest> {

    private final IpAccessRestService ipAccessRestService;

    @Override
    public ResponseEntity<?> queryByOrg(IpAccessRequest request) {
        return ResponseEntity.ok(JsonResult.success(ipAccessRestService.queryByOrg(request)));
    }

    @Override
    public ResponseEntity<?> queryByUser(IpAccessRequest request) {
        return ResponseEntity.ok(JsonResult.success(ipAccessRestService.queryByUser(request)));
    }

    @Override
    public ResponseEntity<?> create(IpAccessRequest request) {
        return ResponseEntity.ok(JsonResult.success(ipAccessRestService.initVisitor(request)));
    }

    @Override
    public ResponseEntity<?> update(IpAccessRequest request) {
        return ResponseEntity.ok(JsonResult.success(ipAccessRestService.update(request)));
    }

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
