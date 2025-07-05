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
@Tag(name = "主题管理", description = "主题管理相关接口，包括查询、创建、更新、删除等操作")
public class TopicRestController extends BaseRestController<TopicRequest> {

    private final TopicRestService topicRestService;

    /**
     * 根据组织查询主题
     * 
     * @param request 查询请求
     * @return 分页主题列表
     */
    @Operation(summary = "根据组织查询主题", description = "返回当前组织的主题列表")
    @Override
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
    @Operation(summary = "根据用户查询主题", description = "返回当前用户的主题列表")
    @Override
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
    @Operation(summary = "根据UID查询主题", description = "通过唯一标识符查询主题")
    @Override
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
    @Operation(summary = "创建主题", description = "创建新的主题")
    @ActionAnnotation(title = "主题", action = "新建", description = "create topic")
    @Override
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
    @Operation(summary = "更新主题", description = "更新已存在的主题信息")
    @ActionAnnotation(title = "主题", action = "更新", description = "update topic")
    @Override
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
    @Operation(summary = "订阅主题", description = "订阅指定主题")
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
    @Operation(summary = "取消订阅主题", description = "取消订阅指定主题")
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
    @Operation(summary = "删除主题", description = "删除指定主题")
    @ActionAnnotation(title = "主题", action = "删除", description = "delete topic")
    @Override
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
    @Operation(summary = "导出主题列表", description = "将主题数据导出为Excel格式")
    @ActionAnnotation(title = "主题", action = "导出", description = "export topic")
    @Override
    public Object export(TopicRequest request, HttpServletResponse response) {
        // 如果没有TopicExcel类，需要创建一个
        return exportTemplate(
            request,
            response,
            topicRestService,
            TopicEntity.class, // 如果存在TopicExcel类，应替换为TopicExcel.class
            "主题列表",
            "topic"
        );
    }
    
}
