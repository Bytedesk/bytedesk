package com.bytedesk.ticket.ticketflow;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.task.api.Task;
import org.flowable.engine.task.Comment;
import org.springframework.stereotype.Service;

import com.bytedesk.ticket.comment.TicketCommentService;
import com.bytedesk.ticket.ticket.TicketConsts;
// import com.bytedesk.ticket.ticket.TicketService;
import com.bytedesk.ticket.ticket.TicketEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketFlowService {

    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final RepositoryService repositoryService;
    private final HistoryService historyService;
    // private final TicketService assignmentService;
    private final TicketCommentService commentService;
    // private final AgentRestService agentService;
    // private final WorkgroupRestService workgroupService;

    /**
     * 启动工单流程
     */
    public void startTicketProcess(TicketEntity ticket) {
        log.info("start ticket process for ticket: {}", ticket.getId());
        
        // 设置流程变量
        Map<String, Object> variables = new HashMap<>();
        variables.put("ticket", ticket);
        variables.put("reporter", ticket.getReporter());
        variables.put("priority", ticket.getPriority());
        
        // 启动流程实例
        runtimeService.startProcessInstanceByKey(
            TicketConsts.TICKET_PROCESS_KEY,
            ticket.getId().toString(),
            variables
        );
    }

    /**
     * 分配工单任务
     */
    public void assignTicketTask(String taskId, String assignee) {
        log.info("assign task {} to {}", taskId, assignee);
        taskService.setAssignee(taskId, assignee);
    }

    /**
     * 完成工单任务
     */
    public void completeTicketTask(String taskId, Map<String, Object> variables) {
        log.info("complete task: {}", taskId);
        taskService.complete(taskId, variables);
    }

    /**
     * 添加工单评论
     * ACT_HI_COMMENT - 历史评论表，存储所有的任务评论
     */
    public void addTicketComment(String taskId, String message, String userId) {
        commentService.addComment(taskId, message, userId);
    }

    /**
     * 获取工单评论列表
     */
    public List<Comment> getTicketComments(String taskId) {
        return commentService.getTaskComments(taskId);
    }

    /**
     * 获取工单所有历史评论
     */
    public List<Comment> getProcessInstanceComments(String processInstanceId) {
        return commentService.getProcessInstanceComments(processInstanceId);
    }

    /**
     * 查询用户的工单任务
     */
    public List<Task> queryUserTasks(String assignee) {
        return taskService.createTaskQuery()
            .processDefinitionKey(TicketConsts.TICKET_PROCESS_KEY)
            .taskAssignee(assignee)
            .orderByTaskCreateTime()
            .desc()
            .list();
    }

    /**
     * 部署工单流程
     */
    public void deployTicketProcess() {
        log.info("deploying ticket process");
        repositoryService.createDeployment()
            .addClasspathResource("processes/ticket.bpmn20.xml")
            .name("工单处理流程")
            .deploy();
    }

    /**
     * 获取最新的流程定义
     */
    public ProcessDefinition getLatestProcessDefinition() {
        return repositoryService.createProcessDefinitionQuery()
            .processDefinitionKey(TicketConsts.TICKET_PROCESS_KEY)
            .latestVersion()
            .singleResult();
    }

    /**
     * 查询工单历史
     */
    public List<HistoricProcessInstance> queryTicketHistory(String ticketId) {
        return historyService.createHistoricProcessInstanceQuery()
            .processDefinitionKey(TicketConsts.TICKET_PROCESS_KEY)
            .processInstanceBusinessKey(ticketId)
            .orderByProcessInstanceStartTime()
            .desc()
            .list();
    }

    /**
     * 终止工单流程
     */
    public void terminateTicketProcess(String processInstanceId, String reason) {
        log.info("terminate process: {} for reason: {}", processInstanceId, reason);
        runtimeService.deleteProcessInstance(processInstanceId, reason);
    }

    /**
     * 挂起工单流程
     */
    public void suspendTicketProcess(String processInstanceId) {
        log.info("suspend process: {}", processInstanceId);
        runtimeService.suspendProcessInstanceById(processInstanceId);
    }

    /**
     * 激活工单流程
     */
    public void activateTicketProcess(String processInstanceId) {
        log.info("activate process: {}", processInstanceId);
        runtimeService.activateProcessInstanceById(processInstanceId);
    }

    /**
     * 客户验证工单处理结果
     */
    public void verifyTicket(String taskId, boolean approved) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("approved", approved);
        taskService.complete(taskId, variables);
    }

    /**
     * 提交满意度评价
     */
    public void submitSatisfactionSurvey(String taskId, int rating, String comment) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("satisfaction", rating);
        variables.put("comment", comment);
        taskService.complete(taskId, variables);
    }

    /**
     * 主管审核
     */
    public void supervisorReview(String taskId, boolean reassign) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("reassign", reassign);
        taskService.complete(taskId, variables);
    }

    /**
     * 查询待处理的工单任务
     */
    public List<Task> queryPendingTasks(String processInstanceId) {
        return taskService.createTaskQuery()
            .processInstanceId(processInstanceId)
            .active()
            .orderByTaskCreateTime()
            .desc()
            .list();
    }

    /**
     * 查询组任务（如经理组的任务）
     */
    public List<Task> queryGroupTasks(String groupId) {
        return taskService.createTaskQuery()
            .processDefinitionKey(TicketConsts.TICKET_PROCESS_KEY)
            .taskCandidateGroup(groupId)
            .active()
            .list();
    }

    /**
     * 处理SLA超时
     */
    public void handleSLABreach(String processInstanceId) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("slaBreached", true);
        runtimeService.setVariables(processInstanceId, variables);
    }

    /**
     * 查询超时的工单任务
     */
    public List<Task> queryOverdueTasks() {
        return taskService.createTaskQuery()
            .processDefinitionKey(TicketConsts.TICKET_PROCESS_KEY)
            .taskDueBefore(new Date())
            .active()
            .list();
    }

    /**
     * 查询主管审核任务
     */
    public List<Task> querySupervisorTasks() {
        return taskService.createTaskQuery()
            .processDefinitionKey(TicketConsts.TICKET_PROCESS_KEY)
            .taskCandidateGroup("supervisors")
            .active()
            .list();
    }

    /**
     * 设置任务到期时间
     */
    public void setTaskDueDate(String taskId, Date dueDate) {
        taskService.setDueDate(taskId, dueDate);
    }

    /**
     * 分配工单给工作组
     */
    public void assignTicketToWorkgroup(String taskId, String workgroupId) {
        // 添加工作组作为候选组
        // assignmentService.addCandidateGroup(taskId, workgroupId);
        
        // 获取工作组成员作为候选人
        // List<String> workgroupMembers = assignmentService.getWorkgroupUsers(workgroupId);
        // for (String userId : workgroupMembers) {
        //     assignmentService.addCandidateUser(taskId, userId);
        // }
    }

    /**
     * 查询用户可处理的所有任务（包括个人任务和组任务）
     */
    public List<Task> queryUserAvailableTasks(String userId) {
        // 获取用户所在的工作组
        // List<String> userWorkgroups = assignmentService.getUserWorkgroups(userId);
        
        // return taskService.createTaskQuery()
        //     .processDefinitionKey(TicketConsts.TICKET_PROCESS_KEY)
        //     .or()
        //         .taskAssignee(userId)
        //         .taskCandidateUser(userId)
        //         .taskCandidateGroupIn(userWorkgroups)
        //     .endOr()
        //     .active()
        //     .orderByTaskCreateTime()
        //     .desc()
        //     .list();

        return null;
    }

    /**
     * 认领任务
     */
    public void claimTask(String taskId, String userId) {
        taskService.claim(taskId, userId);
    }

    /**
     * 取消认领任务
     */
    public void unclaimTask(String taskId) {
        taskService.unclaim(taskId);
    }
} 