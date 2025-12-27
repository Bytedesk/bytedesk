/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-14 15:48:59
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-22 17:41:55
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertFalse;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertNull;
// import static org.junit.jupiter.api.Assertions.assertTrue;
// import static org.junit.jupiter.api.Assertions.fail;

// import java.util.Date;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.stream.Collectors;

// import org.flowable.engine.FormService;
// import org.flowable.engine.HistoryService;
// // import org.flowable.engine.ProcessEngine;
// import org.flowable.engine.RuntimeService;
// import org.flowable.engine.TaskService;
// import org.flowable.engine.form.FormProperty;
// import org.flowable.engine.form.TaskFormData;
// import org.flowable.engine.runtime.ProcessInstance;
// import org.flowable.task.api.Task;
// import org.flowable.task.api.history.HistoricTaskInstance;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;

// import com.bytedesk.ticket.ticket.TicketConsts;
// import com.bytedesk.ticket.ticket.TicketPriorityEnum;
// import com.bytedesk.ticket.ticket.TicketStatusEnum;

// import lombok.extern.slf4j.Slf4j;

// import org.flowable.engine.form.FormType;
// import org.flowable.engine.impl.form.EnumFormType;

// @Slf4j
// @SpringBootTest
// public class GroupTicketProcessTests {

//     // @Autowired
//     // private ProcessEngine processEngine;

//     @Autowired
//     private RuntimeService runtimeService;

//     @Autowired
//     private TaskService taskService;

//     @Autowired
//     private FormService formService;

//     @Autowired
//     private HistoryService historyService;

//     // 测试基本工单流程
//     @Test
//     void testBasicTicketProcess() {
//         // 准备流程变量
//         Map<String, Object> variables = new HashMap<>();
//         variables.put("creatorUser", "user1");
//         variables.put("workgroupUid", "support");
//         variables.put("slaTime", "PT4H"); // 4小时SLA

//         // 启动流程实例
//         ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
//             TicketConsts.TICKET_PROCESS_KEY, variables);
//         assertNotNull(processInstance);
//         log.info("Started process instance id {}", processInstance.getId());

//         // 验证创建工单任务
//         Task createTask = taskService.createTaskQuery()
//             .processInstanceId(processInstance.getId())
//             .taskAssignee("user1")
//             .singleResult();
//         assertNotNull(createTask);
//         assertEquals("创建工单", createTask.getName());

//         // 完成工单创建
//         Map<String, Object> ticketVariables = new HashMap<>();
//         ticketVariables.put("title", "Test Ticket");
//         ticketVariables.put("description", "Test Description");
//         ticketVariables.put("priority", TicketPriorityEnum.MEDIUM.name());
//         taskService.complete(createTask.getId(), ticketVariables);

//         // 验证工作组任务
//         Task groupTask = taskService.createTaskQuery()
//             .processInstanceId(processInstance.getId())
//             .taskCandidateGroup("support")
//             .singleResult();
//         assertNotNull(groupTask);
//         assertEquals("工作组处理", groupTask.getName());

//         // 完成工作组处理
//         Map<String, Object> groupVariables = new HashMap<>();
//         groupVariables.put("solution", "问题已修复");
//         groupVariables.put("status", TicketStatusEnum.RESOLVED.name());
//         taskService.complete(groupTask.getId(), groupVariables);

//         // 验证客户验证任务
//         Task verifyTask = taskService.createTaskQuery()
//             .processInstanceId(processInstance.getId())
//             .taskAssignee("user1")
//             .singleResult();
//         assertNotNull(verifyTask);
//         assertEquals("客户确认", verifyTask.getName());
//     }

//     // 测试工单创建者查询
//     @Test
//     void testCreatorTasks() {
//         // 创建几个工单
//         createMultipleTickets("user1", 2);

//         // 查询创建者的待处理任务
//         List<Task> activeTasks = taskService.createTaskQuery()
//             .taskAssignee("user1")
//             .active()
//             .orderByTaskCreateTime()
//             .desc()
//             .list();

//         assertNotNull(activeTasks);
//         log.info("=== 创建者待处理任务 ===");
//         activeTasks.forEach(task -> {
//             log.info("Task: id={}, name={}, createTime={}", 
//                 task.getId(), task.getName(), task.getCreateTime());
            
//             // 获取工单详情
//             Map<String, Object> variables = runtimeService.getVariables(task.getProcessInstanceId());
//             log.info("Variables: title={}, priority={}", 
//                 variables.get("title"), 
//                 variables.get("priority"));
//         });
//     }

//     // 测试工作组任务分配
//     @Test
//     void testGroupTaskAssignment() {
//         // 创建工单并提交到工作组
//         createAndSubmitToGroup();

//         String[] agents = {"agent1", "agent2", "agent3"};
        
//         // 测试每个客服都能看到任务
//         for (String agent : agents) {
//             List<Task> agentTasks = taskService.createTaskQuery()
//                 .taskCandidateGroup("support")
//                 .taskCandidateUser(agent)
//                 .orderByTaskCreateTime()
//                 .desc()
//                 .list();

//             log.info("=== 客服 {} 可见的任务 ===", agent);
//             agentTasks.forEach(task -> {
//                 log.info("Task: id={}, name={}, createTime={}", 
//                     task.getId(), task.getName(), task.getCreateTime());
//             });

//             // 验证任务可见性
//             assertNotNull(agentTasks);
//             assertTrue(agentTasks.size() > 0);
//         }

//         // 测试任务认领
//         Task taskToClaim = taskService.createTaskQuery()
//             .taskCandidateGroup("support")
//             .singleResult();

//         if (taskToClaim != null) {
//             // agent1认领任务
//             taskService.claim(taskToClaim.getId(), "agent1");

//             // 验证其他客服不能认领
//             for (String agent : agents) {
//                 if (!agent.equals("agent1")) {
//                     try {
//                         taskService.claim(taskToClaim.getId(), agent);
//                         fail("应该抛出异常，因为任务已被认领");
//                     } catch (Exception e) {
//                         log.info("预期的异常：任务已被认领 - {}", e.getMessage());
//                     }
//                 }
//             }
//         }
//     }

//     // 辅助方法：创建多个工单
//     private void createMultipleTickets(String creator, int count) {
//         for (int i = 0; i < count; i++) {
//             Map<String, Object> variables = new HashMap<>();
//             variables.put("creatorUser", creator);
//             variables.put("workgroupUid", "support");
//             variables.put("slaTime", "PT4H");
            
//             ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
//                 TicketConsts.TICKET_PROCESS_KEY, variables);
            
//             // 完成创建任务
//             Task createTask = taskService.createTaskQuery()
//                 .processInstanceId(processInstance.getId())
//                 .taskAssignee(creator)
//                 .singleResult();
            
//             Map<String, Object> ticketVariables = new HashMap<>();
//             ticketVariables.put("title", "Ticket " + (i + 1));
//             ticketVariables.put("description", "Description " + (i + 1));
//             ticketVariables.put("priority", TicketPriorityEnum.MEDIUM.name());
//             taskService.complete(createTask.getId(), ticketVariables);
//         }
//     }

//     // 辅助方法：创建工单并提交到工作组
//     private void createAndSubmitToGroup() {
//         createMultipleTickets("user1", 2);
//     }

//     // 测试SLA超时
//     @Test
//     void testSLATimeout() {
//         // 创建一个短SLA时间的工单
//         Map<String, Object> variables = new HashMap<>();
//         variables.put("creatorUser", "user1");
//         variables.put("workgroupUid", "support");
//         variables.put("slaTime", "PT1S"); // 1秒SLA
        
//         ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
//             TicketConsts.TICKET_PROCESS_KEY, variables);
        
//         // 完成创建任务
//         Task createTask = taskService.createTaskQuery()
//             .processInstanceId(processInstance.getId())
//             .taskAssignee("user1")
//             .singleResult();
        
//         Map<String, Object> ticketVariables = new HashMap<>();
//         ticketVariables.put("title", "SLA Test Ticket");
//         ticketVariables.put("description", "SLA Test Description");
//         ticketVariables.put("priority", TicketPriorityEnum.HIGH.name());
//         taskService.complete(createTask.getId(), ticketVariables);

//         // 等待SLA超时
//         try {
//             Thread.sleep(2000);
//         } catch (InterruptedException e) {
//         }

//         // 验证是否触发了SLA通知
//         List<HistoricTaskInstance> historicTasks = historyService.createHistoricTaskInstanceQuery()
//             .processInstanceId(processInstance.getId())
//             .orderByHistoricTaskInstanceStartTime()
//             .asc()
//             .list();

//         historicTasks.forEach(task -> {
//             log.info("Task: name={}, startTime={}, endTime={}", 
//                 task.getName(), task.getCreateTime(), task.getEndTime());
//         });
//     }

//     // 测试创建工单表单
//     @Test
//     void testCreateTicketForm() {
//         // 启动流程实例
//         Map<String, Object> variables = new HashMap<>();
//         variables.put("creatorUser", "user1");
//         variables.put("workgroupUid", "support");
//         variables.put("slaTime", "PT4H");
        
//         ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
//             TicketConsts.TICKET_PROCESS_KEY, variables);
        
//         // 获取创建工单任务
//         Task createTask = taskService.createTaskQuery()
//             .processInstanceId(processInstance.getId())
//             .taskAssignee("user1")
//             .singleResult();
        
//         assertNotNull(createTask);
//         assertEquals("创建工单", createTask.getName());
        
//         // 获取表单信息
//         // FormInfo formModel = formRepositoryService.getFormModelByKey("createTicketForm");
//         TaskFormData formData = formService.getTaskFormData(createTask.getId());
//         List<FormProperty> formFields = formData.getFormProperties();
        
//         // 修改相关的类型
//         Map<String, FormProperty> fieldMap = formFields.stream()
//             .collect(Collectors.toMap(FormProperty::getId, f -> f));
            
//         // 验证必填字段
//         assertTrue(fieldMap.containsKey("title"));
//         assertTrue(fieldMap.get("title").isRequired());
//         assertEquals("text", fieldMap.get("title").getType());
        
//         // 验证优先级枚举值
//         FormProperty priorityProp = fieldMap.get("priority");
//         FormType formType = priorityProp.getType();
//         assertTrue(formType instanceof EnumFormType);
//         // Map<String, String> enumValues = (Map<String, String>)((EnumFormType) formType).getInformation("values");
        
//         // 提交表单
//         Map<String, String> formValues = new HashMap<>();
//         formValues.put("title", "测试工单");
//         formValues.put("description", "这是一个测试工单");
//         formValues.put("priority", TicketPriorityEnum.MEDIUM.name());
        
//         formService.submitTaskFormData(createTask.getId(), formValues);
        
//         // 验证任务已完成
//         Task completedTask = taskService.createTaskQuery()
//             .processInstanceId(processInstance.getId())
//             .taskAssignee("user1")
//             .singleResult();
//         assertNull(completedTask);
//     }

//     // 测试工作组处理表单
//     @Test
//     void testGroupHandleForm() {
//         // 创建并提交到工作组处理阶段
//         ProcessInstance processInstance = createAndSubmitToGroupHandle();
        
//         // 获取工作组任务
//         Task groupTask = taskService.createTaskQuery()
//             .processInstanceId(processInstance.getId())
//             .taskCandidateGroup("support")
//             .singleResult();
            
//         assertNotNull(groupTask);
//         assertEquals("工作组处理", groupTask.getName());
        
//         // 认领任务
//         taskService.claim(groupTask.getId(), "agent1");
        
//         // 获取表单数据
//         TaskFormData formData = formService.getTaskFormData(groupTask.getId());
//         List<FormProperty> formProperties = formData.getFormProperties();
        
//         // 验证表单字段
//         Map<String, FormProperty> propertyMap = formProperties.stream()
//             .collect(Collectors.toMap(FormProperty::getId, p -> p));
            
//         // 验证解决方案字段
//         assertTrue(propertyMap.containsKey("solution"));
//         assertTrue(propertyMap.get("solution").isRequired());
        
//         // 验证状态枚举值
//         FormProperty statusProp = propertyMap.get("status");
//         assertTrue(statusProp instanceof EnumFormType);
//         FormType formType = statusProp.getType();
//         assertTrue(formType instanceof EnumFormType);
//         // 
//         @SuppressWarnings("unchecked")
//         Map<String, String> enumValues = (Map<String, String>) ((EnumFormType) formType).getInformation("values");
//         assertEquals("已解决", enumValues.get("resolved"));
//         assertEquals("处理中", enumValues.get("pending"));
//         assertEquals("已升级", enumValues.get("escalated"));
        
//         // 提交表单
//         Map<String, String> formValues = new HashMap<>();
//         formValues.put("solution", "问题已修复");
//         formValues.put("status", "resolved");
        
//         formService.submitTaskFormData(groupTask.getId(), formValues);
        
//         // 验证任务已完成
//         Task completedTask = taskService.createTaskQuery()
//             .processInstanceId(processInstance.getId())
//             .taskId(groupTask.getId())
//             .singleResult();
//         assertNull(completedTask);
//     }

//     // 测试客户确认表单
//     @Test
//     void testCustomerVerifyForm() {
//         // 创建工单并处理到客户确认阶段
//         ProcessInstance processInstance = createAndSubmitToCustomerVerify();
        
//         // 获取客户确认任务
//         Task verifyTask = taskService.createTaskQuery()
//             .processInstanceId(processInstance.getId())
//             .taskAssignee("user1")
//             .singleResult();
            
//         assertNotNull(verifyTask);
//         assertEquals("客户确认", verifyTask.getName());
        
//         // 获取表单数据
//         TaskFormData formData = formService.getTaskFormData(verifyTask.getId());
//         List<FormProperty> formProperties = formData.getFormProperties();
        
//         // 验证表单字段
//         Map<String, FormProperty> propertyMap = formProperties.stream()
//             .collect(Collectors.toMap(FormProperty::getId, p -> p));
            
//         // 验证满意度字段
//         assertTrue(propertyMap.containsKey("satisfied"));
//         assertTrue(propertyMap.get("satisfied").isRequired());
//         assertEquals("boolean", propertyMap.get("satisfied").getType().getName());
        
//         // 验证备注字段
//         assertTrue(propertyMap.containsKey("comment"));
//         assertFalse(propertyMap.get("comment").isRequired());
        
//         // 提交表单
//         Map<String, String> formValues = new HashMap<>();
//         formValues.put("satisfied", "true");
//         formValues.put("comment", "服务很好");
        
//         formService.submitTaskFormData(verifyTask.getId(), formValues);
        
//         // 验证任务已完成
//         Task completedTask = taskService.createTaskQuery()
//             .processInstanceId(processInstance.getId())
//             .taskId(verifyTask.getId())
//             .singleResult();
//         assertNull(completedTask);
//     }

//     // 测试满意度调查表单
//     @Test
//     void testSatisfactionSurveyForm() {
//         // 创建工单并处理到满意度调查阶段
//         ProcessInstance processInstance = createAndSubmitToSatisfactionSurvey();
        
//         // 获取满意度调查任务
//         Task surveyTask = taskService.createTaskQuery()
//             .processInstanceId(processInstance.getId())
//             .taskAssignee("user1")
//             .singleResult();
            
//         assertNotNull(surveyTask);
//         assertEquals("满意度调查", surveyTask.getName());
        
//         // 获取表单数据
//         TaskFormData formData = formService.getTaskFormData(surveyTask.getId());
//         List<FormProperty> formProperties = formData.getFormProperties();
        
//         // 验证表单字段
//         Map<String, FormProperty> propertyMap = formProperties.stream()
//             .collect(Collectors.toMap(FormProperty::getId, p -> p));
            
//         // 验证评分枚举值
//         FormProperty ratingProp = propertyMap.get("rating");
//         assertTrue(ratingProp instanceof EnumFormType);
//         FormType formType = ratingProp.getType();
//         assertTrue(formType instanceof EnumFormType);

//         @SuppressWarnings("unchecked")
//         Map<String, String> enumValues = (Map<String, String>) ((EnumFormType) formType).getInformation("values");
//         assertEquals("非常满意", enumValues.get("5"));
//         assertEquals("满意", enumValues.get("4"));
//         assertEquals("一般", enumValues.get("3"));
//         assertEquals("不满意", enumValues.get("2"));
//         assertEquals("非常不满意", enumValues.get("1"));
        
//         // 验证反馈意见字段
//         assertTrue(propertyMap.containsKey("feedback"));
        
//         // 提交表单
//         Map<String, String> formValues = new HashMap<>();
//         formValues.put("rating", "5");
//         formValues.put("feedback", "服务人员很专业");
        
//         formService.submitTaskFormData(surveyTask.getId(), formValues);
        
//         // 验证任务已完成
//         Task completedTask = taskService.createTaskQuery()
//             .processInstanceId(processInstance.getId())
//             .taskId(surveyTask.getId())
//             .singleResult();
//         assertNull(completedTask);
//     }

//     // 辅助方法：创建工单并处理到客户确认阶段
//     private ProcessInstance createAndSubmitToCustomerVerify() {
//         ProcessInstance processInstance = createAndSubmitToGroupHandle();
        
//         // 完成工作组处理
//         Task groupTask = taskService.createTaskQuery()
//             .processInstanceId(processInstance.getId())
//             .taskCandidateGroup("support")
//             .singleResult();
            
//         taskService.claim(groupTask.getId(), "agent1");
        
//         Map<String, Object> variables = new HashMap<>();
//         variables.put("solution", "问题已修复");
//         variables.put("status", "resolved");
//         taskService.complete(groupTask.getId(), variables);
        
//         return processInstance;
//     }

//     // 辅助方法：创建工单并处理到满意度调查阶段
//     private ProcessInstance createAndSubmitToSatisfactionSurvey() {
//         ProcessInstance processInstance = createAndSubmitToCustomerVerify();
        
//         // 完成客户确认
//         Task verifyTask = taskService.createTaskQuery()
//             .processInstanceId(processInstance.getId())
//             .taskAssignee("user1")
//             .singleResult();
            
//         Map<String, Object> variables = new HashMap<>();
//         variables.put("satisfied", true);
//         variables.put("comment", "问题解决得很好");
//         taskService.complete(verifyTask.getId(), variables);
        
//         return processInstance;
//     }

//     // 辅助方法：创建工单并提交到工作组处理阶段
//     private ProcessInstance createAndSubmitToGroupHandle() {
//         // 启动流程实例
//         Map<String, Object> variables = new HashMap<>();
//         variables.put("creatorUser", "user1");
//         variables.put("workgroupUid", "support");
//         variables.put("slaTime", "PT4H");
        
//         ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
//             TicketConsts.TICKET_PROCESS_KEY, variables);
        
//         // 完成创建任务
//         Task createTask = taskService.createTaskQuery()
//             .processInstanceId(processInstance.getId())
//             .taskAssignee("user1")
//             .singleResult();
            
//         Map<String, Object> ticketVariables = new HashMap<>();
//         ticketVariables.put("title", "测试工单");
//         ticketVariables.put("description", "这是一个测试工单");
//         ticketVariables.put("priority", TicketPriorityEnum.MEDIUM.name());
//         taskService.complete(createTask.getId(), ticketVariables);
        
//         return processInstance;
//     }

//     @Test
//     void testExecutionListener() {
//         // 准备流程变量
//         Map<String, Object> variables = new HashMap<>();
//         variables.put("creatorUser", "user1");
//         variables.put("workgroupUid", "support");
//         variables.put("slaTime", "PT4H");

//         // 启动流程实例 - 会触发 start 事件
//         ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
//             TicketConsts.TICKET_PROCESS_KEY, variables);
//         assertNotNull(processInstance);

//         // 完成创建工单任务
//         Task createTask = taskService.createTaskQuery()
//             .processInstanceId(processInstance.getId())
//             .taskAssignee("user1")
//             .singleResult();
        
//         Map<String, Object> ticketVariables = new HashMap<>();
//         ticketVariables.put("title", "Test Execution Listener");
//         ticketVariables.put("description", "Testing execution listeners");
//         ticketVariables.put("priority", TicketPriorityEnum.MEDIUM.name());
//         taskService.complete(createTask.getId(), ticketVariables);

//         // 认领并完成工作组任务 - 会触发 assignToGroup 任务的 start 和 end 事件
//         Task groupTask = taskService.createTaskQuery()
//             .processInstanceId(processInstance.getId())
//             .taskCandidateGroup("support")
//             .singleResult();
        
//         taskService.claim(groupTask.getId(), "agent1");
        
//         Map<String, Object> groupVariables = new HashMap<>();
//         groupVariables.put("solution", "问题已修复");
//         groupVariables.put("status", "resolved");
//         taskService.complete(groupTask.getId(), groupVariables);

//         // 完成客户确认任务 - 会触发流程的 end 事件
//         Task verifyTask = taskService.createTaskQuery()
//             .processInstanceId(processInstance.getId())
//             .taskAssignee("user1")
//             .singleResult();
        
//         Map<String, Object> verifyVariables = new HashMap<>();
//         verifyVariables.put("satisfied", true);
//         verifyVariables.put("comment", "服务很好");
//         taskService.complete(verifyTask.getId(), verifyVariables);
//     }

//     @Test
//     void testTaskListener() {
//         // 准备流程变量
//         Map<String, Object> variables = new HashMap<>();
//         variables.put("creatorUser", "user1");
//         variables.put("workgroupUid", "support");
//         variables.put("slaTime", "PT4H");

//         // 启动流程实例
//         ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
//             TicketConsts.TICKET_PROCESS_KEY, variables);
//         assertNotNull(processInstance);

//         // 完成创建工单任务 - 会触发 create 和 complete 事件
//         Task createTask = taskService.createTaskQuery()
//             .processInstanceId(processInstance.getId())
//             .taskAssignee("user1")
//             .singleResult();
        
//         Map<String, Object> ticketVariables = new HashMap<>();
//         ticketVariables.put("title", "Test Task Listener");
//         ticketVariables.put("description", "Testing task listeners");
//         ticketVariables.put("priority", TicketPriorityEnum.MEDIUM.name());
//         taskService.complete(createTask.getId(), ticketVariables);

//         // 认领工作组任务 - 会触发 assignment 事件
//         Task groupTask = taskService.createTaskQuery()
//             .processInstanceId(processInstance.getId())
//             .taskCandidateGroup("support")
//             .singleResult();
        
//         taskService.claim(groupTask.getId(), "agent1");
        
//         // 完成工作组任务 - 会触发 complete 事件
//         Map<String, Object> groupVariables = new HashMap<>();
//         groupVariables.put("solution", "问题已修复");
//         groupVariables.put("status", TicketStatusEnum.RESOLVED.name());
//         taskService.complete(groupTask.getId(), groupVariables);
//     }

//     @Test
//     void testTicketEscalation() {
//         // 准备流程变量
//         Map<String, Object> variables = new HashMap<>();
//         variables.put("creatorUser", "user1");
//         variables.put("workgroupUid", "support");
//         variables.put("slaTime", "PT4H");

//         // 启动流程实例
//         ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
//             TicketConsts.TICKET_PROCESS_KEY, variables);
//         assertNotNull(processInstance);

//         // 完成创建工单任务
//         Task createTask = taskService.createTaskQuery()
//             .processInstanceId(processInstance.getId())
//             .taskAssignee("user1")
//             .singleResult();
        
//         Map<String, Object> ticketVariables = new HashMap<>();
//         ticketVariables.put("title", "Test Escalation");
//         ticketVariables.put("description", "Testing ticket escalation");
//         ticketVariables.put("priority", TicketPriorityEnum.HIGH.name());
//         taskService.complete(createTask.getId(), ticketVariables);

//         // 获取工作组任务
//         Task groupTask = taskService.createTaskQuery()
//             .processInstanceId(processInstance.getId())
//             .taskCandidateGroup("support")
//             .singleResult();
        
//         // 认领任务
//         taskService.claim(groupTask.getId(), "agent1");
        
//         // 完成任务并标记为需要升级
//         Map<String, Object> groupVariables = new HashMap<>();
//         groupVariables.put("solution", "需要更高级别支持");
//         groupVariables.put("status", TicketStatusEnum.ESCALATED.name());  // 触发升级流程
//         taskService.complete(groupTask.getId(), groupVariables);

//         // 验证流程变量
//         Map<String, Object> processVariables = runtimeService.getVariables(processInstance.getId());
//         assertEquals("ESCALATED", processVariables.get("status"));
//         assertNotNull(processVariables.get("escalatedTime"));
//         assertEquals("工单需要更高级别处理", processVariables.get("escalatedReason"));

//         // 验证任务流转
//         Task escalatedTask = taskService.createTaskQuery()
//             .processInstanceId(processInstance.getId())
//             .taskCandidateGroup("support")
//             .singleResult();
//         assertNotNull(escalatedTask);
//         assertEquals("工作组处理", escalatedTask.getName());
//     }

//     // 测试 SLA 超时通知，自动触发（基于时间）
//     @Test
//     void testSLATimeoutNotification() {
//         // 准备流程变量
//         Map<String, Object> variables = new HashMap<>();
//         variables.put("creatorUser", "user1");
//         variables.put("workgroupUid", "support");
//         variables.put("slaTime", "PT1S");  // 设置1秒的SLA时间，方便测试
//         variables.put("startTime", new Date());

//         // 启动流程实例
//         ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
//             TicketConsts.TICKET_PROCESS_KEY, variables);
//         assertNotNull(processInstance);

//         // 完成创建工单任务
//         Task createTask = taskService.createTaskQuery()
//             .processInstanceId(processInstance.getId())
//             .taskAssignee("user1")
//             .singleResult();
        
//         Map<String, Object> ticketVariables = new HashMap<>();
//         ticketVariables.put("title", "Test SLA Timeout");
//         ticketVariables.put("description", "Testing SLA timeout notification");
//         ticketVariables.put("priority", TicketPriorityEnum.HIGH.name());
//         taskService.complete(createTask.getId(), ticketVariables);

//         // 等待2秒，确保SLA超时
//         try {
//             Thread.sleep(2000);
//         } catch (InterruptedException e) {
//             Thread.currentThread().interrupt();
//         }

//         // 验证流程变量
//         Map<String, Object> processVariables = runtimeService.getVariables(processInstance.getId());
//         assertNotNull(processVariables.get("slaTimeoutTime"));
//         assertEquals("超过处理时限", processVariables.get("slaTimeoutReason"));

//         // 验证任务是否返回到工作组
//         Task groupTask = taskService.createTaskQuery()
//             .processInstanceId(processInstance.getId())
//             .taskCandidateGroup("support")
//             .singleResult();
//         assertNotNull(groupTask);
//         assertEquals("工作组处理", groupTask.getName());
//     }

//     // 测试工单升级和返回，人工触发（基于状态）
//     @Test
//     void testTicketEscalationAndReturn() {
//         // 准备流程变量
//         Map<String, Object> variables = new HashMap<>();
//         variables.put("creatorUser", "user1");
//         variables.put("workgroupUid", "support");
//         variables.put("slaTime", "PT4H");

//         // 启动流程实例
//         ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
//             TicketConsts.TICKET_PROCESS_KEY, variables);
//         assertNotNull(processInstance);

//         // 完成创建工单任务
//         Task createTask = taskService.createTaskQuery()
//             .processInstanceId(processInstance.getId())
//             .taskAssignee("user1")
//             .singleResult();
        
//         Map<String, Object> ticketVariables = new HashMap<>();
//         ticketVariables.put("title", "Test Escalation and Return");
//         ticketVariables.put("description", "Testing ticket escalation and return flow");
//         ticketVariables.put("priority", TicketPriorityEnum.HIGH.name());
//         taskService.complete(createTask.getId(), ticketVariables);

//         // 获取工作组任务
//         Task groupTask = taskService.createTaskQuery()
//             .processInstanceId(processInstance.getId())
//             .taskCandidateGroup("support")
//             .singleResult();
        
//         // 认领任务
//         taskService.claim(groupTask.getId(), "agent1");
        
//         // 完成任务并标记为需要升级
//         Map<String, Object> escalateVariables = new HashMap<>();
//         escalateVariables.put("solution", "需要更高级别支持");
//         escalateVariables.put("status", "ESCALATED");
//         taskService.complete(groupTask.getId(), escalateVariables);

//         // 验证流程变量
//         Map<String, Object> processVariables = runtimeService.getVariables(processInstance.getId());
//         assertEquals("ESCALATED", processVariables.get("status"));
//         assertNotNull(processVariables.get("escalatedTime"));
//         assertEquals("工单需要更高级别处理", processVariables.get("escalatedReason"));
//         assertEquals("escalated", processVariables.get("previousStatus"));

//         // 验证任务返回到工作组
//         Task returnedTask = taskService.createTaskQuery()
//             .processInstanceId(processInstance.getId())
//             .taskCandidateGroup("support")
//             .singleResult();
//         assertNotNull(returnedTask);
//         assertEquals("工作组处理", returnedTask.getName());

//         // 完成返回的工作组任务
//         taskService.claim(returnedTask.getId(), "senior_agent");
        
//         // senior_agent完成返回的工作组任务
//         Map<String, Object> resolveVariables = new HashMap<>();
//         resolveVariables.put("solution", "高级支持已解决");
//         resolveVariables.put("status", "RESOLVED");
//         taskService.complete(returnedTask.getId(), resolveVariables);

//         // 验证流程继续到客户确认
//         Task verifyTask = taskService.createTaskQuery()
//             .processInstanceId(processInstance.getId())
//             .taskAssignee("user1")
//             .singleResult();
//         assertNotNull(verifyTask);
//         assertEquals("客户确认", verifyTask.getName());
//     }

//     // 测试工单优先级评估
//     @Test
//     void testTicketPriorityEvaluation() {
//         // 1. 测试高优先级工单
//         testPriorityEvaluation("高优先级工单", TicketPriorityEnum.URGENT.name(), "紧急问题描述", true);
        
//         // 2. 测试中优先级工单
//         testPriorityEvaluation("普通工单", TicketPriorityEnum.MEDIUM.name(), "一般问题描述", false);
        
//         // 3. 测试低优先级工单
//         testPriorityEvaluation("低优先级工单", TicketPriorityEnum.LOW.name(), "minor issue", false);
//     }

//     // 辅助方法：测试不同优先级的工单评估
//     private void testPriorityEvaluation(String title, String inputPriority, String description, boolean isUrgent) {
//         // 准备流程变量
//         Map<String, Object> variables = new HashMap<>();
//         variables.put("creatorUser", "user1");
//         variables.put("workgroupUid", "support");
//         variables.put("slaTime", "PT4H");

//         // 启动流程实例
//         ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
//             TicketConsts.TICKET_PROCESS_KEY, variables);
//         assertNotNull(processInstance);

//         // 获取创建工单任务
//         Task createTask = taskService.createTaskQuery()
//             .processInstanceId(processInstance.getId())
//             .taskAssignee("user1")
//             .singleResult();
//         assertNotNull(createTask);
        
//         // 设置工单信息
//         Map<String, Object> ticketVariables = new HashMap<>();
//         ticketVariables.put("title", title);
//         ticketVariables.put("description", description);
//         ticketVariables.put("priority", inputPriority);
        
//         // 完成创建任务，触发优先级评估
//         taskService.complete(createTask.getId(), ticketVariables);

//         // 获取评估后的流程变量
//         Map<String, Object> processVariables = runtimeService.getVariables(processInstance.getId());
        
//         // 验证优先级评估结果
//         String evaluatedPriority = (String) processVariables.get("evaluatedPriority");
//         assertNotNull(evaluatedPriority);
//         log.info("Ticket priority evaluation - input: {}, evaluated: {}", inputPriority, evaluatedPriority);

//         // 验证SLA时间是否根据优先级正确设置
//         String slaTime = (String) processVariables.get("slaTime");
//         assertNotNull(slaTime);
//         if (isUrgent) {
//             // 紧急工单应该有更短的SLA时间
//             assertTrue(slaTime.contains("PT2H") || slaTime.contains("PT1H"));
//         } else {
//             // 普通工单保持默认SLA时间
//             assertEquals("PT4H", slaTime);
//         }

//         // 验证评估原因
//         String evaluationReason = (String) processVariables.get("evaluationReason");
//         assertNotNull(evaluationReason);
//         log.info("Priority evaluation reason: {}", evaluationReason);

//         // 验证工作组任务是否创建
//         Task groupTask = taskService.createTaskQuery()
//             .processInstanceId(processInstance.getId())
//             .taskCandidateGroup("support")
//             .singleResult();
//         assertNotNull(groupTask);
        
//         // 验证优先级是否影响任务属性
//         if (isUrgent) {
//             assertEquals(50, groupTask.getPriority()); // 高优先级任务
//         } else {
//             assertEquals(0, groupTask.getPriority());  // 普通优先级任务
//         }
//     }

//     // 测试工单消息通知
//     @Test
//     void testTicketNotifications() {
//         // 1. 测试分配通知
//         testAssignedNotification();
        
//         // 2. 测试升级通知
//         testEscalatedNotification();
        
//         // 3. 测试解决通知
//         testResolvedNotification();
//     }

//     // 测试分配通知
//     private void testAssignedNotification() {
//         // 准备流程变量
//         Map<String, Object> variables = new HashMap<>();
//         variables.put("creatorUser", "user1");
//         variables.put("workgroupUid", "support");
//         variables.put("slaTime", "PT4H");

//         // 启动流程实例
//         ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
//             TicketConsts.TICKET_PROCESS_KEY, variables);
//         assertNotNull(processInstance);

//         // 完成创建工单任务
//         Task createTask = taskService.createTaskQuery()
//             .processInstanceId(processInstance.getId())
//             .taskAssignee("user1")
//             .singleResult();
//         assertNotNull(createTask);
        
//         Map<String, Object> ticketVariables = new HashMap<>();
//         ticketVariables.put("title", "Test Assigned Notification");
//         ticketVariables.put("description", "Testing assigned notification");
//         ticketVariables.put("priority", "MEDIUM");
//         taskService.complete(createTask.getId(), ticketVariables);

//         // 获取工作组任务
//         Task groupTask = taskService.createTaskQuery()
//             .processInstanceId(processInstance.getId())
//             .taskCandidateGroup("support")
//             .singleResult();
//         assertNotNull(groupTask);

//         // 认领任务并设置状态为处理中，触发分配通知
//         taskService.claim(groupTask.getId(), "agent1");
//         Map<String, Object> assignedVariables = new HashMap<>();
//         assignedVariables.put("status", "PENDING");
//         taskService.complete(groupTask.getId(), assignedVariables);

//         // 验证消息处理任务
//         Task messageTask = taskService.createTaskQuery()
//             .processInstanceId(processInstance.getId())
//             .taskDefinitionKey("handleAssignedMessage")
//             .singleResult();
//         assertNotNull(messageTask);
//         assertEquals("处理分配通知", messageTask.getName());
//     }

//     // 测试升级通知
//     private void testEscalatedNotification() {
//         // 准备流程变量
//         Map<String, Object> variables = new HashMap<>();
//         variables.put("creatorUser", "user1");
//         variables.put("workgroupUid", "support");
//         variables.put("slaTime", "PT4H");

//         // 启动流程实例
//         ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
//             TicketConsts.TICKET_PROCESS_KEY, variables);
//         assertNotNull(processInstance);

//         // 完成创建工单任务
//         Task createTask = taskService.createTaskQuery()
//             .processInstanceId(processInstance.getId())
//             .taskAssignee("user1")
//             .singleResult();
//         assertNotNull(createTask);
        
//         Map<String, Object> ticketVariables = new HashMap<>();
//         ticketVariables.put("title", "Test Escalated Notification");
//         ticketVariables.put("description", "Testing escalated notification");
//         ticketVariables.put("priority", "HIGH");
//         taskService.complete(createTask.getId(), ticketVariables);

//         // 获取工作组任务
//         Task groupTask = taskService.createTaskQuery()
//             .processInstanceId(processInstance.getId())
//             .taskCandidateGroup("support")
//             .singleResult();
//         assertNotNull(groupTask);

//         // 升级工单，触发升级通知
//         taskService.claim(groupTask.getId(), "agent1");
//         Map<String, Object> escalatedVariables = new HashMap<>();
//         escalatedVariables.put("status", "ESCALATED");
//         escalatedVariables.put("escalatedReason", "需要高级支持");
//         taskService.complete(groupTask.getId(), escalatedVariables);

//         // 验证消息处理任务
//         Task messageTask = taskService.createTaskQuery()
//             .processInstanceId(processInstance.getId())
//             .taskDefinitionKey("handleEscalatedMessage")
//             .singleResult();
//         assertNotNull(messageTask);
//         assertEquals("处理升级通知", messageTask.getName());
//     }

//     // 测试解决通知
//     private void testResolvedNotification() {
//         // 准备流程变量
//         Map<String, Object> variables = new HashMap<>();
//         variables.put("creatorUser", "user1");
//         variables.put("workgroupUid", "support");
//         variables.put("slaTime", "PT4H");

//         // 启动流程实例
//         ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
//             TicketConsts.TICKET_PROCESS_KEY, variables);
//         assertNotNull(processInstance);

//         // 完成创建工单任务
//         Task createTask = taskService.createTaskQuery()
//             .processInstanceId(processInstance.getId())
//             .taskAssignee("user1")
//             .singleResult();
//         assertNotNull(createTask);
        
//         Map<String, Object> ticketVariables = new HashMap<>();
//         ticketVariables.put("title", "Test Resolved Notification");
//         ticketVariables.put("description", "Testing resolved notification");
//         ticketVariables.put("priority", "MEDIUM");
//         taskService.complete(createTask.getId(), ticketVariables);

//         // 获取工作组任务
//         Task groupTask = taskService.createTaskQuery()
//             .processInstanceId(processInstance.getId())
//             .taskCandidateGroup("support")
//             .singleResult();
//         assertNotNull(groupTask);

//         // 解决工单，触发解决通知
//         taskService.claim(groupTask.getId(), "agent1");
//         Map<String, Object> resolvedVariables = new HashMap<>();
//         resolvedVariables.put("status", "RESOLVED");
//         resolvedVariables.put("solution", "问题已修复");
//         taskService.complete(groupTask.getId(), resolvedVariables);

//         // 验证消息处理任务
//         Task messageTask = taskService.createTaskQuery()
//             .processInstanceId(processInstance.getId())
//             .taskDefinitionKey("handleResolvedMessage")
//             .singleResult();
//         assertNotNull(messageTask);
//         assertEquals("处理解决通知", messageTask.getName());
//     }

//     // 流程实例启动后，测试更新工作组
//     @Test
//     void testUpdateWorkgroup() {
//         // 1. 启动流程实例，设置初始工作组
//         Map<String, Object> variables = new HashMap<>();
//         variables.put("reporterUid", "user1");
//         variables.put("workgroupUid", "support1");  // 初始工作组
//         variables.put("orgUid", "org1");
//         variables.put("slaTime", "PT4H");

//         ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
//             TicketConsts.TICKET_PROCESS_KEY, 
//             variables
//         );
//         assertNotNull(processInstance);

//         // 2. 验证当前任务属于初始工作组
//         Task task = taskService.createTaskQuery()
//             .processInstanceId(processInstance.getId())
//             .taskCandidateGroup("support1")
//             .singleResult();
//         assertNotNull(task);
//         assertEquals("assignToGroup", task.getTaskDefinitionKey());

//         // 3. 更新工作组
//         runtimeService.setVariable(processInstance.getId(), "workgroupUid", "support2");

//         // 4. 验证任务已转移到新工作组
//         // 原工作组不再能看到任务
//         task = taskService.createTaskQuery()
//             .processInstanceId(processInstance.getId())
//             .taskCandidateGroup("support1")
//             .singleResult();
//         assertNull(task);

//         // 新工作组可以看到任务
//         task = taskService.createTaskQuery()
//             .processInstanceId(processInstance.getId())
//             .taskCandidateGroup("support2")
//             .singleResult();
//         assertNotNull(task);
//         assertEquals("assignToGroup", task.getTaskDefinitionKey());

//         // 5. 测试任务认领和完成
//         taskService.claim(task.getId(), "agent1");
        
//         Map<String, Object> completeVariables = new HashMap<>();
//         completeVariables.put("status", "RESOLVED");
//         taskService.complete(task.getId(), completeVariables);

//         // 6. 验证流程变量
//         String finalWorkgroup = (String) runtimeService.getVariable(processInstance.getId(), "workgroupUid");
//         assertEquals("support2", finalWorkgroup);
//     }
    
// }
