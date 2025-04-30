/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-14 17:57:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-30 21:02:34
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue_member;

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
@RequestMapping("/api/v1/queue/member")
public class QueueMemberRestController extends BaseRestController<QueueMemberRequest> {

    private final QueueMemberRestService queueMemberRestService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(QueueMemberRequest request) {
        
        Page<QueueMemberResponse> page = queueMemberRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUser(QueueMemberRequest request) {
        
        Page<QueueMemberResponse> page = queueMemberRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUid(QueueMemberRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    

    @Override
    public ResponseEntity<?> create(QueueMemberRequest request) {
        
        QueueMemberResponse response = queueMemberRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    public ResponseEntity<?> update(QueueMemberRequest request) {
        
        QueueMemberResponse response = queueMemberRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    public ResponseEntity<?> delete(QueueMemberRequest request) {
        
        queueMemberRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Override
    public Object export(QueueMemberRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            queueMemberRestService,
            QueueMemberExcel.class,
            "监控成员",
            "queue-member"
        );
    }
}
