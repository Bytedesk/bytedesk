/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-17 15:17:39
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-06 10:11:47
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.ip.black;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.black.BlackExcel;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/ip/black")
@AllArgsConstructor
public class IpBlacklistRestController extends BaseRestController<IpBlacklistRequest> {

    private final IpBlacklistRestService ipBlacklistRestService;

    @Override
    public ResponseEntity<?> queryByOrg(IpBlacklistRequest request) {

        Page<IpBlacklistResponse> page = ipBlacklistRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUser(IpBlacklistRequest request) {

        Page<IpBlacklistResponse> page = ipBlacklistRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUid(IpBlacklistRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    @Override
    public ResponseEntity<?> create(IpBlacklistRequest request) {

        IpBlacklistResponse response = ipBlacklistRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    public ResponseEntity<?> update(IpBlacklistRequest request) {

        IpBlacklistResponse response = ipBlacklistRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    public ResponseEntity<?> delete(IpBlacklistRequest request) {

        ipBlacklistRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Override
    public Object export(IpBlacklistRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            ipBlacklistRestService,
            BlackExcel.class,
            "黑名单Ip",
            "blackIp"
        );
    }

    


}
