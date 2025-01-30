package com.bytedesk.ticket.service;

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