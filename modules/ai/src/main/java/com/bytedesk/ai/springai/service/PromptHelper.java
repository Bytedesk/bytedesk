package com.bytedesk.ai.springai.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.message.content.RobotContent;
import com.bytedesk.kbase.llm_faq.FaqProtobuf;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PromptHelper {

    @Autowired
    private MessageRestService messageRestService;

    public List<Message> buildMessagesForSse(String query, String context, RobotProtobuf robot,
            MessageProtobuf messageProtobufQuery) {
        // 添加空值检查
        if (robot.getLlm() == null) {
            log.error("robot.getLlm() 为 null,使用默认系统提示词");
            List<Message> messages = new ArrayList<>();
            messages.add(new SystemMessage(I18Consts.I18N_DEFAULT_SYSTEM_PROMPT));
            if (StringUtils.hasText(context)) {
                messages.add(new SystemMessage(I18Consts.I18N_SEARCH_RESULT_PREFIX + context));
            }
            messages.add(new UserMessage(query));
            return messages;
        }
        
        String systemPrompt = robot.getLlm().getPrompt();
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(systemPrompt));

        if (robot.getLlm() != null && robot.getLlm().getContextMsgCount() > 0) {
            String threadTopic = messageProtobufQuery.getThread().getTopic();
            int limit = robot.getLlm().getContextMsgCount();
            List<MessageEntity> recentMessages = messageRestService.getRecentMessages(threadTopic, limit);
            if (!recentMessages.isEmpty()) {
                log.info("添加 {} 条历史聊天记录", recentMessages.size());
                for (MessageEntity messageEntity : recentMessages) {
                    String content = stripThinkTags(messageEntity.getContent());
                    if (MessageTypeEnum.TEXT.name().equals(messageEntity.getType())
                            || MessageTypeEnum.IMAGE.name().equals(messageEntity.getType())
                            || MessageTypeEnum.FILE.name().equals(messageEntity.getType())
                            || MessageTypeEnum.VIDEO.name().equals(messageEntity.getType())
                            || MessageTypeEnum.AUDIO.name().equals(messageEntity.getType())) {
                        messages.add(new UserMessage(content));
                    } else if (MessageTypeEnum.SYSTEM.name().equals(messageEntity.getType())) {
                        messages.add(new SystemMessage(content));
                    } else if (MessageTypeEnum.ROBOT_STREAM.name().equals(messageEntity.getType())) {
                        try {
                            RobotContent rc = RobotContent.fromJson(messageEntity.getContent(), RobotContent.class);
                            String answer = rc != null ? rc.getAnswer() : null;
                            if (answer != null && !answer.isEmpty()) {
                                messages.add(new AssistantMessage(answer));
                            }
                        } catch (Exception ignore) {
                            // 忽略解析失败，保持不追加，避免发送原始 JSON
                        }
                    }
                }
            }
        }

        if (StringUtils.hasText(context)) {
            messages.add(new SystemMessage(I18Consts.I18N_SEARCH_RESULT_PREFIX + context));
        }

        messages.add(new UserMessage(query));
        return messages;
    }

    public List<Message> buildMessagesForSync(String query, String context, RobotProtobuf robot,
            MessageProtobuf messageProtobufQuery) {
        // 添加空值检查
        if (robot.getLlm() == null) {
            log.error("robot.getLlm() 为 null,使用默认系统提示词");
            List<Message> messages = new ArrayList<>();
            messages.add(new SystemMessage(I18Consts.I18N_DEFAULT_SYSTEM_PROMPT));
            if (StringUtils.hasText(context)) {
                messages.add(new SystemMessage(I18Consts.I18N_SEARCH_RESULT_PREFIX + context));
            }
            messages.add(new UserMessage(query));
            return messages;
        }
        
        String systemPrompt = robot.getLlm().getPrompt();
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(systemPrompt));

        if (robot.getLlm() != null && robot.getLlm().getContextMsgCount() > 0) {
            String threadTopic = messageProtobufQuery.getThread().getTopic();
            int limit = robot.getLlm().getContextMsgCount();
            List<MessageEntity> recentMessages = messageRestService.getRecentMessages(threadTopic, limit);
            if (!recentMessages.isEmpty()) {
                log.info("添加 {} 条历史聊天记录", recentMessages.size());
                for (MessageEntity messageEntity : recentMessages) {
                    String content = stripThinkTags(messageEntity.getContent());
                    if (messageEntity.isFromVisitor() || messageEntity.isFromUser() || messageEntity.isFromMember()) {
                        messages.add(new UserMessage(content));
                    } else {
                        messages.add(new SystemMessage(content));
                    }
                }
            }
        }

        if (StringUtils.hasText(context)) {
            messages.add(new SystemMessage(I18Consts.I18N_SEARCH_RESULT_PREFIX + context));
        }

        messages.add(new UserMessage(query));
        return messages;
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
                if (message instanceof SystemMessage) {
                    fullPrompt.append(I18Consts.I18N_SYSTEM_PREFIX).append(content).append("\n");
                } else if (message instanceof UserMessage) {
                    fullPrompt.append(I18Consts.I18N_USER_PREFIX).append(content).append("\n");
                } else if (message instanceof AssistantMessage) {
                    fullPrompt.append(I18Consts.I18N_ASSISTANT_PREFIX).append(content).append("\n");
                } else {
                    fullPrompt.append(content).append("\n");
                }
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
}
