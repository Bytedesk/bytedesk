/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-06 10:20:05
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-06 10:20:05
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.thread.delegate;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 流程执行监听器
 * 
 * 监听流程执行过程中的事件，记录日志
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
        
        log.info("流程执行事件: eventName={}, processInstanceId={}, activityId={}, activityName={}", 
                eventName, processInstanceId, activityId, activityName);
        
        // 记录关键流程变量
        if ("start".equals(eventName)) {
            // 流程开始
            log.info("流程开始执行: processInstanceId={}", processInstanceId);
        } else if ("end".equals(eventName)) {
            // 流程结束
            log.info("流程活动结束: processInstanceId={}, activityId={}", 
                    processInstanceId, activityId);
            
            // 如果是robotService活动结束，记录转人工决策相关变量
            if ("robotService".equals(activityId)) {
                Boolean needHumanService = (Boolean) execution.getVariable("needHumanService");
                Boolean visitorRequestedTransfer = (Boolean) execution.getVariable("visitorRequestedTransfer");
                log.info("机器人服务执行完成: needHumanService={}, visitorRequestedTransfer={}", 
                        needHumanService, visitorRequestedTransfer);
            }
        } else if ("take".equals(eventName)) {
            // 执行流程线
            String sourceId = execution.getVariable("_ACTIVITI_SKIP_EXPRESSION_ENABLED", String.class);
            String targetId = execution.getCurrentActivityId();
            log.info("流程执行流转: from={}, to={}", sourceId, targetId);
        }
    }
}
