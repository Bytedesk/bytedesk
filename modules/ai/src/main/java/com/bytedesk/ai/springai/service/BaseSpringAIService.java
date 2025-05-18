package com.bytedesk.ai.springai.service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bytedesk.ai.robot.RobotConsts;
import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.ai.robot.RobotRestService;
import com.bytedesk.ai.robot.RobotSearchTypeEnum;
import com.bytedesk.ai.robot_message.RobotMessageCache;
import com.bytedesk.ai.robot_message.RobotMessageRequest;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageExtra;
import com.bytedesk.core.message.MessagePersistCache;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kbase.faq.FaqProtobuf;
import com.bytedesk.kbase.faq.vector.FaqVector;
import com.bytedesk.kbase.faq.vector.FaqVectorSearchResult;
import com.bytedesk.kbase.faq.vector.FaqVectorService;
import com.bytedesk.kbase.faq.elastic.FaqElastic;
import com.bytedesk.kbase.faq.elastic.FaqElasticSearchResult;
import com.bytedesk.kbase.faq.elastic.FaqElasticService;
import com.bytedesk.kbase.llm_chunk.ChunkElastic;
import com.bytedesk.kbase.llm_chunk.ChunkElasticSearchResult;
import com.bytedesk.kbase.llm_chunk.ChunkElasticService;
import com.bytedesk.kbase.llm_chunk.ChunkVector;
import com.bytedesk.kbase.llm_chunk.ChunkVectorSearchResult;
import com.bytedesk.kbase.llm_chunk.ChunkVectorService;
import com.bytedesk.kbase.llm_text.TextElastic;
import com.bytedesk.kbase.llm_text.TextElasticSearchResult;
import com.bytedesk.kbase.llm_text.TextElasticService;
import com.bytedesk.kbase.llm_text.TextVector;
import com.bytedesk.kbase.llm_text.TextVectorSearchResult;
import com.bytedesk.kbase.llm_text.TextVectorService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseSpringAIService implements SpringAIService {

    @Autowired
    protected FaqElasticService faqElasticService;

    @Autowired
    protected FaqVectorService faqVectorService;

    @Autowired
    protected TextElasticService textElasticService;

    @Autowired
    protected TextVectorService textVectorService;

    @Autowired
    protected ChunkElasticService chunkElasticService;

    @Autowired
    protected ChunkVectorService chunkVectorService;

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

    // 可以添加更多自动注入的依赖，而不需要修改子类构造函数

    // 保留一个无参构造函数，或者只接收特定的必需依赖
    protected BaseSpringAIService() {
        // 无参构造函数
    }

    // 可以保留一个带参数的构造函数用于单元测试或特殊情况
    protected BaseSpringAIService(SpringAIVectorStoreService springAIVectorService,
            IMessageSendService messageSendService) {
        // this.springAIVectorService = springAIVectorService;
        this.messageSendService = messageSendService;
    }

    @Override
    public void sendWebsocketMessage(String query, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply) {
        Assert.hasText(query, "Query must not be empty");
        Assert.notNull(robot, "RobotEntity must not be null");
        Assert.notNull(messageProtobufQuery, "MessageProtobuf must not be null");
        //
        String prompt = "";
        if (StringUtils.hasText(robot.getKbUid()) && robot.getIsKbEnabled()) {
            String context = String.join("\n", "");
            prompt = buildKbPrompt(robot.getLlm().getPrompt(), query, context);
        } else {
            prompt = robot.getLlm().getPrompt();
        }
        //
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(prompt));
        messages.add(new UserMessage(query));
        //
        Prompt aiPrompt = new Prompt(messages);
        processPrompt(aiPrompt, robot, messageProtobufQuery, messageProtobufReply);
    }

    @Override
    public void sendSseMessage(String query, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, SseEmitter emitter) {
        Assert.hasText(query, "Query must not be empty");
        Assert.notNull(emitter, "SseEmitter must not be null");

        // 如果知识库未启用，直接跟据配置的提示词进行回复
        if (!StringUtils.hasText(robot.getKbUid()) || !robot.getIsKbEnabled()) {
            log.info("知识库未启用或未指定知识库UID");
            // 使用通用方法处理提示词和SSE消息，传入空上下文
            String context = "";
            createAndProcessPrompt(query, context, robot, messageProtobufQuery, messageProtobufReply, emitter);
            return;
        }

        List<FaqProtobuf> searchResultList = new ArrayList<>();

        // 根据搜索类型执行相应的搜索
        String searchType = robot.getLlm().getSearchType();
        if (searchType == null) {
            searchType = RobotSearchTypeEnum.FULLTEXT.name(); // 默认使用全文搜索
        }

        // 执行搜索
        switch (RobotSearchTypeEnum.valueOf(searchType)) {
            case VECTOR:
                log.info("使用向量搜索");
                executeVectorSearch(query, robot.getKbUid(), searchResultList);
                break;
            case MIXED:
                log.info("使用混合搜索");
                executeFulltextSearch(query, robot.getKbUid(), searchResultList);
                executeVectorSearch(query, robot.getKbUid(), searchResultList);
                break;
            case FULLTEXT:
            default:
                log.info("使用全文搜索");
                executeFulltextSearch(query, robot.getKbUid(), searchResultList);
                break;
        }

        // 根据是否启用LLM决定如何处理结果
        if (robot.getLlm().isEnabled()) {
            // 启用大模型
            processLlmResponse(query, searchResultList, robot, messageProtobufQuery, messageProtobufReply, emitter);
        } else {
            // 未开启大模型，使用搜索结果直接回复
            processDirectResponse(query, searchResultList, robot, messageProtobufQuery, messageProtobufReply, emitter);
        }
    }

    /**
     * 执行全文搜索
     */
    private void executeFulltextSearch(String query, String kbUid, List<FaqProtobuf> searchResultList) {
        List<FaqElasticSearchResult> searchResults = faqElasticService.searchFaq(query, kbUid, null, null);
        for (FaqElasticSearchResult withScore : searchResults) {
            FaqElastic faq = withScore.getFaqElastic();
            FaqProtobuf faqProtobuf = FaqProtobuf.fromElastic(faq);
            // 
            searchResultList.add(faqProtobuf);
        }
        // 
        List<TextElasticSearchResult> textResults = textElasticService.searchTexts(query, kbUid, null, null);
        for (TextElasticSearchResult withScore : textResults) {
            TextElastic text = withScore.getTextElastic();
            FaqProtobuf faqProtobuf = FaqProtobuf.fromText(text);
            // 
            searchResultList.add(faqProtobuf);
        }
        // 
        List<ChunkElasticSearchResult> chunkResults = chunkElasticService.searchChunks(query, kbUid, null, null);
        for (ChunkElasticSearchResult withScore : chunkResults) {
            ChunkElastic chunk = withScore.getChunkElastic();
            // 
            FaqProtobuf faqProtobuf = FaqProtobuf.fromChunk(chunk);
            searchResultList.add(faqProtobuf);
        }
    }

    /**
     * 执行向量搜索
     */
    private void executeVectorSearch(String query, String kbUid,  List<FaqProtobuf> searchResultList) {
        List<FaqVectorSearchResult> searchResults = faqVectorService.searchFaqVector(query, kbUid, null, null, 5);
        for (FaqVectorSearchResult withScore : searchResults) {
            FaqVector faqVector = withScore.getFaqVector();
            // 
            FaqProtobuf faqProtobuf = FaqProtobuf.fromFaqVector(faqVector);
            searchResultList.add(faqProtobuf);
        }
        // 
        List<TextVectorSearchResult> textResults = textVectorService.searchTextVector(query, kbUid, null, null, 5);
        for (TextVectorSearchResult withScore : textResults) {
            TextVector textVector = withScore.getTextVector();
            // 
            FaqProtobuf faqProtobuf = FaqProtobuf.fromTextVector(textVector);
            searchResultList.add(faqProtobuf);
        }
        // 
        List<ChunkVectorSearchResult> chunkResults = chunkVectorService.searchChunkVector(query, kbUid, null, null, 5);
        for (ChunkVectorSearchResult withScore : chunkResults) {
            ChunkVector chunkVector = withScore.getChunkVector();
            // 
            FaqProtobuf faqProtobuf = FaqProtobuf.fromChunkVector(chunkVector);
            searchResultList.add(faqProtobuf);
        }
    }

    private void processLlmResponse(String query, List<FaqProtobuf> searchResultList, RobotProtobuf robot,
            MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply,
            SseEmitter emitter) {
        log.info("BaseSpringAIService processLlmResponse searchContentList {}", searchResultList.size());
        //
        if (searchResultList.isEmpty()) {
            // 直接返回未找到相关问题答案
            String answer = robot.getLlm().getDefaultReply(); //RobotConsts.ROBOT_UNMATCHED;
            processAnswerMessage(answer, MessageTypeEnum.TEXT, robot, messageProtobufQuery, messageProtobufReply, true,
                    emitter);
            return;
        }
        // 
        String context = String.join("\n", searchResultList.stream().map(FaqProtobuf::toJson).toList());
        // 使用通用方法处理提示词和SSE消息
        createAndProcessPrompt(query, context, robot, messageProtobufQuery, messageProtobufReply, emitter);
    }

    /**
     * 创建提示词并处理SSE消息的通用方法
     * 
     * @param query 用户查询
     * @param context 上下文信息
     * @param robot 机器人配置
     * @param messageProtobufQuery 查询消息
     * @param messageProtobufReply 回复消息
     * @param emitter SSE发射器
     */
    private void createAndProcessPrompt(String query, String context, RobotProtobuf robot,
            MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply,
            SseEmitter emitter) {

        String prompt = buildKbPrompt(robot.getLlm().getPrompt(), query, context);
        //
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(prompt));
        messages.add(new UserMessage(query));
        // 根据配置，拉取历史聊天记录        
        if (robot.getLlm() != null && robot.getLlm().getContextMsgCount() > 0) {
            // 从缓存中获取最近的消息
            String threadTopic = messageProtobufQuery.getThread().getTopic();
            int limit = robot.getLlm().getContextMsgCount();
            List<MessageEntity> recentMessages = messageRestService.getRecentMessages(threadTopic, limit);
            for (MessageEntity messageEntity : recentMessages) {
                // messageEntity.getContent()中有可能包含 <think>xxxx</think>，需要将其替换掉
                String content = "";

                // 将消息添加到消息列表
                if (messageEntity.isFromVisitor() 
                    || messageEntity.isFromUser() 
                    || messageEntity.isFromMember()) {
                    // 访客消息
                    messages.add(new UserMessage(messageEntity.getContent()));
                } else if (messageEntity.isFromRobot()) {
                    // 机器人消息
                    messages.add(new SystemMessage(messageEntity.getContent()));
                } else if (messageEntity.isFromAgent()) {
                    // 客服消息
                    messages.add(new SystemMessage(messageEntity.getContent()));
                } else if (messageEntity.isFromSystem()) {
                    // 系统消息
                    messages.add(new SystemMessage(messageEntity.getContent()));
                } else {
                    // 其他类型的消息
                    messages.add(new SystemMessage(messageEntity.getContent()));
                }
            }
        }
        log.info("BaseSpringAIService createAndProcessPrompt messages {}", messages);
        //
        Prompt aiPrompt = new Prompt(messages);
        processPromptSSE(aiPrompt, robot, messageProtobufQuery, messageProtobufReply, emitter);
    }

    /**
     * 直接返回搜索结果，不经过大模型
     */
    private void processDirectResponse(String query, List<FaqProtobuf> searchContentList, RobotProtobuf robot,
            MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, SseEmitter emitter) {
        log.info("BaseSpringAIService processSearchResponse searchContentList {}", searchContentList);
        //
        if (searchContentList.isEmpty()) {
            // 直接返回未找到相关问题答案
            String answer = robot.getLlm().getDefaultReply(); //RobotConsts.ROBOT_UNMATCHED;
            processAnswerMessage(answer, MessageTypeEnum.TEXT, robot, messageProtobufQuery, messageProtobufReply, true,
                    emitter);
            return;
        } else {
            // 搜索到内容，返回搜索内容
            FaqProtobuf firstFaq = searchContentList.get(0);

            // 如果有多个搜索结果，将其余的添加为相关问题
            if (searchContentList.size() > 1) {
                List<FaqProtobuf> relatedFaqs = new ArrayList<>(searchContentList.subList(1, searchContentList.size()));
                firstFaq.setRelatedFaqs(relatedFaqs);
            }

            // 将处理后的单个FaqProtobuf对象转换为JSON字符串
            String answer = firstFaq.toJson();
            processAnswerMessage(answer, MessageTypeEnum.FAQ_ANSWER, robot, messageProtobufQuery, messageProtobufReply, false,
                    emitter);
        }
    }

    private void processAnswerMessage(String answer, MessageTypeEnum type, RobotProtobuf robot,
            MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, Boolean isUnanswered, SseEmitter emitter) {
        messageProtobufReply.setType(type);
        messageProtobufReply.setContent(answer);
        messageProtobufReply.setClient(ClientEnum.SYSTEM);
        log.info("BaseSpringAIService processAnswerMessage messageProtobufReply {}", messageProtobufReply);
        // 保存消息到数据库
        persistMessage(messageProtobufQuery, messageProtobufReply, isUnanswered);
        String messageJson = messageProtobufReply.toJson();
        try {
            // 发送SSE事件
            emitter.send(SseEmitter.event()
                    .data(messageJson)
                    .id(messageProtobufReply.getUid())
                    .name("message"));
            emitter.complete();
        } catch (Exception e) {
            log.error("BaseSpringAIService processAnswerMessage Error sending SSE event 1：", e);
            emitter.completeWithError(e);
        }
    }

    @Override
    public String generateFaqPairsAsync(String chunk) {
        if (!StringUtils.hasText(chunk)) {
            return "";
        }
        String prompt = RobotConsts.PROMPT_LLM_GENERATE_FAQ_TEMPLATE.replace("{chunk}", chunk);
        //
        return generateFaqPairs(prompt);
    }

    @Override
    public void generateFaqPairsSync(String chunk) {
        Assert.hasText(chunk, "Chunk must not be empty");

        String prompt = RobotConsts.PROMPT_LLM_GENERATE_FAQ_TEMPLATE.replace("{chunk}", chunk);
        int maxRetries = 3;
        int retryCount = 0;
        int retryDelay = 1000;

        while (retryCount < maxRetries) {
            try {
                String result = generateFaqPairs(prompt);
                log.info("FAQ generation result: {}", result);
                return;
            } catch (Exception e) {
                retryCount++;
                if (retryCount == maxRetries) {
                    log.error("Failed to generate FAQ pairs after {} retries", maxRetries, e);
                    throw new RuntimeException("Failed to generate FAQ pairs", e);
                }

                try {
                    Thread.sleep(retryDelay * (1 << (retryCount - 1)));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted while retrying", ie);
                }
            }
        }
    }

    @Override
    public void persistMessage(MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply, Boolean isUnanswered) {
        Assert.notNull(messageProtobufQuery, "MessageProtobufQuery must not be null");
        Assert.notNull(messageProtobufReply, "MessageProtobufReply must not be null");
        messagePersistCache.pushForPersist(messageProtobufReply.toJson());
        //
        MessageExtra extraObject = MessageExtra.fromJson(messageProtobufReply.getExtra());
        //
        // 记录未找到相关答案的问题到另外一个表，便于梳理问题
        RobotMessageRequest robotMessage = RobotMessageRequest.builder()
                .uid(messageProtobufReply.getUid()) // 使用机器人回复消息作为uid
                .type(messageProtobufQuery.getType().name())
                .status(messageProtobufReply.getStatus().name())
                //
                .topic(messageProtobufQuery.getThread().getTopic())
                .threadUid(messageProtobufQuery.getThread().getUid())
                //
                .content(messageProtobufQuery.getContent())
                .answer(messageProtobufReply.getContent())
                //
                .user(messageProtobufQuery.getUser().toJson())
                .robot(messageProtobufReply.getUser().toJson())
                // messageProtobufReply.getContent().equals(RobotConsts.ROBOT_UNMATCHED)
                .isUnAnswered(isUnanswered)
                .orgUid(extraObject.getOrgUid())
                //
                .build();
        robotMessageCache.pushRequest(robotMessage);
    }

    public String buildKbPrompt(String systemPrompt, String query, String context) {
        return systemPrompt + "\n" +
                "用户提问: " + query + "\n" +
                "搜索结果: " + context;
    }

    // 抽象方法，由具体实现类提供
    protected abstract void processPrompt(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply);

    protected abstract String processPromptSync(String message);

    protected abstract void processPromptSSE(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, SseEmitter emitter);

    // 抽象方法，由具体实现类提供
    protected abstract String generateFaqPairs(String prompt);

    /**
     * 发送消息的通用方法
     * 
     * @param type                 消息类型
     * @param content              消息内容
     * @param messageProtobufReply 回复消息对象
     */
    protected void sendMessage(MessageTypeEnum type, String content, MessageProtobuf messageProtobufReply) {
        messageProtobufReply.setType(type);
        messageProtobufReply.setContent(content);
        messageSendService.sendProtobufMessage(messageProtobufReply);
    }

    /**
     * 检查SseEmitter是否已完成
     * 
     * @param emitter SSE发射器
     * @return 如果emitter已完成或关闭返回true，否则返回false
     */
    protected boolean isEmitterCompleted(SseEmitter emitter) {
        if (emitter == null) {
            return true;
        }

        try {
            // 尝试发送一个心跳消息，如果emitter已完成则会抛出异常
            // 使用一个空注释作为心跳，这不会影响客户端
            emitter.send(SseEmitter.event().comment("heartbeat"));
            return false; // 如果发送成功，则表示emitter未完成
        } catch (Exception e) {
            // 如果发送失败，说明emitter已经完成或关闭
            log.debug("Emitter appears to be completed: {}", e.getMessage());
            return true;
        }
    }

    /**
     * 处理SSE错误的通用方法
     * 
     * @param error                发生的错误
     * @param messageProtobufQuery 查询消息
     * @param messageProtobufReply 回复消息
     * @param emitter              SSE发射器
     */
    protected void handleSseError(Throwable error, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, SseEmitter emitter) {
        try {
            // 检查emitter是否已完成
            if (emitter != null && !isEmitterCompleted(emitter)) {
                messageProtobufReply.setType(MessageTypeEnum.ERROR);
                messageProtobufReply.setContent("服务暂时不可用，请稍后重试");
                // 保存消息到数据库
                persistMessage(messageProtobufQuery, messageProtobufReply, true);
                String messageJson = messageProtobufReply.toJson();

                emitter.send(SseEmitter.event()
                        .data(messageJson)
                        .id(messageProtobufReply.getUid())
                        .name("message"));
                emitter.complete();
            } else {
                log.warn("SSE emitter already completed, skipping sending error message");
                // 仍然需要持久化消息
                messageProtobufReply.setType(MessageTypeEnum.ERROR);
                messageProtobufReply.setContent("服务暂时不可用，请稍后重试");
                persistMessage(messageProtobufQuery, messageProtobufReply, true);
            }
        } catch (Exception e) {
            log.error("Error handling SSE error", e);
            try {
                if (emitter != null && !isEmitterCompleted(emitter)) {
                    emitter.completeWithError(e);
                }
            } catch (Exception ex) {
                log.error("Failed to complete emitter with error", ex);
            }
        }
    }

    /**
     * 发送流式开始消息
     * 
     * @param messageProtobufReply 回复消息对象
     * @param emitter              SSE发射器
     * @param initialContent       初始内容，通常为"正在思考中..."
     */
    protected void sendStreamStartMessage(MessageProtobuf messageProtobufReply, SseEmitter emitter,
            String initialContent) {
        try {
            if (!isEmitterCompleted(emitter)) {
                messageProtobufReply.setType(MessageTypeEnum.STREAM_START);
                messageProtobufReply.setContent(initialContent);
                String startJson = messageProtobufReply.toJson();
                emitter.send(SseEmitter.event()
                        .data(startJson)
                        .id(messageProtobufReply.getUid())
                        .name("message"));
            }
        } catch (Exception e) {
            log.error("Error sending stream start message", e);
        }
    }

    /**
     * 发送流式内容消息
     * 
     * @param messageProtobufQuery 查询消息对象
     * @param messageProtobufReply 回复消息对象
     * @param emitter              SSE发射器
     * @param content              消息内容
     */
    protected void sendStreamMessage(MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply,
            SseEmitter emitter, String content) {
        try {
            if (StringUtils.hasLength(content) && !isEmitterCompleted(emitter)) {
                messageProtobufReply.setContent(content);
                messageProtobufReply.setType(MessageTypeEnum.STREAM);
                // 保存消息到数据库
                persistMessage(messageProtobufQuery, messageProtobufReply, false);
                String messageJson = messageProtobufReply.toJson();
                // 发送SSE事件
                emitter.send(SseEmitter.event()
                        .data(messageJson)
                        .id(messageProtobufReply.getUid())
                        .name("message"));
            }
        } catch (Exception e) {
            log.error("Error sending stream message", e);
        }
    }

    /**
     * 发送流式结束消息
     * 
     * @param messageProtobufQuery 查询消息对象
     * @param messageProtobufReply 回复消息对象
     * @param emitter              SSE发射器
     */
    protected void sendStreamEndMessage(MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply,
            SseEmitter emitter) {
        try {
            if (!isEmitterCompleted(emitter)) {
                // 发送流结束标记
                messageProtobufReply.setType(MessageTypeEnum.STREAM_END);
                messageProtobufReply.setContent("");
                // 保存消息到数据库
                persistMessage(messageProtobufQuery, messageProtobufReply, false);
                String messageJson = messageProtobufReply.toJson();
                //
                emitter.send(SseEmitter.event()
                        .data(messageJson)
                        .id(messageProtobufReply.getUid())
                        .name("message"));
                emitter.complete();
            }
        } catch (Exception e) {
            log.error("Error sending stream end message", e);
        }
    }

    /**
     * 创建动态的聊天选项（通用方法，使用泛型）
     * 
     * @param <T>           选项类型参数
     * @param llm           机器人LLM配置
     * @param optionBuilder 选项构建器函数接口
     * @return 根据机器人配置创建的选项
     */
    protected <T> T createDynamicOptions(RobotLlm llm, Function<RobotLlm, T> optionBuilder) {
        if (llm == null || !StringUtils.hasText(llm.getModel())) {
            return null;
        }

        try {
            // 使用提供的构建器函数创建选项
            return optionBuilder.apply(llm);
        } catch (Exception e) {
            log.error("Error creating dynamic options for model {}", llm.getModel(), e);
            return null;
        }
    }
}