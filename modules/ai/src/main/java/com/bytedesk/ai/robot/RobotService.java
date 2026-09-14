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
import java.util.Optional;
import java.util.UUID;
import java.time.ZonedDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bytedesk.ai.robot_message.RobotMessageUtils;
import com.bytedesk.ai.segment.SegmentService;
import com.bytedesk.ai.service.BaseSpringAIService;
import com.bytedesk.ai.service.RobotVoiceTranscriber;
import com.bytedesk.ai.service.SseMessageHelper;
import com.bytedesk.ai.service.SpringAIServiceRegistry;
import com.bytedesk.ai.service.SseMessageJsonConsumer;
import com.bytedesk.ai.service.SsePersistenceControl;
import com.bytedesk.ai.utils.ConvertAiUtils;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.llm.LlmProviderConstants;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.message.MessageService;
import com.bytedesk.core.message.content.AudioContent;
import com.bytedesk.core.message.content.RobotContent;
import com.bytedesk.core.message.content.VoiceContent;
import com.bytedesk.core.message.enums.MessageTypeEnum;
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

    /**
     * 语音识别失败提示（机器人开启 asrEnabled 但转写失败时下发，不静默吞掉）
     */
    private static final String VOICE_TRANSCRIPTION_FAILED_TIP = "语音识别失败，请稍后重试或改用文字提问";

    private final SpringAIServiceRegistry springAIServiceRegistry;
    private final ThreadRestService threadRestService;
    private final MessageService messageService;
    private final MessageRestService messageRestService;
    private final RobotRestService robotRestService;
    private final SegmentService segmentService;
    private final SseMessageHelper sseMessageHelper;

    /**
     * 可选注入：语音转写 SPI 实现位于 enterprise/ai（DashScopeRobotVoiceTranscriber）。
     * 社区版/未授权时 getIfAvailable() 返回 null，跳过转写不影响编译与运行。
     */
    private final ObjectProvider<RobotVoiceTranscriber> robotVoiceTranscriberProvider;

    @Override
    protected RobotRestService getRobotRestService() {
        return robotRestService;
    }

    @Override
    protected SpringAIServiceRegistry getSpringAIServiceRegistry() {
        return springAIServiceRegistry;
    }

    /**
     * 用指定 RobotEntity.uid 直接同步调用大模型（适合外部平台：飞书/公众号等）。
     *
     * <p>这是“无 Thread 上下文”的简单入口；更复杂的上下文/消息落库可在后续对齐 Thread/Message 管线。</p>
     */
    public String processSyncTextByRobotUid(String robotUid, String orgUid, String query, boolean searchKnowledgeBase) {
        Assert.hasText(robotUid, "robotUid is required");
        Assert.hasText(orgUid, "orgUid is required");
        Assert.hasText(query, "query is required");

        Optional<RobotEntity> robotOptional = robotRestService.findByUid(robotUid);
        if (robotOptional.isEmpty()) {
            throw new RuntimeException("robot not found by uid: " + robotUid);
        }

        RobotEntity robotEntity = robotOptional.get();
        if (StringUtils.hasText(robotEntity.getOrgUid()) && !orgUid.equals(robotEntity.getOrgUid())) {
            throw new RuntimeException("robot does not belong to orgUid: " + orgUid);
        }

        if (robotEntity.getSettings() == null || robotEntity.getLlm() == null
                || !StringUtils.hasText(robotEntity.getLlm().getTextProvider())) {
            throw new RuntimeException(I18Consts.I18N_LLM_CONFIG_TIP);
        }

        String provider = robotEntity.getLlm().getTextProvider();
        BaseSpringAIService service = (BaseSpringAIService) springAIServiceRegistry.getServiceByProviderName(provider);
        RobotProtobuf robot = ConvertAiUtils.convertToRobotProtobuf(robotEntity);
        return service.processSyncRequest(query, robot, searchKnowledgeBase);
    }

    /**
     * 用指定 RobotEntity.uid 流式生成并聚合为最终文本返回（适合外部平台：飞书/公众号等）。
     *
     * <p>内部复用现有 SSE 流式链路，但通过收集器聚合 ROBOT_STREAM 的 answer 分片，最终返回一段文本。</p>
     */
    public String processStreamTextByRobotUid(String robotUid, String orgUid, String query, boolean searchKnowledgeBase) {
        Assert.hasText(robotUid, "robotUid is required");
        Assert.hasText(orgUid, "orgUid is required");
        Assert.hasText(query, "query is required");

        Optional<RobotEntity> robotOptional = robotRestService.findByUid(robotUid);
        if (robotOptional.isEmpty()) {
            throw new RuntimeException("robot not found by uid: " + robotUid);
        }

        RobotEntity robotEntity = robotOptional.get();
        if (StringUtils.hasText(robotEntity.getOrgUid()) && !orgUid.equals(robotEntity.getOrgUid())) {
            throw new RuntimeException("robot does not belong to orgUid: " + orgUid);
        }

        if (robotEntity.getSettings() == null || robotEntity.getLlm() == null
                || !StringUtils.hasText(robotEntity.getLlm().getTextProvider())) {
            throw new RuntimeException(I18Consts.I18N_LLM_CONFIG_TIP);
        }

        String provider = robotEntity.getLlm().getTextProvider();
        BaseSpringAIService service = (BaseSpringAIService) springAIServiceRegistry.getServiceByProviderName(provider);
        RobotProtobuf robot = ConvertAiUtils.convertToRobotProtobuf(robotEntity);

        // 外部平台没有内部 thread 上下文，这里构造一个“虚拟 thread”，用于 prompt helper 读取历史消息时不 NPE。
        String seed = UUID.randomUUID().toString();
        ThreadProtobuf thread = ThreadProtobuf.builder()
                .uid("external-" + seed)
                .topic("external/" + orgUid + "/" + robotUid)
                .build();

        MessageProtobuf messageProtobufQuery = MessageProtobuf.builder()
                .uid("q-" + seed)
                .type(MessageTypeEnum.TEXT)
                .content(query)
                .thread(thread)
                .createdAt(ZonedDateTime.now())
                .build();

        MessageProtobuf messageProtobufReply = MessageProtobuf.builder()
                .uid("r-" + seed)
                .type(MessageTypeEnum.ROBOT_STREAM)
                .content("")
                .thread(thread)
                .createdAt(ZonedDateTime.now())
                .build();

        final long timeoutMs = 45_000L;
        CollectingSseEmitter collectingEmitter = new CollectingSseEmitter(timeoutMs);

        try {
            service.sendSseMessage(query, robot, messageProtobufQuery, messageProtobufReply, collectingEmitter);
            String aggregated = collectingEmitter.awaitText(timeoutMs);
            if (StringUtils.hasText(aggregated)) {
                return aggregated;
            }
            String errorText = collectingEmitter.getErrorText();
            if (StringUtils.hasText(errorText)) {
                return errorText;
            }
            // 兜底：流式未产出有效内容时回退同步
            return service.processSyncRequest(query, robot, searchKnowledgeBase);
        } catch (Exception e) {
            log.warn("processStreamTextByRobotUid failed, fallback to sync: {}", e.getMessage());
            return service.processSyncRequest(query, robot, searchKnowledgeBase);
        }
    }

    private static class CollectingSseEmitter extends SseEmitter implements SseMessageJsonConsumer, SsePersistenceControl {

        private final StringBuilder answerBuilder = new StringBuilder();
        private final CountDownLatch doneLatch = new CountDownLatch(1);
        private final AtomicReference<String> errorTextRef = new AtomicReference<>(null);

        CollectingSseEmitter(long timeoutMs) {
            super(timeoutMs);
            this.onCompletion(doneLatch::countDown);
            this.onTimeout(doneLatch::countDown);
            this.onError((ex) -> {
                if (ex != null && errorTextRef.get() == null) {
                    errorTextRef.compareAndSet(null, ex.getMessage());
                }
                doneLatch.countDown();
            });
        }

        @Override
        public boolean isPersistenceEnabled() {
            return false;
        }

        @Override
        public void acceptMessageJson(String messageJson) {
            if (!StringUtils.hasText(messageJson)) {
                return;
            }
            try {
                MessageProtobuf message = MessageProtobuf.fromJson(messageJson);
                if (message == null || message.getType() == null) {
                    return;
                }

                switch (message.getType()) {
                    case ROBOT_STREAM -> {
                        RobotContent rc = RobotContent.fromJson(message.getContent(), RobotContent.class);
                        if (rc != null && StringUtils.hasText(rc.getAnswer())) {
                            answerBuilder.append(rc.getAnswer());
                        }
                    }
                    case ROBOT_STREAM_END -> doneLatch.countDown();
                    case ROBOT_STREAM_ERROR -> {
                        RobotContent rc = RobotContent.fromJson(message.getContent(), RobotContent.class);
                        String err = rc != null ? rc.getAnswer() : null;
                        if (!StringUtils.hasText(err)) {
                            err = rc != null ? rc.getReasonContent() : null;
                        }
                        if (StringUtils.hasText(err)) {
                            errorTextRef.compareAndSet(null, err);
                        }
                        doneLatch.countDown();
                    }
                    default -> {
                        // ignore
                    }
                }
            } catch (Exception ignore) {
                // ignore parse errors
            }
        }

        String awaitText(long timeoutMs) {
            try {
                doneLatch.await(timeoutMs, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return answerBuilder.toString();
        }

        String getErrorText() {
            return errorTextRef.get();
        }
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
        if (threadEntity == null) {
            // getThreadByTopic 内部已 orElseThrow 保证非空，此处为防御性兜底：
            // record 访问器 thread() 无法向编译器空值分析证明非空
            throw new RuntimeException("thread entity is null, threadTopic: " + validationResult.getThreadTopic());
        }
        RobotProtobuf robot = robotContext.robot();
        log.info("processSseVisitorMessage thread reply");

        // 机器人回复访客消息
        MessageProtobuf messageProtobufReply = RobotMessageUtils.createRobotMessage(
            validationResult.getThreadProtobuf(),
            robot,
            validationResult.getMessageProtobuf());

        if (!shouldProcessVisitorSseMessage(validationResult.getMessageProtobuf(), query)) {
            log.info("Skip visitor SSE AI processing for messageType={}, threadUid={}, reason=empty-or-non-ai-query",
                    validationResult.getMessageProtobuf().getType(),
                    validationResult.getThreadProtobuf() != null ? validationResult.getThreadProtobuf().getUid() : null);
            sseMessageHelper.sendStreamEndMessage(
                    validationResult.getMessageProtobuf(),
                    messageProtobufReply,
                    emitter,
                    0,
                    0,
                    0,
                    null,
                    LlmProviderConstants.ZHIPUAI,
                    "visitor-sse-skip",
                    false);
            return;
        }

        // 语音消息自动 ASR（规划 §4.3）：VOICE/AUDIO 先转文字再送 LLM；
        // 未开启 ASR / 无转写实现 / 识别失败 → 跳过 AI 处理（修复 VoiceContent JSON 直送 LLM 缺陷，§3 G2）
        if (isVoiceOrAudioMessage(validationResult.getMessageProtobuf())) {
            VoiceQueryResult voiceQueryResult = transcribeVoiceQuery(
                    validationResult.getMessageProtobuf(), robot, threadEntity.getOrgUid(), emitter);
            if (!StringUtils.hasText(voiceQueryResult.text())) {
                if (voiceQueryResult.transcriptionFailed()) {
                    // asrEnabled 但识别失败：明确提示，不静默吞掉（规划 §4.3）
                    log.warn("Visitor voice transcription failed, send failure tip, threadUid={}",
                            validationResult.getThreadProtobuf() != null ? validationResult.getThreadProtobuf().getUid() : null);
                    sseMessageHelper.sendDefaultReplySse(query, VOICE_TRANSCRIPTION_FAILED_TIP, robot,
                            validationResult.getMessageProtobuf(), messageProtobufReply, emitter);
                    return;
                }
                log.info("Skip visitor SSE AI processing for voice message, threadUid={}, reason=asr-disabled-or-unavailable",
                        validationResult.getThreadProtobuf() != null ? validationResult.getThreadProtobuf().getUid() : null);
                sseMessageHelper.sendStreamEndMessage(
                        validationResult.getMessageProtobuf(),
                        messageProtobufReply,
                        emitter,
                        0,
                        0,
                        0,
                        null,
                        LlmProviderConstants.ZHIPUAI,
                        "visitor-sse-voice-skip",
                        false);
                return;
            }
            // 转写成功：以识别文本作为 query 送 LLM
            query = voiceQueryResult.text();
            log.info("Visitor voice message transcribed, uid={}, textLength={}",
                    validationResult.getMessageProtobuf().getUid(), query.length());
        }

        if (RobotUtils.shouldBypassRobotReply(threadEntity)) {
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

        // 语音消息自动 ASR（同步链路，如微信公众号）：VOICE/AUDIO 先转文字再送 LLM，
        // 未开启/无实现/失败则不送 LLM（返回空回复，修复 JSON 直送 LLM 缺陷，§3 G2）
        if (isVoiceOrAudioMessage(validationResult.getMessageProtobuf())) {
            VoiceQueryResult voiceQueryResult = transcribeVoiceQuery(
                    validationResult.getMessageProtobuf(), robot, robot != null ? robot.getOrgUid() : null, null);
            if (!StringUtils.hasText(voiceQueryResult.text())) {
                log.info("Skip sync visitor AI processing for voice message, threadUid={}, reason=asr-{}",
                        validationResult.getThreadProtobuf() != null ? validationResult.getThreadProtobuf().getUid() : null,
                        voiceQueryResult.transcriptionFailed() ? "failed" : "disabled-or-unavailable");
                if (voiceQueryResult.transcriptionFailed()) {
                    messageProtobufReply.setContent(VOICE_TRANSCRIPTION_FAILED_TIP);
                } else {
                    messageProtobufReply.setContent("");
                }
                return messageProtobufReply;
            }
            query = voiceQueryResult.text();
            log.info("Sync visitor voice message transcribed, uid={}, textLength={}",
                    validationResult.getMessageProtobuf().getUid(), query.length());
        }

        // 查询重写 + 分词扩展查询（原 Pipeline 逻辑合并至访客接口）
        String rewritten = query;
        // try {
        //     if (robot != null && robot.getOrgUid() != null) {
        //         rewritten = queryRewrite(query, robot.getOrgUid());
        //     }
        // } catch (Exception e) {
        //     log.warn("query rewrite failed, fallback to original: {}", e.getMessage());
        // }
        List<String> tokens = RobotUtils.preprocessAndSegment(rewritten, segmentService);
        String finalQuery = RobotUtils.buildExpandedQuery(rewritten, tokens);

        // 使用公共方法处理AI消息（统一走fallback逻辑）
        String aiResponse = processSyncAIWithFallback(finalQuery, robot,
                validationResult.getMessageProtobuf(), messageProtobufReply);

        // 设置AI回复内容
        messageProtobufReply.setContent(aiResponse);
        return messageProtobufReply;
    }

    // ==================== Pipeline 风格接口已移除：逻辑已并入访客接口 ====================

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
                RobotProtobuf fullRobot = ConvertAiUtils.convertToRobotProtobuf(robotEntity);
                if (StringUtils.hasText(robotBasic.getType())) {
                    fullRobot.setType(robotBasic.getType());
                }
                log.debug("Got full robot config from database for uid: {}", robotEntity.getUid());
                return fullRobot;
            }
        } catch (Exception e) {
            log.warn("Failed to get robot from database, fallback to thread data: {}", e.getMessage());
        }

        log.warn("Using robot data from thread for topic: {}", threadTopic);
        return robotBasic;
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

    private boolean shouldProcessVisitorSseMessage(MessageProtobuf messageProtobuf, String query) {
        if (messageProtobuf == null) {
            return false;
        }

        // 按消息类型过滤：参考前端 forceStomp 逻辑，以下类型不走 AI/SSE 机器人处理
        MessageTypeEnum messageType = messageProtobuf.getType();
        if (messageType == null) {
            return false;
        }

        switch (messageType) {
            case RATE:
            case GOODS:
            case ORDER:
            case FORM:
            case PREFORM:
                log.info("Skip visitor SSE AI processing for messageType={}, threadUid={}, reason=non-ai-message-type",
                        messageType,
                        messageProtobuf.getThread() != null ? messageProtobuf.getThread().getUid() : null);
                return false;
            default:
                break;
        }

        if (!StringUtils.hasText(query)) {
            log.warn("Skip visitor SSE message with empty query, type={}, uid={}",
                    messageType,
                    messageProtobuf.getUid());
            return false;
        }

        return true;
    }

    // ==================== 语音消息自动 ASR（规划 §4.3，G2 缺陷修复） ====================

    /**
     * 语音消息识别结果：text 非空 = 转写成功（以 text 作为 LLM query）；
     * text 为空 = 跳过 AI 处理（未开启 ASR / 无转写实现 / 识别失败）。
     */
    private record VoiceQueryResult(String text, boolean asrEnabled, boolean transcriptionFailed) {
    }

    private boolean isVoiceOrAudioMessage(MessageProtobuf messageProtobuf) {
        return messageProtobuf != null
                && (MessageTypeEnum.VOICE == messageProtobuf.getType()
                        || MessageTypeEnum.AUDIO == messageProtobuf.getType());
    }

    /**
     * 解析语音/音频消息中的文件 URL。
     */
    private String extractAudioFileUrl(MessageProtobuf messageProtobuf) {
        String content = messageProtobuf.getContent();
        if (!StringUtils.hasText(content)) {
            return null;
        }
        try {
            if (MessageTypeEnum.VOICE == messageProtobuf.getType()) {
                VoiceContent voiceContent = VoiceContent.fromJson(content);
                return voiceContent != null ? voiceContent.getUrl() : null;
            }
            AudioContent audioContent = AudioContent.fromJson(content);
            return audioContent != null ? audioContent.getUrl() : null;
        } catch (Exception e) {
            log.warn("Failed to parse voice/audio content json, uid={}, error={}",
                    messageProtobuf.getUid(), e.getMessage());
            return null;
        }
    }

    /**
     * 解析语音消息中前端已携带的识别结果（VoiceContent.text）。
     * 仅 VOICE 类型有 text 字段；AUDIO 消息返回 null。
     */
    private String extractPresetVoiceTranscript(MessageProtobuf messageProtobuf) {
        if (messageProtobuf == null || MessageTypeEnum.VOICE != messageProtobuf.getType()) {
            return null;
        }
        String content = messageProtobuf.getContent();
        if (!StringUtils.hasText(content)) {
            return null;
        }
        try {
            VoiceContent voiceContent = VoiceContent.fromJson(content);
            return voiceContent != null && StringUtils.hasText(voiceContent.getText())
                    ? voiceContent.getText().trim()
                    : null;
        } catch (Exception e) {
            log.warn("Failed to parse preset voice transcript, uid={}, error={}",
                    messageProtobuf.getUid(), e.getMessage());
            return null;
        }
    }

    /**
     * 语音消息自动转写：机器人开启 asrEnabled 且存在转写实现（企业版）时，
     * 先将录音转文字并把识别文本回写到消息内容（VoiceContent.text），
     * 再以文本作为 query 送大模型。
     *
     * 前端已携带识别结果（VoiceContent.text，如 SpeechInputModal 实时识别后发送）时
     * 直接复用，跳过二次 ASR（识别结果随消息内容已入库，无需回写）。
     *
     * 返回值语义见 {@link VoiceQueryResult}：
     * - 转写成功 → text 非空；
     * - 未开启 ASR / 无实现 / 无音频 URL / 识别失败 → text 为空（调用方跳过 AI 处理，
     *   修复现状 VoiceContent JSON 直送 LLM 的缺陷，规划 §3 G2）。
     */
    private VoiceQueryResult transcribeVoiceQuery(MessageProtobuf messageProtobuf, RobotProtobuf robot,
            String orgUid, SseEmitter emitter) {
        // 前端已携带识别结果：直接作为 query 送 LLM，避免二次识别（与客户端发文本消息同等信任级别）
        String presetTranscript = extractPresetVoiceTranscript(messageProtobuf);
        if (StringUtils.hasText(presetTranscript)) {
            log.info("Voice message already carries transcript text, skip re-transcription, uid={}, textLength={}",
                    messageProtobuf.getUid(), presetTranscript.length());
            return new VoiceQueryResult(presetTranscript, true, false);
        }

        if (robot == null || robot.getLlm() == null || !Boolean.TRUE.equals(robot.getLlm().getAsrEnabled())) {
            // 未开启语音识别：语音消息不送 LLM（行为修正，规划 §3 G2）
            log.info("Robot ASR disabled, skip AI processing for voice message, uid={}, threadUid={}",
                    messageProtobuf.getUid(),
                    messageProtobuf.getThread() != null ? messageProtobuf.getThread().getUid() : null);
            return new VoiceQueryResult(null, false, false);
        }

        RobotVoiceTranscriber transcriber = robotVoiceTranscriberProvider.getIfAvailable();
        if (transcriber == null) {
            // 社区版/未授权：无转写实现，跳过（不报错，行为与未开启一致）
            log.info("RobotVoiceTranscriber not available (community edition?), skip voice message, uid={}",
                    messageProtobuf.getUid());
            return new VoiceQueryResult(null, true, false);
        }

        String fileUrl = extractAudioFileUrl(messageProtobuf);
        if (!StringUtils.hasText(fileUrl)) {
            log.warn("Voice message has no audio url, skip transcription, uid={}", messageProtobuf.getUid());
            return new VoiceQueryResult(null, true, false);
        }

        String text;
        try {
            text = transcriber.transcribe(robot, orgUid, fileUrl);
        } catch (Exception e) {
            log.error("Robot voice transcription failed, uid={}, fileUrl={}, error={}",
                    messageProtobuf.getUid(), fileUrl, e.getMessage(), e);
            return new VoiceQueryResult(null, true, true);
        }
        if (!StringUtils.hasText(text)) {
            return new VoiceQueryResult(null, true, true);
        }

        // 转写成功：回写 VoiceContent.text 到 DB，并通过当前 SSE 连接下发「查询消息更新帧」，
        // 前端按 uid 去重原地更新语音气泡（规划 §4.3 方案 B，零新增推送基础设施）
        String updatedContent = writeBackVoiceText(messageProtobuf, text);
        if (updatedContent != null && emitter != null) {
            messageProtobuf.setContent(updatedContent);
            sseMessageHelper.sendQueryMessageUpdateFrame(messageProtobuf, emitter);
        }
        return new VoiceQueryResult(text, true, false);
    }

    /**
     * 将识别文本回写到消息内容（VoiceContent.text；AUDIO 消息无 text 字段则跳过回写），
     * 返回回写后的 content JSON；消息不存在或非 VOICE 类型返回 null。
     */
    private String writeBackVoiceText(MessageProtobuf messageProtobuf, String text) {
        try {
            if (!MessageTypeEnum.VOICE.equals(messageProtobuf.getType())
                    || !StringUtils.hasText(messageProtobuf.getUid())) {
                return null;
            }
            MessageEntity message = messageRestService.findByUid(messageProtobuf.getUid()).orElse(null);
            if (message == null) {
                log.warn("Voice message not found for text write-back, uid={}", messageProtobuf.getUid());
                return null;
            }
            if (!MessageTypeEnum.VOICE.name().equals(message.getType())) {
                return null;
            }
            VoiceContent voiceContent = VoiceContent.fromJson(message.getContent());
            if (voiceContent == null) {
                voiceContent = VoiceContent.builder().build();
            }
            voiceContent.setText(text);
            message.setContent(voiceContent.toJson());
            messageRestService.save(message);
            return message.getContent();
        } catch (Exception e) {
            log.warn("Failed to write back voice transcript text, uid={}, error={}",
                    messageProtobuf.getUid(), e.getMessage());
            return null;
        }
    }

    /**
     * 通用的AI消息处理方法，包含fallback逻辑
     */
    private void processAIWithFallback(String query, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, SseEmitter emitter) {

        String provider = RobotUtils.getAIProviderName(robot);

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

        String provider = RobotUtils.getAIProviderName(robot);

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
