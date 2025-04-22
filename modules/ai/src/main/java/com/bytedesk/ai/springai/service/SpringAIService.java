/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-26 14:48:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-22 17:43:56
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.core.message.MessageProtobuf;

/**
 * AI服务接口
 * 定义所有AI服务的通用方法
 */
public interface SpringAIService {

    /**
     * 发送websocket消息
     * @param query 用户查询
     * @param robot 机器人实体
     * @param messageProtobuf 消息协议
     */
    void sendWebsocketMessage(String query, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,  MessageProtobuf messageProtobufReply);
    
    /**
     * 发送Sse消息
     * @param message 消息
     * @param emitter SseEmitter
     */
    void sendSseMessage(String query, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,  MessageProtobuf messageProtobufReply, SseEmitter emitter);

    // 给访客使用
    // void sendSseVisitorMessage(String query, RobotEntity robot, MessageProtobuf messageProtobuf, SseEmitter emitter);
    
    /**
     * 异步生成FAQ对
     * @param chunk 文本块
     * @return 生成的FAQ对
     */
    String generateFaqPairsAsync(String chunk);

    /**
     * 同步生成FAQ对
     * @param chunk 文本块
     */
    void generateFaqPairsSync(String chunk);

    // void persistMessage(String messageJson);
    void persistMessage(MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply);

}
