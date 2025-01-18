/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-17 15:17:39
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-18 20:39:55
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.ip.black;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/ip/black")
@AllArgsConstructor
public class IpBlacklistRestController extends BaseRestController<IpBlacklistRequest> {

    private final IpBlacklistRestService ipBlacklistRestService;

    @Override
    public ResponseEntity<?> queryByOrg(IpBlacklistRequest request) {
        return ResponseEntity.ok(JsonResult.success(ipBlacklistRestService.queryByOrg(request)));
    }

    @Override
    public ResponseEntity<?> queryByUser(IpBlacklistRequest request) {
        return ResponseEntity.ok(JsonResult.success(ipBlacklistRestService.queryByUser(request)));
    }

    @Override
    public ResponseEntity<?> create(IpBlacklistRequest request) {
        return ResponseEntity.ok(JsonResult.success(ipBlacklistRestService.create(request)));
    }

    @Override
    public ResponseEntity<?> update(IpBlacklistRequest request) {
        return ResponseEntity.ok(JsonResult.success(ipBlacklistRestService.update(request)));
    }

    @Override
    public ResponseEntity<?> delete(IpBlacklistRequest request) {
        ipBlacklistRestService.delete(request);
        return ResponseEntity.ok(JsonResult.success());
    }


}
