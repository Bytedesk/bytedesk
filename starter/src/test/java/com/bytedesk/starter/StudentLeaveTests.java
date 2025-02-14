package com.bytedesk.starter;

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
class StudentLeaveTests {

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

	// 查询学生已完成的请假申请
	@Test
	void testStudentHistoricTasks() {
		// 先创建并完成一些请假申请
		createAndCompleteStudentTasks();

		// 查询学生已完成的历史任务
		List<HistoricTaskInstance> historicTasks = processEngine.getHistoryService()
			.createHistoricTaskInstanceQuery()
			.taskAssignee("student1")
			.finished()  // 只查询已完成的任务
			.orderByHistoricTaskInstanceEndTime()
			.desc()
			.list();

		assertNotNull(historicTasks);
		log.info("=== 学生已完成的请假申请 ===");
		historicTasks.forEach(task -> {
			log.info("Historic task: id={}, name={}, createTime={}, endTime={}", 
				task.getId(), 
				task.getName(),
				task.getCreateTime(),
				task.getEndTime());
			assertEquals("请假申请", task.getName());

			// 获取流程实例的完整历史记录
			List<HistoricTaskInstance> processHistory = processEngine.getHistoryService()
				.createHistoricTaskInstanceQuery()
				.processInstanceId(task.getProcessInstanceId())
				.orderByHistoricTaskInstanceStartTime()
				.asc()
				.list();
			
			log.info("请假申请处理流程：");
			processHistory.forEach(historyTask -> {
				log.info("- {}: {} (处理人: {})", 
					historyTask.getName(),
					historyTask.getEndTime() != null ? "已完成" : "处理中",
					historyTask.getAssignee() != null ? historyTask.getAssignee() : "未认领");
			});

			// 获取流程变量
			List<HistoricVariableInstance> variables = processEngine.getHistoryService()
				.createHistoricVariableInstanceQuery()
				.processInstanceId(task.getProcessInstanceId())
				.list();
			
			log.info("申请详情：");
			variables.forEach(var -> {
				log.info("- {}: {}", var.getVariableName(), var.getValue());
			});
		});
	}

	// 查询学生待处理的请假申请
	@Test
	void testStudentActiveTasks() {
		// 先创建一些请假申请
		createLeaveProcesses();

		// 查询学生待处理的任务
		List<Task> activeTasks = taskService.createTaskQuery()
			.taskAssignee("student1")
			.active()  // 只查询活动的任务
			.orderByTaskCreateTime()
			.desc()
			.list();

		assertNotNull(activeTasks);
		log.info("=== 学生待处理的请假申请 ===");
		activeTasks.forEach(task -> {
			log.info("Active task: id={}, name={}, createTime={}", 
				task.getId(), 
				task.getName(),
				task.getCreateTime());
			assertEquals("请假申请", task.getName());

			// 获取流程变量
			Map<String, Object> variables = runtimeService.getVariables(task.getProcessInstanceId());
			log.info("申请详情：");
			log.info("- 请假天数: {}", variables.get("day"));
			log.info("- 申请人: {}", variables.get("studentUser"));
		});

		// 输出统计信息
		log.info("待处理申请数量: {}", activeTasks.size());
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
				task.getCreateTime(),
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
				task.getCreateTime(),
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

	// 查询教师所有任务（包括待处理和已完成）
	@Test
	void testTeacherAllTasks() {
		// 先创建一些任务并完成部分
		createAndSubmitToTeacher();  // 创建待处理任务
		createAndCompleteTeacherTasks();  // 创建并完成一些任务

		// 查询待处理任务
		List<Task> activeTasks = taskService.createTaskQuery()
			.taskCandidateGroup("teachers")
			.orderByTaskCreateTime()
			.desc()
			.list();

		// 查询历史任务
		List<HistoricTaskInstance> historicTasks = processEngine.getHistoryService()
			.createHistoricTaskInstanceQuery()
			.taskCandidateGroup("teachers")
			.finished()
			.orderByHistoricTaskInstanceEndTime()
			.desc()
			.list();

		// 验证并输出结果
		assertNotNull(activeTasks);
		assertNotNull(historicTasks);

		log.info("=== 教师待处理任务 ===");
		activeTasks.forEach(task -> {
			log.info("Active task: id={}, name={}, createTime={}", 
				task.getId(), 
				task.getName(),
				task.getCreateTime());
			
			// 获取流程变量
			Map<String, Object> variables = runtimeService.getVariables(task.getProcessInstanceId());
			log.info("Variables: studentUser={}, day={}", 
				variables.get("studentUser"),
				variables.get("day"));
		});

		log.info("=== 教师已完成任务 ===");
		historicTasks.forEach(task -> {
			log.info("Historic task: id={}, name={}, createTime={}, endTime={}, assignee={}", 
				task.getId(), 
				task.getName(),
				task.getCreateTime(),
				task.getEndTime(),
				task.getAssignee());
			
			// 获取历史变量
			List<HistoricVariableInstance> variables = processEngine.getHistoryService()
				.createHistoricVariableInstanceQuery()
				.processInstanceId(task.getProcessInstanceId())
				.list();
			
			variables.forEach(var -> {
				log.info("Historic variable: {}={}", var.getVariableName(), var.getValue());
			});
		});

		// 输出统计信息
		log.info("Total tasks: active={}, completed={}", 
			activeTasks.size(), 
			historicTasks.size());
	}

	// 查询校长所有任务（包括待处理和已完成）
	@Test
	void testPrincipalAllTasks() {
		// 先创建一些任务并完成部分
		createAndSubmitToPrincipal();  // 创建待处理任务
		createAndCompletePrincipalTasks();  // 创建并完成一些任务

		// 查询待处理任务
		List<Task> activeTasks = taskService.createTaskQuery()
			.taskCandidateGroup("principals")
			.orderByTaskCreateTime()
			.desc()
			.list();

		// 查询历史任务
		List<HistoricTaskInstance> historicTasks = processEngine.getHistoryService()
			.createHistoricTaskInstanceQuery()
			.taskCandidateGroup("principals")
			.finished()
			.orderByHistoricTaskInstanceEndTime()
			.desc()
			.list();

		// 验证并输出结果
		assertNotNull(activeTasks);
		assertNotNull(historicTasks);

		log.info("=== 校长待处理任务 ===");
		activeTasks.forEach(task -> {
			log.info("Active task: id={}, name={}, createTime={}", 
				task.getId(), 
				task.getName(),
				task.getCreateTime());
			
			// 获取流程变量
			Map<String, Object> variables = runtimeService.getVariables(task.getProcessInstanceId());
			log.info("Variables: studentUser={}, day={}", 
				variables.get("studentUser"),
				variables.get("day"));
		});

		log.info("=== 校长已完成任务 ===");
		historicTasks.forEach(task -> {
			log.info("Historic task: id={}, name={}, createTime={}, endTime={}, assignee={}", 
				task.getId(), 
				task.getName(),
				task.getCreateTime(),
				task.getEndTime(),
				task.getAssignee());
			
			// 获取历史变量
			List<HistoricVariableInstance> variables = processEngine.getHistoryService()
				.createHistoricVariableInstanceQuery()
				.processInstanceId(task.getProcessInstanceId())
				.list();
			
			variables.forEach(var -> {
				log.info("Historic variable: {}={}", var.getVariableName(), var.getValue());
			});
		});

		// 输出统计信息
		log.info("Total tasks: active={}, completed={}", 
			activeTasks.size(), 
			historicTasks.size());
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

	// 辅助方法：创建并完成学生任务
	private void createAndCompleteStudentTasks() {
		// 创建一些请假申请
		createLeaveProcesses();
		
		// 完成所有学生申请任务
		List<Task> studentTasks = taskService.createTaskQuery()
			.taskAssignee("student1")
			.list();
		
		studentTasks.forEach(task -> {
			// 完成任务
			Map<String, Object> variables = new HashMap<>();
			variables.put("reason", "生病");  // 添加请假原因
			taskService.complete(task.getId(), variables);
		});
	}

	// 测试教师组任务分配和查看
	@Test
	void testTeacherGroupTasks() {
		// 1. 创建教师组成员
		String[] teachers = {"teacher1", "teacher2", "teacher3"};
		
		// 2. 创建一些待处理的请假申请
		createAndSubmitToTeacher();  // 创建几个教师待处理任务

		// 3. 测试每个教师都能查看到相同的任务列表
		for (String teacher : teachers) {
			// 查询该教师可以看到的任务
			List<Task> teacherTasks = taskService.createTaskQuery()
				.taskCandidateGroup("teachers")  // 查询教师组的任务
				.taskCandidateUser(teacher)      // 特定教师可见的任务
				.orderByTaskCreateTime()
				.desc()
				.list();

			log.info("=== 教师 {} 可见的任务 ===", teacher);
			teacherTasks.forEach(task -> {
				log.info("Task: id={}, name={}, createTime={}", 
					task.getId(), 
					task.getName(),
					task.getCreateTime());
			});

			// 验证任务数量
			assertNotNull(teacherTasks);
			assertTrue(teacherTasks.size() > 0);

			// 验证任务内容
			teacherTasks.forEach(task -> {
				assertEquals("老师审批", task.getName());
				
				// 验证其他教师也能看到这个任务
				for (String otherTeacher : teachers) {
					if (!otherTeacher.equals(teacher)) {
						boolean canSeeTask = taskService.createTaskQuery()
							.taskId(task.getId())
							.taskCandidateUser(otherTeacher)
							.count() > 0;
						assertTrue(canSeeTask, 
							String.format("教师 %s 应该能看到任务 %s", otherTeacher, task.getId()));
					}
				}
			});
		}

		// 4. 测试任务认领
		// 获取一个待处理任务
		Task taskToClaim = taskService.createTaskQuery()
			.taskCandidateGroup("teachers")
			.singleResult();
		
		if (taskToClaim != null) {
			// teacher1 认领任务
			taskService.claim(taskToClaim.getId(), "teacher1");
			
			// 验证任务已被认领
			Task claimedTask = taskService.createTaskQuery()
				.taskId(taskToClaim.getId())
				.singleResult();
			assertEquals("teacher1", claimedTask.getAssignee());
			
			// 验证其他教师不能再认领
			for (String otherTeacher : teachers) {
				if (!otherTeacher.equals("teacher1")) {
					try {
						taskService.claim(taskToClaim.getId(), otherTeacher);
						fail("应该抛出异常，因为任务已被认领");
					} catch (Exception e) {
						// 预期会抛出异常
						log.info("预期的异常：任务已被认领 - {}", e.getMessage());
					}
				}
			}
			
			// teacher1 可以归还任务
			taskService.unclaim(taskToClaim.getId());
			
			// 验证任务可以被其他教师认领
			Task unclaimedTask = taskService.createTaskQuery()
				.taskId(taskToClaim.getId())
				.singleResult();
			assertNull(unclaimedTask.getAssignee());
			
			// teacher2 可以认领任务
			taskService.claim(taskToClaim.getId(), "teacher2");
			Task reclaimedTask = taskService.createTaskQuery()
				.taskId(taskToClaim.getId())
				.singleResult();
			assertEquals("teacher2", reclaimedTask.getAssignee());
		}
	}

	// 测试教师组任务处理统计
	@Test
	void testTeacherGroupTasksStatistics() {
		// 创建一些任务并由不同教师处理
		createMultipleTasksForTeachers();

		String[] teachers = {"teacher1", "teacher2", "teacher3"};
		
		// 统计每个教师的任务处理情况
		for (String teacher : teachers) {
			// 统计已处理的任务
			long completedCount = processEngine.getHistoryService()
				.createHistoricTaskInstanceQuery()
				.taskAssignee(teacher)
				.finished()
				.count();
				
			// 统计正在处理的任务
			long activeCount = taskService.createTaskQuery()
				.taskAssignee(teacher)
				.count();
				
			log.info("教师 {} 的任务统计：", teacher);
			log.info("- 已完成任务数: {}", completedCount);
			log.info("- 处理中任务数: {}", activeCount);
		}
	}

	// 辅助方法：创建多个任务并由不同教师处理
	private void createMultipleTasksForTeachers() {
		// 创建6个请假申请（将由3个教师处理）
		for (int i = 0; i < 6; i++) {
			Map<String, Object> variables = new HashMap<>();
			variables.put("studentUser", "student" + (i + 1));
			variables.put("teacherGroup", "teachers");
			variables.put("principalGroup", "principals");
			variables.put("day", 1);
			ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("StudentLeave", variables);
			
			// 完成学生申请
			Task studentTask = taskService.createTaskQuery()
				.processInstanceId(processInstance.getId())
				.taskAssignee("student" + (i + 1))
				.singleResult();
			taskService.complete(studentTask.getId());
			
			// 由不同教师处理任务
			Task teacherTask = taskService.createTaskQuery()
				.processInstanceId(processInstance.getId())
				.taskCandidateGroup("teachers")
				.singleResult();
			
			String assignTeacher = "teacher" + ((i % 3) + 1);
			taskService.claim(teacherTask.getId(), assignTeacher);
			
			// 随机完成部分任务
			if (i % 2 == 0) {
				Map<String, Object> teacherVariables = new HashMap<>();
				teacherVariables.put("outcome", "通过");
				taskService.complete(teacherTask.getId(), teacherVariables);
			}
		}
	}

	@Test
	void contextLoads() {
	}

}
