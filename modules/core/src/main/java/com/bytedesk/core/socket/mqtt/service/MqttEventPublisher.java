/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-10-13 10:20:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.socket.mqtt.service;

import org.springframework.stereotype.Service;

import com.bytedesk.core.config.BytedeskEventPublisher;

import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
@Service
@AllArgsConstructor
public class MqttEventPublisher {

  private final BytedeskEventPublisher bytedeskEventPublisher;

  public void publishMqttConnectedEvent(String clientId) {
    bytedeskEventPublisher.publishMqttConnectedEvent(clientId);
  }

  public void publishMqttDisconnectedEvent(String clientId) {
    bytedeskEventPublisher.publishMqttDisconnectedEvent(clientId);
  }

  public void publishMqttSubscribeEvent(String topic, String clientId) {
    bytedeskEventPublisher.publishMqttSubscribeEvent(topic, clientId);
  }

  public void publishMqttUnsubscribeEvent(String topic, String clientId) {
    bytedeskEventPublisher.publishMqttUnsubscribeEvent(topic, clientId);
  }

}
