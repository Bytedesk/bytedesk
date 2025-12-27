/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-14 15:48:59
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-14 17:24:12
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.starter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.flowable.engine.FormService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.form.FormProperty;
import org.flowable.engine.form.TaskFormData;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

import org.flowable.engine.form.FormType;
import org.flowable.engine.impl.form.EnumFormType;

@Slf4j
@SpringBootTest
public class GroupTicketProcessTests {

    @Autowired
    private ProcessEngine processEngine;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private FormService formService;

    // @Autowired
    // private FormRepositoryService formRepositoryService;

    // 测试基本工单流程
    @Test
    void testBasicTicketProcess() {
        // 准备流程变量
        Map<String, Object> variables = new HashMap<>();
        variables.put("creatorUser", "user1");
        variables.put("workgroupUid", "support");
        variables.put("slaTime", "PT4H"); // 4小时SLA

        // 启动流程实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
            "groupTicketProcess", variables);
        assertNotNull(processInstance);
        log.info("Started process instance id {}", processInstance.getId());

        // 验证创建工单任务
        Task createTask = taskService.createTaskQuery()
            .processInstanceId(processInstance.getId())
            .taskAssignee("user1")
            .singleResult();
        assertNotNull(createTask);
        assertEquals("Create Ticket", createTask.getName());

        // 完成工单创建
        Map<String, Object> ticketVariables = new HashMap<>();
        ticketVariables.put("title", "Test Ticket");
        ticketVariables.put("description", "Test Description");
        ticketVariables.put("priority", "medium");
        taskService.complete(createTask.getId(), ticketVariables);

        // 验证工作组任务
        Task groupTask = taskService.createTaskQuery()
            .processInstanceId(processInstance.getId())
            .taskCandidateGroup("support")
            .singleResult();
        assertNotNull(groupTask);
        assertEquals("Group Handle", groupTask.getName());

        // 完成工作组处理
        Map<String, Object> groupVariables = new HashMap<>();
        groupVariables.put("solution", "Problem solved");
        groupVariables.put("status", "resolved");
        taskService.complete(groupTask.getId(), groupVariables);

        // 验证客户验证任务
        Task verifyTask = taskService.createTaskQuery()
            .processInstanceId(processInstance.getId())
            .taskAssignee("user1")
            .singleResult();
        assertNotNull(verifyTask);
        assertEquals("Customer Verify", verifyTask.getName());
    }

    // 测试工单创建者查询
    @Test
    void testCreatorTasks() {
        // 创建几个工单
        createMultipleTickets("user1", 2);

        // 查询创建者的待处理任务
        List<Task> activeTasks = taskService.createTaskQuery()
            .taskAssignee("user1")
            .active()
            .orderByTaskCreateTime()
            .desc()
            .list();

        assertNotNull(activeTasks);
        log.info("=== 创建者待处理任务 ===");
        activeTasks.forEach(task -> {
            log.info("Task: id={}, name={}, createTime={}", 
                task.getId(), task.getName(), task.getCreateTime());
            
            // 获取工单详情
            Map<String, Object> variables = runtimeService.getVariables(task.getProcessInstanceId());
            log.info("Variables: title={}, priority={}", 
                variables.get("title"), 
                variables.get("priority"));
        });
    }

    // 测试工作组任务分配
    @Test
    void testGroupTaskAssignment() {
        // 创建工单并提交到工作组
        createAndSubmitToGroup();

        String[] agents = {"agent1", "agent2", "agent3"};
        
        // 测试每个客服都能看到任务
        for (String agent : agents) {
            List<Task> agentTasks = taskService.createTaskQuery()
                .taskCandidateGroup("support")
                .taskCandidateUser(agent)
                .orderByTaskCreateTime()
                .desc()
                .list();

            log.info("=== 客服 {} 可见的任务 ===", agent);
            agentTasks.forEach(task -> {
                log.info("Task: id={}, name={}, createTime={}", 
                    task.getId(), task.getName(), task.getCreateTime());
            });

            // 验证任务可见性
            assertNotNull(agentTasks);
            assertTrue(agentTasks.size() > 0);
        }

        // 测试任务认领
        Task taskToClaim = taskService.createTaskQuery()
            .taskCandidateGroup("support")
            .singleResult();

        if (taskToClaim != null) {
            // agent1认领任务
            taskService.claim(taskToClaim.getId(), "agent1");

            // 验证其他客服不能认领
            for (String agent : agents) {
                if (!agent.equals("agent1")) {
                    try {
                        taskService.claim(taskToClaim.getId(), agent);
                        fail("应该抛出异常，因为任务已被认领");
                    } catch (Exception e) {
                        log.info("预期的异常：任务已被认领 - {}", e.getMessage());
                    }
                }
            }
        }
    }

    // 辅助方法：创建多个工单
    private void createMultipleTickets(String creator, int count) {
        for (int i = 0; i < count; i++) {
            Map<String, Object> variables = new HashMap<>();
            variables.put("creatorUser", creator);
            variables.put("workgroupUid", "support");
            variables.put("slaTime", "PT4H");
            
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                "groupTicketProcess", variables);
            
            // 完成创建任务
            Task createTask = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .taskAssignee(creator)
                .singleResult();
            
            Map<String, Object> ticketVariables = new HashMap<>();
            ticketVariables.put("title", "Ticket " + (i + 1));
            ticketVariables.put("description", "Description " + (i + 1));
            ticketVariables.put("priority", "medium");
            taskService.complete(createTask.getId(), ticketVariables);
        }
    }

    // 辅助方法：创建工单并提交到工作组
    private void createAndSubmitToGroup() {
        createMultipleTickets("user1", 2);
    }

    // 测试SLA超时
    @Test
    void testSLATimeout() {
        // 创建一个短SLA时间的工单
        Map<String, Object> variables = new HashMap<>();
        variables.put("creatorUser", "user1");
        variables.put("workgroupUid", "support");
        variables.put("slaTime", "PT1S"); // 1秒SLA
        
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
            "groupTicketProcess", variables);
        
        // 完成创建任务
        Task createTask = taskService.createTaskQuery()
            .processInstanceId(processInstance.getId())
            .taskAssignee("user1")
            .singleResult();
        
        Map<String, Object> ticketVariables = new HashMap<>();
        ticketVariables.put("title", "SLA Test Ticket");
        ticketVariables.put("description", "SLA Test Description");
        ticketVariables.put("priority", "high");
        taskService.complete(createTask.getId(), ticketVariables);

        // 等待SLA超时
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            log.error("SLA timeout test sleep interrupted", e);
        }

        // 验证是否触发了SLA通知
        List<HistoricTaskInstance> historicTasks = processEngine.getHistoryService()
            .createHistoricTaskInstanceQuery()
            .processInstanceId(processInstance.getId())
            .orderByHistoricTaskInstanceStartTime()
            .asc()
            .list();

        historicTasks.forEach(task -> {
            log.info("Task: name={}, startTime={}, endTime={}", 
                task.getName(), task.getCreateTime(), task.getEndTime());
        });
    }

    // 测试创建工单表单
    @Test
    void testCreateTicketForm() {
        // 启动流程实例
        Map<String, Object> variables = new HashMap<>();
        variables.put("creatorUser", "user1");
        variables.put("workgroupUid", "support");
        variables.put("slaTime", "PT4H");
        
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
            "groupTicketProcess", variables);
        
        // 获取创建工单任务
        Task createTask = taskService.createTaskQuery()
            .processInstanceId(processInstance.getId())
            .taskAssignee("user1")
            .singleResult();
        
        assertNotNull(createTask);
        assertEquals("创建工单", createTask.getName());
        
        // 获取表单信息
        // FormInfo formModel = formRepositoryService.getFormModelByKey("createTicketForm");
        TaskFormData formData = formService.getTaskFormData(createTask.getId());
        List<FormProperty> formFields = formData.getFormProperties();
        
        // 修改相关的类型
        Map<String, FormProperty> fieldMap = formFields.stream()
            .collect(Collectors.toMap(FormProperty::getId, f -> f));
            
        // 验证必填字段
        assertTrue(fieldMap.containsKey("title"));
        assertTrue(fieldMap.get("title").isRequired());
        assertEquals("text", fieldMap.get("title").getType());
        
        // 验证优先级枚举值
        FormProperty priorityProp = fieldMap.get("priority");
        FormType formType = priorityProp.getType();
        assertTrue(formType instanceof EnumFormType);
        // Map<String, String> enumValues = (Map<String, String>)((EnumFormType) formType).getInformation("values");
        
        // 提交表单
        Map<String, String> formValues = new HashMap<>();
        formValues.put("title", "测试工单");
        formValues.put("description", "这是一个测试工单");
        formValues.put("priority", "medium");
        
        formService.submitTaskFormData(createTask.getId(), formValues);
        
        // 验证任务已完成
        Task completedTask = taskService.createTaskQuery()
            .processInstanceId(processInstance.getId())
            .taskAssignee("user1")
            .singleResult();
        assertNull(completedTask);
    }

    // 测试工作组处理表单
    @Test
    void testGroupHandleForm() {
        // 创建并提交到工作组处理阶段
        ProcessInstance processInstance = createAndSubmitToGroupHandle();
        
        // 获取工作组任务
        Task groupTask = taskService.createTaskQuery()
            .processInstanceId(processInstance.getId())
            .taskCandidateGroup("support")
            .singleResult();
            
        assertNotNull(groupTask);
        assertEquals("工作组处理", groupTask.getName());
        
        // 认领任务
        taskService.claim(groupTask.getId(), "agent1");
        
        // 获取表单数据
        TaskFormData formData = formService.getTaskFormData(groupTask.getId());
        List<FormProperty> formProperties = formData.getFormProperties();
        
        // 验证表单字段
        Map<String, FormProperty> propertyMap = formProperties.stream()
            .collect(Collectors.toMap(FormProperty::getId, p -> p));
            
        // 验证解决方案字段
        assertTrue(propertyMap.containsKey("solution"));
        assertTrue(propertyMap.get("solution").isRequired());
        
        // 验证状态枚举值
        FormProperty statusProp = propertyMap.get("status");
        assertTrue(statusProp instanceof EnumFormType);
        FormType formType = statusProp.getType();
        assertTrue(formType instanceof EnumFormType);
        // 
        @SuppressWarnings("unchecked")
        Map<String, String> enumValues = (Map<String, String>) ((EnumFormType) formType).getInformation("values");
        assertEquals("已解决", enumValues.get("resolved"));
        assertEquals("处理中", enumValues.get("pending"));
        assertEquals("已升级", enumValues.get("escalated"));
        
        // 提交表单
        Map<String, String> formValues = new HashMap<>();
        formValues.put("solution", "问题已修复");
        formValues.put("status", "resolved");
        
        formService.submitTaskFormData(groupTask.getId(), formValues);
        
        // 验证任务已完成
        Task completedTask = taskService.createTaskQuery()
            .processInstanceId(processInstance.getId())
            .taskId(groupTask.getId())
            .singleResult();
        assertNull(completedTask);
    }

    // 测试客户确认表单
    @Test
    void testCustomerVerifyForm() {
        // 创建工单并处理到客户确认阶段
        ProcessInstance processInstance = createAndSubmitToCustomerVerify();
        
        // 获取客户确认任务
        Task verifyTask = taskService.createTaskQuery()
            .processInstanceId(processInstance.getId())
            .taskAssignee("user1")
            .singleResult();
            
        assertNotNull(verifyTask);
        assertEquals("客户确认", verifyTask.getName());
        
        // 获取表单数据
        TaskFormData formData = formService.getTaskFormData(verifyTask.getId());
        List<FormProperty> formProperties = formData.getFormProperties();
        
        // 验证表单字段
        Map<String, FormProperty> propertyMap = formProperties.stream()
            .collect(Collectors.toMap(FormProperty::getId, p -> p));
            
        // 验证满意度字段
        assertTrue(propertyMap.containsKey("satisfied"));
        assertTrue(propertyMap.get("satisfied").isRequired());
        assertEquals("boolean", propertyMap.get("satisfied").getType().getName());
        
        // 验证备注字段
        assertTrue(propertyMap.containsKey("comment"));
        assertFalse(propertyMap.get("comment").isRequired());
        
        // 提交表单
        Map<String, String> formValues = new HashMap<>();
        formValues.put("satisfied", "true");
        formValues.put("comment", "服务很好");
        
        formService.submitTaskFormData(verifyTask.getId(), formValues);
        
        // 验证任务已完成
        Task completedTask = taskService.createTaskQuery()
            .processInstanceId(processInstance.getId())
            .taskId(verifyTask.getId())
            .singleResult();
        assertNull(completedTask);
    }

    // 测试满意度调查表单
    @Test
    void testSatisfactionSurveyForm() {
        // 创建工单并处理到满意度调查阶段
        ProcessInstance processInstance = createAndSubmitToSatisfactionSurvey();
        
        // 获取满意度调查任务
        Task surveyTask = taskService.createTaskQuery()
            .processInstanceId(processInstance.getId())
            .taskAssignee("user1")
            .singleResult();
            
        assertNotNull(surveyTask);
        assertEquals("满意度调查", surveyTask.getName());
        
        // 获取表单数据
        TaskFormData formData = formService.getTaskFormData(surveyTask.getId());
        List<FormProperty> formProperties = formData.getFormProperties();
        
        // 验证表单字段
        Map<String, FormProperty> propertyMap = formProperties.stream()
            .collect(Collectors.toMap(FormProperty::getId, p -> p));
            
        // 验证评分枚举值
        FormProperty ratingProp = propertyMap.get("rating");
        assertTrue(ratingProp instanceof EnumFormType);
        FormType formType = ratingProp.getType();
        assertTrue(formType instanceof EnumFormType);

        @SuppressWarnings("unchecked")
        Map<String, String> enumValues = (Map<String, String>) ((EnumFormType) formType).getInformation("values");
        assertEquals("非常满意", enumValues.get("5"));
        assertEquals("满意", enumValues.get("4"));
        assertEquals("一般", enumValues.get("3"));
        assertEquals("不满意", enumValues.get("2"));
        assertEquals("非常不满意", enumValues.get("1"));
        
        // 验证反馈意见字段
        assertTrue(propertyMap.containsKey("feedback"));
        
        // 提交表单
        Map<String, String> formValues = new HashMap<>();
        formValues.put("rating", "5");
        formValues.put("feedback", "服务人员很专业");
        
        formService.submitTaskFormData(surveyTask.getId(), formValues);
        
        // 验证任务已完成
        Task completedTask = taskService.createTaskQuery()
            .processInstanceId(processInstance.getId())
            .taskId(surveyTask.getId())
            .singleResult();
        assertNull(completedTask);
    }

    // 辅助方法：创建工单并处理到客户确认阶段
    private ProcessInstance createAndSubmitToCustomerVerify() {
        ProcessInstance processInstance = createAndSubmitToGroupHandle();
        
        // 完成工作组处理
        Task groupTask = taskService.createTaskQuery()
            .processInstanceId(processInstance.getId())
            .taskCandidateGroup("support")
            .singleResult();
            
        taskService.claim(groupTask.getId(), "agent1");
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("solution", "问题已修复");
        variables.put("status", "resolved");
        taskService.complete(groupTask.getId(), variables);
        
        return processInstance;
    }

    // 辅助方法：创建工单并处理到满意度调查阶段
    private ProcessInstance createAndSubmitToSatisfactionSurvey() {
        ProcessInstance processInstance = createAndSubmitToCustomerVerify();
        
        // 完成客户确认
        Task verifyTask = taskService.createTaskQuery()
            .processInstanceId(processInstance.getId())
            .taskAssignee("user1")
            .singleResult();
            
        Map<String, Object> variables = new HashMap<>();
        variables.put("satisfied", true);
        variables.put("comment", "问题解决得很好");
        taskService.complete(verifyTask.getId(), variables);
        
        return processInstance;
    }

    // 辅助方法：创建工单并提交到工作组处理阶段
    private ProcessInstance createAndSubmitToGroupHandle() {
        // 启动流程实例
        Map<String, Object> variables = new HashMap<>();
        variables.put("creatorUser", "user1");
        variables.put("workgroupUid", "support");
        variables.put("slaTime", "PT4H");
        
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
            "groupTicketProcess", variables);
        
        // 完成创建任务
        Task createTask = taskService.createTaskQuery()
            .processInstanceId(processInstance.getId())
            .taskAssignee("user1")
            .singleResult();
            
        Map<String, Object> ticketVariables = new HashMap<>();
        ticketVariables.put("title", "测试工单");
        ticketVariables.put("description", "这是一个测试工单");
        ticketVariables.put("priority", "medium");
        taskService.complete(createTask.getId(), ticketVariables);
        
        return processInstance;
    }


}
