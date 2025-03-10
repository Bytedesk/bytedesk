/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-26 14:48:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-06 17:37:16
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.spring;

import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.core.message.MessageProtobuf;

/**
 * AI服务接口
 * 定义所有AI服务的通用方法
 */
public interface SpringAIService {

    /**
     * 发送基于知识库的消息
     * @param query 用户查询
     * @param robot 机器人实体
     * @param messageProtobuf 消息协议
     */
    void sendWsKbMessage(String query, RobotEntity robot, MessageProtobuf messageProtobuf);
    
    /**
     * 发送普通消息
     * @param query 用户查询
     * @param robotLlm 机器人LLM配置
     * @param messageProtobuf 消息协议
     */
    void sendWsMessage(String query, RobotLlm robotLlm, MessageProtobuf messageProtobuf);

    /**
     * 发送基于知识库的自动回复消息
     * @param query 用户查询
     * @param kbUid 知识库ID
     * @param messageProtobuf 消息协议
     */
    void sendWsKbAutoReply(String query, String kbUid, MessageProtobuf messageProtobuf);

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
}
