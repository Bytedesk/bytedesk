/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-14 15:48:59
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-14 15:56:01
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class GroupTicketProcessTests {

    @Autowired
    private ProcessEngine processEngine;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

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
            e.printStackTrace();
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
}
