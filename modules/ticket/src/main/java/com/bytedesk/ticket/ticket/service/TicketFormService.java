package com.bytedesk.ticket.ticket.service;

import org.flowable.engine.FormService;
import org.flowable.engine.form.FormData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional
public class TicketFormService {
    
    @Autowired
    private FormService formService;
    
    public FormData getStartForm(String processDefinitionId) {
        return formService.getStartFormData(processDefinitionId);
    }
    
    public FormData getTaskForm(String taskId) {
        return formService.getTaskFormData(taskId);
    }
    
    public void submitStartForm(String processDefinitionId, Map<String, String> variables) {
        formService.submitStartFormData(processDefinitionId, variables);
    }
    
    public void submitTaskForm(String taskId, Map<String, String> variables) {
        formService.submitTaskFormData(taskId, variables);
    }
} 