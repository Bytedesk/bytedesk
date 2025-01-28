package com.bytedesk.ticket.ticket.listener;

import org.flowable.task.service.delegate.CandidateUserListener;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TicketCandidateListener implements CandidateUserListener {
    
    @Override
    public void onAdd(DelegateTask delegateTask, String candidateUser) {
        String taskId = delegateTask.getId();
        log.info("Candidate user added: {} to task {}", candidateUser, taskId);
        // TODO: 处理添加候选人事件
        // 例如：发送通知给新候选人
    }

    @Override
    public void onDelete(DelegateTask delegateTask, String candidateUser) {
        String taskId = delegateTask.getId();
        log.info("Candidate user removed: {} from task {}", candidateUser, taskId);
        // TODO: 处理删除候选人事件
        // 例如：发送通知给被移除的候选人
    }
} 