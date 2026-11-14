package com.bytedesk.ai.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.ai.service.agent.AgentResponseTimingContext;
import com.volcengine.ark.runtime.exception.ArkHttpException;

import org.springframework.ai.chat.messages.Message;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.content.RobotContent;
import com.bytedesk.core.message.enums.MessageTypeEnum;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Slf4j
@Component
public class SseMessageHelper {

    private final MessagePersistenceHelper messagePersistenceHelper;

    private final PromptHelper promptHelper;

    private final AgentResponseTimingContext agentResponseTimingContext;

    private boolean shouldPersist(SseEmitter emitter) {
        if (emitter instanceof SsePersistenceControl persistControl) {
            return persistControl.isPersistenceEnabled();
        }
        return true;
    }

    /**
     * 发送「查询消息更新帧」：自动 ASR 转写完成后，把回填识别文本后的原消息
     * （如 VoiceContent.text）通过当前 SSE 连接下发，前端按 uid 去重即原地更新气泡。
     * 仅推送，不持久化（消息落库由转写链路负责）。
     */
    public void sendQueryMessageUpdateFrame(MessageProtobuf messageProtobufQuery, SseEmitter emitter) {
        if (messageProtobufQuery == null) {
            return;
        }
        try {
            if (!isEmitterCompleted(emitter)) {
                String messageJson = messageProtobufQuery.toJson();
                notifyConsumer(emitter, messageJson);
                emitter.send(SseEmitter.event().data(messageJson).id(messageProtobufQuery.getUid()).name("message"));
            }
        } catch (org.springframework.web.context.request.async.AsyncRequestNotUsableException e) {
            log.debug("SSE connection no longer usable during query message update: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error sending query message update frame", e);
        }
    }

    private void notifyConsumer(SseEmitter emitter, String messageJson) {
        if (emitter instanceof SseMessageJsonConsumer consumer) {
            try {
                consumer.acceptMessageJson(messageJson);
            } catch (Exception e) {
                log.debug("SseMessageJsonConsumer threw exception: {}", e.getMessage());
            }
        }
    }

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
                agentResponseTimingContext.start(messageProtobufReply.getUid());
                String startJson = messageProtobufReply.toJson();
                notifyConsumer(emitter, startJson);
                emitter.send(SseEmitter.event().data(startJson).id(messageProtobufReply.getUid()).name("message"));
            }
        } catch (org.springframework.web.context.request.async.AsyncRequestNotUsableException e) {
            log.debug("SSE connection no longer usable during stream start: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error sending stream start message", e);
        }
    }

    public void sendStreamMessage(MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply,
            SseEmitter emitter, String content, String reasonContent,
            List<RobotContent.SourceReference> sourceReferences) {
        // 统一委托到重载版本，避免重复实现
        sendStreamMessage(
                messageProtobufQuery,
                messageProtobufReply,
                emitter,
                content,
                reasonContent,
                sourceReferences,
                false, // isUnanswered 默认 false，与原实现一致
                false, // completeAfterSend 默认 false，与原实现一致
                false // contentIsStreamContentJson 默认 false，按入参构建 StreamContent
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
            if ((!StringUtils.hasLength(content) && !StringUtils.hasLength(reasonContent))
                    || isEmitterCompleted(emitter)) {
                return;
            }

            // 若未显式标记为 JSON，但传入内容看起来是 RobotContent JSON，
            // 为避免嵌套，这里解析后仅提取其中的 answer/reasonContent，继续按统一外层包装发送。
            if (!contentIsStreamContentJson && looksLikeRobotContentJson(content)) {
                try {
                    RobotContent parsed = RobotContent.fromJson(content, RobotContent.class);
                    String parsedAnswer = parsed.getAnswer();
                    String parsedReason = parsed.getReasonContent();
                    if (StringUtils.hasLength(parsedAnswer)) {
                        content = parsedAnswer;
                    }
                    if (!StringUtils.hasLength(reasonContent) && StringUtils.hasLength(parsedReason)) {
                        reasonContent = parsedReason;
                    }
                } catch (Exception ignore) {
                    // 解析失败则退化为按纯文本处理
                }
            }

            if (contentIsStreamContentJson) {
                // 直接发送已构建的 StreamContent JSON
                messageProtobufReply.setContent(content);
                messageProtobufReply.setType(MessageTypeEnum.ROBOT_STREAM);
            } else {
                // 与原有实现一致的构建逻辑
                RobotContent.RobotContentBuilder<?, ?> builder = RobotContent.builder()
                        .answer(content != null ? content : "")
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
            String messageJson = messageProtobufReply.toJson();
            notifyConsumer(emitter, messageJson);

            if (shouldPersist(emitter)) {
                messagePersistenceHelper.persistMessage(messageProtobufQuery, messageProtobufReply, unanswered);
            }
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

    // 尝试解析为 RobotContent，以判断传入内容是否已是完整的 StreamContent JSON
    private boolean looksLikeRobotContentJson(String content) {
        try {
            if (content == null) {
                return false;
            }
            // 简单的快速判断，避免无谓的反序列化尝试
            if (!(content.startsWith("{") && content.contains("\"answer\""))) {
                return false;
            }
            RobotContent.fromJson(content, RobotContent.class);
            return true;
        } catch (Exception ignore) {
            return false;
        }
    }

    public void sendStreamEndMessage(MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply,
            SseEmitter emitter, long promptTokens, long completionTokens, long totalTokens, Prompt prompt,
            String aiProvider, String aiModel) {
        sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter, promptTokens, completionTokens,
                totalTokens, prompt, aiProvider, aiModel, true);
    }

    public void sendStreamEndMessage(MessageProtobuf messageProtobufQuery, MessageProtobuf messageProtobufReply,
            SseEmitter emitter, long promptTokens, long completionTokens, long totalTokens, Prompt prompt,
            String aiProvider, String aiModel, boolean persistMessage) {
        log.info(
                "SseMessageHelper sendStreamEndMessage query {}, reply {}, promptTokens {}, completionTokens {}, totalTokens {}, provider {}, model {}, persist {}",
                messageProtobufQuery.getContent(), messageProtobufReply.getContent(), promptTokens, completionTokens,
                totalTokens, aiProvider, aiModel, persistMessage);
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
                if (persistMessage && shouldPersist(emitter)) {
                    long latencyMs = agentResponseTimingContext.finishMillis(messageProtobufReply.getUid());
                    messagePersistenceHelper.persistMessage(messageProtobufQuery, messageProtobufReply, false,
                            latencyMs, promptTokens, completionTokens, totalTokens, prompt, aiProvider, aiModel);
                }
                String messageJson = messageProtobufReply.toJson();
                notifyConsumer(emitter, messageJson);
                emitter.send(SseEmitter.event().data(messageJson).id(messageProtobufReply.getUid()).name("message"));
                emitter.complete();
            }
        } catch (org.springframework.web.context.request.async.AsyncRequestNotUsableException e) {
            log.debug("SSE connection no longer usable during stream end: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error sending stream end message", e);
        }
    }

    // 发送默认回复（SSE，使用已构建的 StreamContent JSON，标记未命中并完成）
    public void sendDefaultReplySse(String query, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, SseEmitter emitter) {
        String answer = robot.getLlm() != null && robot.getLlm().getDefaultReply() != null
                ? robot.getLlm().getDefaultReply()
                : I18Consts.I18N_ROBOT_DEFAULT_REPLY;
        sendDefaultReplySse(query, answer, robot, messageProtobufQuery, messageProtobufReply, emitter);
    }

    public void sendDefaultReplySse(String query, String answer, RobotProtobuf robot,
            MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, SseEmitter emitter) {
        // 将 i18n key（如 "i18n.robot.noreply"）解析为本地化文本，避免 key 直接泄漏给前端
        // answer = I18nTextResolver.resolveIfKey(answer);
        String robotStreamContent = promptHelper.createRobotStreamContentAnswer(query, answer, new ArrayList<>(),
                robot);
        sendStreamMessage(
                messageProtobufQuery,
                messageProtobufReply,
                emitter,
                robotStreamContent,
                null,
                null,
                true,
                true,
                true);
    }

    public void sendMessageWebsocket(MessageTypeEnum type, String content, MessageProtobuf messageProtobufReply) {
        // WebSocket 功能暂未启用：保留方法签名以兼容其他类的调用，内部不执行发送
        log.debug("sendMessageWebsocket skipped (WebSocket disabled). type={}, uid={}",
                type, messageProtobufReply != null ? messageProtobufReply.getUid() : null);
    }

    // 统一封装：构建 SSE 所需 Prompt 并调用 service.processPromptSse，带异常处理
    public void processPromptSseWithContext(
            BaseSpringAIService service,
            String query,
            String context,
            RobotProtobuf robot,
            MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply,
            List<RobotContent.SourceReference> sourceReferences,
            SseEmitter emitter,
            String errorLogTag) {
        List<Message> messages = promptHelper.buildMessagesForSse(query, context, robot, messageProtobufQuery);
        Prompt aiPrompt = promptHelper.toPrompt(messages);
        try {
            service.processPromptSse(aiPrompt, robot, messageProtobufQuery, messageProtobufReply,
                    sourceReferences != null ? sourceReferences : new java.util.ArrayList<>(), emitter);
        } catch (Exception e) {
            log.error("processPromptSse 异常({})", errorLogTag, e);
            handleSseError(new Exception(I18Consts.I18N_ROBOT_PROCESSING_ERROR), messageProtobufQuery,
                    messageProtobufReply, emitter);
        }
    }

    public void handleSseError(Throwable error, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, SseEmitter emitter) {
        try {
            if (emitter != null && !isEmitterCompleted(emitter)) {
                // 以 StreamContent JSON 发送错误，保持与 START/STREAM/END 一致
                String errorText = resolveErrorText(error);
                RobotContent errorContent = RobotContent.builder()
                        .question(messageProtobufQuery != null ? messageProtobufQuery.getContent() : null)
                        .questionUid(messageProtobufQuery != null ? messageProtobufQuery.getUid() : null)
                        .answer(errorText)
                        .reasonContent(errorText)
                        .build();
                messageProtobufReply.setType(MessageTypeEnum.ROBOT_STREAM_ERROR);
                messageProtobufReply.setContent(errorContent.toJson());
                String messageJson = messageProtobufReply.toJson();
                notifyConsumer(emitter, messageJson);
                if (shouldPersist(emitter)) {
                    long latencyMs = agentResponseTimingContext.finishMillis(messageProtobufReply.getUid());
                    messagePersistenceHelper.persistMessage(messageProtobufQuery, messageProtobufReply, true,
                            latencyMs, 0, 0, 0, null, "", "");
                }
                try {
                    emitter.send(
                            SseEmitter.event().data(messageJson).id(messageProtobufReply.getUid()).name("message"));
                    emitter.complete();
                } catch (org.springframework.web.context.request.async.AsyncRequestNotUsableException e) {
                    log.debug("SSE connection no longer usable during error handling: {}", e.getMessage());
                } catch (Exception sendException) {
                    log.error("Error sending SSE error message", sendException);
                }
            } else {
                log.warn("SSE emitter already completed, skipping sending error message");
                String errorText = resolveErrorText(error);
                RobotContent errorContent = RobotContent.builder()
                        .question(messageProtobufQuery != null ? messageProtobufQuery.getContent() : null)
                        .questionUid(messageProtobufQuery != null ? messageProtobufQuery.getUid() : null)
                        .answer(errorText)
                        .reasonContent(errorText)
                        .build();
                messageProtobufReply.setType(MessageTypeEnum.ROBOT_STREAM_ERROR);
                messageProtobufReply.setContent(errorContent.toJson());
                if (shouldPersist(emitter)) {
                    long latencyMs = agentResponseTimingContext.finishMillis(messageProtobufReply.getUid());
                    messagePersistenceHelper.persistMessage(messageProtobufQuery, messageProtobufReply, true,
                            latencyMs, 0, 0, 0, null, "", "");
                }
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

    /**
     * 提取真实错误信息展示给前端，避免所有异常都显示为通用配置提示。
     *
     * <p>优先取最内层 cause 的 message（如供应商异常携带的 code 与描述）；
     * 但最内层仅有 "HTTP 404" 这类状态码文本时（如 ArkHttpException 的
     * cause 为 retrofit2.HttpException），逐层向外寻找最近一个携带有效信息的
     * 异常。ArkHttpException 额外格式化为 "[code] 原始描述" 便于前端阅读。
     * message 截断防止超长堆栈/HTML 撑爆前端气泡。</p>
     */
    public static String resolveErrorText(Throwable error) {
        if (error != null) {
            // 由外到内收集异常链，再从最内层向外寻找第一个有信息量的消息
            List<Throwable> chain = new ArrayList<>();
            Throwable current = error;
            while (current != null && !chain.contains(current)) {
                chain.add(current);
                current = current.getCause();
            }
            for (int i = chain.size() - 1; i >= 0; i--) {
                Throwable throwable = chain.get(i);
                String message = throwable.getMessage();
                if (StringUtils.hasText(message) && !isStatusOnlyMessage(message)) {
                    return truncate(formatErrorMessage(throwable, message.strip()));
                }
            }
            // 全链路均无有效信息，回退到原有取值逻辑
            Throwable mostSpecific = NestedExceptionUtils.getMostSpecificCause(error);
            String message = mostSpecific.getMessage();
            if (!StringUtils.hasText(message)) {
                message = error.getMessage();
            }
            if (StringUtils.hasText(message)) {
                return truncate(message.strip());
            }
            return mostSpecific.getClass().getSimpleName();
        }
        return I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
    }

    private static String truncate(String text) {
        if (text.length() > 500) {
            return text.substring(0, 500) + "...";
        }
        return text;
    }

    /**
     * 判断消息是否仅为 HTTP 状态码文本（如 retrofit2.HttpException 的 "HTTP 404 "），
     * 这类信息对定位问题没有帮助。
     */
    private static boolean isStatusOnlyMessage(String message) {
        String cleaned = message.strip();
        return cleaned.matches("HTTP \\d{3}") || cleaned.matches("\\d{3}");
    }

    /**
     * 对特定供应商异常做可读化格式处理。
     *
     * <p>ArkHttpException#getMessage() 返回 toString()，形如
     * {@code ArkHttpException{statusCode=404, message='xxx', code='ModelNotOpen', ...}}，
     * 直接展示不友好；从中提取 message 与 code 字段拼为 "[code] 描述"。</p>
     */
    private static String formatErrorMessage(Throwable throwable, String message) {
        if (throwable instanceof ArkHttpException && message.startsWith("ArkHttpException{")) {
            String detail = extractQuotedField(message, "message");
            if (StringUtils.hasText(detail)) {
                String code = extractQuotedField(message, "code");
                return StringUtils.hasText(code) ? "[" + code + "] " + detail : detail;
            }
        }
        return message;
    }

    private static String extractQuotedField(String text, String field) {
        Matcher matcher = Pattern.compile(field + "='((?:\\\\.|[^'])*)'").matcher(text);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public boolean isEmitterCompleted(SseEmitter emitter) {
        if (emitter == null) {
            return true;
        }
        return false;
    }

}
