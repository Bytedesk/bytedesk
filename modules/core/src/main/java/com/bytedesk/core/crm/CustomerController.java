/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:06:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-24 22:09:06
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.crm;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/crm")
@AllArgsConstructor
public class CustomerController extends BaseRestController<CustomerRequest> {

    private final CustomerService customerService;

    @Override
    public ResponseEntity<?> queryByOrg(CustomerRequest request) {
        
        Page<CustomerResponse> response = customerService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    public ResponseEntity<?> queryByUser(CustomerRequest request) {
        
        Page<CustomerResponse> response = customerService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    public ResponseEntity<?> create(CustomerRequest request) {
        
        CustomerResponse response = customerService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    public ResponseEntity<?> update(CustomerRequest request) {
        
        CustomerResponse response = customerService.update(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    public ResponseEntity<?> delete(CustomerRequest request) {
        
        customerService.delete(request);

        return ResponseEntity.ok(JsonResult.success(request.getUid()));
    }

    
    
}
