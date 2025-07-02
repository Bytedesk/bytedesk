/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-02 10:21:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-02 11:50:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.workflow.variable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 工作流变量服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowVariableService {

    private final WorkflowVariableRepository variableRepository;
    
    /**
     * 创建或更新工作流变量
     * 
     * @param workflowUid 工作流实例UID
     * @param name 变量名称
     * @param value 变量值
     * @param type 变量类型
     * @param scope 变量作用域
     * @return 工作流变量实体
     */
    public WorkflowVariableEntity setVariable(String workflowUid, String name, Object value, 
            WorkflowVariableTypeEnum type, WorkflowVariableScopeEnum scope) {
        
        // 查找现有变量
        Optional<WorkflowVariableEntity> optional = variableRepository.findByWorkflowUidAndName(workflowUid, name);
        WorkflowVariableEntity variable;
        
        if (optional.isPresent()) {
            // 更新现有变量
            variable = optional.get();
        } else {
            // 创建新变量
            variable = new WorkflowVariableEntity();
            variable.setName(name);
            variable.setWorkflowUid(workflowUid);
            variable.setScope(scope.name());
        }
        
        // 根据类型设置值
        String valueStr = convertValueToString(value);
        variable.setValue(valueStr);
        // variable.setType(type.name());
        
        // 保存变量
        return variableRepository.save(variable);
    }
    
    /**
     * 创建或更新局部变量
     * 
     * @param workflowUid 工作流实例UID
     * @param nodeUid 节点UID
     * @param name 变量名称
     * @param value 变量值
     * @param type 变量类型
     * @return 工作流变量实体
     */
    public WorkflowVariableEntity setLocalVariable(String workflowUid, String nodeUid, String name, 
            Object value, WorkflowVariableTypeEnum type) {
        
        // 查找现有变量
        Optional<WorkflowVariableEntity> optional = variableRepository.findByWorkflowUidAndNodeUidAndName(
                workflowUid, nodeUid, name);
        WorkflowVariableEntity variable;
        
        if (optional.isPresent()) {
            // 更新现有变量
            variable = optional.get();
        } else {
            // 创建新变量
            variable = new WorkflowVariableEntity();
            variable.setName(name);
            variable.setWorkflowUid(workflowUid);
            variable.setNodeUid(nodeUid);
            variable.setScope(WorkflowVariableScopeEnum.LOCAL.name());
        }
        
        // 根据类型设置值
        String valueStr = convertValueToString(value);
        variable.setValue(valueStr);
        // variable.setType(type.name());
        
        // 保存变量
        return variableRepository.save(variable);
    }
    
    /**
     * 获取工作流变量
     * 
     * @param workflowUid 工作流实例UID
     * @param name 变量名称
     * @return 变量值
     */
    public Object getVariable(String workflowUid, String name) {
        Optional<WorkflowVariableEntity> optional = variableRepository.findByWorkflowUidAndName(workflowUid, name);
        if (!optional.isPresent()) {
            return null;
        }
        
        WorkflowVariableEntity variable = optional.get();
        return convertStringToValue(variable.getValue(), variable.getType());
    }
    
    /**
     * 获取局部变量
     * 
     * @param workflowUid 工作流实例UID
     * @param nodeUid 节点UID
     * @param name 变量名称
     * @return 变量值
     */
    public Object getLocalVariable(String workflowUid, String nodeUid, String name) {
        Optional<WorkflowVariableEntity> optional = variableRepository.findByWorkflowUidAndNodeUidAndName(
                workflowUid, nodeUid, name);
        if (!optional.isPresent()) {
            return null;
        }
        
        WorkflowVariableEntity variable = optional.get();
        return convertStringToValue(variable.getValue(), variable.getType());
    }
    
    /**
     * 获取工作流所有变量
     * 
     * @param workflowUid 工作流实例UID
     * @return 变量Map
     */
    public Map<String, Object> getVariables(String workflowUid) {
        List<WorkflowVariableEntity> variables = variableRepository.findByWorkflowUid(workflowUid);
        Map<String, Object> result = new java.util.HashMap<>();
        
        for (WorkflowVariableEntity variable : variables) {
            result.put(variable.getName(), convertStringToValue(variable.getValue(), variable.getType()));
        }
        
        return result;
    }
    
    /**
     * 获取节点所有局部变量
     * 
     * @param workflowUid 工作流实例UID
     * @param nodeUid 节点UID
     * @return 变量Map
     */
    public Map<String, Object> getLocalVariables(String workflowUid, String nodeUid) {
        List<WorkflowVariableEntity> variables = variableRepository.findByWorkflowUidAndNodeUid(workflowUid, nodeUid);
        Map<String, Object> result = new java.util.HashMap<>();
        
        for (WorkflowVariableEntity variable : variables) {
            result.put(variable.getName(), convertStringToValue(variable.getValue(), variable.getType()));
        }
        
        return result;
    }
    
    /**
     * 删除工作流变量
     * 
     * @param workflowUid 工作流实例UID
     * @param name 变量名称
     */
    public void removeVariable(String workflowUid, String name) {
        variableRepository.deleteByWorkflowUidAndName(workflowUid, name);
    }
    
    /**
     * 删除局部变量
     * 
     * @param workflowUid 工作流实例UID
     * @param nodeUid 节点UID
     * @param name 变量名称
     */
    public void removeLocalVariable(String workflowUid, String nodeUid, String name) {
        variableRepository.deleteByWorkflowUidAndNodeUidAndName(workflowUid, nodeUid, name);
    }
    
    /**
     * 删除工作流所有变量
     * 
     * @param workflowUid 工作流实例UID
     */
    public void removeAllVariables(String workflowUid) {
        variableRepository.deleteByWorkflowUid(workflowUid);
    }
    
    /**
     * 删除节点所有局部变量
     * 
     * @param workflowUid 工作流实例UID
     * @param nodeUid 节点UID
     */
    public void removeAllLocalVariables(String workflowUid, String nodeUid) {
        variableRepository.deleteByWorkflowUidAndNodeUid(workflowUid, nodeUid);
    }
    
    /**
     * 将值转换为字符串
     */
    private String convertValueToString(Object value) {
        if (value == null) {
            return null;
        }
        
        if (value instanceof String || value instanceof Number || value instanceof Boolean) {
            return value.toString();
        } else {
            return JSON.toJSONString(value);
        }
    }
    
    /**
     * 将字符串转换为对应类型的值
     */
    private Object convertStringToValue(String valueStr, String type) {
        if (valueStr == null) {
            return null;
        }
        
        WorkflowVariableTypeEnum typeEnum = WorkflowVariableTypeEnum.valueOf(type);
        
        switch (typeEnum) {
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
                    log.error("Error parsing number: {}", valueStr, e);
                    return 0;
                }
            case BOOLEAN:
                return Boolean.parseBoolean(valueStr);
            case DATE:
                try {
                    return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(valueStr);
                } catch (Exception e) {
                    log.error("Error parsing date: {}", valueStr, e);
                    return null;
                }
            case OBJECT:
                return JSON.parseObject(valueStr);
            case ARRAY:
                return JSON.parseArray(valueStr);
            case FILE:
                return valueStr; // 文件引用直接返回字符串路径
            default:
                return valueStr;
        }
    }
}
