package com.bytedesk.ai.service;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.bytedesk.ai.robot_message.RobotMessageCache;
import com.bytedesk.ai.robot_message.RobotMessageRequest;
import com.bytedesk.core.message.MessageExtra;
import com.bytedesk.core.message.MessagePersistCache;
import com.bytedesk.core.message.MessageProtobuf;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MessagePersistenceHelper {

    @Autowired
    private MessagePersistCache messagePersistCache;

    @Autowired
    private RobotMessageCache robotMessageCache;

    @Autowired
    private PromptHelper promptHelper;

    public void persistMessage(MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply,
            Boolean isUnanswered) {
        persistMessage(messageProtobufQuery, messageProtobufReply, isUnanswered, 0, 0, 0, null, "", "");
    }

    public void persistMessage(MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply,
            Boolean isUnanswered, long promptTokens, long completionTokens, long totalTokens, Prompt prompt,
            String aiProvider, String aiModel) {
        Assert.notNull(messageProtobufQuery, "MessageProtobufQuery must not be null");
        Assert.notNull(messageProtobufReply, "MessageProtobufReply must not be null");

        String fullPromptContent = null;
        if (prompt != null) {
            fullPromptContent = promptHelper.extractFullPromptContent(prompt.getInstructions());
        }

        log.info(
                "MessagePersistenceHelper persistMessage query {}, reply {}, promptTokens {}, completionTokens {}, totalTokens {}, provider {}, model {}",
                messageProtobufQuery.getContent(), messageProtobufReply.getContent(), promptTokens, completionTokens,
                totalTokens, aiProvider, aiModel);

        messagePersistCache.pushForPersist(messageProtobufReply.toJson());

        MessageExtra extraObject = MessageExtra.fromJson(messageProtobufReply.getExtra());

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
                // 添加token使用情况
                .promptTokens((int) promptTokens)
                .completionTokens((int) completionTokens)
                .totalTokens((int) totalTokens)
                // 添加完整prompt内容
                .prompt(fullPromptContent)
                // 添加AI模型信息
                .aiProvider(aiProvider)
                .aiModel(aiModel)
                //
                .build();
                
        robotMessageCache.pushRequest(robotMessage);
    }
}
