/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-27 12:20:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-19 11:41:48
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

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/black")
@AllArgsConstructor
public class BlackRestController extends BaseRestController<BlackRequest> {

    private final BlackRestService blackService;

    @Override
    public ResponseEntity<?> queryByOrg(BlackRequest request) {
        
        Page<BlackResponse> page = blackService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUser(BlackRequest request) {
        
        Page<BlackResponse> page = blackService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> create(BlackRequest request) {
        
        return ResponseEntity.ok(JsonResult.success(blackService.create(request)));
    }

    @Override
    public ResponseEntity<?> update(BlackRequest request) {
        
        return ResponseEntity.ok(JsonResult.success(blackService.update(request)));
    }

    @Override
    public ResponseEntity<?> delete(BlackRequest request) {

        blackService.delete(request);
        
        return ResponseEntity.ok(JsonResult.success());
    }

}
