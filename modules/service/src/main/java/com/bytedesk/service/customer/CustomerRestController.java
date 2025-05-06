/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:06:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-06 10:45:39
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.customer;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/crm")
@AllArgsConstructor
public class CustomerRestController extends BaseRestController<CustomerRequest> {

    private final CustomerRestService customerRestService;

    @Override
    public ResponseEntity<?> queryByOrg(CustomerRequest request) {
        
        Page<CustomerResponse> response = customerRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    public ResponseEntity<?> queryByUser(CustomerRequest request) {
        
        Page<CustomerResponse> response = customerRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    public ResponseEntity<?> create(CustomerRequest request) {
        
        CustomerResponse response = customerRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    public ResponseEntity<?> update(CustomerRequest request) {
        
        CustomerResponse response = customerRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    public ResponseEntity<?> delete(CustomerRequest request) {
        
        customerRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success(request.getUid()));
    }

    @Override
    public Object export(CustomerRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            customerRestService,
            CustomerExcel.class,
            "客户信息",
            "customer"
        );
    }

    @Override
    public ResponseEntity<?> queryByUid(CustomerRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    
    
}
