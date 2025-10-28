package com.bytedesk.ai.springai.service;

import java.util.List;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.message.content.RobotContent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SseMessageHelper {

    @Autowired
    private MessagePersistenceHelper messagePersistenceHelper;

    public void sendStreamStartMessage(
            MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply,
            SseEmitter emitter,
            String initialContent) {
        try {
            if (!isEmitterCompleted(emitter)) {
                // 组装 StreamContent
                RobotContent.RobotContentBuilder<?, ?> builder = RobotContent.builder()
                        .question(messageProtobufQuery != null ? messageProtobufQuery.getContent() : null)
                        .questionUid(messageProtobufQuery != null ? messageProtobufQuery.getUid() : null)
                        .answer("");
                if (StringUtils.hasLength(initialContent)) {
                    builder.reasonContent(initialContent);
                }
                RobotContent streamContent = builder.build();

                messageProtobufReply.setType(MessageTypeEnum.ROBOT_STREAM_START);
                messageProtobufReply.setContent(streamContent.toJson());
                String startJson = messageProtobufReply.toJson();
                emitter.send(SseEmitter.event().data(startJson).id(messageProtobufReply.getUid()).name("message"));
            }
        } catch (org.springframework.web.context.request.async.AsyncRequestNotUsableException e) {
            log.debug("SSE connection no longer usable during stream start: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error sending stream start message", e);
        }
    }

    public void sendStreamMessage(MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply,
            SseEmitter emitter, String content, String reasonContent, List<RobotContent.SourceReference> sourceReferences) {
        // 统一委托到重载版本，避免重复实现
        sendStreamMessage(
                messageProtobufQuery,
                messageProtobufReply,
                emitter,
                content,
                reasonContent,
                sourceReferences,
                false,   // isUnanswered 默认 false，与原实现一致
                false,   // completeAfterSend 默认 false，与原实现一致
                false    // contentIsStreamContentJson 默认 false，按入参构建 StreamContent
        );
    }

    public void sendStreamMessage(
            MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply,
            SseEmitter emitter,
            String content,
            String reasonContent,
            List<RobotContent.SourceReference> sourceReferences,
            Boolean isUnanswered,
            boolean completeAfterSend,
            boolean contentIsStreamContentJson) {
        log.info("SseMessageHelper sendStreamMessage(overload) contentIsJson={}, completeAfterSend={}, isUnanswered={}",
                contentIsStreamContentJson, completeAfterSend, isUnanswered);
        try {
            if (!StringUtils.hasLength(content) || isEmitterCompleted(emitter)) {
                return;
            }

            if (contentIsStreamContentJson) {
                // 直接发送已构建的 StreamContent JSON
                messageProtobufReply.setContent(content);
                messageProtobufReply.setType(MessageTypeEnum.ROBOT_STREAM);
            } else {
                // 与原有实现一致的构建逻辑
                RobotContent.RobotContentBuilder<?, ?> builder = RobotContent.builder()
                        .answer(content)
                        .question(messageProtobufQuery != null ? messageProtobufQuery.getContent() : null)
                        .questionUid(messageProtobufQuery != null ? messageProtobufQuery.getUid() : null);
                if (StringUtils.hasLength(reasonContent)) {
                    builder.reasonContent(reasonContent);
                }
                RobotContent streamContent = builder.build();
                if (sourceReferences != null) {
                    streamContent.setSources(sourceReferences);
                }
                messageProtobufReply.setContent(streamContent.toJson());
                messageProtobufReply.setType(MessageTypeEnum.ROBOT_STREAM);
            }

            // 默认按照提供的 isUnanswered 进行持久化标记
            boolean unanswered = isUnanswered != null ? isUnanswered.booleanValue() : false;
            messagePersistenceHelper.persistMessage(messageProtobufQuery, messageProtobufReply, unanswered);

            String messageJson = messageProtobufReply.toJson();
            emitter.send(SseEmitter.event().data(messageJson).id(messageProtobufReply.getUid()).name("message"));

            if (completeAfterSend && !isEmitterCompleted(emitter)) {
                emitter.complete();
            }
        } catch (org.springframework.web.context.request.async.AsyncRequestNotUsableException e) {
            log.debug("SSE connection no longer usable during stream message (overload): {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error sending stream message (overload)", e);
        }
    }

    public void sendStreamEndMessage(MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply,
            SseEmitter emitter, long promptTokens, long completionTokens, long totalTokens, Prompt prompt,
            String aiProvider, String aiModel) {
        log.info(
                "SseMessageHelper sendStreamEndMessage query {}, reply {}, promptTokens {}, completionTokens {}, totalTokens {}, provider {}, model {}",
                messageProtobufQuery.getContent(), messageProtobufReply.getContent(), promptTokens, completionTokens,
                totalTokens, aiProvider, aiModel);
        try {
            if (!isEmitterCompleted(emitter)) {
                // 以 StreamContent JSON 作为 END 的消息体
                RobotContent endContent = RobotContent.builder()
                        .question(messageProtobufQuery != null ? messageProtobufQuery.getContent() : null)
                        .questionUid(messageProtobufQuery != null ? messageProtobufQuery.getUid() : null)
                        .answer("")
                        .build();

                messageProtobufReply.setType(MessageTypeEnum.ROBOT_STREAM_END);
                messageProtobufReply.setContent(endContent.toJson());
                messagePersistenceHelper.persistMessage(messageProtobufQuery, messageProtobufReply, false, promptTokens,
                        completionTokens, totalTokens, prompt, aiProvider, aiModel);
                String messageJson = messageProtobufReply.toJson();
                emitter.send(SseEmitter.event().data(messageJson).id(messageProtobufReply.getUid()).name("message"));
                emitter.complete();
            }
        } catch (org.springframework.web.context.request.async.AsyncRequestNotUsableException e) {
            log.debug("SSE connection no longer usable during stream end: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error sending stream end message", e);
        }
    }

    public void sendMessageWebsocket(MessageTypeEnum type, String content, MessageProtobuf messageProtobufReply) {
        // WebSocket 功能暂未启用：保留方法签名以兼容其他类的调用，内部不执行发送
        log.debug("sendMessageWebsocket skipped (WebSocket disabled). type={}, uid={}",
                type, messageProtobufReply != null ? messageProtobufReply.getUid() : null);
    }

    public void handleSseError(Throwable error, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, SseEmitter emitter) {
        try {
            if (emitter != null && !isEmitterCompleted(emitter)) {
                // 以 StreamContent JSON 发送错误，保持与 START/STREAM/END 一致
                String errorText = I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
                RobotContent errorContent = RobotContent.builder()
                        .question(messageProtobufQuery != null ? messageProtobufQuery.getContent() : null)
                        .questionUid(messageProtobufQuery != null ? messageProtobufQuery.getUid() : null)
                        .answer("")
                        .reasonContent(errorText)
                        .build();
                messageProtobufReply.setType(MessageTypeEnum.ROBOT_STREAM_ERROR);
                messageProtobufReply.setContent(errorContent.toJson());
                messagePersistenceHelper.persistMessage(messageProtobufQuery, messageProtobufReply, true);
                String messageJson = messageProtobufReply.toJson();
                try {
                    emitter.send(SseEmitter.event().data(messageJson).id(messageProtobufReply.getUid()).name("message"));
                    emitter.complete();
                } catch (org.springframework.web.context.request.async.AsyncRequestNotUsableException e) {
                    log.debug("SSE connection no longer usable during error handling: {}", e.getMessage());
                } catch (Exception sendException) {
                    log.error("Error sending SSE error message", sendException);
                }
            } else {
                log.warn("SSE emitter already completed, skipping sending error message");
                String errorText = I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
                RobotContent errorContent = RobotContent.builder()
                        .question(messageProtobufQuery != null ? messageProtobufQuery.getContent() : null)
                        .questionUid(messageProtobufQuery != null ? messageProtobufQuery.getUid() : null)
                        .answer("")
                        .reasonContent(errorText)
                        .build();
                messageProtobufReply.setType(MessageTypeEnum.ROBOT_STREAM_ERROR);
                messageProtobufReply.setContent(errorContent.toJson());
                messagePersistenceHelper.persistMessage(messageProtobufQuery, messageProtobufReply, true);
            }
        } catch (Exception e) {
            log.error("Error handling SSE error", e);
            try {
                if (emitter != null && !isEmitterCompleted(emitter)) {
                    emitter.completeWithError(e);
                }
            } catch (Exception ex) {
                log.debug("Failed to complete emitter with error: {}", ex.getMessage());
            }
        }
    }

    public boolean isEmitterCompleted(SseEmitter emitter) {
        if (emitter == null) {
            return true;
        }
        return false;
    }

}
