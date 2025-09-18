/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 17:31:01
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.demos.consumer;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Description;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/ai/api/v1/consumer")
@AllArgsConstructor
@Tag(name = "Consumer Management", description = "Consumer management APIs for organizing and categorizing content with consumers")
@Description("Consumer Management Controller - Content consumer and categorization APIs")
public class ConsumerRestController extends BaseRestController<ConsumerRequest, ConsumerRestService> {

    private final ConsumerRestService consumerRestService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @ActionAnnotation(title = "Consumer", action = "org query", description = "query consumer by org")
    @Operation(summary = "Query Consumers by Organization", description = "Retrieve consumers for the current organization")
    @Override
    public ResponseEntity<?> queryByOrg(ConsumerRequest request) {
        
        Page<ConsumerResponse> consumers = consumerRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(consumers));
    }

    @ActionAnnotation(title = "Consumer", action = "user query", description = "query consumer by user")
    @Operation(summary = "Query Consumers by User", description = "Retrieve consumers for the current user")
    @Override
    public ResponseEntity<?> queryByUser(ConsumerRequest request) {
        
        Page<ConsumerResponse> consumers = consumerRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(consumers));
    }

    @ActionAnnotation(title = "Consumer", action = "detail query", description = "query consumer by uid")
    @Operation(summary = "Query Consumer by UID", description = "Retrieve a specific consumer by its unique identifier")
    @Override
    public ResponseEntity<?> queryByUid(ConsumerRequest request) {
        
        ConsumerResponse consumer = consumerRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(consumer));
    }

    @ActionAnnotation(title = "Consumer", action = "create", description = "create consumer")
    @Operation(summary = "Create Consumer", description = "Create a new consumer")
    @Override
    public ResponseEntity<?> create(ConsumerRequest request) {
        
        ConsumerResponse consumer = consumerRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(consumer));
    }

    @ActionAnnotation(title = "Consumer", action = "update", description = "update consumer")
    @Operation(summary = "Update Consumer", description = "Update an existing consumer")
    @Override
    public ResponseEntity<?> update(ConsumerRequest request) {
        
        ConsumerResponse consumer = consumerRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(consumer));
    }

    @ActionAnnotation(title = "Consumer", action = "delete", description = "delete consumer")
    @Operation(summary = "Delete Consumer", description = "Delete a consumer")
    @Override
    public ResponseEntity<?> delete(ConsumerRequest request) {
        
        consumerRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "Consumer", action = "export", description = "export consumer")
    @Operation(summary = "Export Consumers", description = "Export consumers to Excel format")
    @Override
    @GetMapping("/export")
    public Object export(ConsumerRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            consumerRestService,
            ConsumerExcel.class,
            "Consumer",
            "consumer"
        );
    }

    
    
}