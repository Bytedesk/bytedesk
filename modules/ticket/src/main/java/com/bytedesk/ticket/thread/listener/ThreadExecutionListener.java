/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-24 08:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-04 15:23:42
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.thread.listener;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 客服会话流程执行监听器
 * 
 * 监听客服会话流程的执行事件：
 * - 会话开始
 * - 会话结束
 * - 流程节点流转
 */
@Slf4j
@Component("threadExecutionListener")
public class ThreadExecutionListener implements ExecutionListener {
    
    private static final long serialVersionUID = 1L;

    @Override
    public void notify(DelegateExecution execution) {
        String eventName = execution.getEventName();
        String processInstanceId = execution.getProcessInstanceId();
        String activityId = execution.getCurrentActivityId();
        String activityName = execution.getCurrentActivityName();
        
        log.info("Thread execution event: {}, processInstanceId: {}, activityId: {}, activityName: {}", 
            eventName, processInstanceId, activityId, activityName);

        // 获取流程变量
        Map<String, Object> variables = execution.getVariables();
        log.debug("Thread process variables: {}", variables);

        switch (eventName) {
            case EVENTNAME_START:
                handleThreadStart(execution);
                break;
            case EVENTNAME_END:
                handleThreadEnd(execution);
                break;
            case EVENTNAME_TAKE:
                handleThreadTransition(execution);
                break;
            default:
                log.warn("Unhandled thread execution event: {}", eventName);
                break;
        }
    }

    private void handleThreadStart(DelegateExecution execution) {
        // 会话开始时的业务逻辑
        String userUid = (String) execution.getVariable("userUid");
        log.info("Thread started by visitor: {}", userUid);
        
        // 记录会话开始时间
        // execution.setVariable("threadStartTime", System.currentTimeMillis());
    }

    private void handleThreadEnd(DelegateExecution execution) {
        // 会话结束时的业务逻辑
        log.info("Thread ended: {}", execution.getProcessInstanceId());
        
        // try {
        //     // 记录会话结束时间
        //     safeSetVariable(execution, "threadEndTime", System.currentTimeMillis());
            
        //     // 计算会话总时长
        //     Long startTime = (Long) execution.getVariable("threadStartTime");
        //     if (startTime != null) {
        //         Long duration = System.currentTimeMillis() - startTime;
        //         safeSetVariable(execution, "threadDuration", duration);
        //         log.info("Thread duration: {} ms", duration);
        //     }
        // } catch (Exception e) {
        //     // 当流程已结束，变量无法设置时记录日志但不中断流程
        //     log.warn("无法在会话结束时设置流程变量: {}", e.getMessage());
        // }
    }

    /**
     * 安全地设置流程变量，避免在流程已结束时引发外键约束异常
     * 
     * @param execution 执行实例
     * @param execution 执行上下文
     */
    // private void safeSetVariable(DelegateExecution execution, String variableName, Object variableValue) {
    //     try {
    //         // 检查流程实例是否仍然活跃
    //         String executionId = execution.getId();
    //         String processInstanceId = execution.getProcessInstanceId();
            
    //         if (executionId == null || processInstanceId == null) {
    //             log.debug("无法设置变量 {}: 执行实例ID或流程实例ID为空", variableName);
    //             return;
    //         }
            
    //         // 额外检查：尝试获取当前活动ID，如果失败则说明执行已经结束
    //         try {
    //             String currentActivityId = execution.getCurrentActivityId();
    //             if (currentActivityId == null) {
    //                 log.debug("无法设置变量 {}: 当前活动ID为空，流程可能已结束", variableName);
    //                 return;
    //             }
    //         } catch (Exception e) {
    //             log.debug("无法设置变量 {}: 获取当前活动ID失败，流程可能已结束: {}", variableName, e.getMessage());
    //             return;
    //         }
            
    //         // 检查通过，设置变量
    //         execution.setVariable(variableName, variableValue);
    //         log.debug("成功设置变量 {} 为流程 {}", variableName, processInstanceId);
    //     } catch (Exception e) {
    //         log.debug("设置流程变量 {} 失败: {}", variableName, e.getMessage());
    //     }
    // }

    private void handleThreadTransition(DelegateExecution execution) {
        // 会话流转时的业务逻辑
        log.info("Thread transition from {} to {}", 
            execution.getCurrentActivityId(), 
            execution.getCurrentActivityName());
    }
}