/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-02 23:15:29
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-16 09:08:47
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket.delegate;

import org.flowable.engine.TaskService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.bytedesk.ticket.ticket.TicketConsts;
import com.bytedesk.ticket.ticket.TicketSLAService;
import com.bytedesk.ticket.ticket_sla.TicketSlaTypeEnum;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SLA超时通知
 * 
 * 两种服务任务的区别：
 * SLA 超时：自动触发，基于时间
 * 工单升级：人工触发，基于状态
 * 
 * 当 CUSTOMER_VERIFY SLA 超时时，自动通过验证（verified=true），使工单流程正常结束。
 */
@Slf4j
@Component("ticketSLATimeoutNotificationDelegate")
@RequiredArgsConstructor
public class TicketSLATimeoutNotificationDelegate implements JavaDelegate {

    private final TicketSLAService ticketSLAService;
    private final TaskService taskService;

    @Override
    public void execute(DelegateExecution execution) {
        String processInstanceId = execution.getProcessInstanceId();
        log.info("SLA timeout for process: {}", processInstanceId);
        
        // 获取流程变量
        String status = (String) execution.getVariable("status");
        Date startTime = (Date) execution.getVariable("startTime");
        String slaTime = (String) execution.getVariable("slaTime");
        String slaTypeValue = (String) execution.getVariable(TicketConsts.TICKET_VARIABLE_SLA_TYPE);
        String activityId = execution.getCurrentActivityId();
        TicketSlaTypeEnum slaType = resolveSlaType(slaTypeValue, activityId);
        
        // 设置 SLA 相关变量
        execution.setVariable("slaTimeoutTime", new Date());
        execution.setVariable("slaTimeoutReason", "超过处理时限");
        String taskDefinitionKey = resolveTaskDefinitionKey(activityId);
        boolean breached = ticketSLAService.markBreachedByNode(processInstanceId, taskDefinitionKey, "超过处理时限");
        if (!breached) {
            breached = ticketSLAService.markBreachedByProcessInstance(processInstanceId, slaType, "超过处理时限");
        }
        execution.setVariable("slaBreached", breached);
        execution.setVariable("slaBreachedType", breached ? slaType.name() : null);
        
        log.info("SLA timeout notification - processId: {}, status: {}, startTime: {}, slaTime: {}", 
            processInstanceId, status, startTime, slaTime);

        // 客户验证 SLA 超时：自动通过验证，使流程正常结束
        if (slaType == TicketSlaTypeEnum.CUSTOMER_VERIFY) {
            autoVerifyCustomerVerifyTask(execution, processInstanceId);
        }
    }

    /**
     * 客户验证 SLA 超时自动通过：找到 customerVerify 任务，设置 verified=true 并完成。
     */
    private void autoVerifyCustomerVerifyTask(DelegateExecution execution, String processInstanceId) {
        try {
            List<Task> activeTasks = taskService.createTaskQuery()
                    .processInstanceId(processInstanceId)
                    .active()
                    .list();
            if (activeTasks == null || activeTasks.isEmpty()) {
                log.warn("No active tasks found for auto-verify, processInstanceId={}", processInstanceId);
                return;
            }

            // 找到 customerVerify 任务（taskDefinitionKey 通常为 "customerVerify"）
            Task verifyTask = activeTasks.stream()
                    .filter(task -> task.getTaskDefinitionKey() != null
                            && task.getTaskDefinitionKey().toLowerCase().contains("verify"))
                    .findFirst()
                    .orElse(null);

            if (verifyTask == null) {
                log.warn("No verify task found in active tasks for auto-verify, processInstanceId={}", processInstanceId);
                return;
            }

            // 设置 verified=true 并完成任务
            Map<String, Object> variables = new HashMap<>();
            variables.put("verified", true);
            variables.put("slaAutoVerified", true);
            variables.put("slaAutoVerifiedTime", new Date());

            taskService.setVariables(verifyTask.getId(), variables);
            taskService.complete(verifyTask.getId(), variables);

            log.info("Auto-verified customerVerify task: taskId={}, processInstanceId={}",
                    verifyTask.getId(), processInstanceId);
        } catch (Exception e) {
            log.error("Failed to auto-verify customerVerify task, processInstanceId={}", processInstanceId, e);
        }
    }

    private TicketSlaTypeEnum resolveSlaType(String slaTypeValue, String activityId) {
        if (StringUtils.hasText(slaTypeValue)) {
            return TicketSlaTypeEnum.valueOf(slaTypeValue);
        }
        String normalizedActivityId = StringUtils.hasText(activityId) ? activityId.toLowerCase() : "";
        if (normalizedActivityId.contains("claim")) {
            return TicketSlaTypeEnum.CLAIM;
        }
        if (normalizedActivityId.contains("first_response") || normalizedActivityId.contains("firstresponse")) {
            return TicketSlaTypeEnum.FIRST_RESPONSE;
        }
        if (normalizedActivityId.contains("verify")) {
            return TicketSlaTypeEnum.CUSTOMER_VERIFY;
        }
        return TicketSlaTypeEnum.RESOLUTION;
    }

    private String resolveTaskDefinitionKey(String activityId) {
        if (!StringUtils.hasText(activityId)) {
            return activityId;
        }
        String marker = "_sla_timer";
        int markerIndex = activityId.indexOf(marker);
        if (markerIndex > 0) {
            return activityId.substring(0, markerIndex);
        }
        return activityId;
    }
}
