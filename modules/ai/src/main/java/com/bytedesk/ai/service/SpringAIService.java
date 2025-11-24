/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-26 14:48:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-12 10:43:22
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.service;

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
     * @param messageRequest 消息协议
     */
    void sendWebsocketMessage(String query, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,  MessageProtobuf messageProtobufReply);
    
    /**
     * 发送Sse消息
     * @param messageRequest 消息
     * @param emitter SseEmitter
     */
    void sendSseMessage(String query, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,  MessageProtobuf messageProtobufReply, SseEmitter emitter);
    /**
     * 发送同步消息并返回回复内容
     * @param query 用户查询
     * @param robot 机器人实体
     * @param messageProtobufQuery 查询消息
     * @param messageProtobufReply 回复消息
     * @return 回复内容
     */
    String sendSyncMessage(String query, RobotProtobuf robot, MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply);
    
    /**
     * 直接处理LLM请求并同步返回结果，支持控制是否查询知识库
     * 主要用于RobotAgentService中的processLlmRequest方法
     * 
     * @param query 用户查询
     * @param robot 机器人配置
     * @param searchKnowledgeBase 是否需要查询知识库
     * @return 大模型生成的回复内容
     */
    String processSyncRequest(String query, RobotProtobuf robot, boolean searchKnowledgeBase);

    /**
     * 消息持久化
     * 用于保存用户查询和机器人回复的消息记录
     * @param messageProtobufQuery
     * @param messageProtobufReply
     * @param isUnanswered
     */
    // void persistMessage(MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply, Boolean isUnanswered);

}
