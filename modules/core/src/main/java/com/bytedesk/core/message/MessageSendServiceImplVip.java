/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-15 17:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-15 17:07:42
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.annotation.TabooJsonFilter;
import com.bytedesk.core.exception.TabooException;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.utils.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Service
public class MessageSendServiceImplVip implements IMessageSendService {

    // ...existing code...

    /**
     * 发送JSON消息
     */
    @Override
    @TabooJsonFilter(title = "taboo", action = "sendJsonMessage")
    public void sendJsonMessage(String messageJson) {
        // 敏感词过滤由AOP处理
        
        // 检查是否被标记为敏感内容
        if (TabooContext.isMessageBlocked()) {
            // 清除标记以避免影响后续请求
            TabooContext.clear();
            
            // 记录消息被拦截
            log.info("消息因包含敏感词已被拦截");
            
            // 可以选择向客户端发送一个系统通知
            try {
                sendSensitiveContentBlockedNotification(messageJson);
            } catch (Exception e) {
                log.error("发送敏感内容拦截通知失败", e);
            }
            
            // 直接返回，不继续处理原始消息
            return;
        }
        
        // 正常处理消息
        // ...existing message processing code...
    }
    
    /**
     * 发送敏感内容被拦截的通知
     */
    private void sendSensitiveContentBlockedNotification(String originalMessageJson) {
        try {
            // 解析原始消息以获取必要信息
            MessageProtobuf originalMessage = JSON.parseObject(originalMessageJson, MessageProtobuf.class);
            
            // 创建一个系统通知消息
            MessageProtobuf notificationMessage = new MessageProtobuf();
            notificationMessage.setType(MessageTypeEnum.SYSTEM);
            notificationMessage.setContent("您的消息包含敏感内容，已被系统拦截");
            notificationMessage.setStatus("SENT");
            notificationMessage.setCreatedAt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            notificationMessage.setClient("SYSTEM");
            notificationMessage.setExtra(originalMessage.getExtra());
            notificationMessage.setThread(originalMessage.getThread());
            notificationMessage.setUser(originalMessage.getUser());
            
            // 发送系统通知
            String notificationJson = JSON.toJSONString(notificationMessage);
            // 这里使用能直接发送消息而绕过敏感词检测的方法
            sendSystemMessage(notificationJson);
        } catch (Exception e) {
            log.error("创建敏感内容通知消息失败", e);
        }
    }
    
    /**
     * 发送系统消息（绕过敏感词检测）
     */
    private void sendSystemMessage(String messageJson) {
        // 实现直接发送系统消息的逻辑
        // 这里应当调用底层消息发送方法，而不是再次调用sendJsonMessage
        // ...implementation
    }

    // ...existing code...
}