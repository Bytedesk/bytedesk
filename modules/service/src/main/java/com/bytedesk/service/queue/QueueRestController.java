/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:03:29
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-30 21:00:16
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/queue")
public class QueueRestController extends BaseRestController<QueueRequest> {

    private final QueueRestService queueRestService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg (QueueRequest request) {
        
        Page <QueueResponse> page = queueRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUser (QueueRequest request) {
        
        Page <QueueResponse> page = queueRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUid(QueueRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    @Override
    public ResponseEntity<?> create (QueueRequest request) {

        QueueResponse queueResponse = queueRestService.create(request);
        
        return ResponseEntity.ok(JsonResult.success(queueResponse));
    }

    @Override
    public ResponseEntity<?> update (QueueRequest request) {

        QueueResponse queueResponse = queueRestService.update(request);
        
        return ResponseEntity.ok(JsonResult.success(queueResponse));
    }

    @Override
    public ResponseEntity<?> delete (QueueRequest request) {

        queueRestService.delete(request);
        
        return ResponseEntity.ok(JsonResult.success());
    }

    @Override
    public Object export(QueueRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            queueRestService,
            QueueExcel.class,
            "监控队列",
            "queue"
        );
    }

    

    


}
