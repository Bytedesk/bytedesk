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
import com.bytedesk.core.message.MessageExtra;
import com.bytedesk.core.message.MessagePersistCache;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kbase.faq.FaqElastic;
import com.bytedesk.kbase.faq.FaqElasticSearchResult;
import com.bytedesk.kbase.faq.FaqProtobuf;
import com.bytedesk.kbase.faq.FaqService;

import lombok.extern.slf4j.Slf4j;
import com.alibaba.fastjson2.JSON;

@Slf4j
public abstract class BaseSpringAIService implements SpringAIService {

    @Autowired
    protected SpringAIVectorStoreService springAIVectorService;

    @Autowired
    protected FaqService faqService;

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

    // 可以添加更多自动注入的依赖，而不需要修改子类构造函数

    // 保留一个无参构造函数，或者只接收特定的必需依赖
    protected BaseSpringAIService() {
        // 无参构造函数
    }

    // 可以保留一个带参数的构造函数用于单元测试或特殊情况
    protected BaseSpringAIService(SpringAIVectorStoreService springAIVectorService,
            IMessageSendService messageSendService) {
        this.springAIVectorService = springAIVectorService;
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
            List<String> contentList = springAIVectorService.searchText(query, robot.getKbUid());
            // TODO: 根据配置，拉取历史聊天记录
            String history = "";
            String context = String.join("\n", contentList);
            prompt = buildKbPrompt(robot.getLlm().getPrompt(), query, history, context);
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
        
        // 如果知识库未启用，直接返回
        if (!StringUtils.hasText(robot.getKbUid()) || !robot.getIsKbEnabled()) {
            log.info("知识库未启用或未指定知识库UID");
            return;
        }
        
        List<String> searchContentList = new ArrayList<>();
        List<FaqProtobuf> faqProtobufList = new ArrayList<>();
        
        // 根据搜索类型执行相应的搜索
        String searchType = robot.getLlm().getSearchType();
        if (searchType == null) {
            searchType = RobotSearchTypeEnum.FULLTEXT.name(); // 默认使用全文搜索
        }
        
        // 执行搜索
        switch (RobotSearchTypeEnum.valueOf(searchType)) {
            case VECTOR:
                log.info("使用向量搜索");
                executeVectorSearch(query, robot.getKbUid(), searchContentList, faqProtobufList);
                break;
            case MIXED:
                log.info("使用混合搜索");
                executeFulltextSearch(query, robot.getKbUid(), searchContentList, faqProtobufList);
                executeVectorSearch(query, robot.getKbUid(), searchContentList, faqProtobufList);
                break;
            case FULLTEXT:
            default:
                log.info("使用全文搜索");
                executeFulltextSearch(query, robot.getKbUid(), searchContentList, faqProtobufList);
                break;
        }
        
        // 根据是否启用LLM决定如何处理结果
        if (robot.getLlm().isEnabled()) {
            // 启用大模型
            processLlmResponse(query, searchContentList, robot, messageProtobufQuery, messageProtobufReply, emitter);
        } else {
            // 未开启大模型，使用搜索结果直接回复
            processSearchResponse(query, faqProtobufList, robot, messageProtobufQuery, messageProtobufReply, emitter);
        }
        
        log.info("BaseSpringAIService sendSseMessage searchContentList {}", searchContentList);
    }
    
    /**
     * 执行全文搜索
     */
    private void executeFulltextSearch(String query, String kbUid, List<String> searchContentList, List<FaqProtobuf> faqProtobufList) {
        List<FaqElasticSearchResult> searchResults = faqService.searchFaq(query, kbUid, null, null);
        for (FaqElasticSearchResult withScore : searchResults) {
            FaqElastic faq = withScore.getFaqElastic();
            FaqProtobuf faqProtobuf = FaqProtobuf.fromElastic(faq);
            
            String formattedFaq = faqProtobuf.toJson();
            searchContentList.add(formattedFaq);
            faqProtobufList.add(faqProtobuf);
        }
    }
    
    /**
     * 执行向量搜索
     */
    private void executeVectorSearch(String query, String kbUid, List<String> searchContentList, List<FaqProtobuf> faqProtobufList) {
        List<String> contentList = springAIVectorService.searchText(query, kbUid);
        searchContentList.addAll(contentList);
        
        for (String content : contentList) {
            FaqProtobuf faqProtobuf = FaqProtobuf.builder()
                    .uid(uidUtils.getUid())
                    .question(query)
                    .answer(content)
                    .build();
            faqProtobufList.add(faqProtobuf);
        }
    }

    private void processLlmResponse(String query, List<String> searchContentList, RobotProtobuf robot,
            MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply,
            SseEmitter emitter) {
        log.info("BaseSpringAIService processLlmResponse searchContentList {}", searchContentList);
        //
        if (searchContentList.isEmpty()) {
            // 直接返回未找到相关问题答案
            String answer = RobotConsts.ROBOT_UNMATCHED;
            processAnswerMessage(answer, MessageTypeEnum.TEXT, robot, messageProtobufQuery, messageProtobufReply, emitter);
            return;
        }
        String context = String.join("\n", searchContentList);
        // TODO: 根据配置，拉取历史聊天记录
        String history = "";
        String prompt = buildKbPrompt(robot.getLlm().getPrompt(), query, history, context);
        // TODO: 返回消息中携带消息搜索结果(来源依据)
        //
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(prompt));
        messages.add(new UserMessage(query));
        log.info("BaseSpringAIService sendSseMemberMessage messages {}", messages);
        //
        Prompt aiPrompt = new Prompt(messages);
        processPromptSSE(aiPrompt, robot, messageProtobufQuery, messageProtobufReply, emitter);
    }

    private void processSearchResponse(String query, List<FaqProtobuf> searchContentList, RobotProtobuf robot,
            MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, SseEmitter emitter) {
        log.info("BaseSpringAIService processSearchResponse searchContentList {}", searchContentList);
        //
        if (searchContentList.isEmpty()) {
            // 直接返回未找到相关问题答案
            String answer = RobotConsts.ROBOT_UNMATCHED;
            processAnswerMessage(answer, MessageTypeEnum.TEXT, robot, messageProtobufQuery, messageProtobufReply, emitter);
            return;
        } else {
            // 搜索到内容，返回搜索内容
            FaqProtobuf firstFaq = searchContentList.get(0);
            FaqProtobuf resultFaq = FaqProtobuf.builder()
                    .uid(firstFaq.getUid())
                    .question(firstFaq.getQuestion())
                    .answer(firstFaq.getAnswer())
                    .build();
            
            // 如果有多个搜索结果，将其余的添加为相关问题
            if (searchContentList.size() > 1) {
                List<FaqProtobuf> relatedFaqs = new ArrayList<>(searchContentList.subList(1, searchContentList.size()));
                resultFaq.setRelatedFaqs(relatedFaqs);
            }
            
            // 将处理后的单个FaqProtobuf对象转换为JSON字符串
            String answer = JSON.toJSONString(resultFaq);
            processAnswerMessage(answer, MessageTypeEnum.FAQ_ANSWER, robot, messageProtobufQuery, messageProtobufReply, emitter);
        }
    }

    private void processAnswerMessage(String answer, MessageTypeEnum type, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, SseEmitter emitter) {
        messageProtobufReply.setType(type);
        messageProtobufReply.setContent(answer);
        messageProtobufReply.setClient(ClientEnum.SYSTEM);
        log.info("BaseSpringAIService processAnswerMessage messageProtobufReply {}", messageProtobufReply);
        // 保存消息到数据库
        persistMessage(messageProtobufQuery, messageProtobufReply);
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
    public void persistMessage(MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply) {
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
                //
                .isUnAnswered(messageProtobufReply.getContent().equals(RobotConsts.ROBOT_UNMATCHED))
                .orgUid(extraObject.getOrgUid())
                //
                .build();
        robotMessageCache.pushRequest(robotMessage);

    }

    public String buildKbPrompt(String systemPrompt, String query, String history, String context) {
        return systemPrompt + "\n" +
                "用户查询: " + query + "\n" +
                "历史聊天记录: " + history + "\n" +
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
     * @param type 消息类型
     * @param content 消息内容
     * @param messageProtobufReply 回复消息对象
     */
    protected void sendMessage(MessageTypeEnum type, String content, MessageProtobuf messageProtobufReply) {
        messageProtobufReply.setType(type);
        messageProtobufReply.setContent(content);
        messageSendService.sendProtobufMessage(messageProtobufReply);
    }

    /**
     * 检查SseEmitter是否已完成
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
     * @param error 发生的错误
     * @param messageProtobufQuery 查询消息
     * @param messageProtobufReply 回复消息
     * @param emitter SSE发射器
     */
    protected void handleSseError(Throwable error, MessageProtobuf messageProtobufQuery, 
                                 MessageProtobuf messageProtobufReply, SseEmitter emitter) {
        try {
            // 检查emitter是否已完成
            if (emitter != null && !isEmitterCompleted(emitter)) {
                messageProtobufReply.setType(MessageTypeEnum.ERROR);
                messageProtobufReply.setContent("服务暂时不可用，请稍后重试");
                // 保存消息到数据库
                persistMessage(messageProtobufQuery, messageProtobufReply);
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
                persistMessage(messageProtobufQuery, messageProtobufReply);
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
     * @param messageProtobufReply 回复消息对象
     * @param emitter SSE发射器
     * @param initialContent 初始内容，通常为"正在思考中..."
     */
    protected void sendStreamStartMessage(MessageProtobuf messageProtobufReply, SseEmitter emitter, String initialContent) {
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
     * @param messageProtobufQuery 查询消息对象
     * @param messageProtobufReply 回复消息对象
     * @param emitter SSE发射器
     * @param content 消息内容
     */
    protected void sendStreamMessage(MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply, 
            SseEmitter emitter, String content) {
        try {
            if (StringUtils.hasLength(content) && !isEmitterCompleted(emitter)) {
                messageProtobufReply.setContent(content);
                messageProtobufReply.setType(MessageTypeEnum.STREAM);
                // 保存消息到数据库
                persistMessage(messageProtobufQuery, messageProtobufReply);
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
     * @param messageProtobufQuery 查询消息对象
     * @param messageProtobufReply 回复消息对象
     * @param emitter SSE发射器
     */
    protected void sendStreamEndMessage(MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply, 
            SseEmitter emitter) {
        try {
            if (!isEmitterCompleted(emitter)) {
                // 发送流结束标记
                messageProtobufReply.setType(MessageTypeEnum.STREAM_END);
                messageProtobufReply.setContent(""); 
                // 保存消息到数据库
                persistMessage(messageProtobufQuery, messageProtobufReply);
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
     * @param <T> 选项类型参数
     * @param llm 机器人LLM配置
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