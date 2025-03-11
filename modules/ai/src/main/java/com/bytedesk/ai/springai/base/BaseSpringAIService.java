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
import com.bytedesk.ai.springai.spring.SpringAIService;
import com.bytedesk.ai.springai.spring.SpringAIVectorService;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageProtobuf;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseSpringAIService implements SpringAIService {

    protected final Optional<SpringAIVectorService> springAIVectorService;
    protected final IMessageSendService messageSendService;

    protected BaseSpringAIService(Optional<SpringAIVectorService> springAIVectorService,
                                IMessageSendService messageSendService) {
        this.springAIVectorService = springAIVectorService;
        this.messageSendService = messageSendService;
    }

    @Override
    public void sendWsKbMessage(String query, RobotEntity robot, MessageProtobuf messageProtobuf) {
        Assert.hasText(query, "Query must not be empty");
        Assert.notNull(robot, "RobotEntity must not be null");
        Assert.notNull(messageProtobuf, "MessageProtobuf must not be null");
        Assert.isTrue(springAIVectorService.isPresent(), "SpringAIVectorService must be present");

        List<String> contentList = springAIVectorService.get().searchText(query, robot.getKbUid());
        String context = String.join("\n", contentList);
        String prompt = buildKbPrompt(robot.getLlm().getPrompt(), query, context);
        
        List<Message> messages = buildMessages(robot.getLlm().getPrompt(), prompt);
        Prompt aiPrompt = new Prompt(messages);
        
        processPrompt(aiPrompt, messageProtobuf);
    }

    @Override
    public void sendWsMessage(String query, RobotEntity robot, MessageProtobuf messageProtobuf) {
        Assert.hasText(query, "Query must not be empty");
        Assert.notNull(robot, "Robot must not be null");
        Assert.notNull(messageProtobuf, "MessageProtobuf must not be null");

        String prompt = buildPrompt(robot.getLlm().getPrompt(), query);
        List<Message> messages = buildMessages(robot.getLlm().getPrompt(), prompt);
        Prompt aiPrompt = new Prompt(messages);
        
        processPrompt(aiPrompt, messageProtobuf);
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
    public void sendWsKbAutoReply(String query, String kbUid, MessageProtobuf messageProtobuf) {
        Assert.hasText(query, "Query must not be empty");
        Assert.hasText(kbUid, "Knowledge base UID must not be empty");
        Assert.notNull(messageProtobuf, "MessageProtobuf must not be null");
        Assert.isTrue(springAIVectorService.isPresent(), "SpringAIVectorService must be present");

        List<String> contentList = springAIVectorService.get().searchText(query, kbUid);
        String context = String.join("\n", contentList);
        String prompt = RobotConsts.PROMPT_BLUEPRINT.replace("{context}", context).replace("{query}", query);
        
        List<Message> messages = buildMessages(prompt, prompt);
        Prompt aiPrompt = new Prompt(messages);
        
        processPrompt(aiPrompt, messageProtobuf);
    }

    protected String buildKbPrompt(String systemPrompt, String query, String context) {
        return systemPrompt + "\n" +
               "用户查询: " + query + "\n" +
               "历史聊天记录: " + "\n" +
               "搜索结果: " + context;
    }

    protected String buildPrompt(String systemPrompt, String query) {
        return systemPrompt + "\n" + query;
    }

    protected List<Message> buildMessages(String systemPrompt, String prompt) {
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(systemPrompt));
        messages.add(new UserMessage(prompt));
        return messages;
    }

    // 抽象方法，由具体实现类提供
    protected abstract void processPrompt(Prompt prompt, MessageProtobuf messageProtobuf);
    protected abstract String processPromptSync(String message);
    protected abstract void processPromptSSE(String uid, String message, SseEmitter emitter);
    // 抽象方法，由具体实现类提供
    protected abstract String generateFaqPairs(String prompt);
} 