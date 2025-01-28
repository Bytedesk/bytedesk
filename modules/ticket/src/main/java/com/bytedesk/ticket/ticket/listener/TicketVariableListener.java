package com.bytedesk.ticket.ticket.listener;

import org.flowable.engine.delegate.VariableListener;
import org.flowable.variable.service.delegate.VariableListenerDelegate;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TicketVariableListener implements VariableListener {
    
    @Override
    public void onCreate(VariableListenerDelegate delegate) {
        String variableName = delegate.getVariableName();
        Object variableValue = delegate.getValue();
        log.info("Variable created: {} = {}", variableName, variableValue);
        // TODO: 处理变量创建事件
    }

    @Override
    public void onUpdate(VariableListenerDelegate delegate) {
        String variableName = delegate.getVariableName();
        Object oldValue = delegate.getOldValue();
        Object newValue = delegate.getValue();
        log.info("Variable updated: {} from {} to {}", variableName, oldValue, newValue);
        // TODO: 处理变量更新事件
    }

    @Override
    public void onDelete(VariableListenerDelegate delegate) {
        String variableName = delegate.getVariableName();
        Object value = delegate.getValue();
        log.info("Variable deleted: {} = {}", variableName, value);
        // TODO: 处理变量删除事件
    }
} 