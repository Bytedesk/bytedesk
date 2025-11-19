package com.bytedesk.ai.springai.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.ai.robot.RobotRestService;
import com.bytedesk.ai.robot_message.RobotMessageCache;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessagePersistCache;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.message.content.RobotContent;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kbase.llm_chunk.elastic.ChunkElasticService;
import com.bytedesk.kbase.llm_chunk.vector.ChunkVectorService;
import com.bytedesk.kbase.llm_faq.FaqProtobuf;
import com.bytedesk.kbase.llm_faq.elastic.FaqElasticService;
import com.bytedesk.kbase.llm_faq.vector.FaqVectorService;
import com.bytedesk.kbase.llm_text.elastic.TextElasticService;
import com.bytedesk.kbase.llm_text.vector.TextVectorService;
import com.bytedesk.kbase.llm_webpage.elastic.WebpageElasticService;
import com.bytedesk.kbase.llm_webpage.vector.WebpageVectorService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseSpringAIService implements SpringAIService {

    @Autowired
    protected FaqElasticService faqElasticService;

    @Autowired(required = false)
    protected FaqVectorService faqVectorService;

    @Autowired
    protected TextElasticService textElasticService;

    @Autowired(required = false)
    protected TextVectorService textVectorService;

    @Autowired
    protected ChunkElasticService chunkElasticService;

    @Autowired(required = false)
    protected ChunkVectorService chunkVectorService;

    @Autowired
    protected WebpageElasticService webpageElasticService;

    @Autowired(required = false)
    protected WebpageVectorService webpageVectorService;

    // @Autowired
    // protected ArticleElasticService articleElasticService;

    // @Autowired(required = false)
    // protected ArticleVectorService articleVectorService;

    @Autowired
    protected IMessageSendService messageSendService;

    @Autowired
    protected UidUtils uidUtils;

    @Autowired
    protected RobotRestService robotRestService;

    @Autowired
    protected ThreadRestService threadRestService;

    @Autowired
    protected MessagePersistCache messagePersistCache;

    @Autowired
    protected RobotMessageCache robotMessageCache;

    @Autowired
    protected MessageRestService messageRestService;

    @Autowired
    protected ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    protected KnowledgeBaseSearchHelper knowledgeBaseSearchHelper;

    @Autowired
    protected PromptHelper promptHelper;

    @Autowired
    protected MessagePersistenceHelper messagePersistenceHelper;

    @Autowired
    protected SseMessageHelper sseMessageHelper;

    // 保留一个无参构造函数，或者只接收特定的必需依赖
    protected BaseSpringAIService() {
        // 无参构造函数
    }

    // 1. 核心消息处理方法
    @Override
    public void sendWebsocketMessage(String query, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply) {
        // WebSocket 功能暂未启用，方法保留以满足接口约定
        log.info("sendWebsocketMessage is disabled temporarily; skipping WebSocket handling.");
        return;
    }

    @Override
    public void sendSseMessage(String query, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, SseEmitter emitter) {
        Assert.hasText(query, "Query must not be empty");
        Assert.notNull(emitter, "SseEmitter must not be null");

        boolean kbEnabled = StringUtils.hasText(robot.getKbUid()) && robot.getKbEnabled();

        // 知识库未启用:直接基于提示词走 LLM 流式
        if (!kbEnabled) {
            log.info("知识库未启用或未指定知识库UID");
            // 检查 LLM 是否配置
            if (robot.getLlm() == null) {
                log.error("robot.getLlm() 为 null,无法处理请求");
                String answer = I18Consts.I18N_ROBOT_PROCESSING_ERROR;
                String robotStreamContent = promptHelper.createRobotStreamContentAnswer(query, answer,
                        new ArrayList<>(), robot);
                sseMessageHelper.sendStreamMessage(
                        messageProtobufQuery,
                        messageProtobufReply,
                        emitter,
                        robotStreamContent,
                        null,
                        null,
                        true,
                        true,
                        true);
                return;
            } else if (robot.getLlm().getEnabled()) {
                // 开启大模型对话，且无知识库：根据配置决定是否使用 LLM 回答
                boolean useLlmWhenKbEmpty = robot.getLlm() != null
                        && Boolean.TRUE.equals(robot.getLlm().getUseLlmWhenKbEmpty());
                if (useLlmWhenKbEmpty) {
                    sseMessageHelper.processPromptSseWithContext(this, query, "", robot, messageProtobufQuery,
                            messageProtobufReply, new ArrayList<>(), emitter, "无知识库");
                } else {
                    // 配置为不使用 LLM：直接返回默认回复
                    sseMessageHelper.sendDefaultReplySse(query, robot, messageProtobufQuery, messageProtobufReply,
                            emitter);
                }
            } else {
                // 未开启大模型对话，且无知识库：直接返回默认回复并结束 SSE
                sseMessageHelper.sendDefaultReplySse(query, robot, messageProtobufQuery, messageProtobufReply,
                        emitter);
            }

            return;
        }

        boolean llmEnabled = robot.getLlm() != null && robot.getLlm().getEnabled();

        if (llmEnabled) {
            // 启用 LLM：聚合 KB 结果作为上下文提示词
            // SearchResultWithSources aggregated = knowledgeBaseSearchHelper
            //         .rerankMergeTopK(knowledgeBaseSearchHelper.searchKnowledgeBaseWithSources(query, robot), robot);
            SearchResultWithSources aggregated = knowledgeBaseSearchHelper.searchKnowledgeBaseWithSources(query, robot);
            List<FaqProtobuf> kbResults = aggregated.getSearchResults();
            List<RobotContent.SourceReference> sourceReferences = Boolean.TRUE.equals(robot.getKbSourceEnabled())
                    ? aggregated.getSourceReferences()
                    : new ArrayList<>();
            log.info("LLM 模式，KB 结果数 {}, 来源数 {}", kbResults.size(), sourceReferences.size());
            
            if (kbResults.isEmpty()) {
                // 未命中 KB：根据配置选择 默认回复 或 继续使用 LLM
                boolean useLlmWhenKbEmpty = robot.getLlm() != null
                        && Boolean.TRUE.equals(robot.getLlm().getUseLlmWhenKbEmpty());
                if (useLlmWhenKbEmpty) {
                    sseMessageHelper.processPromptSseWithContext(this, query, "", robot, messageProtobufQuery,
                            messageProtobufReply, new ArrayList<>(), emitter, "LLM+KB空");
                } else {
                    // 默认：返回默认回复(ROBOT_STREAM),并结束 SSE
                    sseMessageHelper.sendDefaultReplySse(query, robot, messageProtobufQuery, messageProtobufReply,
                            emitter);
                }
                return;
            }

            String context = promptHelper.buildContextFromFaqs(kbResults);
            sseMessageHelper.processPromptSseWithContext(this, query, context, robot, messageProtobufQuery,
                    messageProtobufReply, sourceReferences, emitter, "LLM+KB");
            return;
        }

        // 未启用 LLM：直接使用 KB 搜索结果回复（ROBOT_STREAM），补充来源
        // SearchResultWithSources aggregated = knowledgeBaseSearchHelper
        //         .rerankMergeTopK(knowledgeBaseSearchHelper.searchKnowledgeBaseWithSources(query, robot), robot);
        SearchResultWithSources aggregated = knowledgeBaseSearchHelper.searchKnowledgeBaseWithSources(query, robot);
        List<FaqProtobuf> kbResults = aggregated.getSearchResults();
        List<RobotContent.SourceReference> sourceReferences = Boolean.TRUE.equals(robot.getKbSourceEnabled())
                ? aggregated.getSourceReferences()
                : new ArrayList<>();

        boolean isUnanswered;
        String answer;
        if (kbResults.isEmpty()) {
            isUnanswered = true;
            answer = robot.getLlm() != null && robot.getLlm().getDefaultReply() != null
                    ? robot.getLlm().getDefaultReply()
                    : I18Consts.I18N_ROBOT_DEFAULT_REPLY;
        } else {
            FaqProtobuf firstFaq = kbResults.get(0);
            if (kbResults.size() > 1) {
                firstFaq.setRelatedFaqs(new ArrayList<>(kbResults.subList(1, kbResults.size())));
            }
            answer = firstFaq.toJson();
            isUnanswered = false;
        }

        StringBuilder contextBuilder = new StringBuilder();
        for (RobotContent.SourceReference source : sourceReferences) {
            contextBuilder.append("Source: ").append(source.getSourceName()).append("\n");
            contextBuilder.append("Content: ").append(source.getContentSummary()).append("\n\n");
        }

        RobotContent.RobotContentBuilder<?, ?> streamContentBuilder = RobotContent.builder()
                .question(query)
                .answer(answer)
                .regenerationContext(contextBuilder.toString())
                .kbUid(robot.getKbUid())
                .robotUid(robot.getUid());

        // 仅当显式开启来源展示时，才设置 sources
        if (Boolean.TRUE.equals(robot.getKbSourceEnabled())) {
            streamContentBuilder.sources(sourceReferences);
        }

        RobotContent streamContent = streamContentBuilder.build();
        sseMessageHelper.sendStreamMessage(
                messageProtobufQuery,
                messageProtobufReply,
                emitter,
                streamContent.toJson(),
                null,
                null,
                isUnanswered,
                true,
                true);
    }

    @Override
    public String sendSyncMessage(String query, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply) {
        Assert.hasText(query, "Query must not be empty");
        Assert.notNull(robot, "Robot must not be null");
        Assert.notNull(messageProtobufQuery, "MessageProtobufQuery must not be null");
        Assert.notNull(messageProtobufReply, "MessageProtobufReply must not be null");

        try {
            boolean kbEnabled = StringUtils.hasText(robot.getKbUid()) && robot.getKbEnabled();
            boolean llmEnabled = robot.getLlm() != null && robot.getLlm().getEnabled();

            // 知识库未启用但 LLM 启用：用空上下文直接走 LLM
            if (!kbEnabled && llmEnabled) {
                List<Message> messages = promptHelper.buildMessagesForSync(query, "", robot, messageProtobufQuery);
                Prompt aiPrompt = promptHelper.toPrompt(messages);
                String response = processPromptSync(aiPrompt.toString(), robot);
                PromptResult promptResult = new PromptResult(response, aiPrompt);

                // 用 StreamContent 包装同步回复
                RobotContent streamContent = RobotContent.builder()
                        .question(query)
                        .answer(promptResult.getResponse())
                        .kbUid(robot.getKbUid())
                        .robotUid(robot.getUid())
                        .build();

                messageProtobufReply.setContent(streamContent.toJson());
                messageProtobufReply.setType(MessageTypeEnum.ROBOT);

                String modelType = robot.getLlm() != null && robot.getLlm().getTextModel() != null
                        ? robot.getLlm().getTextModel()
                        : "";
                messagePersistenceHelper.persistMessage(messageProtobufQuery, messageProtobufReply, false, 0, 0, 0,
                        promptResult.getPrompt(), "", modelType);
                messageSendService.sendProtobufMessage(messageProtobufReply);
                return promptResult.getResponse();
            }

            // 其余场景需要 KB 结果
            List<FaqProtobuf> kbResults = knowledgeBaseSearchHelper.searchKnowledgeBase(query, robot);
            log.info("sendSyncMessage kbResults {}", kbResults.size());

            if (llmEnabled) {
                // LLM 启用：KB 为空→默认文本；KB 非空→带上下文调用 LLM
                if (kbResults.isEmpty()) {
                    boolean useLlmWhenKbEmpty = robot.getLlm() != null
                            && Boolean.TRUE.equals(robot.getLlm().getUseLlmWhenKbEmpty());
                    if (useLlmWhenKbEmpty) {
                        // 使用空上下文直接走 LLM
                        List<Message> messages = promptHelper.buildMessagesForSync(query, "", robot,
                                messageProtobufQuery);
                        Prompt aiPrompt = promptHelper.toPrompt(messages);
                        String response = processPromptSync(aiPrompt.toString(), robot);
                        PromptResult promptResult = new PromptResult(response, aiPrompt);

                        RobotContent streamContent = RobotContent.builder()
                                .question(query)
                                .answer(promptResult.getResponse())
                                .kbUid(robot.getKbUid())
                                .robotUid(robot.getUid())
                                .build();

                        messageProtobufReply.setContent(streamContent.toJson());
                        messageProtobufReply.setType(MessageTypeEnum.ROBOT);
                        String modelType = robot.getLlm() != null && robot.getLlm().getTextModel() != null
                                ? robot.getLlm().getTextModel()
                                : "";
                        messagePersistenceHelper.persistMessage(messageProtobufQuery, messageProtobufReply, false, 0, 0,
                                0,
                                promptResult.getPrompt(), "", modelType);
                        messageSendService.sendProtobufMessage(messageProtobufReply);
                        return promptResult.getResponse();
                    } else {
                        // 返回默认回复
                        String answer = robot.getLlm() != null && robot.getLlm().getDefaultReply() != null
                                ? robot.getLlm().getDefaultReply()
                                : I18Consts.I18N_ROBOT_DEFAULT_REPLY;
                        RobotContent streamContent = RobotContent.builder()
                                .question(query)
                                .answer(answer)
                                .kbUid(robot.getKbUid())
                                .robotUid(robot.getUid())
                                .build();
                        messageProtobufReply.setContent(streamContent.toJson());
                        messageProtobufReply.setType(MessageTypeEnum.ROBOT);
                        messagePersistenceHelper.persistMessage(messageProtobufQuery, messageProtobufReply, true);
                        messageSendService.sendProtobufMessage(messageProtobufReply);
                        return answer;
                    }
                } else {
                    String context = String.join("\n", kbResults.stream().map(FaqProtobuf::toJson).toList());
                    List<Message> messages = promptHelper.buildMessagesForSync(query, context, robot,
                            messageProtobufQuery);
                    Prompt aiPrompt = promptHelper.toPrompt(messages);
                    String response = processPromptSync(aiPrompt.toString(), robot);
                    PromptResult promptResult = new PromptResult(response, aiPrompt);

                    RobotContent streamContent = RobotContent.builder()
                            .question(query)
                            .answer(promptResult.getResponse())
                            .regenerationContext(context)
                            .kbUid(robot.getKbUid())
                            .robotUid(robot.getUid())
                            .build();

                    messageProtobufReply.setContent(streamContent.toJson());
                    messageProtobufReply.setType(MessageTypeEnum.ROBOT);
                    String modelType = robot.getLlm() != null && robot.getLlm().getTextModel() != null
                            ? robot.getLlm().getTextModel()
                            : "";
                    messagePersistenceHelper.persistMessage(messageProtobufQuery, messageProtobufReply, false, 0, 0, 0,
                            promptResult.getPrompt(), "", modelType);
                    messageSendService.sendProtobufMessage(messageProtobufReply);
                    return promptResult.getResponse();
                }
            }

            // LLM 未启用：直接返回 KB 结果
            String answer;
            MessageTypeEnum messageType;
            boolean isUnanswered;
            if (kbResults.isEmpty()) {
                answer = robot.getLlm() != null && robot.getLlm().getDefaultReply() != null
                        ? robot.getLlm().getDefaultReply()
                        : I18Consts.I18N_ROBOT_DEFAULT_REPLY;
                messageType = MessageTypeEnum.ROBOT;
                isUnanswered = true;
            } else {
                FaqProtobuf firstFaq = kbResults.get(0);
                if (kbResults.size() > 1) {
                    firstFaq.setRelatedFaqs(new ArrayList<>(kbResults.subList(1, kbResults.size())));
                }
                answer = firstFaq.toJson();
                messageType = MessageTypeEnum.FAQ_ANSWER;
                isUnanswered = false;
            }

            RobotContent streamContent = RobotContent.builder()
                    .question(query)
                    .answer(answer)
                    .kbUid(robot.getKbUid())
                    .robotUid(robot.getUid())
                    .build();

            messageProtobufReply.setContent(streamContent.toJson());
            messageProtobufReply.setType(messageType);
            messagePersistenceHelper.persistMessage(messageProtobufQuery, messageProtobufReply, isUnanswered);
            messageSendService.sendProtobufMessage(messageProtobufReply);
            return answer;
        } catch (Exception e) {
            log.error("Error in sendSyncMessage", e);
            String errorMessage = I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
            RobotContent streamError = RobotContent.builder()
                    .question(query)
                    .answer("")
                    .reasonContent(errorMessage)
                    .kbUid(robot.getKbUid())
                    .robotUid(robot.getUid())
                    .build();
            messageProtobufReply.setContent(streamError.toJson());
            messageProtobufReply.setType(MessageTypeEnum.ROBOT_ERROR);
            messagePersistenceHelper.persistMessage(messageProtobufQuery, messageProtobufReply, true);
            messageSendService.sendProtobufMessage(messageProtobufReply);
            return errorMessage;
        }
    }

    @Override
    public String processSyncRequest(String query, RobotProtobuf robot, boolean searchKnowledgeBase) {
        // 检查是否启用大模型
        if (robot.getLlm() == null || !robot.getLlm().getEnabled()) {
            log.warn("LLM未启用，无法处理直接LLM请求");
            return I18Consts.I18N_SORRY_LLM_DISABLED;
        }

        String prompt = robot.getLlm().getPrompt();
        log.info("处理直接LLM请求: query={}, prompt={}, robot={}, searchKnowledgeBase={}",
                query, prompt, robot.getUid(), searchKnowledgeBase);

        List<FaqProtobuf> searchResultList = new ArrayList<>();

        // 根据参数决定是否查询知识库
        if (searchKnowledgeBase) {
            searchResultList = knowledgeBaseSearchHelper.searchKnowledgeBase(query, robot);
            log.info("processDirectLlmRequest searchResultList {}", searchResultList);
        } else {
            log.info("跳过知识库查询，直接使用提示词处理");
        }

        // 构建提示词
        StringBuilder aiPrompt = new StringBuilder();
        if (StringUtils.hasText(prompt)) {
            aiPrompt.append(prompt);
        } else {
            // 如果未提供提示词，使用默认提示词
            aiPrompt.append(I18Consts.I18N_CONTEXT_BASED_ANSWER);
        }

        // 如果有搜索结果，添加为上下文
        if (!searchResultList.isEmpty()) {
            String context = String.join("\n", searchResultList.stream().map(FaqProtobuf::toJson).toList());
            log.info("processDirectLlmRequest context {}", context);
            aiPrompt.append(I18Consts.I18N_CONTEXT_LABEL).append(context).append("\n\n");
        }

        // 添加用户查询
        aiPrompt.append(I18Consts.I18N_QUESTION_LABEL).append(query);

        // 调用子类实现的处理方法
        try {
            String response = processPromptSync(aiPrompt.toString(), robot);
            log.info("processDirectLlmRequest response {}", response);
            if (response != null && response.contains("<think>")) {
                log.debug("processDirectLlmRequest 替换前的内容: {}", response);
                response = response.replaceAll("(?s)<think>.*?</think>", "");
                log.debug("processDirectLlmRequest 替换后的内容: {}", response);
            }
            return response;
        } catch (Exception e) {
            log.error("处理LLM请求失败", e);
            return I18Consts.I18N_SORRY_SERVICE_UNAVAILABLE;
        }
    }

    // 带prompt参数的抽象方法重载
    protected abstract void processPromptWebsocket(Prompt prompt, RobotProtobuf robot,
            MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply);

    protected abstract void processPromptSse(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, List<RobotContent.SourceReference> sourceReferences,
            SseEmitter emitter);

    // 带prompt参数的抽象方法重载
    protected abstract String processPromptSync(String message, RobotProtobuf robot);

}