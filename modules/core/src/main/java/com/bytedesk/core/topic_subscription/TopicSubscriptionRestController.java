/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-11-29 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.topic_subscription;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/v1/topic/subscription")
@AllArgsConstructor
@Tag(name = "TopicSubscription Management", description = "TopicSubscription management APIs for organizing and categorizing content with topic_subscriptions")
@Description("TopicSubscription Management Controller - Content topic_subscriptionging and categorization APIs")
public class TopicSubscriptionRestController extends BaseRestController<TopicSubscriptionRequest, TopicSubscriptionRestService> {

    private final TopicSubscriptionRestService topic_subscriptionRestService;

    @ActionAnnotation(title = "TopicSubscription", action = "组织查询", description = "query topic_subscription by org")
    @Operation(summary = "Query TopicSubscriptions by Organization", description = "Retrieve topic_subscriptions for the current organization")
    @PreAuthorize(TopicSubscriptionPermissions.HAS_TOPIC_SUBSCRIPTION_READ)
    @Override
    public ResponseEntity<?> queryByOrg(TopicSubscriptionRequest request) {
        
        Page<TopicSubscriptionResponse> topic_subscriptions = topic_subscriptionRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(topic_subscriptions));
    }

    @ActionAnnotation(title = "TopicSubscription", action = "用户查询", description = "query topic_subscription by user")
    @Operation(summary = "Query TopicSubscriptions by User", description = "Retrieve topic_subscriptions for the current user")
    @PreAuthorize(TopicSubscriptionPermissions.HAS_TOPIC_SUBSCRIPTION_READ)
    @Override
    public ResponseEntity<?> queryByUser(TopicSubscriptionRequest request) {
        
        Page<TopicSubscriptionResponse> topic_subscriptions = topic_subscriptionRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(topic_subscriptions));
    }

    @ActionAnnotation(title = "TopicSubscription", action = "查询详情", description = "query topic_subscription by uid")
    @Operation(summary = "Query TopicSubscription by UID", description = "Retrieve a specific topic_subscription by its unique identifier")
    @PreAuthorize(TopicSubscriptionPermissions.HAS_TOPIC_SUBSCRIPTION_READ)
    @Override
    public ResponseEntity<?> queryByUid(TopicSubscriptionRequest request) {
        
        TopicSubscriptionResponse topic_subscription = topic_subscriptionRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(topic_subscription));
    }

    @ActionAnnotation(title = "TopicSubscription", action = "新建", description = "create topic_subscription")
    @Operation(summary = "Create TopicSubscription", description = "Create a new topic_subscription")
    @Override
    @PreAuthorize(TopicSubscriptionPermissions.HAS_TOPIC_SUBSCRIPTION_CREATE)
    public ResponseEntity<?> create(TopicSubscriptionRequest request) {
        
        TopicSubscriptionResponse topic_subscription = topic_subscriptionRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(topic_subscription));
    }

    @ActionAnnotation(title = "TopicSubscription", action = "更新", description = "update topic_subscription")
    @Operation(summary = "Update TopicSubscription", description = "Update an existing topic_subscription")
    @Override
    @PreAuthorize(TopicSubscriptionPermissions.HAS_TOPIC_SUBSCRIPTION_UPDATE)
    public ResponseEntity<?> update(TopicSubscriptionRequest request) {
        
        TopicSubscriptionResponse topic_subscription = topic_subscriptionRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(topic_subscription));
    }

    @ActionAnnotation(title = "TopicSubscription", action = "删除", description = "delete topic_subscription")
    @Operation(summary = "Delete TopicSubscription", description = "Delete a topic_subscription")
    @Override
    @PreAuthorize(TopicSubscriptionPermissions.HAS_TOPIC_SUBSCRIPTION_DELETE)
    public ResponseEntity<?> delete(TopicSubscriptionRequest request) {
        
        topic_subscriptionRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "TopicSubscription", action = "导出", description = "export topic_subscription")
    @Operation(summary = "Export TopicSubscriptions", description = "Export topic_subscriptions to Excel format")
    @Override
    @PreAuthorize(TopicSubscriptionPermissions.HAS_TOPIC_SUBSCRIPTION_EXPORT)
    @GetMapping("/export")
    public Object export(TopicSubscriptionRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            topic_subscriptionRestService,
            TopicSubscriptionExcel.class,
            "TopicSubscription",
            "topic_subscription"
        );
    }

    
    
}