/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-30 09:04:30
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-30 09:05:38
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket.controller;

import java.util.List;
import java.util.Map;

import org.flowable.engine.form.FormProperty;
import org.flowable.engine.form.TaskFormData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.ticket.ticket.service.TicketFormService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "工单表单管理接口")
@RequestMapping("/api/v1/ticket/form")
public class TicketFormController {

    private final TicketFormService formService;

    @GetMapping("/task/{taskId}")
    @Operation(summary = "获取任务表单")
    public ResponseEntity<JsonResult<TaskFormData>> getTaskForm(@PathVariable String taskId) {
        TaskFormData formData = formService.getTaskForm(taskId);
        return ResponseEntity.ok(JsonResult.success(formData));
    }

    @GetMapping("/task/{taskId}/properties")
    @Operation(summary = "获取表单属性")
    public ResponseEntity<JsonResult<List<FormProperty>>> getFormProperties(@PathVariable String taskId) {
        List<FormProperty> properties = formService.getFormProperties(taskId);
        return ResponseEntity.ok(JsonResult.success(properties));
    }

    @PostMapping("/task/{taskId}/submit")
    @Operation(summary = "提交任务表单")
    public ResponseEntity<JsonResult<Boolean>> submitTaskForm(
            @PathVariable String taskId,
            @RequestBody Map<String, String> properties) {
        formService.submitTaskForm(taskId, properties);
        return ResponseEntity.ok(JsonResult.success());
    }

    @PostMapping("/task/{taskId}/save")
    @Operation(summary = "保存表单数据")
    public ResponseEntity<JsonResult<Boolean>> saveFormData(
            @PathVariable String taskId,
            @RequestBody Map<String, String> properties) {
        formService.saveFormData(taskId, properties);
        return ResponseEntity.ok(JsonResult.success());
    }
} 