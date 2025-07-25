package com.bytedesk.ai.springai.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.ai.robot.RobotRestService;
import com.bytedesk.ai.robot.RobotSearchTypeEnum;
import com.bytedesk.ai.robot_message.RobotMessageCache;
import com.bytedesk.ai.robot_message.RobotMessageRequest;
import com.bytedesk.ai.springai.event.LlmTokenUsageEvent;
import com.bytedesk.core.constant.LlmConsts;
import com.bytedesk.core.enums.ChannelEnum;
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
import com.bytedesk.kbase.llm_chunk.elastic.ChunkElastic;
import com.bytedesk.kbase.llm_chunk.elastic.ChunkElasticSearchResult;
import com.bytedesk.kbase.llm_chunk.elastic.ChunkElasticService;
import com.bytedesk.kbase.llm_chunk.vector.ChunkVector;
import com.bytedesk.kbase.llm_chunk.vector.ChunkVectorSearchResult;
import com.bytedesk.kbase.llm_chunk.vector.ChunkVectorService;
import com.bytedesk.kbase.faq.elastic.FaqElastic;
import com.bytedesk.kbase.faq.elastic.FaqElasticSearchResult;
import com.bytedesk.kbase.faq.elastic.FaqElasticService;
import com.bytedesk.kbase.llm_text.elastic.TextElastic;
import com.bytedesk.kbase.llm_text.elastic.TextElasticSearchResult;
import com.bytedesk.kbase.llm_text.elastic.TextElasticService;
import com.bytedesk.kbase.llm_text.vector.TextVector;
import com.bytedesk.kbase.llm_text.vector.TextVectorSearchResult;
import com.bytedesk.kbase.llm_text.vector.TextVectorService;

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

    @Autowired
    protected ApplicationEventPublisher applicationEventPublisher;

    // 可以添加更多自动注入的依赖，而不需要修改子类构造函数

    // 保留一个无参构造函数，或者只接收特定的必需依赖
    protected BaseSpringAIService() {
        // 无参构造函数
    }

    // 可以保留一个带参数的构造函数用于单元测试或特殊情况
    protected BaseSpringAIService(IMessageSendService messageSendService) {
        this.messageSendService = messageSendService;
    }

    // 1. 核心消息处理方法
    @Override
    public void sendWebsocketMessage(String query, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply) {
        Assert.hasText(query, "Query must not be empty");
        Assert.notNull(robot, "RobotEntity must not be null");
        Assert.notNull(messageProtobufQuery, "MessageProtobuf must not be null");
        
        // 使用通用方法处理知识库搜索和响应生成
        List<FaqProtobuf> searchResultList = searchKnowledgeBase(query, robot);
        
        // 根据是否启用LLM决定如何处理结果
        if (robot.getLlm().getEnabled()) {
            // 启用大模型
            processLlmResponseWebsocket(query, searchResultList, robot, messageProtobufQuery, messageProtobufReply);
        } else {
            // 未开启大模型，直接返回搜索结果
            if (searchResultList.isEmpty()) {
                // 直接返回未找到相关问题答案
                String answer = robot.getLlm().getDefaultReply();
                messageProtobufReply.setType(MessageTypeEnum.TEXT);
                messageProtobufReply.setContent(answer);
                messageProtobufReply.setChannel(ChannelEnum.SYSTEM);
                
                // 保存消息到数据库
                persistMessage(messageProtobufQuery, messageProtobufReply, true);
                
                // 发送消息
                messageSendService.sendProtobufMessage(messageProtobufReply);
            } else {
                // 搜索到内容，返回搜索内容
                FaqProtobuf firstFaq = searchResultList.get(0);

                // 如果有多个搜索结果，将其余的添加为相关问题
                if (searchResultList.size() > 1) {
                    List<FaqProtobuf> relatedFaqs = new ArrayList<>(searchResultList.subList(1, searchResultList.size()));
                    firstFaq.setRelatedFaqs(relatedFaqs);
                }

                // 将处理后的单个FaqProtobuf对象转换为JSON字符串
                String answer = firstFaq.toJson();
                messageProtobufReply.setType(MessageTypeEnum.FAQ_ANSWER);
                messageProtobufReply.setContent(answer);
                messageProtobufReply.setChannel(ChannelEnum.SYSTEM);
                
                // 保存消息到数据库
                persistMessage(messageProtobufQuery, messageProtobufReply, false);
                
                // 发送消息
                messageSendService.sendProtobufMessage(messageProtobufReply);
            }
        }
    }

    @Override
    public void sendSseMessage(String query, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, SseEmitter emitter) {
        Assert.hasText(query, "Query must not be empty");
        Assert.notNull(emitter, "SseEmitter must not be null");

        // 使用通用方法处理知识库搜索和响应生成
        List<FaqProtobuf> searchResultList = searchKnowledgeBase(query, robot);
        
        // 如果知识库未启用，直接根据配置的提示词进行回复
        if (!StringUtils.hasText(robot.getKbUid()) || !robot.getKbEnabled()) {
            log.info("知识库未启用或未指定知识库UID");
            // 使用通用方法处理提示词和SSE消息，传入空上下文
            String context = "";
            createAndProcessPrompt(query, context, robot, messageProtobufQuery, messageProtobufReply, emitter);
            return;
        }

        // 根据是否启用LLM决定如何处理结果
        if (robot.getLlm().getEnabled()) {
            // 启用大模型
            processLlmResponse(query, searchResultList, robot, messageProtobufQuery, messageProtobufReply, emitter);
        } else {
            // 未开启大模型，使用搜索结果直接回复
            processDirectResponse(query, searchResultList, robot, messageProtobufQuery, messageProtobufReply, emitter);
        }
    }

    @Override
    public String sendSyncMessage(String query, RobotProtobuf robot, MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply) {
        Assert.hasText(query, "Query must not be empty");
        Assert.notNull(robot, "Robot must not be null");
        Assert.notNull(messageProtobufQuery, "MessageProtobufQuery must not be null");
        Assert.notNull(messageProtobufReply, "MessageProtobufReply must not be null");
        
        try {
            // 使用通用方法处理知识库搜索和响应生成
            List<FaqProtobuf> searchResultList = searchKnowledgeBase(query, robot);
            log.info("BaseSpringAIService sendSyncMessage searchResultList {}", searchResultList);
            
            // 如果知识库未启用或未开启LLM，直接使用基本提示词
            if ((!StringUtils.hasText(robot.getKbUid()) || !robot.getKbEnabled()) && robot.getLlm().getEnabled()) {
                log.info("知识库未启用或未指定知识库UID，但开启了LLM");
                // 使用空上下文调用通用方法
                PromptResult promptResult = createAndProcessPromptSyncWithPrompt(query, "", robot, messageProtobufQuery, messageProtobufReply);
                
                // 设置回复内容和类型
                messageProtobufReply.setContent(promptResult.getResponse());
                messageProtobufReply.setType(MessageTypeEnum.TEXT);
                log.info("BaseSpringAIService sendSyncMessage messageProtobufReply 1 {}", messageProtobufReply);
                
                // 保存消息，包含prompt内容和AI模型信息
                String modelType = robot.getLlm() != null && robot.getLlm().getTextModel() != null ? robot.getLlm().getTextModel() : "";
                persistMessage(messageProtobufQuery, messageProtobufReply, false, 0, 0, 0, promptResult.getFullPromptContent(), "", modelType);
                
                // 发送消息
                messageSendService.sendProtobufMessage(messageProtobufReply);
                
                return promptResult.getResponse();
            }
            
            // 根据是否启用LLM决定如何处理结果
            if (robot.getLlm().getEnabled()) {
                log.info("BaseSpringAIService sendSyncMessage searchContentList {}", searchResultList.size());
                // 启用大模型，组合搜索结果和提示词
                if (searchResultList.isEmpty()) {
                    // 未找到相关知识，使用默认回复
                    String answer = robot.getLlm().getDefaultReply();
                    
                    messageProtobufReply.setContent(answer);
                    messageProtobufReply.setType(MessageTypeEnum.TEXT);
                    log.info("BaseSpringAIService sendSyncMessage messageProtobufReply 2 {}", messageProtobufReply);
                    
                    // 保存错误消息（标记为未回答成功）
                    persistMessage(messageProtobufQuery, messageProtobufReply, true);
                    
                    // 发送消息
                    messageSendService.sendProtobufMessage(messageProtobufReply);
                    
                    return answer;
                } else {
                    // 获取搜索结果作为上下文
                    String context = String.join("\n", searchResultList.stream().map(FaqProtobuf::toJson).toList());
                    log.info("BaseSpringAIService sendSyncMessage context {}", context);
                    
                    // 使用通用方法处理同步消息
                    PromptResult promptResult = createAndProcessPromptSyncWithPrompt(query, context, robot, messageProtobufQuery, messageProtobufReply);
                    log.info("BaseSpringAIService sendSyncMessage response {}", promptResult.getResponse());
                    
                    // 设置回复内容和类型
                    messageProtobufReply.setContent(promptResult.getResponse());
                    messageProtobufReply.setType(MessageTypeEnum.TEXT);
                    log.info("BaseSpringAIService sendSyncMessage messageProtobufReply 3 {}", messageProtobufReply);
                    
                    // 保存消息，包含prompt内容和AI模型信息
                    String modelType = robot.getLlm() != null && robot.getLlm().getTextModel() != null ? robot.getLlm().getTextModel() : "";
                    persistMessage(messageProtobufQuery, messageProtobufReply, false, 0, 0, 0, promptResult.getFullPromptContent(), "", modelType);
                    
                    // 发送消息
                    messageSendService.sendProtobufMessage(messageProtobufReply);
                    
                    return promptResult.getResponse();
                }
            } else {
                // 不启用大模型，直接返回搜索结果
                String answer;
                MessageTypeEnum messageType;
                boolean isUnanswered;
                
                if (searchResultList.isEmpty()) {
                    // 未找到相关知识，使用默认回复
                    answer = robot.getLlm().getDefaultReply();
                    messageType = MessageTypeEnum.TEXT;
                    isUnanswered = true;
                } else {
                    // 搜索到内容，返回搜索内容
                    FaqProtobuf firstFaq = searchResultList.get(0);
                    
                    // 如果有多个搜索结果，将其余的添加为相关问题
                    if (searchResultList.size() > 1) {
                        List<FaqProtobuf> relatedFaqs = new ArrayList<>(searchResultList.subList(1, searchResultList.size()));
                        firstFaq.setRelatedFaqs(relatedFaqs);
                    }
                    
                    // 将处理后的单个FaqProtobuf对象转换为JSON字符串
                    answer = firstFaq.toJson();
                    messageType = MessageTypeEnum.FAQ_ANSWER;
                    isUnanswered = false;
                }
                
                // 设置回复内容和类型
                messageProtobufReply.setContent(answer);
                messageProtobufReply.setType(messageType);
                log.info("BaseSpringAIService sendSyncMessage messageProtobufReply 4 {}", messageProtobufReply);
                
                // 保存消息
                persistMessage(messageProtobufQuery, messageProtobufReply, isUnanswered);
                
                // 发送消息
                messageSendService.sendProtobufMessage(messageProtobufReply);
                
                return answer;
            }
        } catch (Exception e) {
            log.error("Error in sendSyncMessage", e);
            String errorMessage = "服务暂时不可用，请稍后重试";
            messageProtobufReply.setContent(errorMessage);
            messageProtobufReply.setType(MessageTypeEnum.ERROR);
            
            // 保存错误消息
            persistMessage(messageProtobufQuery, messageProtobufReply, true);
            
            // 发送错误消息
            messageSendService.sendProtobufMessage(messageProtobufReply);
            
            return errorMessage;
        }
    }

    @Override
    public void persistMessage(MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply,
            Boolean isUnanswered) {
        // 调用重载方法，传入默认的token使用情况（0）和空prompt
        persistMessage(messageProtobufQuery, messageProtobufReply, isUnanswered, 0, 0, 0, "");
    }

    /**
     * 持久化消息，包含token使用情况
     * 
     * @param messageProtobufQuery 查询消息
     * @param messageProtobufReply 回复消息
     * @param isUnanswered 是否未回答
     * @param promptTokens prompt token数量
     * @param completionTokens completion token数量
     * @param totalTokens 总token数量
     */
    public void persistMessage(MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply,
            Boolean isUnanswered, long promptTokens, long completionTokens, long totalTokens) {
        // 调用重载方法，传入空prompt
        persistMessage(messageProtobufQuery, messageProtobufReply, isUnanswered, promptTokens, completionTokens, totalTokens, "");
    }

    /**
     * 持久化消息，包含token使用情况和prompt内容
     * 
     * @param messageProtobufQuery 查询消息
     * @param messageProtobufReply 回复消息
     * @param isUnanswered 是否未回答
     * @param promptTokens prompt token数量
     * @param completionTokens completion token数量
     * @param totalTokens 总token数量
     * @param prompt 传入到大模型的完整prompt内容
     */
    public void persistMessage(MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply,
            Boolean isUnanswered, long promptTokens, long completionTokens, long totalTokens, String prompt) {
        // 调用重载方法，传入空的AI提供商和模型信息
        persistMessage(messageProtobufQuery, messageProtobufReply, isUnanswered, promptTokens, completionTokens, totalTokens, prompt, "", "");
    }

    /**
     * 持久化消息，包含token使用情况、prompt内容和AI模型信息
     * 
     * @param messageProtobufQuery 查询消息
     * @param messageProtobufReply 回复消息
     * @param isUnanswered 是否未回答
     * @param promptTokens prompt token数量
     * @param completionTokens completion token数量
     * @param totalTokens 总token数量
     * @param prompt 传入到大模型的完整prompt内容
     * @param aiProvider 大模型提供商
     * @param aiModel 大模型名称
     */
    public void persistMessage(MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply,
            Boolean isUnanswered, long promptTokens, long completionTokens, long totalTokens, String prompt, 
            String aiProvider, String aiModel) {
        Assert.notNull(messageProtobufQuery, "MessageProtobufQuery must not be null");
        Assert.notNull(messageProtobufReply, "MessageProtobufReply must not be null");
        log.info("BaseSpringAIService persistMessage messageProtobufQuery {}, messageProtobufReply {}, promptTokens {}, completionTokens {}, totalTokens {}, prompt {}, aiProvider {}, aiModel {}", 
            messageProtobufQuery.getContent(), messageProtobufReply.getContent(), 
            promptTokens, completionTokens, totalTokens, prompt, aiProvider, aiModel);
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
                .isUnAnswered(isUnanswered)
                .orgUid(extraObject.getOrgUid())
                //
                // 添加token使用情况
                .promptTokens((int) promptTokens)
                .completionTokens((int) completionTokens)
                .totalTokens((int) totalTokens)
                //
                // 添加完整prompt内容
                .prompt(prompt)
                //
                // 添加AI模型信息
                .aiProvider(aiProvider)
                .aiModel(aiModel)
                //
                .build();
        robotMessageCache.pushRequest(robotMessage);
    }

    @Override
    public String processDirectLlmRequest(String query, RobotProtobuf robot) {
        // 检查是否启用大模型
        if (robot.getLlm() == null || !robot.getLlm().getEnabled()) {
            log.warn("LLM未启用，无法处理直接LLM请求");
            return "抱歉，大模型功能未启用";
        }

        String prompt = robot.getLlm().getPrompt();
        log.info("处理直接LLM请求: query={}, prompt={}, robot={}", query, prompt, robot.getUid());
        
        // 使用通用方法处理知识库搜索和响应生成
        List<FaqProtobuf> searchResultList = searchKnowledgeBase(query, robot);
        log.info("processDirectLlmRequest searchResultList {}", searchResultList);
        
        // 构建提示词
        StringBuilder aiPrompt = new StringBuilder();
        if (StringUtils.hasText(prompt)) {
            aiPrompt.append(prompt);
        } else {
            // 如果未提供提示词，使用默认提示词
            aiPrompt.append("请根据以下上下文回答问题：\n\n");
        }
        
        // 如果有搜索结果，添加为上下文
        if (!searchResultList.isEmpty()) {
            String context = String.join("\n", searchResultList.stream().map(FaqProtobuf::toJson).toList());
            log.info("processDirectLlmRequest context {}", context);
            aiPrompt.append("上下文：\n").append(context).append("\n\n");
        }
        
        // 添加用户查询
        aiPrompt.append("问题：\n").append(query);
        
        // 获取完整的prompt内容用于存储
        String fullPromptContent = aiPrompt.toString();
        log.info("processDirectLlmRequest fullPromptContent: {}", fullPromptContent);
        
        // 调用子类实现的处理方法
        try {
            String response = processPromptSync(aiPrompt.toString(), robot, fullPromptContent);
            log.info("processDirectLlmRequest response {}", response);
            if (response != null && response.contains("<think>")) {
                log.debug("processDirectLlmRequest 替换前的内容: {}", response);
                response = response.replaceAll("(?s)<think>.*?</think>", "");
                log.debug("processDirectLlmRequest 替换后的内容: {}", response);
            }
            return response;
        } catch (Exception e) {
            log.error("处理LLM请求失败", e);
            return "抱歉，服务暂时不可用，请稍后再试。";
        }
    }

    // 2. 知识库搜索相关方法
    protected List<FaqProtobuf> searchKnowledgeBase(String query, RobotProtobuf robot) {
        // 如果知识库未启用，直接返回空列表
        if (!StringUtils.hasText(robot.getKbUid()) || !robot.getKbEnabled()) {
            log.info("知识库未启用或未指定知识库UID");
            return new ArrayList<>();
        }
        
        // 创建搜索结果列表
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
        
        return searchResultList;
    }

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

    private void executeVectorSearch(String query, String kbUid, List<FaqProtobuf> searchResultList) {
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

    // 3. LLM 处理相关方法
    private void processLlmResponse(String query, List<FaqProtobuf> searchResultList, RobotProtobuf robot,
            MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply,
            SseEmitter emitter) {
        log.info("BaseSpringAIService processLlmResponse searchContentList {}", searchResultList.size());
        //
        if (searchResultList.isEmpty()) {
            // 直接返回未找到相关问题答案
            String answer = robot.getLlm().getDefaultReply();
            processAnswerMessage(answer, MessageTypeEnum.TEXT, robot, messageProtobufQuery, messageProtobufReply, true,
                    emitter);
            return;
        }
        //
        String context = String.join("\n", searchResultList.stream().map(FaqProtobuf::toJson).toList());
        // 使用通用方法处理提示词和SSE消息
        createAndProcessPrompt(query, context, robot, messageProtobufQuery, messageProtobufReply, emitter);
    }

    protected void processLlmResponseWebsocket(String query, List<FaqProtobuf> searchResultList, RobotProtobuf robot,
            MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply) {
        log.info("BaseSpringAIService processLlmResponseWebsocket searchContentList {}", searchResultList.size());
        
        if (searchResultList.isEmpty()) {
            // 直接返回未找到相关问题答案
            String answer = robot.getLlm().getDefaultReply();
            messageProtobufReply.setType(MessageTypeEnum.TEXT);
            messageProtobufReply.setContent(answer);
            messageProtobufReply.setChannel(ChannelEnum.SYSTEM);
            
            // 保存消息到数据库
            persistMessage(messageProtobufQuery, messageProtobufReply, true);
            
            // 发送消息
            messageSendService.sendProtobufMessage(messageProtobufReply);
            return;
        }
        
        // 有搜索结果，构建上下文
        String context = String.join("\n", searchResultList.stream().map(FaqProtobuf::toJson).toList());
        
        // 构建消息列表，添加系统提示词
        String systemPrompt = robot.getLlm().getPrompt();
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(systemPrompt));
        
        // 添加搜索结果作为上下文
        if (StringUtils.hasText(context)) {
            messages.add(new SystemMessage("搜索结果: " + context));
        }
        
        // 添加用户查询
        messages.add(new UserMessage(query));
        
        // 创建并处理提示
        Prompt aiPrompt = new Prompt(messages);
        
        // 提取完整的prompt内容用于存储
        String fullPromptContent = extractFullPromptContent(messages);
        log.info("BaseSpringAIService processLlmResponseWebsocket fullPromptContent: {}", fullPromptContent);
        
        processPromptWebsocket(aiPrompt, robot, messageProtobufQuery, messageProtobufReply, fullPromptContent);
    }

    private void createAndProcessPrompt(String query, String context, RobotProtobuf robot,
            MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply,
            SseEmitter emitter) {

        // 创建系统提示信息，包含知识库上下文
        String systemPrompt = robot.getLlm().getPrompt();
        
        // 初始化消息列表
        List<Message> messages = new ArrayList<>();
        
        // 1. 先添加系统消息（不包含用户当前查询）
        messages.add(new SystemMessage(systemPrompt));
        
        // 2. 根据配置，拉取并添加历史聊天记录
        if (robot.getLlm() != null && robot.getLlm().getContextMsgCount() > 0) {
            String threadTopic = messageProtobufQuery.getThread().getTopic();
            int limit = robot.getLlm().getContextMsgCount();
            List<MessageEntity> recentMessages = messageRestService.getRecentMessages(threadTopic, limit);
            
            if (!recentMessages.isEmpty()) {
                log.info("添加 {} 条历史聊天记录", recentMessages.size());
                
                for (MessageEntity messageEntity : recentMessages) {
                    // 处理消息内容，移除<think>标签
                    String content = messageEntity.getContent();
                    if (content != null && content.contains("<think>")) {
                        log.debug("替换前的内容: {}", content);
                        content = content.replaceAll("(?s)<think>.*?</think>", "");
                        log.debug("替换后的内容: {}", content);
                    }

                    // 根据消息来源添加不同类型的消息
                    if (messageEntity.isFromVisitor() 
                            || messageEntity.isFromUser()
                            || messageEntity.isFromMember()) {
                        messages.add(new UserMessage(content));
                    } else {
                        // 机器人、客服、系统等其他消息统一使用SystemMessage
                        messages.add(new SystemMessage(content));
                    }
                }
            }
        }
        
        // 3. 添加当前查询的上下文信息
        if (StringUtils.hasText(context)) {
            messages.add(new SystemMessage("搜索结果: " + context));
        }
        
        // 4. 最后添加当前用户查询
        messages.add(new UserMessage(query));
        
        // 记录消息，用于调试
        log.info("BaseSpringAIService createAndProcessPrompt messages {}", messages);
        
        // 创建并处理提示
        Prompt aiPrompt = new Prompt(messages);
        
        // 提取完整的prompt内容用于存储
        String fullPromptContent = extractFullPromptContent(messages);
        log.info("BaseSpringAIService createAndProcessPrompt fullPromptContent: {}", fullPromptContent);
        
        processPromptSse(aiPrompt, robot, messageProtobufQuery, messageProtobufReply, emitter, fullPromptContent);
    }

    // private String createAndProcessPromptSync(String query, String context, RobotProtobuf robot,
    //         MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply) {
    //     return createAndProcessPromptSyncWithPrompt(query, context, robot, messageProtobufQuery, messageProtobufReply).getResponse();
    // }

    private PromptResult createAndProcessPromptSyncWithPrompt(String query, String context, RobotProtobuf robot,
            MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply) {
        
        // 创建系统提示信息，包含知识库上下文
        String systemPrompt = robot.getLlm().getPrompt();
        
        // 初始化消息列表
        List<Message> messages = new ArrayList<>();
        
        // 1. 先添加系统消息（不包含用户当前查询）
        messages.add(new SystemMessage(systemPrompt));
        
        // 2. 根据配置，拉取并添加历史聊天记录
        if (robot.getLlm() != null && robot.getLlm().getContextMsgCount() > 0) {
            String threadTopic = messageProtobufQuery.getThread().getTopic();
            int limit = robot.getLlm().getContextMsgCount();
            List<MessageEntity> recentMessages = messageRestService.getRecentMessages(threadTopic, limit);
            
            if (!recentMessages.isEmpty()) {
                log.info("添加 {} 条历史聊天记录", recentMessages.size());
                
                for (MessageEntity messageEntity : recentMessages) {
                    // 处理消息内容，移除<think>标签
                    String content = messageEntity.getContent();
                    if (content != null && content.contains("<think>")) {
                        log.debug("替换前的内容: {}", content);
                        content = content.replaceAll("(?s)<think>.*?</think>", "");
                        log.debug("替换后的内容: {}", content);
                    }

                    // 根据消息来源添加不同类型的消息
                    if (messageEntity.isFromVisitor() 
                            || messageEntity.isFromUser()
                            || messageEntity.isFromMember()) {
                        messages.add(new UserMessage(content));
                    } else {
                        // 机器人、客服、系统等其他消息统一使用SystemMessage
                        messages.add(new SystemMessage(content));
                    }
                }
            }
        }
        
        // 3. 添加当前查询的上下文信息
        if (StringUtils.hasText(context)) {
            messages.add(new SystemMessage("搜索结果: " + context));
        }
        
        // 4. 最后添加当前用户查询
        messages.add(new UserMessage(query));
        
        // 记录消息，用于调试
        log.info("BaseSpringAIService createAndProcessPromptSync messages {}", messages);
        
        // 创建提示并处理
        Prompt aiPrompt = new Prompt(messages);
        
        // 提取完整的prompt内容用于存储
        String fullPromptContent = extractFullPromptContent(messages);
        log.info("BaseSpringAIService createAndProcessPromptSync fullPromptContent: {}", fullPromptContent);
        
        String response = processPromptSync(aiPrompt.toString(), robot, fullPromptContent);
        return new PromptResult(response, fullPromptContent);
    }

    private void processDirectResponse(String query, List<FaqProtobuf> searchContentList, RobotProtobuf robot,
            MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, SseEmitter emitter) {
        log.info("BaseSpringAIService processSearchResponse searchContentList {}", searchContentList);
        //
        if (searchContentList.isEmpty()) {
            // 直接返回未找到相关问题答案
            String answer = robot.getLlm().getDefaultReply(); // RobotConsts.ROBOT_UNMATCHED;
            processAnswerMessage(answer, MessageTypeEnum.TEXT, robot, messageProtobufQuery, messageProtobufReply,
                    true,
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
            processAnswerMessage(answer, MessageTypeEnum.FAQ_ANSWER, robot, messageProtobufQuery, messageProtobufReply,
                    false,
                    emitter);
        }
    }

    // 4. SSE/WebSocket 消息处理相关方法
    private void processAnswerMessage(String answer, MessageTypeEnum type, RobotProtobuf robot,
            MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, Boolean isUnanswered, SseEmitter emitter) {
        messageProtobufReply.setType(type);
        messageProtobufReply.setContent(answer);
        messageProtobufReply.setChannel(ChannelEnum.SYSTEM);
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

    protected void sendMessageWebsocket(MessageTypeEnum type, String content, MessageProtobuf messageProtobufReply) {
        messageProtobufReply.setType(type);
        messageProtobufReply.setContent(content);
        messageSendService.sendProtobufMessage(messageProtobufReply);
    }

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

    protected void sendStreamMessage(MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply,
            SseEmitter emitter, String content) {
        log.info("BaseSpringAIService sendStreamMessage content {}", content);
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
     * 发送流结束消息，包含token使用情况、prompt内容和AI模型信息
     *
     * @param messageProtobufQuery 查询消息
     * @param messageProtobufReply 回复消息
     * @param emitter SSE发射器
     * @param promptTokens prompt token数量
     * @param completionTokens completion token数量
     * @param totalTokens 总token数量
     * @param prompt 传入到大模型的完整prompt内容
     * @param aiProvider 大模型提供商
     * @param aiModel 大模型名称
     */
    protected void sendStreamEndMessage(MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply,
            SseEmitter emitter, long promptTokens, long completionTokens, long totalTokens, String prompt, String aiProvider, String aiModel) {
        log.info("BaseSpringAIService sendStreamEndMessage messageProtobufQuery {}, messageProtobufReply {}, promptTokens {}, completionTokens {}, totalTokens {}, prompt {}, aiProvider {}, aiModel {}", 
            messageProtobufQuery.getContent(), messageProtobufReply.getContent(), 
            promptTokens, completionTokens, totalTokens, prompt, aiProvider, aiModel);
        try {
            if (!isEmitterCompleted(emitter)) {
                // 发送流结束标记
                messageProtobufReply.setType(MessageTypeEnum.STREAM_END);
                messageProtobufReply.setContent("");
                // 保存消息到数据库，包含token使用情况、prompt内容和AI模型信息
                persistMessage(messageProtobufQuery, messageProtobufReply, false, promptTokens, completionTokens, totalTokens, prompt, aiProvider, aiModel);
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

    // 5. 工具和辅助方法
    protected String extractTextFromResponse(Object response) {
        try {
            if (response == null) {
                return "No response received";
            }
            
            // 处理不同类型的响应
            if (response instanceof ChatResponse) {
                // 新版API返回ChatResponse
                return ((ChatResponse)response).getResult().getOutput().getText();
            } else if (response instanceof String) {
                // 字符串直接返回
                return (String) response;
            } else if (response instanceof AssistantMessage) {
                // AssistantMessage提取文本
                return ((AssistantMessage)response).getText();
            } else {
                // 其他类型尝试toString()
                log.info("Unknown response type: {}", response.getClass().getName());
                return response.toString();
            }

        } catch (Exception e) {
            log.error("Error extracting text from response", e);
            return "Error processing response";
        }
    }
            
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

    protected <T> T createDynamicOptions(RobotLlm llm, Function<RobotLlm, T> optionBuilder) {
        if (llm == null || !StringUtils.hasText(llm.getTextModel())) {
            return null;
        }

        try {
            // 使用提供的构建器函数创建选项
            return optionBuilder.apply(llm);
        } catch (Exception e) {
            log.error("Error creating dynamic options for model {}", llm.getTextModel(), e);
            return null;
        }
    }

    // ==================== AI Token Statistics Methods ====================
    
    /**
     * Record AI token usage statistics by publishing an event
     * 
     * @param robot Robot configuration
     * @param aiProvider AI provider name
     * @param aiModelType AI model type
     * @param promptTokens Number of prompt tokens consumed
     * @param completionTokens Number of completion tokens consumed
     * @param success Whether the request was successful
     * @param responseTime Response time in milliseconds
     */
    protected void recordAiTokenUsage(RobotProtobuf robot, String aiProvider, String aiModelType,
            long promptTokens, long completionTokens, boolean success, long responseTime) {
        try {
            if (robot == null || !StringUtils.hasText(robot.getOrgUid())) {
                log.warn("Cannot record AI token usage: robot or orgUid is null");
                return;
            }

            // 获取token单价（这里可以根据不同的模型和提供商设置不同的价格）
            java.math.BigDecimal tokenUnitPrice = getTokenUnitPrice(aiProvider, aiModelType);
            
            // 发布AI Token使用事件
            publishAiTokenUsageEvent(
                robot.getOrgUid(), 
                aiProvider, 
                aiModelType, 
                promptTokens, 
                completionTokens, 
                success, 
                responseTime, 
                tokenUnitPrice
            );
            
            log.info("Published AI token usage event: provider={}, model={}, tokens={}+{}={}, success={}, responseTime={}ms", 
                    aiProvider, aiModelType, promptTokens, completionTokens, promptTokens + completionTokens, success, responseTime);
        } catch (Exception e) {
            log.error("Error publishing AI token usage event", e);
        }
    }

    /**
     * Get token unit price based on AI provider and model type
     * 
     * @param aiProvider AI provider
     * @param aiModelType AI model type
     * @return Token unit price in USD
     */
    protected java.math.BigDecimal getTokenUnitPrice(String aiProvider, String aiModelType) {
        // 这里可以根据不同的提供商和模型设置不同的价格
        // 实际应用中可以从配置文件或数据库中读取
        if (LlmConsts.OPENAI.equalsIgnoreCase(aiProvider)) {
            if ("gpt-4".equalsIgnoreCase(aiModelType)) {
                return new java.math.BigDecimal("0.03"); // $0.03 per 1K tokens
            } else if ("gpt-3.5-turbo".equalsIgnoreCase(aiModelType)) {
                return new java.math.BigDecimal("0.002"); // $0.002 per 1K tokens
            }
        } else if (LlmConsts.BAIDU.equalsIgnoreCase(aiProvider)) {
            if ("ernie-bot-4".equalsIgnoreCase(aiModelType)) {
                return new java.math.BigDecimal("0.012"); // $0.012 per 1K tokens
            } else if ("ernie-bot".equalsIgnoreCase(aiModelType)) {
                return new java.math.BigDecimal("0.002"); // $0.002 per 1K tokens
            }
        } else if (LlmConsts.ZHIPUAI.equalsIgnoreCase(aiProvider)) {
            if ("glm-4".equalsIgnoreCase(aiModelType)) {
                return new java.math.BigDecimal("0.01"); // $0.01 per 1K tokens
            } else if ("glm-3-turbo".equalsIgnoreCase(aiModelType)) {
                return new java.math.BigDecimal("0.005"); // $0.005 per 1K tokens
            }
        } else if (LlmConsts.DASHSCOPE.equalsIgnoreCase(aiProvider)) {
            if ("qwen-turbo".equalsIgnoreCase(aiModelType)) {
                return new java.math.BigDecimal("0.002"); // $0.002 per 1K tokens
            } else if ("qwen-plus".equalsIgnoreCase(aiModelType)) {
                return new java.math.BigDecimal("0.01"); // $0.01 per 1K tokens
            }
        } else if (LlmConsts.DEEPSEEK.equalsIgnoreCase(aiProvider)) {
            if ("deepseek-chat".equalsIgnoreCase(aiModelType)) {
                return new java.math.BigDecimal("0.002"); // $0.002 per 1K tokens
            } else if ("deepseek-coder".equalsIgnoreCase(aiModelType)) {
                return new java.math.BigDecimal("0.005"); // $0.005 per 1K tokens
            }
        } else if (LlmConsts.GITEE.equalsIgnoreCase(aiProvider)) {
            if ("gitee-chat".equalsIgnoreCase(aiModelType)) {
                return new java.math.BigDecimal("0.001"); // $0.001 per 1K tokens
            }
        } else if (LlmConsts.SILICONFLOW.equalsIgnoreCase(aiProvider)) {
            if ("siliconflow-chat".equalsIgnoreCase(aiModelType)) {
                return new java.math.BigDecimal("0.002"); // $0.002 per 1K tokens
            }
        } else if (LlmConsts.VOLCENGINE.equalsIgnoreCase(aiProvider)) {
            if ("volcengine-chat".equalsIgnoreCase(aiModelType)) {
                return new java.math.BigDecimal("0.002"); // $0.002 per 1K tokens
            }
        } else if (LlmConsts.OPENROUTER.equalsIgnoreCase(aiProvider)) {
            if ("openrouter-chat".equalsIgnoreCase(aiModelType)) {
                return new java.math.BigDecimal("0.002"); // $0.002 per 1K tokens
            }
        } else if (LlmConsts.TENCENT.equalsIgnoreCase(aiProvider)) {
            if ("hunyuan-pro".equalsIgnoreCase(aiModelType)) {
                return new java.math.BigDecimal("0.01"); // $0.01 per 1K tokens
            } else if ("hunyuan-standard".equalsIgnoreCase(aiModelType)) {
                return new java.math.BigDecimal("0.002"); // $0.002 per 1K tokens
            }
        } else if (LlmConsts.OLLAMA.equalsIgnoreCase(aiProvider)) {
            if ("llama2".equalsIgnoreCase(aiModelType)) {
                return new java.math.BigDecimal("0.0001"); // $0.0001 per 1K tokens (local model)
            } else if ("mistral".equalsIgnoreCase(aiModelType)) {
                return new java.math.BigDecimal("0.0001"); // $0.0001 per 1K tokens (local model)
            }
        }
        
        // 默认价格
        return new java.math.BigDecimal("0.01");
    }

    /**
     * Extract token usage from ChatResponse metadata
     * 
     * @param response ChatResponse from AI service
     * @return TokenUsage object containing prompt and completion tokens
     */
    protected TokenUsage extractTokenUsage(Object response) {
        try {
            if (response instanceof org.springframework.ai.chat.model.ChatResponse) {
                org.springframework.ai.chat.model.ChatResponse chatResponse = (org.springframework.ai.chat.model.ChatResponse) response;
                var metadata = chatResponse.getMetadata();
                log.info("BaseSpringAIService extractTokenUsage metadata {}", metadata);
                
                if (metadata != null) {
                    // 尝试从metadata中提取token信息
                    Object promptTokens = metadata.get("prompt_tokens");
                    Object completionTokens = metadata.get("completion_tokens");
                    Object totalTokens = metadata.get("total_tokens");
                    
                    long prompt = 0, completion = 0, total = 0;
                    
                    if (promptTokens instanceof Number) {
                        prompt = ((Number) promptTokens).longValue();
                    }
                    if (completionTokens instanceof Number) {
                        completion = ((Number) completionTokens).longValue();
                    }
                    if (totalTokens instanceof Number) {
                        total = ((Number) totalTokens).longValue();
                    }
                    
                    // 如果没有单独的prompt和completion tokens，但有total tokens，则估算
                    if (total > 0 && (prompt == 0 || completion == 0)) {
                        // 简单估算：假设prompt占30%，completion占70%
                        prompt = (long) (total * 0.3);
                        completion = total - prompt;
                    }
                    
                    // 如果仍然没有token信息，尝试从其他字段提取
                    if (total == 0) {
                        // 尝试从usage字段提取（智谱AI的特殊情况）
                        Object usage = metadata.get("usage");
                        if (usage != null) {
                            log.info("BaseSpringAIService extractTokenUsage found usage field: {}", usage);
                            // 如果usage是Map类型，尝试从中提取token信息
                            if (usage instanceof java.util.Map) {
                                java.util.Map<?, ?> usageMap = (java.util.Map<?, ?>) usage;
                                Object usagePromptTokens = usageMap.get("prompt_tokens");
                                Object usageCompletionTokens = usageMap.get("completion_tokens");
                                Object usageTotalTokens = usageMap.get("total_tokens");
                                
                                if (usagePromptTokens instanceof Number) {
                                    prompt = ((Number) usagePromptTokens).longValue();
                                }
                                if (usageCompletionTokens instanceof Number) {
                                    completion = ((Number) usageCompletionTokens).longValue();
                                }
                                if (usageTotalTokens instanceof Number) {
                                    total = ((Number) usageTotalTokens).longValue();
                                }
                            }
                        }
                        
                        // 如果仍然没有token信息，尝试从其他可能的字段提取
                        if (total == 0) {
                            // 尝试从其他可能的字段名提取
                            String[] possibleTotalTokenKeys = {"total_tokens", "tokens", "token_count", "usage_total"};
                            String[] possiblePromptTokenKeys = {"prompt_tokens", "input_tokens", "request_tokens"};
                            String[] possibleCompletionTokenKeys = {"completion_tokens", "output_tokens", "response_tokens"};
                            
                            for (String key : possibleTotalTokenKeys) {
                                Object value = metadata.get(key);
                                if (value instanceof Number) {
                                    total = ((Number) value).longValue();
                                    break;
                                }
                            }
                            
                            for (String key : possiblePromptTokenKeys) {
                                Object value = metadata.get(key);
                                if (value instanceof Number) {
                                    prompt = ((Number) value).longValue();
                                    break;
                                }
                            }
                            
                            for (String key : possibleCompletionTokenKeys) {
                                Object value = metadata.get(key);
                                if (value instanceof Number) {
                                    completion = ((Number) value).longValue();
                                    break;
                                }
                            }
                        }
                        
                        // 如果仍然没有找到token信息，尝试从response的原始数据中提取
                        if (total == 0) {
                            log.info("BaseSpringAIService extractTokenUsage no token info found in metadata, checking response structure");
                            // 记录所有metadata键值对以便调试
                            for (String key : metadata.keySet()) {
                                Object value = metadata.get(key);
                                log.info("BaseSpringAIService extractTokenUsage metadata [{}]: {} (class: {})", 
                                        key, value, value != null ? value.getClass().getName() : "null");
                            }
                            
                            // 尝试从metadata的toString()中解析usage信息
                            String metadataStr = metadata.toString();
                            log.info("BaseSpringAIService extractTokenUsage parsing metadata string: {}", metadataStr);
                            
                            // 查找usage信息，格式通常是: DefaultUsage{promptTokens=16, completionTokens=14, totalTokens=30}
                            if (metadataStr.contains("DefaultUsage{") || metadataStr.contains("usage:")) {
                                // 提取promptTokens
                                int promptStart = metadataStr.indexOf("promptTokens=");
                                int promptEnd = metadataStr.indexOf(",", promptStart);
                                if (promptEnd == -1) promptEnd = metadataStr.indexOf("}", promptStart);
                                
                                // 提取completionTokens
                                int completionStart = metadataStr.indexOf("completionTokens=");
                                int completionEnd = metadataStr.indexOf(",", completionStart);
                                if (completionEnd == -1) completionEnd = metadataStr.indexOf("}", completionStart);
                                
                                // 提取totalTokens
                                int totalStart = metadataStr.indexOf("totalTokens=");
                                int totalEnd = metadataStr.indexOf("}", totalStart);
                                
                                if (promptStart > 0 && promptEnd > promptStart) {
                                    try {
                                        prompt = Long.parseLong(metadataStr.substring(promptStart + 13, promptEnd));
                                    } catch (NumberFormatException e) {
                                        log.warn("Could not parse promptTokens from: {}", metadataStr.substring(promptStart + 13, promptEnd));
                                    }
                                }
                                
                                if (completionStart > 0 && completionEnd > completionStart) {
                                    try {
                                        completion = Long.parseLong(metadataStr.substring(completionStart + 17, completionEnd));
                                    } catch (NumberFormatException e) {
                                        log.warn("Could not parse completionTokens from: {}", metadataStr.substring(completionStart + 17, completionEnd));
                                    }
                                }
                                
                                if (totalStart > 0 && totalEnd > totalStart) {
                                    try {
                                        total = Long.parseLong(metadataStr.substring(totalStart + 12, totalEnd));
                                    } catch (NumberFormatException e) {
                                        log.warn("Could not parse totalTokens from: {}", metadataStr.substring(totalStart + 12, totalEnd));
                                    }
                                }
                                
                                log.info("BaseSpringAIService extractTokenUsage parsed from string - prompt: {}, completion: {}, total: {}", 
                                        prompt, completion, total);
                            }
                            
                            // 尝试从response的其他部分提取token信息
                            if (total == 0) {
                                total = extractTokenUsageFromResponse(chatResponse);
                                if (total > 0) {
                                    // 简单估算：假设prompt占30%，completion占70%
                                    prompt = (long) (total * 0.3);
                                    completion = total - prompt;
                                }
                            }
                        }
                    }
                    
                    log.info("BaseSpringAIService extractTokenUsage extracted tokens - prompt: {}, completion: {}, total: {}", 
                            prompt, completion, total);
                    return new TokenUsage(prompt, completion, total);
                }
            }
        } catch (Exception e) {
            log.error("Error extracting token usage from response", e);
        }
        
        // 如果无法提取，返回默认值
        log.warn("BaseSpringAIService extractTokenUsage could not extract token usage, returning zeros");
        return new TokenUsage(0, 0, 0);
    }

    /**
     * Extract token usage from response object itself (for cases where metadata doesn't contain token info)
     * 
     * @param chatResponse ChatResponse object
     * @return total token count if found, 0 otherwise
     */
    private long extractTokenUsageFromResponse(org.springframework.ai.chat.model.ChatResponse chatResponse) {
        try {
            // 尝试从response的其他部分提取token信息
            // 检查是否有任何包含token信息的字段
            if (chatResponse.getResults() != null && !chatResponse.getResults().isEmpty()) {
                for (org.springframework.ai.chat.model.Generation generation : chatResponse.getResults()) {
                    var generationMetadata = generation.getMetadata();
                    if (generationMetadata != null) {
                        log.info("BaseSpringAIService extractTokenUsageFromResponse generation metadata: {}", generationMetadata);
                        // 检查generation metadata中是否有token信息
                        Object tokens = generationMetadata.get("tokens");
                        if (tokens instanceof Number) {
                            return ((Number) tokens).longValue();
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error extracting token usage from response object", e);
        }
        return 0;
    }

    /**
     * 提取完整的prompt内容
     * 
     * @param messages 消息列表
     * @return 格式化的完整prompt内容
     */
    protected String extractFullPromptContent(List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            return "";
        }
        
        StringBuilder fullPrompt = new StringBuilder();
        for (Message message : messages) {
            String content = message.getText();
            if (content != null && !content.trim().isEmpty()) {
                if (message instanceof SystemMessage) {
                    fullPrompt.append("[系统] ").append(content).append("\n");
                } else if (message instanceof UserMessage) {
                    fullPrompt.append("[用户] ").append(content).append("\n");
                } else if (message instanceof AssistantMessage) {
                    fullPrompt.append("[助手] ").append(content).append("\n");
                } else {
                    fullPrompt.append(content).append("\n");
                }
            }
        }
        
        return fullPrompt.toString().trim();
    }


    /**
     * 发布AI Token使用事件
     * 
     * @param orgUid 组织UID
     * @param aiProvider AI提供商
     * @param aiModelType AI模型类型
     * @param promptTokens Prompt token数量
     * @param completionTokens Completion token数量
     * @param success 请求是否成功
     * @param responseTime 响应时间（毫秒）
     * @param tokenUnitPrice Token单价
     */
    public void publishAiTokenUsageEvent(String orgUid, String aiProvider, String aiModelType,
            long promptTokens, long completionTokens, boolean success, long responseTime, BigDecimal tokenUnitPrice) {
        
        LlmTokenUsageEvent event = new LlmTokenUsageEvent(this, orgUid, aiProvider, aiModelType,
                promptTokens, completionTokens, success, responseTime, tokenUnitPrice);
        
        applicationEventPublisher.publishEvent(event);
        
        log.debug("Published AI token usage event: provider={}, model={}, tokens={}+{}={}, success={}, responseTime={}ms", 
                aiProvider, aiModelType, promptTokens, completionTokens, promptTokens + completionTokens, success, responseTime);
    }

    /**
     * Prompt处理结果类
     */
    protected static class PromptResult {
        private final String response;
        private final String fullPromptContent;

        public PromptResult(String response, String fullPromptContent) {
            this.response = response;
            this.fullPromptContent = fullPromptContent;
        }

        public String getResponse() { return response; }
        public String getFullPromptContent() { return fullPromptContent; }
    }

    /**
     * Token usage data class
     */
    protected static class TokenUsage {
        private final long promptTokens;
        private final long completionTokens;
        private final long totalTokens;

        public TokenUsage(long promptTokens, long completionTokens, long totalTokens) {
            this.promptTokens = promptTokens;
            this.completionTokens = completionTokens;
            this.totalTokens = totalTokens;
        }

        public long getPromptTokens() { return promptTokens; }
        public long getCompletionTokens() { return completionTokens; }
        public long getTotalTokens() { return totalTokens; }
    }

    // 带prompt参数的抽象方法重载
    protected abstract void processPromptWebsocket(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, String fullPromptContent);

    // 带prompt参数的抽象方法重载
    protected abstract String processPromptSync(String message, RobotProtobuf robot, String fullPromptContent);

    protected abstract void processPromptSse(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, SseEmitter emitter, String fullPromptContent);
}