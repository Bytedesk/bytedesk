package com.bytedesk.ai.springai.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.ai.robot.RobotConsts;
import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.ai.robot.RobotRestService;
import com.bytedesk.ai.robot_message.RobotMessage;
import com.bytedesk.ai.springai.spring.SpringAIService;
import com.bytedesk.ai.springai.spring.SpringAIVectorService;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessagePersistCache;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.uid.UidUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseSpringAIService implements SpringAIService {

    protected final Optional<SpringAIVectorService> springAIVectorService;
    protected final IMessageSendService messageSendService;
    protected final UidUtils uidUtils;
    protected final RobotRestService robotRestService;
    protected final ThreadRestService threadRestService;
    protected final MessagePersistCache messagePersistCache;

    protected BaseSpringAIService(Optional<SpringAIVectorService> springAIVectorService,
            IMessageSendService messageSendService,
            UidUtils uidUtils,
            RobotRestService robotRestService,
            ThreadRestService threadRestService,
            MessagePersistCache messagePersistCache) {
        this.springAIVectorService = springAIVectorService;
        this.messageSendService = messageSendService;
        this.uidUtils = uidUtils;
        this.robotRestService = robotRestService;
        this.threadRestService = threadRestService;
        this.messagePersistCache = messagePersistCache;
    }

    @Override
    public void sendWebsocketMessage(String query, RobotEntity robot, MessageProtobuf messageProtobuf) {
        Assert.hasText(query, "Query must not be empty");
        Assert.notNull(robot, "RobotEntity must not be null");
        Assert.notNull(messageProtobuf, "MessageProtobuf must not be null");
        Assert.isTrue(springAIVectorService.isPresent(), "SpringAIVectorService must be present");

        String prompt = "";
        if (StringUtils.hasText(robot.getKbUid()) && robot.isKbEnabled()) {
            List<String> contentList = springAIVectorService.get().searchText(query, robot.getKbUid());
            String context = String.join("\n", contentList);
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
        processPrompt(aiPrompt, messageProtobuf);
    }

    @Override
    public void sendSseMessage(String query, RobotProtobuf robot, MessageProtobuf messageProtobuf, SseEmitter emitter) {
        Assert.hasText(query, "Query must not be empty");
        Assert.notNull(emitter, "SseEmitter must not be null");
        // sendSseTypingMessage(messageProtobuf, emitter);
        //
        String prompt = "";
        if (StringUtils.hasText(robot.getKbUid()) && robot.getIsKbEnabled()) {
            List<String> contentList = springAIVectorService.get().searchText(query, robot.getKbUid());
            if (contentList.isEmpty()) {
                // TODO: 记录未找到相关答案的问题到数据库
                RobotMessage robotMessage = RobotMessage.builder()
                        .uid(uidUtils.getUid())
                        .content(query)
                        .robot(robot.toJson())
                        .build();

                // 直接返回未找到相关问题答案
                messageProtobuf.setType(MessageTypeEnum.TEXT);
                messageProtobuf.setContent("未查找到相关问题答案");
                messageProtobuf.setClient(ClientEnum.SYSTEM);
                // 保存消息到数据库
                String messageJson = JSON.toJSONString(messageProtobuf);
                persistMessage(messageJson);
                try {
                    // 发送SSE事件
                    emitter.send(SseEmitter.event()
                            .data(messageJson)
                            .id(messageProtobuf.getUid())
                            .name("message"));
                } catch (Exception e) {
                    log.error("BaseSpringAIService sendSseMemberMessage Error sending SSE event 1：", e);
                    emitter.completeWithError(e);
                }
                return;
            }
            String context = String.join("\n", contentList);
            // TODO: 根据配置，拉取历史聊天记录
            // String history = "";
            prompt = buildKbPrompt(robot.getLlm().getPrompt(), query, context);
        } else {
            prompt = robot.getLlm().getPrompt();
        }
        // TODO: 判断是否开启大模型
        // TODO: 返回消息中携带消息搜索结果(来源依据)
        //
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(prompt));
        messages.add(new UserMessage(query));
        log.info("BaseSpringAIService sendSseMemberMessage messages {}", messages);
        //
        Prompt aiPrompt = new Prompt(messages);
        processPromptSSE(aiPrompt, messageProtobuf, emitter);
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
    public void persistMessage(String messageJson) {
        messagePersistCache.pushForPersist(messageJson);
    }

    // private void sendSseTypingMessage(MessageProtobuf messageProtobuf, SseEmitter
    // emitter) {
    // //
    // MessageProtobuf clonedMessage = SerializationUtils.clone(messageProtobuf);
    // clonedMessage.setUid(messageProtobuf.getUid());
    // clonedMessage.setType(MessageTypeEnum.TYPING);
    // // clonedMessage.setContent(I18Consts.I18N_TYPING);
    // // clonedMessage.setContent("...");
    // try {
    // emitter.send(SseEmitter.event()
    // .data(JSON.toJSONString(clonedMessage))
    // .id(clonedMessage.getUid())
    // .name("message"));
    // } catch (Exception e) {
    // // TODO: handle exception
    // }
    // }

    public String buildKbPrompt(String systemPrompt, String query, String context) {
        return systemPrompt + "\n" +
                "用户查询: " + query + "\n" +
                "历史聊天记录: " + "\n" +
                "搜索结果: " + context;
    }

    // 抽象方法，由具体实现类提供
    protected abstract void processPrompt(Prompt prompt, MessageProtobuf messageProtobuf);

    protected abstract String processPromptSync(String message);

    protected abstract void processPromptSSE(Prompt prompt, MessageProtobuf messageProtobuf, SseEmitter emitter);

    // 抽象方法，由具体实现类提供
    protected abstract String generateFaqPairs(String prompt);
}