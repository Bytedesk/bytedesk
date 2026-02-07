/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-02-05 10:40:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-02-05 10:40:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.mq.impl;

import java.util.Map;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.stereotype.Component;

import com.bytedesk.core.mq.MqSendOptions;
import com.bytedesk.core.mq.MqSender;

import jakarta.jms.JMSException;

@Component
@ConditionalOnProperty(name = "bytedesk.mq.type", havingValue = "artemis", matchIfMissing = true)
public class ArtemisMqSender implements MqSender {

    private final JmsTemplate jmsTemplate;

    public ArtemisMqSender(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    public void send(String destination, Object payload, MqSendOptions options) {
        MessagePostProcessor postProcessor = jmsMessage -> {
            if (options != null) {
                if (options.getDelayMs() != null && options.getDelayMs() > 0) {
                    long deliveryTime = System.currentTimeMillis() + options.getDelayMs();
                    jmsMessage.setLongProperty("_AMQ_SCHED_DELIVERY", deliveryTime);
                }
                if (options.getPriority() != null) {
                    jmsMessage.setJMSPriority(options.getPriority());
                }
                applyHeaders(jmsMessage, options.getHeadersOrEmpty());
            }
            return jmsMessage;
        };

        jmsTemplate.convertAndSend(destination, payload, postProcessor);
    }

    private void applyHeaders(jakarta.jms.Message jmsMessage, Map<String, Object> headers) throws JMSException {
        for (Map.Entry<String, Object> entry : headers.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String stringValue) {
                jmsMessage.setStringProperty(key, stringValue);
            } else if (value instanceof Integer intValue) {
                jmsMessage.setIntProperty(key, intValue);
            } else if (value instanceof Long longValue) {
                jmsMessage.setLongProperty(key, longValue);
            } else if (value instanceof Boolean booleanValue) {
                jmsMessage.setBooleanProperty(key, booleanValue);
            } else if (value != null) {
                jmsMessage.setObjectProperty(key, value);
            }
        }
    }
}