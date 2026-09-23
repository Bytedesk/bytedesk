/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-20 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_feishu;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.bytedesk.kbase.llm_feishu.event.FeishuDocCreateEvent;
import com.bytedesk.kbase.llm_feishu.event.FeishuDocDeleteEvent;
import com.bytedesk.kbase.llm_feishu.event.FeishuDocUpdateDocEvent;
import com.bytedesk.kbase.llm_feishu.mq.FeishuDocMessageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AFTER_COMMIT 事件监听：索引消息在事务提交后发送，消费者不会读到未提交数据；
 * fallbackExecution=true 兜底无事务上下文的场景。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FeishuDocEventListener {

    private final FeishuDocMessageService feishuDocMessageService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onFeishuDocCreateEvent(FeishuDocCreateEvent event) {
        String uid = event.getDoc().getUid();
        log.info("FeishuDocEventListener onFeishuDocCreateEvent: {}", event.getDoc().getTitle());
        feishuDocMessageService.sendToIndexQueue(uid);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onFeishuDocUpdateDocEvent(FeishuDocUpdateDocEvent event) {
        String uid = event.getDoc().getUid();
        log.info("FeishuDocEventListener onFeishuDocUpdateDocEvent: {}", event.getDoc().getTitle());
        feishuDocMessageService.sendToIndexQueue(uid);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onFeishuDocDeleteEvent(FeishuDocDeleteEvent event) {
        String uid = event.getDoc().getUid();
        log.info("FeishuDocEventListener onFeishuDocDeleteEvent: {}", event.getDoc().getTitle());
        feishuDocMessageService.sendToDeleteQueue(uid);
    }
}
