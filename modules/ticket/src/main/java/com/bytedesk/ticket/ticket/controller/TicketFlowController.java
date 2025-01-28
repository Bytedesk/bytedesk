package com.bytedesk.ticket.ticket.controller;

import java.util.List;
import java.util.Map;

import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.task.api.Task;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bytedesk.ticket.ticket.TicketEntity;
import com.bytedesk.ticket.ticket.service.TicketFlowService;

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

    @PostMapping("/start")
    @Operation(summary = "启动工单流程")
    public ResponseEntity<Void> startProcess(@RequestBody TicketEntity ticket) {
        log.info("start process for ticket: {}", ticket.getId());
        ticketFlowService.startTicketProcess(ticket);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/task/{taskId}/assign")
    @Operation(summary = "分配工单任务")
    public ResponseEntity<Void> assignTask(
            @PathVariable String taskId,
            @RequestParam String assignee) {
        ticketFlowService.assignTicketTask(taskId, assignee);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/task/{taskId}/complete")
    @Operation(summary = "完成工单任务")
    public ResponseEntity<Void> completeTask(
            @PathVariable String taskId,
            @RequestBody Map<String, Object> variables) {
        ticketFlowService.completeTicketTask(taskId, variables);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/task/{taskId}/comment")
    @Operation(summary = "添加工单评论")
    public ResponseEntity<Void> addComment(
            @PathVariable String taskId,
            @RequestParam String message) {
        ticketFlowService.addTicketComment(taskId, message);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/tasks")
    @Operation(summary = "查询用户的工单任务")
    public ResponseEntity<List<Task>> getUserTasks(@RequestParam String assignee) {
        List<Task> tasks = ticketFlowService.queryUserTasks(assignee);
        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/deploy")
    @Operation(summary = "部署工单流程")
    public ResponseEntity<Void> deployProcess() {
        ticketFlowService.deployTicketProcess();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/definition/latest")
    @Operation(summary = "获取最新的流程定义")
    public ResponseEntity<ProcessDefinition> getLatestDefinition() {
        ProcessDefinition definition = ticketFlowService.getLatestProcessDefinition();
        return ResponseEntity.ok(definition);
    }

    @GetMapping("/history/{ticketId}")
    @Operation(summary = "查询工单历史")
    public ResponseEntity<List<HistoricProcessInstance>> getHistory(@PathVariable String ticketId) {
        List<HistoricProcessInstance> history = ticketFlowService.queryTicketHistory(ticketId);
        return ResponseEntity.ok(history);
    }

    @DeleteMapping("/process/{processInstanceId}")
    @Operation(summary = "终止工单流程")
    public ResponseEntity<Void> terminateProcess(
            @PathVariable String processInstanceId,
            @RequestParam String reason) {
        ticketFlowService.terminateTicketProcess(processInstanceId, reason);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/process/{processInstanceId}/suspend")
    @Operation(summary = "挂起工单流程")
    public ResponseEntity<Void> suspendProcess(@PathVariable String processInstanceId) {
        ticketFlowService.suspendTicketProcess(processInstanceId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/process/{processInstanceId}/activate")
    @Operation(summary = "激活工单流程")
    public ResponseEntity<Void> activateProcess(@PathVariable String processInstanceId) {
        ticketFlowService.activateTicketProcess(processInstanceId);
        return ResponseEntity.ok().build();
    }
} 