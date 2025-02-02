/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-02 11:21:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-02 11:47:01
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.starter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class FlowableTests {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    /**
     * 查询流程定义列表, 涉及到 act_re_procdef表，部署成功会新增记录
     */
    @Test
    public void testProcessDefinition() {
        List<ProcessDefinition> processList = repositoryService.createProcessDefinitionQuery().list();
        for (ProcessDefinition processDefinition : processList) {
            log.info("ProcessDefinition name = {},deploymentId = {}", processDefinition.getName(),
                    processDefinition.getDeploymentId());
        }
    }

    @Test
    public void testStudentLeaveFlow() {
        // 发起请假
        Map<String, Object> map = new HashMap<>();
        map.put("day", 2);
        map.put("studentUser", "小明");
        ProcessInstance studentLeave = runtimeService.startProcessInstanceByKey("StudentLeave", map);
        Task task = taskService.createTaskQuery().processInstanceId(studentLeave.getId()).singleResult();
        taskService.complete(task.getId());

        // 老师审批
        List<Task> teacherTaskList = taskService.createTaskQuery().taskCandidateGroup("teacher").list();
        Map<String, Object> teacherMap = new HashMap<>();
        teacherMap.put("outcome", "通过");
        for (Task teacherTask : teacherTaskList) {
            taskService.complete(teacherTask.getId(), teacherMap);
        }

        // 校长审批
        List<Task> principalTaskList = taskService.createTaskQuery().taskCandidateGroup("principal").list();
        Map<String, Object> principalMap = new HashMap<>();
        principalMap.put("outcome", "通过");
        for (Task principalTask : principalTaskList) {
            taskService.complete(principalTask.getId(), principalMap);
        }

        // 查看历史
        List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(studentLeave.getId())
                .finished()
                .orderByHistoricActivityInstanceEndTime().asc()
                .list();
        for (HistoricActivityInstance activity : activities) {
            System.out.println(activity.getActivityName());
        }
    }
}
