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

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseSpringAIService implements SpringAIService {

    @Autowired
    protected SpringAIVectorStoreService springAIVectorService;

    @Autowired
    protected SpringAIFullTextService springAIFullTextService;

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
    public void sendWebsocketMessage(String query, RobotProtobuf robot, MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply) {
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
        // sendSseTypingMessage(messageProtobuf, emitter);
        // search type
        List<String> searchContentList = new ArrayList<>();
        if (robot.getLlm().getSearchType() == RobotSearchTypeEnum.FULLTEXT.name()) {
            // 使用全文搜索
            List<String> fullTextList = springAIFullTextService.searchQa(query, robot.getKbUid(), null, null);
            searchContentList.addAll(fullTextList);
        } else if (robot.getLlm().getSearchType() == RobotSearchTypeEnum.VECTOR.name()) {
            // 使用向量搜索
            List<String> contentList = springAIVectorService.searchText(query, robot.getKbUid());
            searchContentList.addAll(contentList);
            // 
        } else if (robot.getLlm().getSearchType() == RobotSearchTypeEnum.MIXED.name()) {
            // 混合搜索
            List<String> fullTextList = springAIFullTextService.searchQa(query, robot.getKbUid(), null, null);
            searchContentList.addAll(fullTextList);
            List<String> contentList = springAIVectorService.searchText(query, robot.getKbUid());
            searchContentList.addAll(contentList);
        } else {
            // 默认全文搜索
            List<String> fullTextList = springAIFullTextService.searchQa(query, robot.getKbUid(), null, null);
            searchContentList.addAll(fullTextList);
        }
        log.info("BaseSpringAIService sendSseMessage searchContentList {}", searchContentList);

        // 判断是否开启大模型
        if (robot.getLlm().isEnabled()) {
            // 启用大模型
            processLlmResponse(query, searchContentList, robot, messageProtobufQuery, messageProtobufReply, emitter);
        } else {
            // 未开启大模型，关键词匹配，使用搜索
            processSearchResponse(query, searchContentList, robot, messageProtobufQuery, messageProtobufReply, emitter);
        }
    }

    private void processLlmResponse(String query, List<String> searchContentList, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, SseEmitter emitter) {
        //
        String prompt = "";
        if (StringUtils.hasText(robot.getKbUid()) && robot.getIsKbEnabled()) {
            
            if (searchContentList.isEmpty()) {
                // 直接返回未找到相关问题答案
                String answer = RobotConsts.ROBOT_UNMATCHED;
                processAnswerMessage(answer, robot, messageProtobufQuery, messageProtobufReply, emitter);
                return;
            }
            String context = String.join("\n", searchContentList);
            // TODO: 根据配置，拉取历史聊天记录
            String history = "";
            prompt = buildKbPrompt(robot.getLlm().getPrompt(), query, history, context);
        } else {
            prompt = robot.getLlm().getPrompt();
        }
        // TODO: 返回消息中携带消息搜索结果(来源依据)
        //
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(prompt));
        messages.add(new UserMessage(query));
        log.info("BaseSpringAIService sendSseMemberMessage messages {}", messages);
        //
        Prompt aiPrompt = new Prompt(messages);
        
        // 添加机器人配置信息到消息中，使子类能获取
        // messageProtobufQuery.setRobotLlm(robot.getLlm());
        
        processPromptSSE(aiPrompt, robot, messageProtobufQuery, messageProtobufReply, emitter);
    }

    private void processSearchResponse(String query, List<String> searchContentList, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, SseEmitter emitter) {

        if (StringUtils.hasText(robot.getKbUid()) && robot.getIsKbEnabled()) {

            if (searchContentList.isEmpty()) {
                // 直接返回未找到相关问题答案
                String answer = RobotConsts.ROBOT_UNMATCHED;
                processAnswerMessage(answer, robot, messageProtobufQuery, messageProtobufReply, emitter);
                return;
            } else {
                // 搜索到内容，返回搜索内容
                String answer = String.join("\n", searchContentList);
                processAnswerMessage(answer, robot, messageProtobufQuery, messageProtobufReply, emitter);
            }
        } else {
            // 未设置知识库
            // 直接返回未找到相关问题答案
            String answer = RobotConsts.ROBOT_UNMATCHED;
            processAnswerMessage(answer, robot, messageProtobufQuery, messageProtobufReply, emitter);
        }
    }

    private void processAnswerMessage(String answer, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, SseEmitter emitter) {
        messageProtobufReply.setType(MessageTypeEnum.TEXT);
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

    /**
     * 从MessageProtobuf中提取RobotLlm配置
     * 
     * @param messageProtobuf 包含RobotLlm配置的消息
     * @return 提取的RobotLlm配置，如果无法提取则返回null
     */
    // protected RobotLlm extractRobotLlm(MessageProtobuf messageProtobuf) {
    //     if (messageProtobuf == null) {
    //         return null;
    //     }
        
    //     try {
    //         // 假设我们在MessageProtobuf中添加了RobotLlm字段
    //         return messageProtobuf.getRobotLlm();
    //     } catch (Exception e) {
    //         log.warn("Failed to extract RobotLlm from message", e);
    //         return null;
    //     }
    // }
}