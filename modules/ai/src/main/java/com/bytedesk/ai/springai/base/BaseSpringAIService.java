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

import com.bytedesk.ai.robot.RobotConsts;
import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.ai.robot.RobotRestService;
import com.bytedesk.ai.springai.spring.SpringAIService;
import com.bytedesk.ai.springai.spring.SpringAIVectorService;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessagePersistCache;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.thread.ThreadProtobuf;
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
        //
        processPrompt(aiPrompt, messageProtobuf);
    }

    @Override
    public void sendSseMemberMessage(String query, RobotProtobuf robot, MessageProtobuf messageProtobuf, SseEmitter emitter) {
        // Assert.hasText(messageJson, "Message must not be empty");
        Assert.notNull(emitter, "SseEmitter must not be null");

        log.info("BaseSpringAIService sendSseMemberMessage query {}", query);
        ThreadProtobuf threadProtobuf = messageProtobuf.getThread();
        //
        // MessageProtobuf clonedMessage = SerializationUtils.clone(messageProtobuf);
        // clonedMessage.setUid(uidUtils.getUid());
        // clonedMessage.setType(MessageTypeEnum.PROCESSING);
        // messageSendService.sendProtobufMessage(clonedMessage);
        //
        String prompt = "";
        if (StringUtils.hasText(robot.getKbUid()) && robot.getIsKbEnabled()) {
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
        log.info("BaseSpringAIService sendSseMemberMessage messages {}", messages);
        //
        Prompt aiPrompt = new Prompt(messages);
        //
        processPromptSSE(aiPrompt, threadProtobuf, messageProtobuf, emitter);
    }

    @Override
    public void sendSseVisitorMessage(String query, RobotEntity robot, MessageProtobuf messageProtobuf, SseEmitter emitter) {
        // Assert.hasText(messageJson, "Message must not be empty");
        Assert.notNull(emitter, "SseEmitter must not be null");

        log.info("BaseSpringAIService sendSseVisitorMessage query {}", query);
        ThreadProtobuf threadProtobuf = messageProtobuf.getThread();
        //
        // MessageProtobuf clonedMessage = SerializationUtils.clone(messageProtobuf);
        // clonedMessage.setUid(uidUtils.getUid());
        // clonedMessage.setType(MessageTypeEnum.PROCESSING);
        // messageSendService.sendProtobufMessage(clonedMessage);
        //
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
        log.info("BaseSpringAIService sendSseVisitorMessage messages {}", messages);
        //
        Prompt aiPrompt = new Prompt(messages);
        //
        processPromptSSE(aiPrompt, threadProtobuf, messageProtobuf, emitter);
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


    public String buildKbPrompt(String systemPrompt, String query, String context) {
        return systemPrompt + "\n" +
                "用户查询: " + query + "\n" +
                "历史聊天记录: " + "\n" +
                "搜索结果: " + context;
    }

    // 抽象方法，由具体实现类提供
    protected abstract void processPrompt(Prompt prompt, MessageProtobuf messageProtobuf);

    protected abstract String processPromptSync(String message);

    protected abstract void processPromptSSE(Prompt prompt, ThreadProtobuf threadProtobuf,
            MessageProtobuf messageProtobuf, SseEmitter emitter);

    // 抽象方法，由具体实现类提供
    protected abstract String generateFaqPairs(String prompt);
}