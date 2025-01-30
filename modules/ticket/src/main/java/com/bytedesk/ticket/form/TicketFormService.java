/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-23 13:48:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-30 15:47:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.form;

import java.util.List;
import java.util.Map;

import org.flowable.engine.FormService;
import org.flowable.engine.form.FormProperty;
import org.flowable.engine.form.StartFormData;
import org.flowable.engine.form.TaskFormData;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketFormService {

    private final FormService formService;

    /**
     * 获取启动表单
     */
    public StartFormData getStartForm(String processDefinitionId) {
        return formService.getStartFormData(processDefinitionId);
    }

    /**
     * 获取任务表单
     */
    public TaskFormData getTaskForm(String taskId) {
        return formService.getTaskFormData(taskId);
    }

    /**
     * 提交启动表单并启动流程
     */
    public void submitStartForm(String processDefinitionId, Map<String, String> properties) {
        formService.submitStartFormData(processDefinitionId, properties);
    }

    /**
     * 提交任务表单
     */
    public void submitTaskForm(String taskId, Map<String, String> properties) {
        formService.submitTaskFormData(taskId, properties);
    }

    /**
     * 获取表单属性
     */
    public List<FormProperty> getFormProperties(String taskId) {
        TaskFormData formData = formService.getTaskFormData(taskId);
        return formData.getFormProperties();
    }

    /**
     * 保存表单数据
     */
    public void saveFormData(String taskId, Map<String, String> properties) {
        formService.saveFormData(taskId, properties);
    }
} 