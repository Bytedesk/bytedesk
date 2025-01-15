/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-15 14:09:01
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-15 14:13:36
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.bytedesk.core.black.access.VisitorAccessService;
import com.bytedesk.core.utils.JsonResult;

@RestController
@RequestMapping("/api/v1/visitor/access")
public class VisitorAccessController {

    @Autowired
    private VisitorAccessService visitorAccessService;

    @PostMapping("/block/{visitorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> blockVisitor(@PathVariable String visitorId) {
        visitorAccessService.blockVisitor(visitorId);
        return ResponseEntity.ok(JsonResult.success("Visitor blocked successfully"));
    }

    @PostMapping("/unblock/{visitorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> unblockVisitor(@PathVariable String visitorId) {
        visitorAccessService.unblockVisitor(visitorId);
        return ResponseEntity.ok(JsonResult.success("Visitor unblocked successfully"));
    }
} 