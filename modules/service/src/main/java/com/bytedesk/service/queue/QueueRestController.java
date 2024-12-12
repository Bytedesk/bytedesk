/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:03:29
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-02 14:53:39
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.rbac.role.RolePermissions;
import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/visitor/queue")
public class QueueRestController extends BaseRestController <QueueRequest> {

    private final QueueRestService queueService;

    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg (QueueRequest request) {
        
        Page <QueueResponse> page = queueService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUser (QueueRequest request) {
        
        Page <QueueResponse> page = queueService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> create (QueueRequest request) {
        
        return ResponseEntity.ok(JsonResult.success(queueService.create(request)));
    }

    @Override
    public ResponseEntity<?> update (QueueRequest request) {
        
        return ResponseEntity.ok(JsonResult.success(queueService.update(request)));
    }

    @Override
    public ResponseEntity<?> delete (QueueRequest request) {

        queueService.delete(request);
        
        return ResponseEntity.ok(JsonResult.success());
    }

    


}
