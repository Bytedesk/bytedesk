/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-27 12:20:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-23 18:12:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
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
public class BlackController extends BaseRestController<BlackRequest> {

    private final BlackService blackService;

    @Override
    public ResponseEntity<?> queryByOrg(BlackRequest request) {
        
        Page<BlackResponse> page = blackService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUser(BlackRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'query'");
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
