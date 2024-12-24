/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:06:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-23 18:26:37
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.crm;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.service.visitor.VisitorRequest;
import com.bytedesk.service.visitor.VisitorResponse;
import com.bytedesk.service.visitor.VisitorRestService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/vip/crm")
@AllArgsConstructor
public class CustomerController extends BaseRestController<CustomerRequest> {

    private final CustomerService customerService;

    private final VisitorRestService visitorService;

    @GetMapping("/query/org")
    @Override
    public ResponseEntity<?> queryByOrg(CustomerRequest request) {

        Page<CustomerResponse> page = customerService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @GetMapping("/query/visitor/org")
    public ResponseEntity<?> queryVisitorByOrg(VisitorRequest request) {

        Page<VisitorResponse> page = visitorService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }


    @GetMapping("/query")
    @Override
    public ResponseEntity<?> queryByUser(CustomerRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'query'");
    }

    @PostMapping("/create")
    @Override
    public ResponseEntity<?> create(CustomerRequest request) {
        
        CustomerResponse response = customerService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @PostMapping("/update")
    @Override
    public ResponseEntity<?> update(CustomerRequest request) {
        
        CustomerResponse response = customerService.update(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @PostMapping("/delete")
    @Override
    public ResponseEntity<?> delete(CustomerRequest request) {
        
        customerService.delete(request);

        return ResponseEntity.ok(JsonResult.success(request.getUid()));
    }

    
}
