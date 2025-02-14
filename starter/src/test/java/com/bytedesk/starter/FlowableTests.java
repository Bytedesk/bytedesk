/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-02 11:21:45
 * @LastEditors: jack ning github@bytedesk.com
 * @LastEditTime: 2025-02-14 13:13:14
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.starter;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.bytedesk.kbase.upload.UploadEntity;
import com.bytedesk.kbase.upload.UploadTypeEnum;

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

    // 查询全部流程定义列表, 涉及到 act_re_procdef表，部署成功会新增记录
    @Test
    public void testProcessDefinition() {
        List<ProcessDefinition> processList = repositoryService.createProcessDefinitionQuery().list();
        for (ProcessDefinition processDefinition : processList) {
            log.info("流程定义 name={}, key={}, version={}, deploymentId={}", 
                processDefinition.getName(),
                processDefinition.getKey(),
                processDefinition.getVersion(),
                processDefinition.getDeploymentId());
        }
    }

    /**
     * 自定义过滤条件查询流程定义列表, 涉及到 act_re_procdef表，部署成功会新增记录
     */
    @Test
    public void testFilterProcessDefinition() {
        List<ProcessDefinition> processList = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("StudentLeave")           // 按流程定义key过滤
                .processDefinitionName("请假流程")              // 按流程定义名称过滤
                .latestVersion()                               // 只查询最新版本
                .active()                                      // 查询激活的流程
                .orderByProcessDefinitionVersion().desc()      // 按版本降序排序
                .list();

        for (ProcessDefinition processDefinition : processList) {
            log.info("流程定义 name={}, key={}, version={}, deploymentId={}", 
                processDefinition.getName(),
                processDefinition.getKey(),
                processDefinition.getVersion(),
                processDefinition.getDeploymentId());
        }
    }

    // 添加流程定义
    @Test
    public void testAddProcessDefinition() {
        // 部署流程
        Deployment deployment = repositoryService.createDeployment()
                .name("请假流程")
                .addClasspathResource("processes/student-leave.bpmn20.xml")
                .deploy();
        log.info("部署流程成功: {}", deployment.getId());
    }

    // 通过上传文件部署流程
    @Test
    public void testAddProcessDefinitionByUploadFile() throws Exception {
        // 上传文件
        UploadEntity upload = UploadEntity.builder()
                .fileName("请假流程")
                .type(UploadTypeEnum.BPMN.name())
                .fileUrl("https://example.com/processes/student-leave.bpmn20.xml")
                .build();
        
        // 从URL获取InputStream
        InputStream inputStream = new URL(upload.getFileUrl()).openStream();
        
        // 部署流程
        Deployment deployment = repositoryService.createDeployment()
                .name(upload.getFileName())
                .addInputStream(upload.getFileName(), inputStream)
                .deploy();
        log.info("部署流程成功: {}", deployment.getId());
    }

    @Test
    public void testStudentLeaveFlow() {
        
        // 发起请假
        Map<String, Object> map = new HashMap<>();
        map.put("day", 5);
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

    // 按租户ID查询流程定义列表
    @Test
    public void testProcessDefinitionByTenant() {
        String orgUid = "df_org_uid";
        List<ProcessDefinition> processList = repositoryService.createProcessDefinitionQuery()
                .processDefinitionTenantId(orgUid)          // 按租户ID过滤
                .latestVersion()                           // 只查询最新版本
                .active()                                  // 查询激活的流程
                .orderByProcessDefinitionVersion().desc()  // 按版本降序排序
                .list();

        for (ProcessDefinition processDefinition : processList) {
            log.info("租户流程定义 tenantId={}, name={}, key={}, version={}", 
                processDefinition.getTenantId(),
                processDefinition.getName(),
                processDefinition.getKey(),
                processDefinition.getVersion());
        }
    }

    // 部署带租户ID的流程定义
    @Test
    public void testAddProcessDefinitionWithTenant() {
        String orgUid = "df_org_uid";
        // 部署流程
        Deployment deployment = repositoryService.createDeployment()
                .name("请假流程")
                .addClasspathResource("processes/student-leave.bpmn20.xml")
                .tenantId(orgUid)                         // 设置租户ID
                .deploy();
        log.info("部署租户流程成功: deploymentId={}, tenantId={}", deployment.getId(), deployment.getTenantId());
    }

    // 通过上传文件部署带租户ID的流程
    @Test
    public void testAddProcessDefinitionByUploadFileWithTenant() throws Exception {
        String orgUid = "df_org_uid";
        // 上传文件
        UploadEntity upload = UploadEntity.builder()
                .fileName("请假流程")
                .type(UploadTypeEnum.BPMN.name())
                .fileUrl("https://example.com/processes/student-leave.bpmn20.xml")
                .build();
        
        // 从URL获取InputStream
        InputStream inputStream = new URL(upload.getFileUrl()).openStream();
        
        // 部署流程
        Deployment deployment = repositoryService.createDeployment()
                .name(upload.getFileName())
                .addInputStream(upload.getFileName(), inputStream)
                .tenantId(orgUid)                         // 设置租户ID
                .deploy();
        log.info("部署租户流程成功: deploymentId={}, tenantId={}", deployment.getId(), deployment.getTenantId());
    }

    @Test
    public void testMultiTenantSameNameProcess() throws Exception {
        String processName = "请假流程";
        
        // 部署第一个租户的流程
        String tenant1 = "org_uid_1";
        repositoryService.createDeployment()
                .name(processName)
                .addClasspathResource("processes/student-leave.bpmn20.xml")
                .tenantId(tenant1)
                .deploy();
        
        // 部署第二个租户的流程
        String tenant2 = "org_uid_2";
        repositoryService.createDeployment()
                .name(processName)
                .addClasspathResource("processes/student-leave.bpmn20.xml")
                .tenantId(tenant2)
                .deploy();

        // 查询第一个租户的流程
        List<ProcessDefinition> tenant1Processes = repositoryService.createProcessDefinitionQuery()
                .processDefinitionName(processName)
                .processDefinitionTenantId(tenant1)
                .orderByProcessDefinitionVersion().desc()
                .list();

        // 查询第二个租户的流程
        List<ProcessDefinition> tenant2Processes = repositoryService.createProcessDefinitionQuery()
                .processDefinitionName(processName)
                .processDefinitionTenantId(tenant2)
                .orderByProcessDefinitionVersion().desc()
                .list();

        // 验证每个租户都能查到自己的流程
        log.info("租户1的流程数量: {}", tenant1Processes.size());
        log.info("租户2的流程数量: {}", tenant2Processes.size());
        
        for (ProcessDefinition pd : tenant1Processes) {
            log.info("租户1的流程: tenantId={}, name={}, key={}, version={}", 
                pd.getTenantId(), pd.getName(), pd.getKey(), pd.getVersion());
        }
        
        for (ProcessDefinition pd : tenant2Processes) {
            log.info("租户2的流程: tenantId={}, name={}, key={}, version={}", 
                pd.getTenantId(), pd.getName(), pd.getKey(), pd.getVersion());
        }
    }

    @Test
    public void testSameTenantSameNameProcess() throws Exception {
        String tenantId = "org_uid_1";
        String processName = "请假流程";
        
        // 部署第一个流程 - 学生请假流程
        repositoryService.createDeployment()
                .name(processName)
                .addClasspathResource("processes/student-leave.bpmn20.xml")
                .tenantId(tenantId)
                .deploy();
        
        // 部署第二个流程 - 员工请假流程
        repositoryService.createDeployment()
                .name(processName)
                .addClasspathResource("processes/holiday-request.bpmn20.xml")
                .tenantId(tenantId)
                .deploy();

        // 查询该租户下所有同名流程
        List<ProcessDefinition> processes = repositoryService.createProcessDefinitionQuery()
                .processDefinitionName(processName)
                .processDefinitionTenantId(tenantId)
                .orderByProcessDefinitionKey().asc()
                .orderByProcessDefinitionVersion().desc()
                .list();

        // 验证同一租户下的同名流程
        log.info("租户的同名流程数量: {}", processes.size());
        
        for (ProcessDefinition pd : processes) {
            log.info("租户流程: tenantId={}, name={}, key={}, version={}, resourceName={}", 
                pd.getTenantId(),
                pd.getName(),
                pd.getKey(),
                pd.getVersion(),
                pd.getResourceName());
        }
    }
}
