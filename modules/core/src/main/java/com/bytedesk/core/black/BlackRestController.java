/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-27 12:20:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-06 10:21:37
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.black;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/black")
@AllArgsConstructor
public class BlackRestController extends BaseRestController<BlackRequest> {

    private final BlackRestService blackRestService;

    @Override
    public ResponseEntity<?> queryByOrg(BlackRequest request) {
        
        Page<BlackResponse> page = blackRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUser(BlackRequest request) {
        
        Page<BlackResponse> page = blackRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUid(BlackRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    @Override
    public ResponseEntity<?> create(BlackRequest request) {

        BlackResponse response = blackRestService.create(request);
        
        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    public ResponseEntity<?> update(BlackRequest request) {

        BlackResponse response = blackRestService.update(request);
        
        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    public ResponseEntity<?> delete(BlackRequest request) {

        blackRestService.delete(request);
        
        return ResponseEntity.ok(JsonResult.success());
    }

    @Override
    public Object export(BlackRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            blackRestService,
            BlackExcel.class,
            "黑名单",
            "black"
        );
    }
    

}
