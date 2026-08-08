package com.bytedesk.ai.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.ai.llm_provider.LlmProviderRestService;
import com.bytedesk.ai.robot.RobotRestService;
import com.bytedesk.ai.providers.dashscope.tool.DashScopeToolService;
import com.bytedesk.ai.providers.zhipuai.tool.ZhipuaiToolService;
import com.bytedesk.ai.robot_message.RobotMessageCache;
import com.bytedesk.ai.service.agent.AgentCannedResponseMatch;
import com.bytedesk.ai.service.agent.AgentCannedResponseRequest;
import com.bytedesk.ai.service.agent.AgentCannedResponseResolver;
import com.bytedesk.ai.service.agent.AgentCannedResponseTraceContext;
import com.bytedesk.ai.service.agent.AgentResponseTimingContext;
import com.bytedesk.ai.springai.config.ChatClientBuilderFactory;
import com.bytedesk.ai.springai.service.ChatClientInfoService;
import com.bytedesk.ai.tool.utils.RobotToolCallbackResolver;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessagePersistCache;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.message.content.RobotContent;
import com.bytedesk.core.message.enums.MessageTypeEnum;
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
import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.ai.tool_call.ToolCallRestService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
public abstract class BaseSpringAIService implements SpringAIService {

    protected FaqElasticService faqElasticService;

    protected FaqVectorService faqVectorService;

    protected TextElasticService textElasticService;

    protected TextVectorService textVectorService;

    protected ChunkElasticService chunkElasticService;

    protected ChunkVectorService chunkVectorService;

    protected WebpageElasticService webpageElasticService;

    protected WebpageVectorService webpageVectorService;

    // @Autowired
    // protected ArticleElasticService articleElasticService;

    // @Autowired(required = false)
    // protected ArticleVectorService articleVectorService;

    protected IMessageSendService messageSendService;

    protected UidUtils uidUtils;

    protected RobotRestService robotRestService;

    protected ThreadRestService threadRestService;

    protected MessagePersistCache messagePersistCache;

    protected RobotMessageCache robotMessageCache;

    protected MessageRestService messageRestService;

    protected ApplicationEventPublisher applicationEventPublisher;

    protected KnowledgeBaseSearchHelper knowledgeBaseSearchHelper;

    protected PromptHelper promptHelper;

    protected MessagePersistenceHelper messagePersistenceHelper;

    protected SseMessageHelper sseMessageHelper;

    protected ChatClientBuilderFactory chatClientBuilderFactory;

    protected ChatClientInfoService chatClientInfoService;

    protected AgentCannedResponseResolver agentCannedResponseResolver;

    protected AgentCannedResponseTraceContext agentCannedResponseTraceContext;

    protected AgentResponseTimingContext agentResponseTimingContext;

    protected RobotToolCallbackResolver robotToolCallbackResolver;

    protected DashScopeToolService dashScopeToolService;

    protected ZhipuaiToolService zhipuaiToolService;

    protected ToolCallRestService toolCallRestService;

    protected LlmProviderRestService llmProviderRestService;

    protected ProviderToolServiceDispatcher providerToolServiceDispatcher;

    protected ReasoningContentHelper reasoningContentHelper;

    protected CannedResponseEvidenceHelper cannedResponseEvidenceHelper;

    private final ThreadLocal<Map<String, Object>> providerToolRuntimeContextHolder = new ThreadLocal<>();

    // 保留一个无参构造函数，或者只接收特定的必需依赖
    protected BaseSpringAIService() {
        // 无参构造函数
    }

    @Autowired
    protected void setBaseDependencies(FaqElasticService faqElasticService,
            ObjectProvider<FaqVectorService> faqVectorServiceProvider,
            TextElasticService textElasticService,
            ObjectProvider<TextVectorService> textVectorServiceProvider,
            ChunkElasticService chunkElasticService,
            ObjectProvider<ChunkVectorService> chunkVectorServiceProvider,
            WebpageElasticService webpageElasticService,
            ObjectProvider<WebpageVectorService> webpageVectorServiceProvider,
            IMessageSendService messageSendService,
            UidUtils uidUtils,
            RobotRestService robotRestService,
            ThreadRestService threadRestService,
            MessagePersistCache messagePersistCache,
            RobotMessageCache robotMessageCache,
            MessageRestService messageRestService,
            ApplicationEventPublisher applicationEventPublisher,
            KnowledgeBaseSearchHelper knowledgeBaseSearchHelper,
            PromptHelper promptHelper,
            MessagePersistenceHelper messagePersistenceHelper,
            SseMessageHelper sseMessageHelper,
            ObjectProvider<ChatClientBuilderFactory> chatClientBuilderFactoryProvider,
            ObjectProvider<ChatClientInfoService> chatClientInfoServiceProvider,
            ObjectProvider<AgentCannedResponseResolver> agentCannedResponseResolverProvider,
            ObjectProvider<AgentCannedResponseTraceContext> agentCannedResponseTraceContextProvider,
            ObjectProvider<AgentResponseTimingContext> agentResponseTimingContextProvider,
            ObjectProvider<RobotToolCallbackResolver> robotToolCallbackResolverProvider,
            ObjectProvider<DashScopeToolService> dashScopeToolServiceProvider,
            ObjectProvider<ZhipuaiToolService> zhipuaiToolServiceProvider,
            ObjectProvider<ToolCallRestService> toolCallRestServiceProvider,
            ObjectProvider<LlmProviderRestService> llmProviderRestServiceProvider,
            ObjectProvider<ProviderToolServiceDispatcher> providerToolServiceDispatcherProvider,
            ObjectProvider<ReasoningContentHelper> reasoningContentHelperProvider,
            ObjectProvider<CannedResponseEvidenceHelper> cannedResponseEvidenceHelperProvider) {
        this.faqElasticService = faqElasticService;
        this.faqVectorService = faqVectorServiceProvider.getIfAvailable();
        this.textElasticService = textElasticService;
        this.textVectorService = textVectorServiceProvider.getIfAvailable();
        this.chunkElasticService = chunkElasticService;
        this.chunkVectorService = chunkVectorServiceProvider.getIfAvailable();
        this.webpageElasticService = webpageElasticService;
        this.webpageVectorService = webpageVectorServiceProvider.getIfAvailable();
        this.messageSendService = messageSendService;
        this.uidUtils = uidUtils;
        this.robotRestService = robotRestService;
        this.threadRestService = threadRestService;
        this.messagePersistCache = messagePersistCache;
        this.robotMessageCache = robotMessageCache;
        this.messageRestService = messageRestService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.knowledgeBaseSearchHelper = knowledgeBaseSearchHelper;
        this.promptHelper = promptHelper;
        this.messagePersistenceHelper = messagePersistenceHelper;
        this.sseMessageHelper = sseMessageHelper;
        this.chatClientBuilderFactory = chatClientBuilderFactoryProvider.getIfAvailable();
        this.chatClientInfoService = chatClientInfoServiceProvider.getIfAvailable();
        this.agentCannedResponseResolver = agentCannedResponseResolverProvider.getIfAvailable();
        this.agentCannedResponseTraceContext = agentCannedResponseTraceContextProvider.getIfAvailable();
        this.agentResponseTimingContext = agentResponseTimingContextProvider.getIfAvailable();
        this.robotToolCallbackResolver = robotToolCallbackResolverProvider.getIfAvailable();
        this.dashScopeToolService = dashScopeToolServiceProvider.getIfAvailable();
        this.zhipuaiToolService = zhipuaiToolServiceProvider.getIfAvailable();
        this.toolCallRestService = toolCallRestServiceProvider.getIfAvailable();
        this.llmProviderRestService = llmProviderRestServiceProvider.getIfAvailable();
        this.providerToolServiceDispatcher = providerToolServiceDispatcherProvider.getIfAvailable();
        this.reasoningContentHelper = reasoningContentHelperProvider.getIfAvailable();
        this.cannedResponseEvidenceHelper = cannedResponseEvidenceHelperProvider.getIfAvailable();
    }

    protected String tryProcessPromptSyncWithProviderToolService(String message, RobotProtobuf robot) {
        if (providerToolServiceDispatcher == null) {
            return null;
        }
        return providerToolServiceDispatcher.tryDispatch(robot, message,
                providerToolRuntimeContextHolder.get(),
                this::processSyncForIntentRecognition);
    }

    protected boolean tryProcessPromptSseWithProviderToolService(Prompt prompt, RobotProtobuf robot,
            MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply,
            List<RobotContent.SourceReference> sourceReferences, SseEmitter emitter) {
        if (providerToolServiceDispatcher == null) {
            return false;
        }
        String userMessage = messageProtobufQuery != null
                ? providerToolServiceDispatcher.extractLikelyUserMessage(messageProtobufQuery.getContent())
                : null;
        if (!StringUtils.hasText(userMessage)) {
            userMessage = flattenPromptText(prompt);
        }
        if (!StringUtils.hasText(userMessage)) {
            return false;
        }

        String answer = providerToolServiceDispatcher.tryDispatch(robot, userMessage,
                providerToolRuntimeContextHolder.get(),
                this::processSyncForIntentRecognition);
        if (!StringUtils.hasText(answer)) {
            return false;
        }

        String provider = providerToolServiceDispatcher.resolveToolExecutionProvider(robot);
        String model = providerToolServiceDispatcher.resolveToolExecutionModel(robot);
        sseMessageHelper.sendStreamStartMessage(messageProtobufQuery, messageProtobufReply, emitter,
                I18Consts.I18N_THINKING);
        sseMessageHelper.sendStreamMessage(messageProtobufQuery, messageProtobufReply, emitter, answer, null,
                sourceReferences);
        sseMessageHelper.sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter, 0, 0, 0, prompt,
                provider, model);
        return true;
    }

    /**
     * Adapter to pass the structured sync processor to external helpers.
     */
    private <T> T processSyncForIntentRecognition(String query, RobotProtobuf robot, String provider,
            String model, Class<T> outputClass) {
        return processSyncRequest(query, robot, provider, model, outputClass);
    }

    private String flattenPromptText(Prompt prompt) {
        return promptHelper != null ? promptHelper.flattenPromptText(prompt) : null;
    }

    @SuppressWarnings("unchecked")
    protected <T extends ToolCallingChatOptions> T applyRobotToolCallbacks(T options, RobotLlm llm) {
        if (options == null || llm == null || robotToolCallbackResolver == null) {
            return options;
        }

        List<ToolCallback> toolCallbacks = robotToolCallbackResolver.resolveToolCallbacks(llm.getTools());
        if (toolCallbacks.isEmpty()) {
            return options;
        }

        ToolCallingChatOptions.Builder<?> builder = options.mutate();
        builder.toolCallbacks(toolCallbacks);
        if (StringUtils.hasText(llm.getTextProvider())) {
            builder.toolContext("provider", llm.getTextProvider());
        }
        if (StringUtils.hasText(llm.getTextModel())) {
            builder.toolContext("model", llm.getTextModel());
        }
        if (StringUtils.hasText(llm.getToolChoice())) {
            builder.toolContext("toolChoice", llm.getToolChoice());
        }
        return (T) builder.build();
    }

    protected String extractReasoningContent(Generation generation, AssistantMessage assistantMessage) {
        if (reasoningContentHelper == null) {
            return null;
        }
        return reasoningContentHelper.extractReasoningContent(generation, assistantMessage);
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
                answer = resolveDefaultAnswer(query, robot, messageProtobufQuery, messageProtobufReply, true, answer);
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
                    String answer = resolveDefaultAnswer(query, robot, messageProtobufQuery, messageProtobufReply, true,
                            resolveRobotDefaultReply(robot));
                    sseMessageHelper.sendDefaultReplySse(query, answer, robot, messageProtobufQuery,
                            messageProtobufReply, emitter);
                }
            } else {
                // 未开启大模型对话，且无知识库：直接返回默认回复并结束 SSE
                String answer = resolveDefaultAnswer(query, robot, messageProtobufQuery, messageProtobufReply, true,
                        resolveRobotDefaultReply(robot));
                sseMessageHelper.sendDefaultReplySse(query, answer, robot, messageProtobufQuery, messageProtobufReply,
                        emitter);
            }

            return;
        }

        boolean llmEnabled = robot.getLlm() != null && robot.getLlm().getEnabled();

        if (llmEnabled) {
            // 启用 LLM：聚合 KB 结果作为上下文提示词
            // SearchResultWithSources aggregated = knowledgeBaseSearchHelper
            // .rerankMergeTopK(knowledgeBaseSearchHelper.searchKnowledgeBaseWithSources(query,
            // robot), robot);
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
                    String answer = resolveDefaultAnswer(query, robot, messageProtobufQuery, messageProtobufReply, true,
                            resolveRobotDefaultReply(robot));
                    sseMessageHelper.sendDefaultReplySse(query, answer, robot, messageProtobufQuery,
                            messageProtobufReply, emitter);
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
        // .rerankMergeTopK(knowledgeBaseSearchHelper.searchKnowledgeBaseWithSources(query,
        // robot), robot);
        SearchResultWithSources aggregated = knowledgeBaseSearchHelper.searchKnowledgeBaseWithSources(query, robot);
        List<FaqProtobuf> kbResults = aggregated.getSearchResults();
        List<RobotContent.SourceReference> sourceReferences = Boolean.TRUE.equals(robot.getKbSourceEnabled())
                ? aggregated.getSourceReferences()
                : new ArrayList<>();

        boolean isUnanswered;
        String answer;
        if (kbResults.isEmpty()) {
            isUnanswered = true;
            answer = resolveDefaultAnswer(query, robot, messageProtobufQuery, messageProtobufReply, true,
                    resolveRobotDefaultReply(robot));
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

        long startedAtNanos = System.nanoTime();

        try {
            boolean kbEnabled = StringUtils.hasText(robot.getKbUid()) && robot.getKbEnabled();
            boolean llmEnabled = robot.getLlm() != null && robot.getLlm().getEnabled();

            // 知识库未启用但 LLM 启用：用空上下文直接走 LLM
            if (!kbEnabled && llmEnabled) {
                List<Message> messages = promptHelper.buildMessagesForSync(query, "", robot, messageProtobufQuery);
                Prompt aiPrompt = promptHelper.toPrompt(messages);
                String response = withProviderToolRuntimeContext(robot, messageProtobufQuery,
                    () -> processPromptSync(aiPrompt, robot));
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
                messagePersistenceHelper.persistMessage(messageProtobufQuery, messageProtobufReply, false,
                        elapsedMillis(startedAtNanos), 0, 0, 0,
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
                        String response = withProviderToolRuntimeContext(robot, messageProtobufQuery,
                            () -> processPromptSync(aiPrompt, robot));
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
                        messagePersistenceHelper.persistMessage(messageProtobufQuery, messageProtobufReply, false,
                                elapsedMillis(startedAtNanos), 0, 0, 0,
                                promptResult.getPrompt(), "", modelType);
                        messageSendService.sendProtobufMessage(messageProtobufReply);
                        return promptResult.getResponse();
                    } else {
                        // 返回默认回复
                        String answer = resolveDefaultAnswer(query, robot, messageProtobufQuery, messageProtobufReply,
                                false,
                                resolveRobotDefaultReply(robot));
                        RobotContent streamContent = RobotContent.builder()
                                .question(query)
                                .answer(answer)
                                .kbUid(robot.getKbUid())
                                .robotUid(robot.getUid())
                                .build();
                        messageProtobufReply.setContent(streamContent.toJson());
                        messageProtobufReply.setType(MessageTypeEnum.ROBOT);
                        messagePersistenceHelper.persistMessage(messageProtobufQuery, messageProtobufReply, true,
                                elapsedMillis(startedAtNanos), 0, 0, 0, null, "", "");
                        messageSendService.sendProtobufMessage(messageProtobufReply);
                        return answer;
                    }
                } else {
                    String context = String.join("\n", kbResults.stream().map(FaqProtobuf::toJson).toList());
                    List<Message> messages = promptHelper.buildMessagesForSync(query, context, robot,
                            messageProtobufQuery);
                    Prompt aiPrompt = promptHelper.toPrompt(messages);
                    String response = withProviderToolRuntimeContext(robot, messageProtobufQuery,
                            () -> processPromptSync(aiPrompt, robot));
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
                    messagePersistenceHelper.persistMessage(messageProtobufQuery, messageProtobufReply, false,
                            elapsedMillis(startedAtNanos), 0, 0, 0,
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
                answer = resolveDefaultAnswer(query, robot, messageProtobufQuery, messageProtobufReply, false,
                        resolveRobotDefaultReply(robot));
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
            messagePersistenceHelper.persistMessage(messageProtobufQuery, messageProtobufReply, isUnanswered,
                    elapsedMillis(startedAtNanos), 0, 0, 0, null, "", "");
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
            messagePersistenceHelper.persistMessage(messageProtobufQuery, messageProtobufReply, true,
                    elapsedMillis(startedAtNanos), 0, 0, 0, null, "", "");
            messageSendService.sendProtobufMessage(messageProtobufReply);
            return errorMessage;
        }
    }

    protected long elapsedMillis(long startedAtNanos) {
        long elapsedNanos = System.nanoTime() - startedAtNanos;
        return elapsedNanos > 0 ? elapsedNanos / 1_000_000L : 0L;
    }

    protected <T> T withProviderToolRuntimeContext(RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            ProviderToolInvocation<T> invocation) {
        Map<String, Object> previous = providerToolRuntimeContextHolder.get();
        Map<String, Object> context = new HashMap<>();
        if (previous != null && !previous.isEmpty()) {
            context.putAll(previous);
        }
        if (robot != null) {
            if (StringUtils.hasText(robot.getUid())) {
                context.put("robotUid", robot.getUid());
            }
            if (StringUtils.hasText(robot.getOrgUid())) {
                context.put("orgUid", robot.getOrgUid());
            }
        }
        if (messageProtobufQuery != null) {
            if (StringUtils.hasText(messageProtobufQuery.getUid())) {
                context.put("messageUid", messageProtobufQuery.getUid());
            }
            if (messageProtobufQuery.getThread() != null
                    && StringUtils.hasText(messageProtobufQuery.getThread().getUid())) {
                context.put("threadUid", messageProtobufQuery.getThread().getUid());
            }
        }

        providerToolRuntimeContextHolder.set(Collections.unmodifiableMap(context));
        try {
            return invocation.invoke();
        } finally {
            if (previous == null || previous.isEmpty()) {
                providerToolRuntimeContextHolder.remove();
            } else {
                providerToolRuntimeContextHolder.set(previous);
            }
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
        String context = "";
        if (!searchResultList.isEmpty()) {
            context = String.join("\n", searchResultList.stream().map(FaqProtobuf::toJson).toList());
            log.info("processDirectLlmRequest context {}", context);
        }

        List<Message> messages = promptHelper.buildMessagesForSync(query, context, robot, null);
        Prompt aiPrompt = promptHelper.toPrompt(messages);

        // 调用子类实现的处理方法
        try {
            String response = withProviderToolRuntimeContext(robot, null,
                    () -> processPromptSync(aiPrompt, robot));
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

    /**
     * 结构化输出：在保留现有 prompt 逻辑的前提下，
     * 通过 ChatClient.entity(outputClass) 直接将 LLM 输出转换为 POJO。
     *
     * <p>
     * 使用方需自行在 query 中加入 BeanOutputConverter.getFormat() 等格式约束。
     * </p>
     */
    public <T> T processSyncRequest(String query, RobotProtobuf robot, Class<T> outputClass) {
        return processSyncRequest(query, robot, null, null, outputClass);
    }

    protected <T> T processSyncRequest(String query, RobotProtobuf robot, String providerOverride,
            String modelOverride, Class<T> outputClass) {
        Assert.hasText(query, "Query must not be empty");
        Assert.notNull(outputClass, "OutputClass must not be null");

        // 检查是否启用大模型
        if (robot.getLlm() == null || !robot.getLlm().getEnabled()) {
            log.warn("LLM未启用，无法处理直接LLM请求");
            return null;
        }

        String prompt = robot.getLlm().getPrompt();
        log.info("处理直接LLM请求(结构化): query={}, prompt={}, robot={}, outputClass={}",
                query, prompt, robot.getUid(), outputClass.getSimpleName());

        StringBuilder aiPrompt = new StringBuilder();
        if (StringUtils.hasText(prompt)) {
            aiPrompt.append(prompt);
        } else {
            aiPrompt.append(I18Consts.I18N_CONTEXT_BASED_ANSWER);
            aiPrompt.append(query);
        }

        try {
            if (chatClientInfoService == null) {
                throw new IllegalStateException("ChatClientInfoService is not available");
            }

            String provider = StringUtils.hasText(providerOverride)
                    ? providerOverride.trim()
                    : (robot != null && robot.getLlm() != null && StringUtils.hasText(robot.getLlm().getTextProvider())
                            ? robot.getLlm().getTextProvider()
                            : null);

            ChatClient chatClient = StringUtils.hasText(provider)
                    ? chatClientInfoService.getChatClientByProvider(provider)
                    : null;
            if (chatClient == null) {
                chatClient = chatClientInfoService.getPrimaryChatClient();
            }

            if (chatClient == null) {
                throw new IllegalStateException("ChatClient is not available for provider: " + provider);
            }

            Prompt structuredPrompt = StringUtils.hasText(modelOverride)
                    ? new Prompt(List.of(new SystemMessage(aiPrompt.toString()), new UserMessage(query)),
                            ChatOptions.builder().model(modelOverride.trim()).build())
                    : new Prompt(List.of(new SystemMessage(aiPrompt.toString()), new UserMessage(query)));
            return chatClient.prompt(structuredPrompt).call().entity(outputClass);
        } catch (Exception e) {
            log.error("处理LLM请求失败(结构化)", e);
            return null;
        }
    }

    protected Prompt processPromptWithOptions(Prompt prompt, ChatOptions options) {
        if (prompt == null || options == null) {
            return prompt;
        }
        return new Prompt(prompt.getInstructions(), options);
    }

    protected Prompt buildUserOnlyPrompt(String message, ChatOptions options) {
        return options != null ? new Prompt(List.of(new UserMessage(message)), options)
                : new Prompt(List.of(new UserMessage(message)));
    }

    protected ChatClient createChatClient(ChatModel chatModel, Prompt prompt) {
        Assert.notNull(chatModel, "ChatModel must not be null");

        ChatClient.Builder builder = chatClientBuilderFactory != null
            ? chatClientBuilderFactory.builder(chatModel)
            : ChatClient.builder(chatModel);
        if (prompt != null && prompt.getOptions() != null) {
            builder = builder.defaultOptions(prompt.getOptions().mutate());
        }
        return builder.build();
    }

    protected ChatResponse invokePromptSync(ChatClient chatClient, Prompt prompt) {
        Assert.notNull(chatClient, "ChatClient must not be null");
        return prompt != null
                ? chatClient.prompt(prompt).call().chatResponse()
                : chatClient.prompt().call().chatResponse();
    }

    protected ChatResponse invokePromptSync(ChatModel chatModel, Prompt prompt) {
        return invokePromptSync(createChatClient(chatModel, prompt), prompt);
    }

    protected Flux<ChatResponse> invokePromptStream(ChatClient chatClient, Prompt prompt) {
        Assert.notNull(chatClient, "ChatClient must not be null");
        return prompt != null
                ? chatClient.prompt(prompt).stream().chatResponse()
                : chatClient.prompt().stream().chatResponse();
    }

    protected Flux<ChatResponse> invokePromptStream(ChatModel chatModel, Prompt prompt) {
        return invokePromptStream(createChatClient(chatModel, prompt), prompt);
    }

    protected String processPromptSync(Prompt prompt, RobotProtobuf robot) {
        return processPromptSync(promptHelper != null ? promptHelper.flattenPromptText(prompt) : null, robot);
    }

    protected abstract void processPromptSse(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, List<RobotContent.SourceReference> sourceReferences,
            SseEmitter emitter);

    protected String resolveDefaultAnswer(String query, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, boolean streaming, String defaultAnswer) {
        if (agentCannedResponseResolver == null) {
            return defaultAnswer;
        }
        Map<String, String> evidenceFields = buildCannedResponseEvidenceFields(query, messageProtobufQuery,
                defaultAnswer);
        AgentCannedResponseMatch match = agentCannedResponseResolver.resolve(
                new AgentCannedResponseRequest(query, robot, messageProtobufQuery, evidenceFields, streaming,
                        defaultAnswer));
        if (match == null || !StringUtils.hasText(match.answer())) {
            return defaultAnswer;
        }
        if (agentCannedResponseTraceContext != null && messageProtobufReply != null) {
            agentCannedResponseTraceContext.store(messageProtobufReply.getUid(), match.cannedResponseUid());
        }
        return match.answer();
    }

    protected String resolveRobotDefaultReply(RobotProtobuf robot) {
        return robot.getLlm() != null && robot.getLlm().getDefaultReply() != null
                ? robot.getLlm().getDefaultReply()
                : I18Consts.I18N_ROBOT_DEFAULT_REPLY;
    }

    protected Map<String, String> buildCannedResponseEvidenceFields(String query, MessageProtobuf messageProtobufQuery,
            String defaultAnswer) {
        if (cannedResponseEvidenceHelper == null) {
            Map<String, String> fallback = new LinkedHashMap<>();
            if (query != null) {
                fallback.put("query", query);
            }
            return fallback;
        }
        return cannedResponseEvidenceHelper.buildEvidenceFields(query, messageProtobufQuery, defaultAnswer);
    }

    @FunctionalInterface
    protected interface ProviderToolInvocation<T> {
        T invoke();
    }

    // 带prompt参数的抽象方法重载
    protected abstract String processPromptSync(String message, RobotProtobuf robot);

}