/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-02 10:32:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-02 11:55:44
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.workflow.variable;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.core.annotation.ActionAnnotation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 工作流变量控制器
 */
@Tag(name = "工作流变量管理", description = "工作流变量相关接口")
@RestController
@RequestMapping("/api/v1/workflow/variable")
@RequiredArgsConstructor
public class WorkflowVariableController {
    
    private final WorkflowVariableService variableService;
    
    /**
     * 创建工作流变量
     */
    @Operation(summary = "创建工作流变量", description = "创建新的工作流变量")
    @ApiResponse(responseCode = "200", description = "创建成功")
    @ActionAnnotation(title = "工作流变量", action = "新建", description = "create workflow variable")
    @PostMapping("/create")
    public ResponseEntity<?> createVariable(@RequestBody WorkflowVariableRequest request) {
        // 解析变量类型和作用域
        WorkflowVariableTypeEnum type = WorkflowVariableTypeEnum.valueOf(request.getType());
        WorkflowVariableScopeEnum scope = WorkflowVariableScopeEnum.valueOf(request.getScope());
        
        // 解析值
        Object value = parseValue(request.getValue(), type);
        
        // 创建变量
        WorkflowVariableEntity variable;
        if (request.getNodeUid() != null && !request.getNodeUid().isEmpty()) {
            // 创建局部变量
            variable = variableService.setLocalVariable(
                    request.getWorkflowUid(), 
                    request.getNodeUid(), 
                    request.getName(), 
                    value, 
                    type);
        } else {
            // 创建全局变量
            variable = variableService.setVariable(
                    request.getWorkflowUid(), 
                    request.getName(), 
                    value, 
                    type, 
                    scope);
        }
        
        // 构造响应
        WorkflowVariableResponse response = convertToResponse(variable);
        return ResponseEntity.ok(JsonResult.success(response));
    }
    
    /**
     * 获取工作流变量
     */
    @Operation(summary = "获取工作流变量", description = "获取指定工作流变量")
    @ApiResponse(responseCode = "200", description = "获取成功")
    @GetMapping("/{workflowUid}/{name}")
    public ResponseEntity<?> getVariable(
            @PathVariable String workflowUid,
            @PathVariable String name) {
        
        Object value = variableService.getVariable(workflowUid, name);
        
        if (value == null) {
            return ResponseEntity.ok(JsonResult.error("变量不存在"));
        }
        
        return ResponseEntity.ok(JsonResult.success(value));
    }
    
    /**
     * 获取工作流局部变量
     */
    @Operation(summary = "获取工作流局部变量", description = "获取指定节点的局部变量")
    @ApiResponse(responseCode = "200", description = "获取成功")
    @GetMapping("/{workflowUid}/{nodeUid}/{name}")
    public ResponseEntity<?> getLocalVariable(
            @PathVariable String workflowUid,
            @PathVariable String nodeUid,
            @PathVariable String name) {
        
        Object value = variableService.getLocalVariable(workflowUid, nodeUid, name);
        
        if (value == null) {
            return ResponseEntity.ok(JsonResult.error("局部变量不存在"));
        }
        
        return ResponseEntity.ok(JsonResult.success(value));
    }
    
    /**
     * 获取工作流所有变量
     */
    @Operation(summary = "获取工作流所有变量", description = "获取指定工作流的所有变量")
    @ApiResponse(responseCode = "200", description = "获取成功")
    @GetMapping("/{workflowUid}")
    public ResponseEntity<?> getVariables(@PathVariable String workflowUid) {
        Map<String, Object> variables = variableService.getVariables(workflowUid);
        return ResponseEntity.ok(JsonResult.success(variables));
    }
    
    /**
     * 获取节点所有局部变量
     */
    @Operation(summary = "获取节点所有局部变量", description = "获取指定节点的所有局部变量")
    @ApiResponse(responseCode = "200", description = "获取成功")
    @GetMapping("/{workflowUid}/{nodeUid}")
    public ResponseEntity<?> getLocalVariables(
            @PathVariable String workflowUid,
            @PathVariable String nodeUid) {
        
        Map<String, Object> variables = variableService.getLocalVariables(workflowUid, nodeUid);
        return ResponseEntity.ok(JsonResult.success(variables));
    }
    
    /**
     * 删除工作流变量
     */
    @Operation(summary = "删除工作流变量", description = "删除指定工作流变量")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @ActionAnnotation(title = "工作流变量", action = "删除", description = "delete workflow variable")
    @DeleteMapping("/{workflowUid}/{name}")
    public ResponseEntity<?> removeVariable(
            @PathVariable String workflowUid,
            @PathVariable String name) {
        
        variableService.removeVariable(workflowUid, name);
        return ResponseEntity.ok(JsonResult.success());
    }
    
    /**
     * 删除工作流局部变量
     */
    @Operation(summary = "删除工作流局部变量", description = "删除指定节点的局部变量")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @ActionAnnotation(title = "工作流局部变量", action = "删除", description = "delete workflow local variable")
    @DeleteMapping("/{workflowUid}/{nodeUid}/{name}")
    public ResponseEntity<?> removeLocalVariable(
            @PathVariable String workflowUid,
            @PathVariable String nodeUid,
            @PathVariable String name) {
        
        variableService.removeLocalVariable(workflowUid, nodeUid, name);
        return ResponseEntity.ok(JsonResult.success());
    }
    
    /**
     * 删除工作流所有变量
     */
    @Operation(summary = "删除工作流所有变量", description = "删除指定工作流的所有变量")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @ActionAnnotation(title = "工作流变量", action = "批量删除", description = "delete all workflow variables")
    @DeleteMapping("/{workflowUid}")
    public ResponseEntity<?> removeAllVariables(@PathVariable String workflowUid) {
        variableService.removeAllVariables(workflowUid);
        return ResponseEntity.ok(JsonResult.success());
    }
    
    /**
     * 删除节点所有局部变量
     */
    @Operation(summary = "删除节点所有局部变量", description = "删除指定节点的所有局部变量")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @ActionAnnotation(title = "工作流局部变量", action = "批量删除", description = "delete all workflow local variables")
    @DeleteMapping("/{workflowUid}/{nodeUid}")
    public ResponseEntity<?> removeAllLocalVariables(
            @PathVariable String workflowUid,
            @PathVariable String nodeUid) {
        
        variableService.removeAllLocalVariables(workflowUid, nodeUid);
        return ResponseEntity.ok(JsonResult.success());
    }
    
    /**
     * 根据变量类型解析值
     */
    private Object parseValue(String valueStr, WorkflowVariableTypeEnum type) {
        if (valueStr == null) {
            return null;
        }
        
        switch (type) {
            case STRING:
                return valueStr;
            case NUMBER:
                try {
                    if (valueStr.contains(".")) {
                        return Double.parseDouble(valueStr);
                    } else {
                        return Integer.parseInt(valueStr);
                    }
                } catch (NumberFormatException e) {
                    return 0;
                }
            case BOOLEAN:
                return Boolean.parseBoolean(valueStr);
            case DATE:
                return valueStr;  // 日期字符串，由服务层处理
            case OBJECT:
                return JSON.parseObject(valueStr);
            case ARRAY:
                return JSON.parseArray(valueStr);
            case FILE:
                return valueStr;  // 文件引用直接返回字符串路径
            default:
                return valueStr;
        }
    }
    
    /**
     * 将实体转换为响应DTO
     */
    private WorkflowVariableResponse convertToResponse(WorkflowVariableEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return WorkflowVariableResponse.builder()
                .uid(entity.getUid())
                .workflowUid(entity.getWorkflowUid())
                .nodeUid(entity.getNodeUid())
                .name(entity.getName())
                .value(variableService.getVariable(entity.getWorkflowUid(), entity.getName()))
                // .type(entity.getType())
                .scope(entity.getScope())
                .description(entity.getDescription())
                .build();
    }
    
    /**
     * 将实体列表转换为响应DTO列表
     */
    // private List<WorkflowVariableResponse> convertToResponseList(List<WorkflowVariableEntity> entities) {
    //     if (entities == null) {
    //         return new ArrayList<>();
    //     }
        
    //     List<WorkflowVariableResponse> responses = new ArrayList<>();
    //     for (WorkflowVariableEntity entity : entities) {
    //         responses.add(convertToResponse(entity));
    //     }
    //     return responses;
    // }
    
}
