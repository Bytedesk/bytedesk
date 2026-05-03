/*
 * @Author: Copilot
 * @Description: Continue workflow threads when visitor submits a workflow choice
 */
package com.bytedesk.service.workflow;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.bytedesk.core.message.MessageExtra;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageSocketService;
import com.bytedesk.core.message.content.ChoiceContent;
import com.bytedesk.core.message.enums.MessageTypeEnum;
import com.bytedesk.core.message.event.MessageJsonEvent;
import com.bytedesk.core.message.utils.MessageConvertUtils;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.socket.protobuf.model.MessageProto;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadExtra;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.enums.ThreadTypeEnum;
import com.bytedesk.core.workflow.WorkflowEntity;
import com.bytedesk.core.workflow.WorkflowRestService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class WorkflowThreadMessageListener {

    private final ThreadRestService threadRestService;
    private final WorkflowRestService workflowRestService;
    private final WorkflowChatService workflowChatService;
    private final MessageSocketService messageSocketService;

    @EventListener
    public void onMessageJsonEvent(MessageJsonEvent event) {
        MessageProtobuf inboundMessage;
        try {
            inboundMessage = MessageProtobuf.fromJson(event.getJson());
        } catch (Exception e) {
            return;
        }

        if (inboundMessage == null || inboundMessage.getThread() == null
                || !StringUtils.hasText(inboundMessage.getThread().getUid())) {
            return;
        }

        UserProtobuf inboundUser = inboundMessage.getUser();
        if (inboundUser == null || !Boolean.TRUE.equals(inboundUser.isVisitor())) {
            return;
        }

        String selectedOptionKey = resolveSelectedOptionKey(inboundMessage);
        String answerText = resolveQuestionAnswer(inboundMessage);

        Optional<ThreadEntity> threadOptional = threadRestService.findByUid(inboundMessage.getThread().getUid());
        if (!threadOptional.isPresent()) {
            return;
        }
        ThreadEntity thread = threadOptional.get();
        if (!ThreadTypeEnum.WORKFLOW.name().equals(thread.getType())) {
            return;
        }

        String workflowUid = resolveWorkflowUid(thread);
        if (!StringUtils.hasText(workflowUid)) {
            return;
        }

        Optional<WorkflowEntity> workflowOptional = workflowRestService.findByUid(workflowUid);
        if (!workflowOptional.isPresent()) {
            return;
        }

        ThreadExtra extra = ThreadExtra.fromJson(thread.getExtra());
        if (!StringUtils.hasText(selectedOptionKey)
                && (!StringUtils.hasText(answerText) || !StringUtils.hasText(extra.getWorkflowWaitingQuestionNodeId()))) {
            return;
        }

        List<MessageProtobuf> responses;
        if (StringUtils.hasText(selectedOptionKey)) {
            responses = workflowChatService.continueAfterChoiceMessages(
                    workflowOptional.get(),
                    thread,
                    selectedOptionKey);
        } else {
            responses = workflowChatService.continueAfterQuestionMessages(
                    workflowOptional.get(),
                    thread,
                    answerText);
        }
        if (responses.isEmpty()) {
            return;
        }

        for (MessageProtobuf response : responses) {
            String responseJson = response.toJson();
            messageSocketService.sendStompMessage(responseJson);
            try {
                MessageProto.Message protoMessage = MessageConvertUtils.toProtoBean(MessageProto.Message.newBuilder(), responseJson);
                if (protoMessage != null) {
                    messageSocketService.sendMqttMessage(protoMessage);
                }
            } catch (IOException e) {
                log.error("Send workflow follow-up mqtt message failed", e);
            }
        }
    }

    private String resolveSelectedOptionKey(MessageProtobuf inboundMessage) {
        if (inboundMessage == null) {
            return null;
        }

        if (MessageTypeEnum.CHOICE_SUBMIT.equals(inboundMessage.getType())) {
            ChoiceContent choiceContent = ChoiceContent.fromJson(inboundMessage.getContent());
            if (choiceContent != null && choiceContent.getSelectedValues() != null && !choiceContent.getSelectedValues().isEmpty()) {
                return choiceContent.getSelectedValues().get(0);
            }
            if (choiceContent != null && choiceContent.getOptions() != null && !choiceContent.getOptions().isEmpty()) {
                ChoiceContent.ChoiceOption option = choiceContent.getOptions().get(0);
                if (option != null) {
                    if (StringUtils.hasText(option.getValue())) {
                        return option.getValue();
                    }
                    if (StringUtils.hasText(option.getOptionUid())) {
                        return option.getOptionUid();
                    }
                    if (StringUtils.hasText(option.getTitle())) {
                        return option.getTitle();
                    }
                }
            }
            return null;
        }

        MessageExtra messageExtra = MessageExtra.fromJson(inboundMessage.getExtra());
        if (messageExtra != null && Boolean.TRUE.equals(messageExtra.getFromQuickButton())) {
            return messageExtra.getQuickButtonUid();
        }
        return null;
    }

    private String resolveQuestionAnswer(MessageProtobuf inboundMessage) {
        if (inboundMessage == null || !MessageTypeEnum.TEXT.equals(inboundMessage.getType())) {
            return null;
        }
        return StringUtils.hasText(inboundMessage.getContent()) ? inboundMessage.getContent().trim() : null;
    }

    private String resolveWorkflowUid(ThreadEntity thread) {
        if (thread == null) {
            return null;
        }
        if (StringUtils.hasText(thread.getUserUid())) {
            return thread.getUserUid();
        }

        UserProtobuf workflow = thread.getWorkflowProtobuf();
        if (workflow != null && StringUtils.hasText(workflow.getUid())) {
            return workflow.getUid();
        }
        return null;
    }
}