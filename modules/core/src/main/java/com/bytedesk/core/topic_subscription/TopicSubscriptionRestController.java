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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Description;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.constant.I18Consts;
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

    private final TopicSubscriptionRestService topicSubscriptionRestService;

    @ActionAnnotation(title = I18Consts.I18N_TOPIC_SUBSCRIPTION, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query topic_subscription by org")
    @Operation(summary = "Query TopicSubscriptions by Organization", description = "Retrieve topic_subscriptions for the current organization")
    @PreAuthorize(TopicSubscriptionPermissions.HAS_TOPIC_SUBSCRIPTION_READ)
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(TopicSubscriptionRequest request) {
        
        Page<TopicSubscriptionResponse> topic_subscriptions = topicSubscriptionRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(topic_subscriptions));
    }

    @ActionAnnotation(title = I18Consts.I18N_TOPIC_SUBSCRIPTION, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query topic_subscription by user")
    @Operation(summary = "Query TopicSubscriptions by User", description = "Retrieve topic_subscriptions for the current user")
    @PreAuthorize(TopicSubscriptionPermissions.HAS_TOPIC_SUBSCRIPTION_READ)
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(TopicSubscriptionRequest request) {
        
        Page<TopicSubscriptionResponse> topic_subscriptions = topicSubscriptionRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(topic_subscriptions));
    }

    @ActionAnnotation(title = I18Consts.I18N_TOPIC_SUBSCRIPTION, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query topic_subscription by uid")
    @Operation(summary = "Query TopicSubscription by UID", description = "Retrieve a specific topic_subscription by its unique identifier")
    @PreAuthorize(TopicSubscriptionPermissions.HAS_TOPIC_SUBSCRIPTION_READ)
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(TopicSubscriptionRequest request) {
        
        TopicSubscriptionResponse topic_subscription = topicSubscriptionRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(topic_subscription));
    }

    @ActionAnnotation(title = I18Consts.I18N_TOPIC_SUBSCRIPTION, action = I18Consts.I18N_ACTION_CREATE, description = "create topic_subscription")
    @Operation(summary = "Create TopicSubscription", description = "Create a new topic_subscription")
    @Override
    @PreAuthorize(TopicSubscriptionPermissions.HAS_TOPIC_SUBSCRIPTION_CREATE)
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody TopicSubscriptionRequest request) {
        
        TopicSubscriptionResponse topic_subscription = topicSubscriptionRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(topic_subscription));
    }

    @ActionAnnotation(title = I18Consts.I18N_TOPIC_SUBSCRIPTION, action = I18Consts.I18N_ACTION_UPDATE, description = "update topic_subscription")
    @Operation(summary = "Update TopicSubscription", description = "Update an existing topic_subscription")
    @Override
    @PreAuthorize(TopicSubscriptionPermissions.HAS_TOPIC_SUBSCRIPTION_UPDATE)
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody TopicSubscriptionRequest request) {
        
        TopicSubscriptionResponse topic_subscription = topicSubscriptionRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(topic_subscription));
    }

    @Operation(summary = "Check Topic Subscription", description = "Check whether current user subscribed to the specified topic")
    @GetMapping("/is/subscribed")
    public ResponseEntity<?> isSubscribed(TopicSubscriptionRequest request) {

        Boolean isSubscribed = topicSubscriptionRestService.isSubscribed(request);

        return ResponseEntity.ok(JsonResult.success(isSubscribed));
    }

    @Operation(summary = "Subscribe Topic", description = "Subscribe to the specified topic")
    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@RequestBody TopicSubscriptionRequest request) {

        topicSubscriptionRestService.subscribe(request);

        return ResponseEntity.ok(JsonResult.success("订阅主题成功"));
    }

    @Operation(summary = "Unsubscribe Topic", description = "Unsubscribe from the specified topic")
    @PostMapping("/unsubscribe")
    public ResponseEntity<?> unsubscribe(@RequestBody TopicSubscriptionRequest request) {

        topicSubscriptionRestService.unsubscribe(request);

        return ResponseEntity.ok(JsonResult.success("取消订阅主题成功"));
    }

    @ActionAnnotation(title = I18Consts.I18N_TOPIC_SUBSCRIPTION, action = I18Consts.I18N_ACTION_DELETE, description = "delete topic_subscription")
    @Operation(summary = "Delete TopicSubscription", description = "Delete a topic_subscription")
    @Override
    @PreAuthorize(TopicSubscriptionPermissions.HAS_TOPIC_SUBSCRIPTION_DELETE)
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody TopicSubscriptionRequest request) {
        
        topicSubscriptionRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_TOPIC_SUBSCRIPTION, action = I18Consts.I18N_ACTION_EXPORT, description = "export topic_subscription")
    @Operation(summary = "Export TopicSubscriptions", description = "Export topic_subscriptions to Excel format")
    @Override
    @PreAuthorize(TopicSubscriptionPermissions.HAS_TOPIC_SUBSCRIPTION_EXPORT)
    @GetMapping("/export")
    public Object export(TopicSubscriptionRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            topicSubscriptionRestService,
            TopicSubscriptionExcel.class,
            "TopicSubscription",
            "topic_subscription"
        );
    }

    
    
}