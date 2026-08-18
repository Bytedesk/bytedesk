/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-23 14:53:23
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-12-18 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket;

import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.ticket.ticket.event.TicketCreateEvent;
import com.bytedesk.ticket.ticket.event.TicketUpdateEvent;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TicketEntityListener {

    @PostPersist
    public void onPostPersist(TicketEntity ticket) {
        log.info("onPostPersist: {}", ticket.getUid());
        publishAfterCommit(new TicketCreateEvent(ticket));
    }

    @PostUpdate
    public void onPostUpdate(TicketEntity ticket) {
        log.info("onPostUpdate: {}", ticket.getUid());
        publishAfterCommit(new TicketUpdateEvent(ticket));
    }

    /**
     * 工单实体事件必须等创建/更新事务提交后再发布。
     *
     * <p>事件监听器是异步执行的（BytedeskEventPublisher 类级 @Async，运行在虚拟线程上）：
     * 若在事务提交前发布，监听器会在独立线程/独立事务中与创建者事务并发执行，
     * 读不到尚未提交的工单行（REPEATABLE_READ 快照），导致流程实例 ID 无法写回、
     * 自动分配被静默跳过；事务回滚时还会遗留孤儿流程实例。
     */
    private void publishAfterCommit(Object event) {
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    bytedeskEventPublisher.publishEvent(event);
                }
            });
        } else {
            // 无事务上下文（理论上不应发生）时退回直接发布
            bytedeskEventPublisher.publishEvent(event);
        }
    }
    
}
