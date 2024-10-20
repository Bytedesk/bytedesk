/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-15 18:13:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.socket.mqtt;

import org.springframework.stereotype.Service;

import com.bytedesk.core.config.BytedeskEventPublisher;
// import com.bytedesk.core.message.IMessageSendService;

import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
@Service
@AllArgsConstructor
public class MqService {

  private final BytedeskEventPublisher bytedeskEventPublisher;
  
  // private final IMessageSendService messageSendService;

  // public void sendJsonMessageToMq(String json) {
  //   // log.debug("sendMessageToMq: {}", json);
  //   // bytedeskEventPublisher.publishMessageJsonEvent(json);
  //   messageSendService.sendMessage(json);
  // }

  // public void sendProtoMessageToMq(byte[] messageBytes) {
  //   // log.debug("sendMessageToMq: {}", messageBytes);
  //   bytedeskEventPublisher.publishMessageProtoEvent(messageBytes);
  // }

  public void publishMqttConnectedEvent(String client) {
    bytedeskEventPublisher.publishMqttConnectedEvent(client);
  }

  public void publishMqttDisconnectedEvent(String client) {
    bytedeskEventPublisher.publishMqttDisconnectedEvent(client);
  }

  public void publishMqttSubscribeEvent(String topic, String clientId) {
    bytedeskEventPublisher.publishMqttSubscribeEvent(topic, clientId);
  }

  public void publishMqttUnsubscribeEvent(String topic, String clientId) {
    bytedeskEventPublisher.publishMqttUnsubscribeEvent(topic, clientId);
  }

}
