package com.bytedesk.ticket.ticket.listener;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TicketExecutionListener implements ExecutionListener {
    
    private static final long serialVersionUID = 1L;

    @Override
    public void notify(DelegateExecution execution) {
        String eventName = execution.getEventName();
        String processInstanceId = execution.getProcessInstanceId();
        
        log.info("Execution event: {}, processInstanceId: {}", eventName, processInstanceId);

        switch (eventName) {
            case EVENTNAME_START:
                handleProcessStart(execution);
                break;
            case EVENTNAME_END:
                handleProcessEnd(execution);
                break;
            case EVENTNAME_TAKE:
                handleProcessTake(execution);
                break;
            default:
                log.warn("Unhandled execution event: {}", eventName);
                break;
        }
    }

    private void handleProcessStart(DelegateExecution execution) {
        log.info("Process started: {}", execution.getProcessInstanceId());
        // TODO: 流程开始时的处理逻辑
    }

    private void handleProcessEnd(DelegateExecution execution) {
        log.info("Process ended: {}", execution.getProcessInstanceId());
        // TODO: 流程结束时的处理逻辑
    }

    private void handleProcessTake(DelegateExecution execution) {
        log.info("Process transition taken: {} -> {}", 
            execution.getCurrentActivityId(), 
            execution.getCurrentActivityName());
        // TODO: 流程流转时的处理逻辑
    }
} 