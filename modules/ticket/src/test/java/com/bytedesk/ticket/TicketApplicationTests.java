package com.bytedesk.ticket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
class TicketApplicationTests {

	@Autowired
	private ProcessEngine processEngine;

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private TaskService taskService;

	// 测试学生请假流程
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

	// 测试学生请假流程（长期请假）
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

	// 查询学生的所有请假申请
	@Test
	void testStudentQueryTasks() {
		// 先创建几个请假申请
		createLeaveProcesses();

		// 查询学生的所有请假申请
		List<Task> studentTasks = taskService.createTaskQuery()
			.taskAssignee("student1")  // 查询指定学生的任务
			.orderByTaskCreateTime()
			.desc()
			.list();

		assertNotNull(studentTasks);
		studentTasks.forEach(task -> {
			log.info("Student task: id={}, name={}, createTime={}", 
				task.getId(), task.getName(), task.getCreateTime());
			assertEquals("请假申请", task.getName());
		});

		// 查询学生的历史请假记录
		List<HistoricProcessInstance> historicInstances = processEngine.getHistoryService()
			.createHistoricProcessInstanceQuery()
			.variableValueEquals("studentUser", "student1")
			.orderByProcessInstanceStartTime()
			.desc()
			.list();

		assertNotNull(historicInstances);
		historicInstances.forEach(instance -> {
			log.info("Historic process: id={}, startTime={}, endTime={}", 
				instance.getId(), instance.getStartTime(), instance.getEndTime());
		});
	}

	// 查询教师的待处理任务
	@Test
	void testTeacherQueryTasks() {
		// 先创建几个请假申请并提交到教师审批
		createAndSubmitToTeacher();

		// 查询教师组的待处理任务
		List<Task> teacherTasks = taskService.createTaskQuery()
			.taskCandidateGroup("teachers")  // 查询教师组的任务
			.orderByTaskCreateTime()
			.desc()
			.list();

		assertNotNull(teacherTasks);
		teacherTasks.forEach(task -> {
			log.info("Teacher task: id={}, name={}, createTime={}", 
				task.getId(), task.getName(), task.getCreateTime());
			assertEquals("老师审批", task.getName());
			
			// 获取关联的流程变量
			Map<String, Object> processVariables = runtimeService.getVariables(task.getProcessInstanceId());
			log.info("Process variables: studentUser={}, day={}", 
				processVariables.get("studentUser"), 
				processVariables.get("day"));
		});
	}

	// 查询校长的待处理任务
	@Test
	void testPrincipalQueryTasks() {
		// 先创建几个长期请假申请并提交到校长审批
		createAndSubmitToPrincipal();

		// 查询校长组的待处理任务
		List<Task> principalTasks = taskService.createTaskQuery()
			.taskCandidateGroup("principals")  // 查询校长组的任务
			.orderByTaskCreateTime()
			.desc()
			.list();

		assertNotNull(principalTasks);
		principalTasks.forEach(task -> {
			log.info("Principal task: id={}, name={}, createTime={}", 
				task.getId(), task.getName(), task.getCreateTime());
			assertEquals("校长审批", task.getName());
			
			// 获取关联的流程变量
			Map<String, Object> processVariables = runtimeService.getVariables(task.getProcessInstanceId());
			log.info("Process variables: studentUser={}, day={}", 
				processVariables.get("studentUser"), 
				processVariables.get("day"));
		});
	}

	// 查询教师已处理的历史任务
	@Test
	void testTeacherHistoricTasks() {
		// 先创建并完成一些教师任务
		createAndCompleteTeacherTasks();

		// 查询教师组已完成的历史任务
		List<HistoricTaskInstance> historicTasks = processEngine.getHistoryService()
			.createHistoricTaskInstanceQuery()
			.taskCandidateGroup("teachers")
			.finished()  // 只查询已完成的任务
			.orderByHistoricTaskInstanceEndTime()
			.desc()
			.list();

		assertNotNull(historicTasks);
		historicTasks.forEach(task -> {
			log.info("Teacher historic task: id={}, name={}, startTime={}, endTime={}", 
				task.getId(), 
				task.getName(),
				task.getStartTime(),
				task.getEndTime());
			assertEquals("老师审批", task.getName());

			// 获取任务的历史变量
			List<HistoricVariableInstance> variables = processEngine.getHistoryService()
				.createHistoricVariableInstanceQuery()
				.processInstanceId(task.getProcessInstanceId())
				.list();
			
			variables.forEach(var -> {
				log.info("Variable: {}={}", var.getVariableName(), var.getValue());
			});
		});
	}

	// 查询校长已处理的历史任务
	@Test
	void testPrincipalHistoricTasks() {
		// 先创建并完成一些校长任务
		createAndCompletePrincipalTasks();

		// 查询校长组已完成的历史任务
		List<HistoricTaskInstance> historicTasks = processEngine.getHistoryService()
			.createHistoricTaskInstanceQuery()
			.taskCandidateGroup("principals")
			.finished()  // 只查询已完成的任务
			.orderByHistoricTaskInstanceEndTime()
			.desc()
			.list();

		assertNotNull(historicTasks);
		historicTasks.forEach(task -> {
			log.info("Principal historic task: id={}, name={}, startTime={}, endTime={}", 
				task.getId(), 
				task.getName(),
				task.getStartTime(),
				task.getEndTime());
			assertEquals("校长审批", task.getName());

			// 获取任务的历史变量
			List<HistoricVariableInstance> variables = processEngine.getHistoryService()
				.createHistoricVariableInstanceQuery()
				.processInstanceId(task.getProcessInstanceId())
				.list();
			
			variables.forEach(var -> {
				log.info("Variable: {}={}", var.getVariableName(), var.getValue());
			});
		});
	}

	// 辅助方法：创建多个请假流程
	private void createLeaveProcesses() {
		// 创建两个请假申请
		for (int i = 0; i < 2; i++) {
			Map<String, Object> variables = new HashMap<>();
			variables.put("studentUser", "student1");
			variables.put("teacherGroup", "teachers");
			variables.put("principalGroup", "principals");
			variables.put("day", 1 + i);  // 一个1天，一个2天
			runtimeService.startProcessInstanceByKey("StudentLeave", variables);
		}
	}

	// 辅助方法：创建请假流程并提交到教师审批
	private void createAndSubmitToTeacher() {
		createLeaveProcesses();
		
		// 完成所有学生申请任务
		List<Task> studentTasks = taskService.createTaskQuery()
			.taskAssignee("student1")
			.list();
		
		studentTasks.forEach(task -> {
			taskService.complete(task.getId());
		});
	}

	// 辅助方法：创建长期请假流程并提交到校长审批
	private void createAndSubmitToPrincipal() {
		// 创建两个3天的请假申请
		for (int i = 0; i < 2; i++) {
			Map<String, Object> variables = new HashMap<>();
			variables.put("studentUser", "student1");
			variables.put("teacherGroup", "teachers");
			variables.put("principalGroup", "principals");
			variables.put("day", 3);  // 3天需要校长审批
			runtimeService.startProcessInstanceByKey("StudentLeave", variables);
		}
		
		// 完成所有学生申请任务
		List<Task> studentTasks = taskService.createTaskQuery()
			.taskAssignee("student1")
			.list();
		
		studentTasks.forEach(task -> {
			taskService.complete(task.getId());
		});
		
		// 完成所有教师审批任务
		List<Task> teacherTasks = taskService.createTaskQuery()
			.taskCandidateGroup("teachers")
			.list();
		
		teacherTasks.forEach(task -> {
			Map<String, Object> teacherVariables = new HashMap<>();
			teacherVariables.put("outcome", "通过");
			taskService.complete(task.getId(), teacherVariables);
		});
	}

	// 辅助方法：创建并完成教师任务
	private void createAndCompleteTeacherTasks() {
		// 创建并提交到教师审批
		createAndSubmitToTeacher();
		
		// 完成所有教师任务
		List<Task> teacherTasks = taskService.createTaskQuery()
			.taskCandidateGroup("teachers")
			.list();
		
		teacherTasks.forEach(task -> {
			// 先认领任务
			taskService.claim(task.getId(), "teacher1");
			
			// 完成任务
			Map<String, Object> variables = new HashMap<>();
			variables.put("outcome", "通过");
			taskService.complete(task.getId(), variables);
		});
	}

	// 辅助方法：创建并完成校长任务
	private void createAndCompletePrincipalTasks() {
		// 创建并提交到校长审批
		createAndSubmitToPrincipal();
		
		// 完成所有校长任务
		List<Task> principalTasks = taskService.createTaskQuery()
			.taskCandidateGroup("principals")
			.list();
		
		principalTasks.forEach(task -> {
			// 先认领任务
			taskService.claim(task.getId(), "principal1");
			
			// 完成任务
			Map<String, Object> variables = new HashMap<>();
			variables.put("outcome", "通过");
			taskService.complete(task.getId(), variables);
		});
	}

	@Test
	void contextLoads() {
	}

}
