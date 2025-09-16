/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-11 17:29:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-16 15:23:20
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bytedesk.ai.robot_message.RobotMessageUtils;
import com.bytedesk.ai.springai.service.SpringAIServiceRegistry;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.constant.LlmProviderConstants;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageService;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadProtobuf;
import com.bytedesk.core.thread.ThreadRestService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;

@Slf4j
@Service
@RequiredArgsConstructor
@Description("Robot Service - AI robot message processing and LLM integration service")
public class RobotService extends AbstractRobotService {

    private final SpringAIServiceRegistry springAIServiceRegistry;
    private final ThreadRestService threadRestService;
    private final MessageService messageService;
    private final RobotRestService robotRestService;

    @Override
    protected RobotRestService getRobotRestService() {
        return robotRestService;
    }

    @Override
    protected SpringAIServiceRegistry getSpringAIServiceRegistry() {
        return springAIServiceRegistry;
    }

    // 处理内部成员SSE请求消息
    public void processSseMemberMessage(String messageJson, SseEmitter emitter) {
        log.info("processSseMemberMessage: messageJson: {}", messageJson);
        Assert.notNull(emitter, "emitter is null");
        
        // 使用公共方法验证和解析消息
        MessageValidationResult validationResult = validateAndParseMessage(messageJson);
        String query = validationResult.getQuery();
        log.info("robot processSseMessage {}", query);
        
        // 使用公共方法获取机器人
        RobotProtobuf robot = getRobotByThreadTopic(validationResult.getThreadTopic());
        log.info("processSseMemberMessage thread reply");
        
        // 创建机器人回复消息
        MessageProtobuf messageProtobufReply = RobotMessageUtils.createRobotMessage(
                validationResult.getThreadProtobuf(), 
                robot,
                validationResult.getMessageProtobuf());
        
        // 处理LLM消息
        processAIWithFallback(query, robot, validationResult.getMessageProtobuf(), 
                messageProtobufReply, emitter);
    }

    // 处理访客端SSE请求消息
    public void processSseVisitorMessage(String messageJson, SseEmitter emitter) {
        log.info("processSseVisitorMessage: messageJson: {}", messageJson);
        Assert.notNull(emitter, "emitter is null");
        
        // 使用公共方法验证和解析消息
        MessageValidationResult validationResult = validateAndParseMessage(messageJson);
        String query = validationResult.getQuery();
        log.info("processSseVisitorMessage robot processSseMessage {}", query);
        
        // 使用公共方法获取机器人
        RobotProtobuf robot = getRobotByThreadTopic(validationResult.getThreadTopic());
        log.info("processSseVisitorMessage thread reply");
        
        // 机器人回复访客消息
        MessageProtobuf messageProtobufReply = RobotMessageUtils.createRobotMessage(
                validationResult.getThreadProtobuf(), 
                robot,
                validationResult.getMessageProtobuf());
        
        // 处理LLM消息
        processAIWithFallback(query, robot, validationResult.getMessageProtobuf(), 
                messageProtobufReply, emitter);
    }

    // 处理访客端同步请求消息，机器人设置为stream=false的情况，用于微信公众号等平台
    public MessageProtobuf processSyncVisitorMessage(String messageJson) {
        log.info("processSyncVisitorMessage: messageJson: {}", messageJson);
        
        // 使用公共方法验证和解析消息
        MessageValidationResult validationResult = validateAndParseMessage(messageJson);
        String query = validationResult.getQuery();
        log.info("processSyncVisitorMessage robot processSyncMessage {}", query);
        
        // 使用公共方法获取机器人
        RobotProtobuf robot = getRobotByThreadTopic(validationResult.getThreadTopic());
        log.info("processSyncVisitorMessage thread reply");
        
        // 机器人回复访客消息
        MessageProtobuf messageProtobufReply = RobotMessageUtils.createRobotMessage(
                validationResult.getThreadProtobuf(), 
                robot,
                validationResult.getMessageProtobuf());
        
        // 使用公共方法处理AI消息
        String aiResponse = processSyncAIWithFallback(query, robot, 
                validationResult.getMessageProtobuf(), messageProtobufReply);
        
        // 设置AI回复内容
        messageProtobufReply.setContent(aiResponse);
        return messageProtobufReply;
    }

    // ==================== 新增的 7 个服务方法 ====================

    /**
     * 备用回复服务 - 可能需要查询知识库作为备用答案
     */
    public String fallbackResponse(String content, String orgUid) {
        return processSyncRequest(RobotConsts.ROBOT_NAME_FALLBACK_RESPONSE, orgUid, content, 
                I18Consts.I18N_LLM_CONFIG_TIP, true);
    }

    /**
     * 查询重写服务 - 纯文本处理，不需要知识库
     */
    public String queryRewrite(String content, String orgUid) {
        return processSyncRequest(RobotConsts.ROBOT_NAME_QUERY_REWRITE, orgUid, content, 
                I18Consts.I18N_LLM_CONFIG_TIP, false);
    }

    /**
     * 摘要生成服务 - 纯文本处理，不需要知识库
     */
    public String summaryGeneration(String content, String orgUid) {
        return processSyncRequest(RobotConsts.ROBOT_NAME_SUMMARY_GENERATION, orgUid, content, 
                I18Consts.I18N_LLM_CONFIG_TIP, false);
    }

    /**
     * 会话标题生成服务 - 纯文本处理，不需要知识库
     */
    public String threadTitleGeneration(String content, String orgUid) {
        return processSyncRequest(RobotConsts.ROBOT_NAME_THREAD_TITLE_GENERATION, orgUid, content, 
                I18Consts.I18N_LLM_CONFIG_TIP, false);
    }

    /**
     * 上下文模板摘要服务 - 纯文本处理，不需要知识库
     */
    public String contextTemplateSummary(String content, String orgUid) {
        return processSyncRequest(RobotConsts.ROBOT_NAME_CONTEXT_TEMPLATE_SUMMARY, orgUid, content, 
                I18Consts.I18N_LLM_CONFIG_TIP, false);
    }

    /**
     * 实体提取服务 - 纯文本处理，不需要知识库
     */
    public String entityExtraction(String content, String orgUid) {
        return processSyncRequest(RobotConsts.ROBOT_NAME_ENTITY_EXTRACTION, orgUid, content, 
                I18Consts.I18N_LLM_CONFIG_TIP, false);
    }

    /**
     * 关系提取服务 - 纯文本处理，不需要知识库
     */
    public String relationshipExtraction(String content, String orgUid) {
        return processSyncRequest(RobotConsts.ROBOT_NAME_RELATIONSHIP_EXTRACTION, orgUid, content, 
                I18Consts.I18N_LLM_CONFIG_TIP, false);
    }

    /**
     * 问题建议服务 - 纯文本处理，不需要知识库
     */
    public String questionSuggest(String content, String orgUid) {
        return processSyncRequest(RobotConsts.ROBOT_NAME_QUESTION_SUGGEST, orgUid, content, 
                I18Consts.I18N_LLM_CONFIG_TIP, false);
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 消息验证和解析的通用方法
     */
    private static class MessageValidationResult {
        private final MessageProtobuf messageProtobuf;
        private final String query;
        private final ThreadProtobuf threadProtobuf;
        private final String threadTopic;

        public MessageValidationResult(MessageProtobuf messageProtobuf, String query, 
                ThreadProtobuf threadProtobuf, String threadTopic) {
            this.messageProtobuf = messageProtobuf;
            this.query = query;
            this.threadProtobuf = threadProtobuf;
            this.threadTopic = threadTopic;
        }

        public MessageProtobuf getMessageProtobuf() { return messageProtobuf; }
        public String getQuery() { return query; }
        public ThreadProtobuf getThreadProtobuf() { return threadProtobuf; }
        public String getThreadTopic() { return threadTopic; }
    }

    /**
     * 验证和解析消息的公共方法
     */
    private MessageValidationResult validateAndParseMessage(String messageJson) {
        Assert.notNull(messageJson, "messageJson is null");
        
        // 处理消息JSON
        String processedJson = messageService.processMessageJson(messageJson);
        MessageProtobuf messageProtobuf = MessageProtobuf.fromJson(processedJson);
        MessageTypeEnum messageType = messageProtobuf.getType();
        String query = messageProtobuf.getContent();
        
        ThreadProtobuf threadProtobuf = messageProtobuf.getThread();
        Assert.notNull(threadProtobuf, "thread is null");
        
        // 暂时仅支持文字消息类型，其他消息类型，大模型暂不处理。
        if (!messageType.equals(MessageTypeEnum.TEXT) &&
                !messageType.equals(MessageTypeEnum.ROBOT_QUESTION)) {
            throw new RuntimeException("暂不支持此消息类型");
        }
        
        String threadTopic = threadProtobuf.getTopic();
        return new MessageValidationResult(messageProtobuf, query, threadProtobuf, threadTopic);
    }

    /**
     * 获取机器人的公共方法
     */
    private RobotProtobuf getRobotByThreadTopic(String threadTopic) {
        ThreadEntity thread = threadRestService.findFirstByTopic(threadTopic)
                .orElseThrow(() -> new RuntimeException("thread with topic " + threadTopic + " not found"));
        Assert.notNull(thread.getRobot(), "thread robot is null, threadTopic:" + threadTopic);
        
        RobotProtobuf robot = RobotProtobuf.fromJson(thread.getRobot());
        if (robot == null) {
            throw new RuntimeException("robot is null, threadTopic:" + threadTopic);
        }
        
        return robot;
    }

    /**
     * AI提供商选择和处理的公共方法
     */
    private String getAIProviderName(RobotProtobuf robot) {
        String provider = LlmProviderConstants.ZHIPUAI;
        if (robot.getLlm() != null) {
            provider = robot.getLlm().getTextProvider().toLowerCase();
        }
        return provider;
    }

    /**
     * 通用的AI消息处理方法，包含fallback逻辑
     */
    private void processAIWithFallback(String query, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, SseEmitter emitter) {
        
        String provider = getAIProviderName(robot);
        
        try {
            // 使用SpringAIServiceRegistry获取对应的服务
            springAIServiceRegistry.getServiceByProviderName(provider)
                .sendSseMessage(query, robot, messageProtobufQuery, messageProtobufReply, emitter);
        } catch (IllegalArgumentException e) {
            log.warn("未找到AI服务提供商: {}, 使用默认提供商: {}", provider, LlmProviderConstants.ZHIPUAI);
            // 如果找不到指定的提供商，尝试使用默认的智谱AI
            try {
                springAIServiceRegistry.getServiceByProviderName(LlmProviderConstants.ZHIPUAI)
                    .sendSseMessage(query, robot, messageProtobufQuery, messageProtobufReply, emitter);
            } catch (Exception ex) {
                log.error("使用默认AI服务提供商失败", ex);
                throw new RuntimeException("无法处理AI消息，所有提供商服务均不可用");
            }
        }
    }

    /**
     * 通用的同步AI消息处理方法，包含fallback逻辑
     */
    private String processSyncAIWithFallback(String query, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply) {
        
        String provider = getAIProviderName(robot);
        
        try {
            // 使用SpringAIServiceRegistry获取对应的服务进行同步处理
            return springAIServiceRegistry.getServiceByProviderName(provider)
                .sendSyncMessage(query, robot, messageProtobufQuery, messageProtobufReply);
        } catch (IllegalArgumentException e) {
            log.warn("未找到AI服务提供商: {}, 使用默认提供商: {}", provider, LlmProviderConstants.ZHIPUAI);
            // 如果找不到指定的提供商，尝试使用默认的智谱AI
            try {
                return springAIServiceRegistry.getServiceByProviderName(LlmProviderConstants.ZHIPUAI)
                    .sendSyncMessage(query, robot, messageProtobufQuery, messageProtobufReply);
            } catch (Exception ex) {
                log.error("使用默认AI服务提供商失败", ex);
                throw new RuntimeException("无法处理AI消息，所有提供商服务均不可用");
            }
        }
    }

}
