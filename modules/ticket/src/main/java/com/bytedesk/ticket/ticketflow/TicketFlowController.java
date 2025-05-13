package com.bytedesk.ticket.ticketflow;

import java.util.List;
import java.util.Map;

import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.task.api.Task;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.ticket.comment.TicketCommentService;
import com.bytedesk.ticket.ticket.TicketEntity;

import org.flowable.engine.task.Comment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "工单流程管理接口")
@RequestMapping("/api/v1/ticket/flow")
public class TicketFlowController {

    private final TicketFlowService ticketFlowService;
    
    private final TicketCommentService commentService;

    @PostMapping("/start")
    @Operation(summary = "启动工单流程")
    public ResponseEntity<JsonResult<Boolean>> startProcess(@RequestBody TicketEntity ticket) {
        log.info("start process for ticket: {}", ticket.getId());
        ticketFlowService.startTicketProcess(ticket);
        return ResponseEntity.ok(JsonResult.success());
    }

    @PutMapping("/task/{taskId}/assign")
    @Operation(summary = "分配工单任务")
    public ResponseEntity<JsonResult<Boolean>> assignTask(
            @PathVariable String taskId,
            @RequestParam String assignee) {
        ticketFlowService.assignTicketTask(taskId, assignee);
        return ResponseEntity.ok(JsonResult.success());
    }

    @PutMapping("/task/{taskId}/complete")
    @Operation(summary = "完成工单任务")
    public ResponseEntity<JsonResult<Boolean>> completeTask(
            @PathVariable String taskId,
            @RequestBody Map<String, Object> variables) {
        ticketFlowService.completeTicketTask(taskId, variables);
        return ResponseEntity.ok(JsonResult.success());
    }

    @PostMapping("/task/{taskId}/comment")
    @Operation(summary = "添加工单评论")
    public ResponseEntity<JsonResult<Boolean>> addComment(
            @PathVariable String taskId,
            @RequestParam String message,
            @RequestParam String userId) {
        commentService.addComment(taskId, message, userId);
        return ResponseEntity.ok(JsonResult.success());
    }

    @GetMapping("/tasks")
    @Operation(summary = "查询用户的工单任务")
    public ResponseEntity<JsonResult<List<Task>>> getUserTasks(@RequestParam String assignee) {
        List<Task> tasks = ticketFlowService.queryUserTasks(assignee);
        return ResponseEntity.ok(JsonResult.success(tasks));
    }

    @PostMapping("/deploy")
    @Operation(summary = "部署工单流程")
    public ResponseEntity<JsonResult<Boolean>> deployProcess() {
        ticketFlowService.deployTicketProcess();
        return ResponseEntity.ok(JsonResult.success());
    }

    @GetMapping("/definition/latest")
    @Operation(summary = "获取最新的流程定义")
    public ResponseEntity<JsonResult<ProcessDefinition>> getLatestDefinition() {
        ProcessDefinition definition = ticketFlowService.getLatestProcessDefinition();
        return ResponseEntity.ok(JsonResult.success(definition));
    }

    @GetMapping("/history/{ticketId}")
    @Operation(summary = "查询工单历史")
    public ResponseEntity<JsonResult<List<HistoricProcessInstance>>> getHistory(@PathVariable String ticketId) {
        List<HistoricProcessInstance> history = ticketFlowService.queryTicketHistory(ticketId);
        return ResponseEntity.ok(JsonResult.success(history));
    }

    @DeleteMapping("/process/{processInstanceId}")
    @Operation(summary = "终止工单流程")
    public ResponseEntity<JsonResult<Boolean>> terminateProcess(
            @PathVariable String processInstanceId,
            @RequestParam String reason) {
        ticketFlowService.terminateTicketProcess(processInstanceId, reason);
        return ResponseEntity.ok(JsonResult.success());
    }

    @PutMapping("/process/{processInstanceId}/suspend")
    @Operation(summary = "挂起工单流程")
    public ResponseEntity<JsonResult<Boolean>> suspendProcess(@PathVariable String processInstanceId) {
        ticketFlowService.suspendTicketProcess(processInstanceId);
        return ResponseEntity.ok(JsonResult.success());
    }

    @PutMapping("/process/{processInstanceId}/activate")
    @Operation(summary = "激活工单流程")
    public ResponseEntity<JsonResult<Boolean>> activateProcess(@PathVariable String processInstanceId) {
        ticketFlowService.activateTicketProcess(processInstanceId);
        return ResponseEntity.ok(JsonResult.success());
    }

    @PutMapping("/task/{taskId}/verify")
    @Operation(summary = "客户验证工单处理结果")
    public ResponseEntity<JsonResult<Boolean>> verifyTicket(
            @PathVariable String taskId,
            @RequestParam boolean approved) {
        ticketFlowService.verifyTicket(taskId, approved);
        return ResponseEntity.ok(JsonResult.success());
    }

    @PutMapping("/task/{taskId}/survey")
    @Operation(summary = "提交满意度评价")
    public ResponseEntity<JsonResult<Boolean>> submitSurvey(
            @PathVariable String taskId,
            @RequestParam int rating,
            @RequestParam String comment) {
        ticketFlowService.submitSatisfactionSurvey(taskId, rating, comment);
        return ResponseEntity.ok(JsonResult.success());
    }

    @PutMapping("/task/{taskId}/review")
    @Operation(summary = "主管审核")
    public ResponseEntity<JsonResult<Boolean>> supervisorReview(
            @PathVariable String taskId,
            @RequestParam boolean reassign) {
        ticketFlowService.supervisorReview(taskId, reassign);
        return ResponseEntity.ok(JsonResult.success());
    }

    @GetMapping("/tasks/group/{groupId}")
    @Operation(summary = "查询组任务")
    public ResponseEntity<JsonResult<List<Task>>> getGroupTasks(
            @PathVariable String groupId) {
        List<Task> tasks = ticketFlowService.queryGroupTasks(groupId);
        return ResponseEntity.ok(JsonResult.success(tasks));
    }

    @GetMapping("/task/{taskId}/comments")
    @Operation(summary = "获取任务评论列表")
    public ResponseEntity<JsonResult<List<Comment>>> getTaskComments(
            @PathVariable String taskId) {
        List<Comment> comments = commentService.getTaskComments(taskId);
        return ResponseEntity.ok(JsonResult.success(comments));
    }

    @GetMapping("/process/{processInstanceId}/comments")
    @Operation(summary = "获取流程实例评论列表")
    public ResponseEntity<JsonResult<List<Comment>>> getProcessComments(
            @PathVariable String processInstanceId) {
        List<Comment> comments = commentService.getProcessInstanceComments(processInstanceId);
        return ResponseEntity.ok(JsonResult.success(comments));
    }
} 