/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-11 17:29:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-29 17:13:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bytedesk.ai.robot_message.RobotMessageUtils;
import com.bytedesk.ai.segment.SegmentService;
import com.bytedesk.ai.service.SseMessageHelper;
import com.bytedesk.ai.service.SpringAIServiceRegistry;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.llm.LlmProviderConstants;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageService;
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
    private final SegmentService segmentService;
    private final SseMessageHelper sseMessageHelper;

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
        RobotContext robotContext = resolveRobotContext(validationResult.getThreadTopic());
        RobotProtobuf robot = robotContext.robot();
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
        RobotContext robotContext = resolveRobotContext(validationResult.getThreadTopic());
        ThreadEntity threadEntity = robotContext.thread();
        RobotProtobuf robot = robotContext.robot();
        log.info("processSseVisitorMessage thread reply");

        // 机器人回复访客消息
        MessageProtobuf messageProtobufReply = RobotMessageUtils.createRobotMessage(
            validationResult.getThreadProtobuf(),
            robot,
            validationResult.getMessageProtobuf());

        if (shouldBypassRobotReply(threadEntity)) {
            log.info("Thread {} is transferring to agent, skip robot stream response", threadEntity.getUid());
            sendTransferInterceptionResponse(validationResult, messageProtobufReply, emitter);
            return;
        }

        // TODO: 影响回答速度，待完善后开启
        // 查询重写 + 分词扩展查询（原 Pipeline 逻辑合并至访客接口）
        // String rewritten = query;
        // try {
        //     if (robot != null && robot.getOrgUid() != null) {
        //         rewritten = queryRewrite(query, robot.getOrgUid());
        //     }
        // } catch (Exception e) {
        //     log.warn("query rewrite failed, fallback to original: {}", e.getMessage());
        // }
        // log.info("processSseVisitorMessage query {} rewritten: {}", query, rewritten);
        // // 分词扩展查询
        // List<String> tokens = preprocessAndSegment(rewritten);
        // log.info("processSseVisitorMessage tokens: {}", tokens);
        // String finalQuery = buildExpandedQuery(rewritten, tokens);  
        // log.info("processSseVisitorMessage finalQuery: {}", finalQuery);
        String finalQuery = query; // 访客端暂不做查询重写和分词扩展

        // 处理LLM消息（统一走fallback逻辑）
        processAIWithFallback(finalQuery, robot, validationResult.getMessageProtobuf(),
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
        RobotContext robotContext = resolveRobotContext(validationResult.getThreadTopic());
        RobotProtobuf robot = robotContext.robot();
        log.info("processSyncVisitorMessage thread reply");

        // 机器人回复访客消息
        MessageProtobuf messageProtobufReply = RobotMessageUtils.createRobotMessage(
                validationResult.getThreadProtobuf(),
                robot,
                validationResult.getMessageProtobuf());

        // 查询重写 + 分词扩展查询（原 Pipeline 逻辑合并至访客接口）
        String rewritten = query;
        try {
            if (robot != null && robot.getOrgUid() != null) {
                rewritten = queryRewrite(query, robot.getOrgUid());
            }
        } catch (Exception e) {
            log.warn("query rewrite failed, fallback to original: {}", e.getMessage());
        }
        List<String> tokens = preprocessAndSegment(rewritten);
        String finalQuery = buildExpandedQuery(rewritten, tokens);

        // 使用公共方法处理AI消息（统一走fallback逻辑）
        String aiResponse = processSyncAIWithFallback(finalQuery, robot,
                validationResult.getMessageProtobuf(), messageProtobufReply);

        // 设置AI回复内容
        messageProtobufReply.setContent(aiResponse);
        return messageProtobufReply;
    }

    // ==================== Pipeline 风格接口已移除：逻辑已并入访客接口 ====================

    private List<String> preprocessAndSegment(String content) {
        if (content == null || content.isBlank()) {
            return List.of();
        }
        // 使用 SegmentService 进行分词，并过滤标点符号，最小长度 1
        List<String> words = segmentService.segmentWords(content);
        return segmentService.filterWords(words, true, 1);
    }

    /**
     * 基于分词结果构建扩展查询，提升召回。
     * - 去重
     * - 限制最大拼接词数，避免过长
     */
    private String buildExpandedQuery(String base, List<String> tokens) {
        if (tokens == null || tokens.isEmpty()) {
            return base;
        }
        // 去重并限制最多 8 个词
        List<String> uniq = tokens.stream().distinct().limit(8).collect(Collectors.toList());
        String extra = String.join(" ", uniq);
        if (extra.isBlank()) {
            return base;
        }
        // 简单拼接，保留原文，提高召回（必要时可改为加权语法由底层模型/检索解析）
        return base + " " + extra;
    }

    /**
     * 查询重写服务 - 纯文本处理，不需要知识库
     */
    public String queryRewrite(String content, String orgUid) {
        try {
            validateParameters(content, orgUid);
            return processSyncRequest(RobotConsts.ROBOT_NAME_QUERY_REWRITE, orgUid, content,
                    I18Consts.I18N_LLM_CONFIG_TIP, false);
        } catch (Exception e) {
            log.error("Error in queryRewrite", e);
            throw new RuntimeException("Service error: " + e.getMessage());
        }
    }

    /**
     * 摘要生成服务 - 纯文本处理，不需要知识库
     */
    public String summaryGeneration(String content, String orgUid) {
        try {
            validateParameters(content, orgUid);
            return processSyncRequest(RobotConsts.ROBOT_NAME_SUMMARY_GENERATION, orgUid, content,
                    I18Consts.I18N_LLM_CONFIG_TIP, false);
        } catch (Exception e) {
            log.error("Error in summaryGeneration", e);
            throw new RuntimeException("Service error: " + e.getMessage());
        }
    }

    /**
     * 会话标题生成服务 - 纯文本处理，不需要知识库
     */
    public String threadTitleGeneration(String content, String orgUid) {
        try {
            validateParameters(content, orgUid);
            return processSyncRequest(RobotConsts.ROBOT_NAME_THREAD_TITLE_GENERATION, orgUid, content,
                    I18Consts.I18N_LLM_CONFIG_TIP, false);
        } catch (Exception e) {
            log.error("Error in threadTitleGeneration", e);
            throw new RuntimeException("Service error: " + e.getMessage());
        }
    }

    /**
     * 上下文模板摘要服务 - 纯文本处理，不需要知识库
     */
    public String contextTemplateSummary(String content, String orgUid) {
        try {
            validateParameters(content, orgUid);
            return processSyncRequest(RobotConsts.ROBOT_NAME_CONTEXT_TEMPLATE_SUMMARY, orgUid, content,
                    I18Consts.I18N_LLM_CONFIG_TIP, false);
        } catch (Exception e) {
            log.error("Error in contextTemplateSummary", e);
            throw new RuntimeException("Service error: " + e.getMessage());
        }
    }

    /**
     * OCR文字提取服务 - 图片文字识别，不需要知识库
     */
    public String ocrExtraction(String imageUrl, String orgUid) {
        try {
            if (!StringUtils.hasText(imageUrl)) {
                throw new IllegalArgumentException("Image URL cannot be null or empty");
            }
            if (!StringUtils.hasText(orgUid)) {
                throw new IllegalArgumentException("Organization UID cannot be null or empty");
            }
            
            // 创建包含图片的消息内容
            String content = "请提取图片中的文字内容"; // 基本提示词，实际提示词在robots.json中定义
            
            // 使用多模态处理，传入图片URL
            return processMultiModalSyncRequest(RobotConsts.ROBOT_NAME_OCR_EXTRACTION, orgUid, content, imageUrl,
                    I18Consts.I18N_LLM_CONFIG_TIP, false);
        } catch (Exception e) {
            log.error("Error in ocrExtraction for imageUrl: {}", imageUrl, e);
            throw new RuntimeException("OCR extraction service error: " + e.getMessage());
        }
    }

    /**
     * 实体提取服务 - 纯文本处理，不需要知识库
     */
    public String entityExtraction(String content, String orgUid) {
        try {
            validateParameters(content, orgUid);
            return processSyncRequest(RobotConsts.ROBOT_NAME_ENTITY_EXTRACTION, orgUid, content,
                    I18Consts.I18N_LLM_CONFIG_TIP, false);
        } catch (Exception e) {
            log.error("Error in entityExtraction", e);
            throw new RuntimeException("Service error: " + e.getMessage());
        }
    }

    /**
     * 关系提取服务 - 纯文本处理，不需要知识库
     */
    public String relationshipExtraction(String content, String orgUid) {
        try {
            validateParameters(content, orgUid);
            return processSyncRequest(RobotConsts.ROBOT_NAME_RELATIONSHIP_EXTRACTION, orgUid, content,
                    I18Consts.I18N_LLM_CONFIG_TIP, false);
        } catch (Exception e) {
            log.error("Error in relationshipExtraction", e);
            throw new RuntimeException("Service error: " + e.getMessage());
        }
    }

    /**
     * 问题建议服务 - 纯文本处理，不需要知识库
     */
    public String questionSuggest(String content, String orgUid) {
        return processSyncRequest(RobotConsts.ROBOT_NAME_QUESTION_SUGGEST, orgUid, content,
                I18Consts.I18N_LLM_CONFIG_TIP, false);
    }

    /**
     * 备用回复服务 - 可能需要查询知识库作为备用答案
     */
    public String fallbackResponse(String content, String orgUid) {
        try {
            validateParameters(content, orgUid);
            return processSyncRequest(RobotConsts.ROBOT_NAME_FALLBACK_RESPONSE, orgUid, content,
                    I18Consts.I18N_LLM_CONFIG_TIP, true);
        } catch (Exception e) {
            log.error("Error in fallbackResponse", e);
            throw new RuntimeException("Service error: " + e.getMessage());
        }
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 验证请求参数
     */
    private void validateParameters(String content, String orgUid) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("content and orgUid are required");
        }
        if (orgUid == null || orgUid.trim().isEmpty()) {
            throw new IllegalArgumentException("content and orgUid are required");
        }
    }

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

        public MessageProtobuf getMessageProtobuf() {
            return messageProtobuf;
        }

        public String getQuery() {
            return query;
        }

        public ThreadProtobuf getThreadProtobuf() {
            return threadProtobuf;
        }

        public String getThreadTopic() {
            return threadTopic;
        }
    }

    /**
     * 验证和解析消息的公共方法
     */
    private MessageValidationResult validateAndParseMessage(String messageJson) {
        Assert.notNull(messageJson, "messageJson is null");

        // 处理消息JSON
        String processedJson = messageService.processMessageJson(messageJson, true);
        MessageProtobuf messageProtobuf = MessageProtobuf.fromJson(processedJson);
        String query = messageProtobuf.getContent();

        ThreadProtobuf threadProtobuf = messageProtobuf.getThread();
        Assert.notNull(threadProtobuf, "thread is null");

        // MessageTypeEnum messageType = messageProtobuf.getType();
        // 已经上线多模态能力，暂时不限制消息类型
        // 暂时仅支持文字消息类型，其他消息类型，大模型暂不处理。
        // if (!messageType.equals(MessageTypeEnum.TEXT) &&
        //         !messageType.equals(MessageTypeEnum.ROBOT_QUESTION)) {
        //     throw new RuntimeException("暂不支持此消息类型");
        // }

        String threadTopic = threadProtobuf.getTopic();
        return new MessageValidationResult(messageProtobuf, query, threadProtobuf, threadTopic);
    }

    private RobotContext resolveRobotContext(String threadTopic) {
        ThreadEntity thread = getThreadByTopic(threadTopic);
        RobotProtobuf robot = buildRobotFromThread(thread, threadTopic);
        return new RobotContext(thread, robot);
    }

    private ThreadEntity getThreadByTopic(String threadTopic) {
        return threadRestService.findFirstByTopic(threadTopic)
                .orElseThrow(() -> new RuntimeException("thread with topic " + threadTopic + " not found"));
    }

    /**
     * 根据线程记录构建机器人信息
     */
    private RobotProtobuf buildRobotFromThread(ThreadEntity thread, String threadTopic) {
        Assert.notNull(thread.getRobot(), "thread robot is null, threadTopic:" + threadTopic);

        RobotProtobuf robotBasic = RobotProtobuf.fromJson(thread.getRobot());
        if (robotBasic == null || !StringUtils.hasText(robotBasic.getUid())) {
            throw new RuntimeException("robot uid is null, threadTopic:" + threadTopic);
        }

        try {
            RobotEntity robotEntity = robotRestService.findByUid(robotBasic.getUid()).orElse(null);
            if (robotEntity != null) {
                RobotProtobuf fullRobot = RobotProtobuf.fromEntity(robotEntity, robotBasic.getType());
                log.debug("Got full robot config from database for uid: {}", robotEntity.getUid());
                return fullRobot;
            }
        } catch (Exception e) {
            log.warn("Failed to get robot from database, fallback to thread data: {}", e.getMessage());
        }

        log.warn("Using robot data from thread for topic: {}", threadTopic);
        return robotBasic;
    }

    private boolean shouldBypassRobotReply(ThreadEntity thread) {
        if (thread == null || !thread.isWorkgroupType()) {
            return false;
        }
        if (hasAgentAssigned(thread)) {
            return true;
        }
        // String transferStatus = thread.getTransferStatus();
        // boolean transferActive = StringUtils.hasText(transferStatus)
        //         && !ThreadTransferStatusEnum.NONE.name().equals(transferStatus);
        // return transferActive || !thread.isRoboting();
        return !thread.isRoboting();
    }

    private boolean hasAgentAssigned(ThreadEntity thread) {
        if (thread == null) {
            return false;
        }
        String agentJson = thread.getAgent();
        return StringUtils.hasText(agentJson) && !BytedeskConsts.EMPTY_JSON_STRING.equals(agentJson);
    }

    private void sendTransferInterceptionResponse(
            MessageValidationResult validationResult,
            MessageProtobuf messageProtobufReply,
            SseEmitter emitter) {
        // 发送机器人转人工拦截提示消息，告知访客当前正在转人工处理中，可能会显示到转接成功欢迎语后面，暂不启用
        // sseMessageHelper.sendStreamMessage(
        //         validationResult.getMessageProtobuf(),
        //         messageProtobufReply,
        //         emitter,
        //         I18Consts.I18N_ROBOT_TO_AGENT_TIP,
        //         null,
        //         null,
        //         true,
        //         false,
        //         false);
        sseMessageHelper.sendStreamEndMessage(
                validationResult.getMessageProtobuf(),
                messageProtobufReply,
                emitter,
                0,
                0,
                0,
                null,
                LlmProviderConstants.ZHIPUAI,
            "robot-transfer-guard",
            false);
    }

    private record RobotContext(ThreadEntity thread, RobotProtobuf robot) {
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
