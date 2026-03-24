/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-13 16:14:26
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-05 15:20:34
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.topic;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

/**
 * 主题管理接口
 * 
 * @author Jackning
 * @since 2024-04-13
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/topic")
@Tag(name = "Topic Management", description = "Topic management APIs, including query, create, update, delete, and subscription operations")
public class TopicRestController extends BaseRestController<TopicRequest, TopicRestService> {

    private final TopicRestService topicRestService;

    /**
     * 根据组织查询主题
     * 
     * @param request 查询请求
     * @return 分页主题列表
     */
    @Operation(summary = "Query Topics by Organization", description = "Return the topic list for the current organization")
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(TopicRequest request) {

        Page<TopicResponse> topicPage = topicRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(topicPage));
    }

    /**
     * 根据用户查询主题
     * 
     * @param request 查询请求
     * @return 分页主题列表
     */
    @Operation(summary = "Query Topics by User", description = "Return the topic list for the current user")
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(TopicRequest request) {

        Page<TopicResponse> topicPage = topicRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(topicPage));
    }

    /**
     * 根据UID查询主题
     * 
     * @param request 查询请求
     * @return 主题信息
     */
    @Operation(summary = "Query Topic by UID", description = "Query the topic by unique identifier")
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(TopicRequest request) {

        TopicResponse topicResponse = topicRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(topicResponse));
    }

    /**
     * 创建主题
     * 
     * @param request 创建主题请求
     * @return 创建的主题
     */
    @Operation(summary = "Create Topic", description = "Create a new topic")
    @ActionAnnotation(title = I18Consts.I18N_TOPIC, action = I18Consts.I18N_ACTION_CREATE, description = "create topic")
    @Override
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody TopicRequest request) {

        TopicResponse topic = topicRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(topic));
    }

    /**
     * 更新主题
     * 
     * @param request 更新主题请求
     * @return 更新后的主题
     */
    @Operation(summary = "Update Topic", description = "Update existing topic information")
    @ActionAnnotation(title = I18Consts.I18N_TOPIC, action = I18Consts.I18N_ACTION_UPDATE, description = "update topic")
    @Override
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody TopicRequest request) {

        TopicResponse topicResponse = topicRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(topicResponse));
    }

    // 判断是否已经订阅 is/subscribed
    @GetMapping("/is/subscribed")
    public ResponseEntity<?> isSubscribed(TopicRequest request) {

        Boolean isSubscribed = topicRestService.isSubscribed(request);

        return ResponseEntity.ok(JsonResult.success(isSubscribed));
    }

    /**
     * 订阅主题
     * 
     * @param request 订阅请求
     * @return 订阅结果
     */
    @Operation(summary = "Subscribe Topic", description = "Subscribe to the specified topic")
    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@RequestBody TopicRequest request) {

        TopicResponse topic = topicRestService.subscribe(request);

        return ResponseEntity.ok(JsonResult.success("订阅主题成功", topic));
    }

    /**
     * 取消订阅主题
     * 
     * @param request 取消订阅请求
     * @return 取消订阅结果
     */
    @Operation(summary = "Unsubscribe Topic", description = "Unsubscribe from the specified topic")
    @PostMapping("/unsubscribe")
    public ResponseEntity<?> unsubscribe(@RequestBody TopicRequest request) {

        TopicResponse topic = topicRestService.unsubscribe(request);

        return ResponseEntity.ok(JsonResult.success("取消订阅主题成功", topic));
    }

    /**
     * 删除主题
     * 
     * @param request 删除请求
     * @return 删除结果
     */
    @Operation(summary = "Delete Topic", description = "Delete the specified topic")
    @ActionAnnotation(title = I18Consts.I18N_TOPIC, action = I18Consts.I18N_ACTION_DELETE, description = "delete topic")
    @Override
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody TopicRequest request) {

        topicRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success("删除主题成功"));
    }

    /**
     * 导出主题列表
     * 
     * @param request 导出请求
     * @param response HTTP响应
     * @return 导出结果
     */
    @Operation(summary = "Export Topic List", description = "Export topic data to Excel format")
    @ActionAnnotation(title = I18Consts.I18N_TOPIC, action = I18Consts.I18N_ACTION_EXPORT, description = "export topic")
    @Override
    @GetMapping("/export")
    public Object export(TopicRequest request, HttpServletResponse response) {
        // 如果没有TopicExcel类，需要创建一个
        return exportTemplate(
            request,
            response,
            topicRestService,
            TopicEntity.class, // 如果存在TopicExcel类，应替换为TopicExcel.class
            "Topic",
            "topic"
        );
    }
    
}
