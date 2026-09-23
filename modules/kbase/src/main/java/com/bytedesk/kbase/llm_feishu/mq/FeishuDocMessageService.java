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
package com.bytedesk.kbase.llm_feishu.mq;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.stereotype.Service;

import com.bytedesk.core.mq.jms.JmsArtemisConsts;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 飞书文档索引消息服务：仅 artemis MQ 模式下发送，与 FAQ 管道同构
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class FeishuDocMessageService {

    private final JmsTemplate jmsTemplate;

    @Value("${bytedesk.mq.type:artemis}")
    private String mqType;

    private boolean isArtemisEnabled() {
        return "artemis".equalsIgnoreCase(mqType);
    }

    public void sendToIndexQueue(String docUid) {
        if (!isArtemisEnabled()) {
            log.debug("当前MQ类型为{}，跳过JMS飞书文档索引发送: {}", mqType, docUid);
            return;
        }
        try {
            FeishuDocIndexMessage message = FeishuDocIndexMessage.builder()
                    .docUid(docUid)
                    .operationType("index")
                    .updateElasticIndex(true)
                    .updateVectorIndex(true)
                    .build();

            MessagePostProcessor postProcessor = jmsMessage -> {
                // 相同文档的消息按组串行，避免乱序
                jmsMessage.setStringProperty("JMSXGroupID", "feishu-doc-" + docUid);
                return jmsMessage;
            };

            jmsTemplate.convertAndSend(JmsArtemisConsts.QUEUE_FEISHU_DOC_INDEX, message, postProcessor);
        } catch (Exception e) {
            log.error("发送飞书文档索引消息失败: {}", e.getMessage(), e);
        }
    }

    public void sendToDeleteQueue(String docUid) {
        if (!isArtemisEnabled()) {
            log.debug("当前MQ类型为{}，跳过JMS飞书文档删除发送: {}", mqType, docUid);
            return;
        }
        try {
            FeishuDocIndexMessage message = FeishuDocIndexMessage.builder()
                    .docUid(docUid)
                    .operationType("delete")
                    .updateElasticIndex(true)
                    .updateVectorIndex(true)
                    .build();

            MessagePostProcessor postProcessor = jmsMessage -> {
                jmsMessage.setStringProperty("JMSXGroupID", "feishu-doc-" + docUid);
                jmsMessage.setJMSPriority(4);
                return jmsMessage;
            };

            jmsTemplate.convertAndSend(JmsArtemisConsts.QUEUE_FEISHU_DOC_INDEX, message, postProcessor);
        } catch (Exception e) {
            log.error("发送飞书文档删除消息失败: {}", e.getMessage(), e);
        }
    }
}
