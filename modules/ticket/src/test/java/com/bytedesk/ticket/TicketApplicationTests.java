package com.bytedesk.ticket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.Map;

// import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
class TicketApplicationTests {

	// @Autowired
	// private ProcessEngine processEngine;

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private TaskService taskService;

	@Test
	void testStudentLeaveProcess() {
		// 准备流程变量
		Map<String, Object> variables = new HashMap<>();
		variables.put("studentUser", "student1");
		variables.put("teacherGroup", "teachers");  // 设置教师组变量
		variables.put("principalGroup", "principals");  // 设置校长组变量
		variables.put("day", 1);  // 请假天数

		// 启动流程实例
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
			"StudentLeave", variables);
		assertNotNull(processInstance);
		log.info("Started process instance id {}", processInstance.getId());

		// 验证学生任务
		Task studentTask = taskService.createTaskQuery()
			.processInstanceId(processInstance.getId())
			.taskAssignee("student1")
			.singleResult();
		assertNotNull(studentTask);
		assertEquals("请假申请", studentTask.getName());

		// 完成学生申请
		taskService.complete(studentTask.getId());

		// 验证教师任务
		Task teacherTask = taskService.createTaskQuery()
			.processInstanceId(processInstance.getId())
			.taskCandidateGroup("teachers")
			.singleResult();
		assertNotNull(teacherTask);
		assertEquals("老师审批", teacherTask.getName());

		// 完成教师审批
		Map<String, Object> teacherVariables = new HashMap<>();
		teacherVariables.put("outcome", "通过");
		taskService.complete(teacherTask.getId(), teacherVariables);

		// 由于请假天数为1天，流程应该结束
		ProcessInstance endedProcess = runtimeService.createProcessInstanceQuery()
			.processInstanceId(processInstance.getId())
			.singleResult();
		assertEquals(null, endedProcess);
	}

	@Test
	void testStudentLeaveLongProcess() {
		// 准备流程变量
		Map<String, Object> variables = new HashMap<>();
		variables.put("studentUser", "student1");
		variables.put("teacherGroup", "teachers");
		variables.put("principalGroup", "principals");
		variables.put("day", 3);  // 3天需要校长审批

		// 启动流程实例
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
			"StudentLeave", variables);
		
		// 完成学生申请
		Task studentTask = taskService.createTaskQuery()
			.processInstanceId(processInstance.getId())
			.taskAssignee("student1")
			.singleResult();
		taskService.complete(studentTask.getId());

		// 完成教师审批
		Task teacherTask = taskService.createTaskQuery()
			.processInstanceId(processInstance.getId())
			.taskCandidateGroup("teachers")
			.singleResult();
		Map<String, Object> teacherVariables = new HashMap<>();
		teacherVariables.put("outcome", "通过");
		taskService.complete(teacherTask.getId(), teacherVariables);

		// 验证校长任务
		Task principalTask = taskService.createTaskQuery()
			.processInstanceId(processInstance.getId())
			.taskCandidateGroup("principals")
			.singleResult();
		assertNotNull(principalTask);
		assertEquals("校长审批", principalTask.getName());
	}

	@Test
	void contextLoads() {
	}

}
