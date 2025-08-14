/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-15 14:56:08
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-18 15:45:15
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.jms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.bytedesk.core.message.MessageProtobuf;

@Service
public class JmsArtemisService {
    
    @Autowired
    private JmsTemplate jmsTemplate;

    public void send(String destination, String message) {
        jmsTemplate.convertAndSend(destination, message);
    }

    public void sendQueueMessage(String json) {
        jmsTemplate.convertAndSend(JmsArtemisConsts.QUEUE_STRING_NAME, json);
    }

    public void sendTopicMessage(String json) {
        jmsTemplate.convertAndSend(JmsArtemisConsts.TOPIC_STRING_NAME, json);
    }

    public void sendQueueMessage(MessageProtobuf message) {
        jmsTemplate.convertAndSend(JmsArtemisConsts.QUEUE_MESSAGE_NAME, message);
    }

    public void sendTopicMessage(MessageProtobuf message) {
        jmsTemplate.convertAndSend(JmsArtemisConsts.TOPIC_MESSAGE_NAME, message);
    }

    public void testQueue() {
        jmsTemplate.convertAndSend(JmsArtemisConsts.QUEUE_TEST_NAME, new JmsArtemisMessage("bytedesk@example.com", "HelloWorld"));
    }

    public void testTopic() {
        jmsTemplate.convertAndSend(JmsArtemisConsts.TOPIC_TEST_NAME, new JmsArtemisMessage("bytedesk@example.com", "HelloWorld"));
    }

}
