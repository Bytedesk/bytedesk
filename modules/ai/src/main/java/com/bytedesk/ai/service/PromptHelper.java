package com.bytedesk.ai.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.ai.service.agent.AgentGuidancePromptRequest;
import com.bytedesk.ai.service.agent.AgentGuidancePromptResolution;
import com.bytedesk.ai.service.agent.AgentGuidancePromptResolver;
import com.bytedesk.ai.service.agent.AgentGuidanceTraceRecorder;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.message.content.RobotContent;
import com.bytedesk.core.message.enums.MessageTypeEnum;
import com.bytedesk.kbase.llm_faq.FaqProtobuf;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Slf4j
@Component
public class PromptHelper {

    private final MessageRestService messageRestService;

    private final ObjectProvider<AgentGuidancePromptResolver> agentGuidancePromptResolverProvider;

    private final ObjectProvider<AgentGuidanceTraceRecorder> agentGuidanceTraceRecorderProvider;

    public List<Message> buildMessagesForSse(String query, String context, RobotProtobuf robot,
            MessageProtobuf messageProtobufQuery) {
        AgentGuidancePromptResolution guidanceResolution = resolveGuidance(query, context, robot, messageProtobufQuery,
                true);
        List<Message> messages = buildMessagesForSse(query, context, robot, messageProtobufQuery,
                guidanceResolution.systemPrompt());
        recordGuidanceTrace(query, context, robot, messageProtobufQuery, true, guidanceResolution, messages);
        return messages;
    }

    public List<Message> buildMessagesForSse(String query, String context, RobotProtobuf robot,
            MessageProtobuf messageProtobufQuery, String guidanceSystemPrompt) {
        // 添加空值检查
        if (robot.getLlm() == null) {
            log.error("robot.getLlm() 为 null,使用默认系统提示词");
            List<Message> messages = new ArrayList<>();
            messages.add(new SystemMessage(I18Consts.I18N_DEFAULT_SYSTEM_PROMPT));
            addSupplementalSystemMessages(messages, guidanceSystemPrompt, context);
            messages.add(new UserMessage(query));
            return messages;
        }

        String systemPrompt = robot.getLlm().getPrompt();
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(systemPrompt));

        if (robot.getLlm() != null && robot.getLlm().getContextMsgCount() > 0
                && messageProtobufQuery != null
                && messageProtobufQuery.getThread() != null) {
            String threadTopic = messageProtobufQuery.getThread().getTopic();
            int limit = robot.getLlm().getContextMsgCount();
            List<MessageEntity> recentMessages = messageRestService.getRecentMessages(threadTopic, limit);
            if (!recentMessages.isEmpty()) {
                log.info("添加 {} 条历史聊天记录", recentMessages.size());
                for (MessageEntity messageEntity : recentMessages) {
                    appendHistoryMessage(messages, messageEntity);
                }
            }
        }

        addSupplementalSystemMessages(messages, guidanceSystemPrompt, context);

        messages.add(new UserMessage(query));
        return messages;
    }

    public List<Message> buildMessagesForSync(String query, String context, RobotProtobuf robot,
            MessageProtobuf messageProtobufQuery) {
        AgentGuidancePromptResolution guidanceResolution = resolveGuidance(query, context, robot, messageProtobufQuery,
                false);
        List<Message> messages = buildMessagesForSync(query, context, robot, messageProtobufQuery,
                guidanceResolution.systemPrompt());
        recordGuidanceTrace(query, context, robot, messageProtobufQuery, false, guidanceResolution, messages);
        return messages;
    }

    public List<Message> buildMessagesForSync(String query, String context, RobotProtobuf robot,
            MessageProtobuf messageProtobufQuery, String guidanceSystemPrompt) {
        // 添加空值检查
        if (robot.getLlm() == null) {
            log.error("robot.getLlm() 为 null,使用默认系统提示词");
            List<Message> messages = new ArrayList<>();
            messages.add(new SystemMessage(I18Consts.I18N_DEFAULT_SYSTEM_PROMPT));
            addSupplementalSystemMessages(messages, guidanceSystemPrompt, context);
            messages.add(new UserMessage(query));
            return messages;
        }

        String systemPrompt = robot.getLlm().getPrompt();
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(systemPrompt));

        if (robot.getLlm() != null && robot.getLlm().getContextMsgCount() > 0
                && messageProtobufQuery != null
                && messageProtobufQuery.getThread() != null) {
            String threadTopic = messageProtobufQuery.getThread().getTopic();
            int limit = robot.getLlm().getContextMsgCount();
            List<MessageEntity> recentMessages = messageRestService.getRecentMessages(threadTopic, limit);
            if (!recentMessages.isEmpty()) {
                log.info("添加 {} 条历史聊天记录", recentMessages.size());
                for (MessageEntity messageEntity : recentMessages) {
                    appendHistoryMessage(messages, messageEntity);
                }
            }
        }

        addSupplementalSystemMessages(messages, guidanceSystemPrompt, context);

        messages.add(new UserMessage(query));
        return messages;
    }

    private void addSupplementalSystemMessages(List<Message> messages, String guidanceSystemPrompt, String context) {
        if (StringUtils.hasText(guidanceSystemPrompt)) {
            messages.add(new SystemMessage(guidanceSystemPrompt));
        }
        if (StringUtils.hasText(context)) {
            messages.add(new SystemMessage(I18Consts.I18N_SEARCH_RESULT_PREFIX + context));
        }
    }

    private AgentGuidancePromptResolution resolveGuidance(String query, String context, RobotProtobuf robot,
            MessageProtobuf messageProtobufQuery, boolean streaming) {
        AgentGuidancePromptResolver resolver = agentGuidancePromptResolverProvider.getIfAvailable();
        if (resolver == null) {
            return AgentGuidancePromptResolution.empty();
        }
        AgentGuidancePromptResolution resolution = resolver.resolve(
                new AgentGuidancePromptRequest(query, context, robot, messageProtobufQuery, streaming));
        return resolution != null ? resolution : AgentGuidancePromptResolution.empty();
    }

    private void recordGuidanceTrace(String query, String context, RobotProtobuf robot,
            MessageProtobuf messageProtobufQuery, boolean streaming,
            AgentGuidancePromptResolution guidanceResolution, List<Message> messages) {
        AgentGuidanceTraceRecorder recorder = agentGuidanceTraceRecorderProvider.getIfAvailable();
        if (recorder == null || guidanceResolution == null) {
            return;
        }
        recorder.recordPromptBuild(
                new AgentGuidancePromptRequest(query, context, robot, messageProtobufQuery, streaming),
                guidanceResolution,
                extractFullPromptContent(messages));
    }

    public Prompt toPrompt(List<Message> messages) {
        return new Prompt(messages);
    }

    public String createRobotStreamContentAnswer(String question, String answer,
            List<RobotContent.SourceReference> sourceReferences, RobotProtobuf robot) {
        StringBuilder contextBuilder = new StringBuilder();
        for (RobotContent.SourceReference source : sourceReferences) {
            contextBuilder.append("Source: ").append(source.getSourceName()).append("\n");
            contextBuilder.append("Content: ").append(source.getContentSummary()).append("\n\n");
        }
        RobotContent streamContent = RobotContent.builder()
                .question(question)
                .answer(answer)
                .sources(sourceReferences)
                .regenerationContext(contextBuilder.toString())
                .kbUid(robot.getKbUid())
                .robotUid(robot.getUid())
                .build();
        return streamContent.toJson();
    }

    public String extractFullPromptContent(List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            return "";
        }
        StringBuilder fullPrompt = new StringBuilder();
        for (Message message : messages) {
            String content = message.getText();
            if (content != null && !content.trim().isEmpty()) {
                fullPrompt.append(content).append("\n");
            }
        }
        return fullPrompt.toString().trim();
    }

    public String extractTextFromResponse(Object response) {
        try {
            if (response == null) {
                return "No response received";
            }
            if (response instanceof ChatResponse) {
                return ((ChatResponse) response).getResult().getOutput().getText();
            } else if (response instanceof String) {
                return (String) response;
            } else if (response instanceof AssistantMessage) {
                return ((AssistantMessage) response).getText();
            } else {
                log.info("Unknown response type: {}", response.getClass().getName());
                return response.toString();
            }
        } catch (Exception e) {
            log.error("Error extracting text from response", e);
            return "Error processing response";
        }
    }

    private String stripThinkTags(String content) {
        if (content != null && content.contains("<think>")) {
            return content.replaceAll("(?s)<think>.*?</think>", "");
        }
        return content;
    }

    public String buildContextFromFaqs(List<FaqProtobuf> searchResultList) {
        StringBuilder contextBuilder = new StringBuilder();
        for (FaqProtobuf faq : searchResultList) {
            if (faq != null) {
                contextBuilder.append(faq.toJson()).append("\n\n");
            }
        }
        return contextBuilder.toString();
    }

    /**
     * Flattens a Prompt's instructions into a single text string,
     * joining each message's text content with newlines.
     */
    public String flattenPromptText(Prompt prompt) {
        if (prompt == null || prompt.getInstructions() == null || prompt.getInstructions().isEmpty()) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (Message instruction : prompt.getInstructions()) {
            if (!StringUtils.hasText(instruction.getText())) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append("\n");
            }
            builder.append(instruction.getText());
        }
        return builder.toString();
    }

    public String extractSystemPromptText(Prompt prompt) {
        if (prompt == null || prompt.getInstructions() == null || prompt.getInstructions().isEmpty()) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (Message instruction : prompt.getInstructions()) {
            if (!(instruction instanceof SystemMessage) || !StringUtils.hasText(instruction.getText())) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append("\n\n");
            }
            builder.append(instruction.getText().trim());
        }
        return builder.length() > 0 ? builder.toString() : null;
    }

    public String extractNonSystemPromptText(Prompt prompt) {
        if (prompt == null || prompt.getInstructions() == null || prompt.getInstructions().isEmpty()) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (Message instruction : prompt.getInstructions()) {
            if (instruction instanceof SystemMessage || !StringUtils.hasText(instruction.getText())) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append("\n\n");
            }
            builder.append(formatPromptMessage(instruction));
        }
        return builder.length() > 0 ? builder.toString() : null;
    }

    public String extractHistoryPromptText(Prompt prompt) {
        if (prompt == null || prompt.getInstructions() == null || prompt.getInstructions().isEmpty()) {
            return null;
        }
        List<Message> instructions = prompt.getInstructions();
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < instructions.size(); i++) {
            Message instruction = instructions.get(i);
            if (!StringUtils.hasText(instruction.getText())) {
                continue;
            }
            if (i == instructions.size() - 1 && instruction instanceof UserMessage) {
                continue;
            }
            if (instruction instanceof SystemMessage) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append("\n\n");
            }
            builder.append(formatPromptMessage(instruction));
        }
        return builder.length() > 0 ? builder.toString() : null;
    }

    private void appendHistoryMessage(List<Message> messages, MessageEntity messageEntity) {
        String content = stripThinkTags(messageEntity.getContent());
        if (!StringUtils.hasText(content)) {
            return;
        }
        if (MessageTypeEnum.SYSTEM.name().equals(messageEntity.getType())) {
            messages.add(new SystemMessage(content));
            return;
        }
        if (messageEntity.isFromVisitor() || messageEntity.isFromUser() || messageEntity.isFromMember()) {
            messages.add(new UserMessage(content));
            return;
        }
        if (MessageTypeEnum.ROBOT_STREAM.name().equals(messageEntity.getType())) {
            try {
                RobotContent rc = RobotContent.fromJson(messageEntity.getContent(), RobotContent.class);
                String answer = rc != null ? stripThinkTags(rc.getAnswer()) : null;
                if (StringUtils.hasText(answer)) {
                    messages.add(new AssistantMessage(answer));
                }
                return;
            } catch (Exception ignore) {
                // 解析失败时回退为普通 assistant 文本，避免把历史回复误放到 system。
            }
        }
        messages.add(new AssistantMessage(content));
    }

    private String formatPromptMessage(Message message) {
        if (message instanceof AssistantMessage) {
            return "Assistant:\n" + message.getText().trim();
        }
        if (message instanceof UserMessage) {
            return "User:\n" + message.getText().trim();
        }
        return message.getText().trim();
    }
}
