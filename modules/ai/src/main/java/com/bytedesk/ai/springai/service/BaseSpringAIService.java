package com.bytedesk.ai.springai.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bytedesk.ai.robot.RobotConsts;
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
import com.bytedesk.kbase.llm.qa.QaElastic;
import com.bytedesk.kbase.llm.qa.QaElasticSearchResult;
import com.bytedesk.kbase.llm.qa.QaProtobuf;
import com.bytedesk.kbase.llm.qa.QaService;

import lombok.extern.slf4j.Slf4j;
import com.alibaba.fastjson2.JSON;

@Slf4j
public abstract class BaseSpringAIService implements SpringAIService {

    @Autowired
    protected SpringAIVectorStoreService springAIVectorService;

    @Autowired
    protected QaService qaService;

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
        List<QaProtobuf> qaProtobufList = new ArrayList<>();
        
        // 根据搜索类型执行相应的搜索
        String searchType = robot.getLlm().getSearchType();
        if (searchType == null) {
            searchType = RobotSearchTypeEnum.FULLTEXT.name(); // 默认使用全文搜索
        }
        
        // 执行搜索
        switch (RobotSearchTypeEnum.valueOf(searchType)) {
            case VECTOR:
                log.info("使用向量搜索");
                executeVectorSearch(query, robot.getKbUid(), searchContentList, qaProtobufList);
                break;
            case MIXED:
                log.info("使用混合搜索");
                executeFulltextSearch(query, robot.getKbUid(), searchContentList, qaProtobufList);
                executeVectorSearch(query, robot.getKbUid(), searchContentList, qaProtobufList);
                break;
            case FULLTEXT:
            default:
                log.info("使用全文搜索");
                executeFulltextSearch(query, robot.getKbUid(), searchContentList, qaProtobufList);
                break;
        }
        
        // 根据是否启用LLM决定如何处理结果
        if (robot.getLlm().isEnabled()) {
            // 启用大模型
            processLlmResponse(query, searchContentList, robot, messageProtobufQuery, messageProtobufReply, emitter);
        } else {
            // 未开启大模型，使用搜索结果直接回复
            processSearchResponse(query, qaProtobufList, robot, messageProtobufQuery, messageProtobufReply, emitter);
        }
        
        log.info("BaseSpringAIService sendSseMessage searchContentList {}", searchContentList);
    }
    
    /**
     * 执行全文搜索
     */
    private void executeFulltextSearch(String query, String kbUid, List<String> searchContentList, List<QaProtobuf> qaProtobufList) {
        List<QaElasticSearchResult> searchResults = qaService.searchQa(query, kbUid, null, null);
        for (QaElasticSearchResult withScore : searchResults) {
            QaElastic qa = withScore.getQaElastic();
            QaProtobuf qaProtobuf = QaProtobuf.fromElastic(qa);
            
            String formattedQa = qaProtobuf.toJson();
            searchContentList.add(formattedQa);
            qaProtobufList.add(qaProtobuf);
        }
    }
    
    /**
     * 执行向量搜索
     */
    private void executeVectorSearch(String query, String kbUid, List<String> searchContentList, List<QaProtobuf> qaProtobufList) {
        List<String> contentList = springAIVectorService.searchText(query, kbUid);
        searchContentList.addAll(contentList);
        
        for (String content : contentList) {
            QaProtobuf qaProtobuf = QaProtobuf.builder()
                    .uid(uidUtils.getUid())
                    .question(query)
                    .answer(content)
                    .build();
            qaProtobufList.add(qaProtobuf);
        }
    }

    private void processLlmResponse(String query, List<String> searchContentList, RobotProtobuf robot,
            MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply,
            SseEmitter emitter) {
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

    private void processSearchResponse(String query, List<QaProtobuf> searchContentList, RobotProtobuf robot,
            MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, SseEmitter emitter) {
        //
        if (searchContentList.isEmpty()) {
            // 直接返回未找到相关问题答案
            String answer = RobotConsts.ROBOT_UNMATCHED;
            processAnswerMessage(answer, MessageTypeEnum.TEXT, robot, messageProtobufQuery, messageProtobufReply, emitter);
            return;
        } else {
            // 搜索到内容，返回搜索内容
            QaProtobuf firstQa = searchContentList.get(0);
            QaProtobuf resultQa = QaProtobuf.builder()
                    .uid(firstQa.getUid())
                    .question(firstQa.getQuestion())
                    .answer(firstQa.getAnswer())
                    .build();
            
            // 如果有多个搜索结果，将其余的添加为相关问题
            if (searchContentList.size() > 1) {
                List<QaProtobuf> relatedQas = new ArrayList<>(searchContentList.subList(1, searchContentList.size()));
                resultQa.setRelatedQas(relatedQas);
            }
            
            // 将处理后的单个QaProtobuf对象转换为JSON字符串
            String answer = JSON.toJSONString(resultQa);
            processAnswerMessage(answer, MessageTypeEnum.QA, robot, messageProtobufQuery, messageProtobufReply, emitter);
        }
    }

    private void processAnswerMessage(String answer, MessageTypeEnum type, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, SseEmitter emitter) {
        messageProtobufReply.setType(type);
        messageProtobufReply.setContent(answer);
        messageProtobufReply.setClient(ClientEnum.SYSTEM);
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
            log.error("BaseSpringAIService sendSseMemberMessage Error sending SSE event 1：", e);
            emitter.completeWithError(e);
        }
    }

    @Override
    public String generateFaqPairsAsync(String chunk) {
        if (!StringUtils.hasText(chunk)) {
            return "";
        }
        String prompt = RobotConsts.PROMPT_LLM_GENERATE_FAQ_TEMPLATE.replace("{chunk}", chunk);
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

    // 新增通用的SSE错误处理方法
    protected void sendErrorSseMessage(String errorMessage, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, SseEmitter emitter) {
        try {
            messageProtobufReply.setType(MessageTypeEnum.ERROR);
            messageProtobufReply.setContent(errorMessage);
            persistMessage(messageProtobufQuery, messageProtobufReply);
            emitter.send(SseEmitter.event()
                    .data(messageProtobufReply.toJson())
                    .id(messageProtobufReply.getUid())
                    .name("error"));
            emitter.complete();
        } catch (Exception e) {
            log.error("Error sending SSE error message", e);
            try {
                emitter.completeWithError(e);
            } catch (Exception ex) {
                log.error("Failed to complete emitter with error", ex);
            }
        }
    }
}