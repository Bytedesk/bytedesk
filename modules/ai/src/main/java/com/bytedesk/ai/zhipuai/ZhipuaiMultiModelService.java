/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-23 13:34:37
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-25 07:53:25
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.zhipuai;

import java.util.ArrayList;
import java.util.List;
import com.bytedesk.ai.utils.AIFileUtils;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bytedesk.ai.provider.LlmProviderEntity;
import com.bytedesk.ai.provider.LlmProviderRestService;
import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.ai.service.BaseSpringAIService;
import com.bytedesk.ai.service.TokenUsageHelper;
import com.bytedesk.core.llm.LlmProviderConstants;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageTypeEnum;

import ai.z.openapi.ZhipuAiClient;
import ai.z.openapi.service.model.ChatCompletionCreateParams;
import ai.z.openapi.service.model.ChatCompletionResponse;
import ai.z.openapi.service.model.ChatMessage;
import ai.z.openapi.service.model.ChatMessageRole;
import ai.z.openapi.service.model.ChatThinking;
import ai.z.openapi.service.model.Delta;
import ai.z.openapi.service.model.FileUrl;
import ai.z.openapi.service.model.ImageUrl;
import ai.z.openapi.service.model.MessageContent;
import ai.z.openapi.service.model.ResponseFormat;
import ai.z.openapi.service.model.VideoUrl;

import lombok.extern.slf4j.Slf4j;
import com.bytedesk.core.message.content.ImageContent;
import com.bytedesk.core.message.content.RobotContent;
import com.bytedesk.core.message.content.VideoContent;
import com.bytedesk.core.message.content.FileContent;
import com.bytedesk.core.message.content.AudioContent;

@Slf4j
@Service
public class ZhipuaiMultiModelService extends BaseSpringAIService {

    @Autowired
    private LlmProviderRestService llmProviderRestService;

    @Autowired(required = false)
    @Qualifier("zhipuAiClient")
    private ZhipuAiClient defaultClient;

    @Autowired
    private TokenUsageHelper tokenUsageHelper;

    private static final String DEFAULT_MULTI_MODEL = "glm-4.1v-thinking-flash";
    // zai-sdk 角色/思维模式常量
    private static final String ZAI_ROLE_SYSTEM = "system";
    private static final String ZAI_THINKING_ENABLED = "enabled";
    private static final String ZAI_THINKING_DISABLED = "disabled";

    private String getModel(RobotProtobuf robot) {
        try {
            if (robot != null && robot.getLlm() != null && robot.getLlm().getTextModel() != null
                    && !robot.getLlm().getTextModel().isEmpty()) {
                return robot.getLlm().getTextModel();
            }
        } catch (Exception ignore) {
        }
        return DEFAULT_MULTI_MODEL;
    }

    /**
     * 根据机器人配置动态创建 ZhipuAiClient，优先使用 provider apiKey，失败则回退默认 Bean。
     */
    private ZhipuAiClient createDynamicClient(RobotLlm llm) {
        try {
            if (llm == null || llm.getTextProviderUid() == null) {
                log.warn("RobotLlm or textProviderUid is null, using default ZhipuAiClient");
                return defaultClient;
            }
            var opt = llmProviderRestService.findByUid(llm.getTextProviderUid());
            if (opt.isEmpty()) {
                log.warn("LlmProvider with uid {} not found, using default ZhipuAiClient", llm.getTextProviderUid());
                return defaultClient;
            }
            LlmProviderEntity provider = opt.get();
            String apiKey = provider.getApiKey();
            if (apiKey == null || apiKey.trim().isEmpty()) {
                log.warn("API key is not configured for provider {}, using default ZhipuAiClient", provider.getUid());
                return defaultClient;
            }
            log.info("Creating dynamic ZhipuAiClient with provider: {} ({})", provider.getType(), provider.getUid());
            return ZhipuAiClient.builder().apiKey(apiKey).build();
        } catch (Exception e) {
            log.error("Failed to create dynamic ZhipuAiClient, using default", e);
            return defaultClient;
        }
    }

    private List<ChatMessage> buildZaiMessagesFromPrompt(Prompt prompt) {
        List<ChatMessage> messages = new ArrayList<>();
        if (prompt == null || prompt.getInstructions() == null) {
            return messages;
        }
        for (Message m : prompt.getInstructions()) {
            if (m instanceof SystemMessage) {
                messages.add(ChatMessage.builder()
                        .role(ZAI_ROLE_SYSTEM)
                        .content(List.of(
                                MessageContent.builder().type(ZAI_TEXT).text(m.getText()).build()))
                        .build());
            } else if (m instanceof UserMessage) {
                messages.add(ChatMessage.builder()
                        .role(ChatMessageRole.USER.value())
                        .content(buildUserContents(m.getText()))
                        .build());
            } else if (m instanceof AssistantMessage) {
                // 将历史助手消息也带上，利于多轮
                messages.add(ChatMessage.builder()
                        .role(ChatMessageRole.ASSISTANT.value())
                        .content(List.of(
                                MessageContent.builder().type(ZAI_TEXT).text(m.getText()).build()))
                        .build());
            } else {
                // 兜底按系统文本处理
                messages.add(ChatMessage.builder()
                        .role(ZAI_ROLE_SYSTEM)
                        .content(List.of(
                                MessageContent.builder().type(ZAI_TEXT).text(m.getText()).build()))
                        .build());
            }
        }
        return messages;
    }

    // 直接根据原始 MessageProtobuf（而不是通过 BD_MEDIA 标记）构建用户多模态内容
    private List<MessageContent> buildUserContentsFromMessage(MessageProtobuf messageProtobufQuery) {
        List<MessageContent> contents = new ArrayList<>();
        if (messageProtobufQuery == null) {
            return contents;
        }
        MessageTypeEnum type = messageProtobufQuery.getType();
        String raw = messageProtobufQuery.getContent();
        try {
            if (type == MessageTypeEnum.IMAGE) {
                ImageContent ic = ImageContent.fromJson(raw);
                String url = ic != null ? ic.getUrl() : null;
                if (url != null && !url.isEmpty()) {
                    String toSend = url;
                    if (AIFileUtils.isLocalLoopbackHttpUrl(url)) {
                        try {
                            String b64 = AIFileUtils.fetchHttpAsBase64(url, 8 * 1024 * 1024);
                            if (b64 != null && !b64.isEmpty()) {
                                toSend = b64;
                                log.debug(
                                        "Converted local image url to base64 (MessageProtobuf) for zai image_url: {} -> (base64)",
                                        url);
                            }
                        } catch (Exception ce) {
                            log.warn("Convert local image to base64 error (MessageProtobuf), fallback to url: {} - {}",
                                    url, ce.getMessage());
                        }
                    }
                    contents.add(MessageContent.builder().type(ZAI_IMAGE_URL)
                            .imageUrl(ImageUrl.builder().url(toSend).build()).build());
                    // 若有图片说明（label），追加一条文本消息
                    if (ic != null && ic.getLabel() != null && !ic.getLabel().isEmpty()) {
                        contents.add(MessageContent.builder().type(ZAI_TEXT).text(ic.getLabel()).build());
                    }
                }
            } else if (type == MessageTypeEnum.ROBOT_STREAM) {
                // 仅提取历史机器人流式消息中的 answer 文本，避免把整段 JSON 作为文本传给模型
                try {
                    RobotContent rc = RobotContent.fromJson(raw, RobotContent.class);
                    String answer = rc != null ? rc.getAnswer() : null;
                    if (answer != null && !answer.isEmpty()) {
                        contents.add(MessageContent.builder()
                                .type(ZAI_TEXT)
                                .text(stripThinkTags(answer))
                                .build());
                    }
                } catch (Exception ignore) {
                    // 忽略解析失败，保持不追加，避免发送原始 JSON
                }
            } else if (type == MessageTypeEnum.VIDEO) {
                VideoContent vc = VideoContent.fromJson(raw);
                String url = vc != null ? vc.getUrl() : null;
                if (url != null && !url.isEmpty()) {
                    contents.add(MessageContent.builder().type(ZAI_VIDEO_URL)
                            .videoUrl(VideoUrl.builder().url(url).build()).build());
                    // 若有视频说明（label），追加一条文本消息
                    if (vc != null && vc.getLabel() != null && !vc.getLabel().isEmpty()) {
                        contents.add(MessageContent.builder().type(ZAI_TEXT).text(vc.getLabel()).build());
                    }
                }
            } else if (type == MessageTypeEnum.FILE || type == MessageTypeEnum.AUDIO) {
                // AUDIO 暂按 file_url 处理
                String url = null;
                FileContent fc = null;
                AudioContent ac = null;
                if (type == MessageTypeEnum.FILE) {
                    fc = FileContent.fromJson(raw);
                    url = fc != null ? fc.getUrl() : null;
                } else {
                    ac = AudioContent.fromJson(raw);
                    url = ac != null ? ac.getUrl() : null;
                }
                if (url != null && !url.isEmpty()) {
                    contents.add(MessageContent.builder().type(ZAI_FILE_URL)
                            .fileUrl(FileUrl.builder().url(url).build()).build());
                    // 若有文件/音频说明（label），追加一条文本消息
                    if (fc != null && fc.getLabel() != null && !fc.getLabel().isEmpty()) {
                        contents.add(MessageContent.builder().type(ZAI_TEXT).text(fc.getLabel()).build());
                    }
                    if (ac != null && ac.getLabel() != null && !ac.getLabel().isEmpty()) {
                        contents.add(MessageContent.builder().type(ZAI_TEXT).text(ac.getLabel()).build());
                    }
                }
            } else {
                // 其他类型按文本
                contents.add(MessageContent.builder().type(ZAI_TEXT).text(raw != null ? raw : "").build());
            }
        } catch (Exception e) {
            log.warn("buildUserContentsFromMessage parse failed, fallback to text: {}", e.getMessage());
            contents.add(MessageContent.builder().type(ZAI_TEXT).text(raw != null ? raw : "").build());
        }
        return contents;
    }

    // 直接根据文本尝试解析媒体
    // JSON（ImageContent/VideoContent/FileContent/AudioContent），否则退化为纯文本。
    private List<MessageContent> buildUserContents(String text) {
        List<MessageContent> contents = new ArrayList<>();
        if (text == null) {
            contents.add(MessageContent.builder().type(ZAI_TEXT).text("").build());
            return contents;
        }
        String trimmed = text.trim();
        // 尝试逐类解析标准 JSON
        try {
            ImageContent ic = ImageContent.fromJson(trimmed);
            if (ic != null && ic.getUrl() != null && !ic.getUrl().isEmpty()) {
                String url = ic.getUrl();
                String toSend = url;
                if (AIFileUtils.isLocalLoopbackHttpUrl(url)) {
                    try {
                        String b64 = AIFileUtils.fetchHttpAsBase64(url, 8 * 1024 * 1024);
                        if (b64 != null && !b64.isEmpty()) {
                            toSend = b64;
                            log.debug("Converted local image url to base64 for zai image_url: {} -> (base64)", url);
                        }
                    } catch (Exception ce) {
                        log.warn("Convert local image to base64 error, fallback to original url: {} - {}", url,
                                ce.getMessage());
                    }
                }
                contents.add(MessageContent.builder().type(ZAI_IMAGE_URL)
                        .imageUrl(ImageUrl.builder().url(toSend).build()).build());
                // 若有图片说明（label），追加一条文本消息
                if (ic.getLabel() != null && !ic.getLabel().isEmpty()) {
                    contents.add(MessageContent.builder().type(ZAI_TEXT).text(ic.getLabel()).build());
                }
                return contents;
            }
        } catch (Exception ignore) {
        }

        try {
            VideoContent vc = VideoContent.fromJson(trimmed);
            if (vc != null && vc.getUrl() != null && !vc.getUrl().isEmpty()) {
                contents.add(MessageContent.builder().type(ZAI_VIDEO_URL)
                        .videoUrl(VideoUrl.builder().url(vc.getUrl()).build()).build());
                // 若有视频说明（label），追加一条文本消息
                if (vc.getLabel() != null && !vc.getLabel().isEmpty()) {
                    contents.add(MessageContent.builder().type(ZAI_TEXT).text(vc.getLabel()).build());
                }
                return contents;
            }
        } catch (Exception ignore) {
        }

        try {
            FileContent fc = FileContent.fromJson(trimmed);
            if (fc != null && fc.getUrl() != null && !fc.getUrl().isEmpty()) {
                contents.add(MessageContent.builder().type(ZAI_FILE_URL)
                        .fileUrl(FileUrl.builder().url(fc.getUrl()).build()).build());
                // 若有文件说明（label），追加一条文本消息
                if (fc.getLabel() != null && !fc.getLabel().isEmpty()) {
                    contents.add(MessageContent.builder().type(ZAI_TEXT).text(fc.getLabel()).build());
                }
                return contents;
            }
        } catch (Exception ignore) {
        }

        try {
            AudioContent ac = AudioContent.fromJson(trimmed);
            if (ac != null && ac.getUrl() != null && !ac.getUrl().isEmpty()) {
                contents.add(MessageContent.builder().type(ZAI_FILE_URL)
                        .fileUrl(FileUrl.builder().url(ac.getUrl()).build()).build());
                // 若有音频说明（label），追加一条文本消息
                if (ac.getLabel() != null && !ac.getLabel().isEmpty()) {
                    contents.add(MessageContent.builder().type(ZAI_TEXT).text(ac.getLabel()).build());
                }
                return contents;
            }
        } catch (Exception ignore) {
        }

        // 以上均无法解析为媒体 JSON，则按纯文本
        contents.add(MessageContent.builder().type(ZAI_TEXT).text(text).build());
        return contents;
    }

    // zai-sdk 消息内容类型常量
    private static final String ZAI_TEXT = "text";
    private static final String ZAI_IMAGE_URL = "image_url";
    private static final String ZAI_VIDEO_URL = "video_url";
    private static final String ZAI_FILE_URL = "file_url";

    // 保留：如需自定义解析辅助，可在此处添加
    private String extractFinalTextFromResponse(ChatCompletionResponse response) {
        try {
            if (response == null || response.getData() == null || response.getData().getChoices() == null
                    || response.getData().getChoices().isEmpty()) {
                return null;
            }
            Object msgObj = response.getData().getChoices().get(0).getMessage();
            if (msgObj instanceof ChatMessage cm) {
                Object contentObj = cm.getContent();
                if (contentObj instanceof List<?>) {
                    StringBuilder sb = new StringBuilder();
                    for (Object o : (List<?>) contentObj) {
                        if (o instanceof MessageContent mc) {
                            if (ZAI_TEXT.equalsIgnoreCase(mc.getType()) && mc.getText() != null) {
                                sb.append(mc.getText());
                            }
                        } else if (o != null) {
                            sb.append(o.toString());
                        }
                    }
                    String text = sb.toString();
                    text = stripThinkTags(text);
                    return text;
                } else if (contentObj instanceof String s) {
                    return stripThinkTags(s);
                }
                // 兜底
                return cm.toString();
            }
            return String.valueOf(msgObj);
        } catch (Exception e) {
            log.warn("extractFinalTextFromResponse failed: {}", e.getMessage());
            return null;
        }
    }

    private String extractDeltaText(Delta delta) {
        if (delta == null)
            return null;
        try {
            // 优先尝试 content 字段
            // 部分 SDK 提供 getContent()，否则回退 toString() 简单提取
            try {
                java.lang.reflect.Method m = delta.getClass().getMethod("getContent");
                Object v = m.invoke(delta);
                if (v instanceof String s && !s.isEmpty()) {
                    String trimmed = s.trim();
                    if ("null".equalsIgnoreCase(trimmed)) {
                        log.debug("extractDeltaText: ignoring literal 'null' from getContent(), delta={}", delta);
                        return null;
                    }
                    return trimmed;
                }
            } catch (NoSuchMethodException ignore) {
            }
            String s = delta.toString();
            // 尝试从形如 "content=..." 里粗略抽取
            int idx = s.indexOf("content=");
            if (idx >= 0) {
                int start = idx + 8;
                int end = s.indexOf(',', start);
                if (end < 0)
                    end = s.length();
                String sub = s.substring(start, end).trim();
                // 去掉可能的引号
                if ((sub.startsWith("\"") && sub.endsWith("\"")) || (sub.startsWith("'") && sub.endsWith("'"))) {
                    sub = sub.substring(1, sub.length() - 1);
                }
                if ("null".equalsIgnoreCase(sub)) {
                    log.debug("extractDeltaText: ignoring literal 'null' from toString() content field, delta={}",
                            delta);
                    return null;
                }
                return sub;
            }
            String sTrim = s == null ? null : s.trim();
            if ("null".equalsIgnoreCase(sTrim)) {
                log.debug("extractDeltaText: ignoring literal 'null' from toString() fallback, delta={}", delta);
                return null;
            }
            return sTrim;
        } catch (Exception e) {
            return delta.toString();
        }
    }

    // 统一移除 <think>...</think>
    private String stripThinkTags(String text) {
        if (text == null)
            return null;
        if (text.contains("<think>")) {
            return text.replaceAll("(?s)<think>.*?</think>", "");
        }
        return text;
    }

    private long estimateTokens(String text) {
        if (text == null || text.isEmpty())
            return 1;
        int zh = 0, en = 0;
        for (char c : text.toCharArray()) {
            if (Character.UnicodeScript.of(c) == Character.UnicodeScript.HAN)
                zh++;
            else
                en++;
        }
        long est = (long) (zh / 1.5 + en / 4.0);
        return Math.max(1, est);
    }

    @Override
    protected String processPromptSync(String message, RobotProtobuf robot) {
        try {
            String model = getModel(robot);
            // 将文本包装为 Prompt -> zai 消息
            List<ChatMessage> msgs = new ArrayList<>();
            msgs.add(ChatMessage.builder()
                    .role(ChatMessageRole.USER.value())
                    .content(buildUserContents(message))
                    .build());
            ZhipuAiClient client = createDynamicClient(robot != null ? robot.getLlm() : null);
            if (client == null) {
                log.error("No available ZhipuAiClient for sync");
                return I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
            }
            boolean enableThinking = robot != null && robot.getLlm() != null
                    && Boolean.TRUE.equals(robot.getLlm().getEnableThinking());
            ChatCompletionCreateParams req = ChatCompletionCreateParams.builder()
                    .model(model)
                    // https://docs.bigmodel.cn/cn/guide/capabilities/struct-output
                    .responseFormat(ResponseFormat.builder().type("json_object").build())
                    .messages(msgs)
                    .thinking(ChatThinking.builder().type(enableThinking ? ZAI_THINKING_ENABLED : ZAI_THINKING_DISABLED)
                            .build())
                    .build();
            long start = System.currentTimeMillis();
            ChatCompletionResponse resp = client.chat().createChatCompletion(req);
            boolean success = resp != null && resp.isSuccess();
            String text = success ? extractFinalTextFromResponse(resp) : null;
            if (text == null)
                text = "";
            text = stripThinkTags(text);
            // 记录用量事件（粗略估算）
            long promptTokens = estimateTokens(message);
            long completionTokens = estimateTokens(text);
            tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.ZHIPUAI, model, promptTokens,
                    completionTokens, success,
                    System.currentTimeMillis() - start);
            return text;
        } catch (Exception e) {
            log.error("processPromptSync failed", e);
            return I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
        }
    }

    /**
     * 多模态同步请求处理，支持图片等媒体类型
     */
    public String processMultiModalSyncRequest(MessageProtobuf messageProtobuf, RobotProtobuf robot,
            boolean searchKnowledgeBase) {
        try {
            String model = getModel(robot);

            // 构建多模态内容
            List<MessageContent> userContents = buildUserContentsFromMessage(messageProtobuf);
            if (userContents.isEmpty()) {
                // 如果没有有效的多模态内容，回退到纯文本处理
                return processPromptSync(messageProtobuf.getContent(), robot);
            }

            // 构建消息列表
            List<ChatMessage> msgs = new ArrayList<>();

            // 如果需要搜索知识库，添加系统消息（这里暂时跳过，因为OCR通常不需要知识库）
            if (searchKnowledgeBase && robot != null) {
                // 可以在这里添加知识库相关的系统提示
            }

            // 添加用户消息
            msgs.add(ChatMessage.builder()
                    .role(ChatMessageRole.USER.value())
                    .content(userContents)
                    .build());

            // 获取客户端
            ZhipuAiClient client = createDynamicClient(robot != null ? robot.getLlm() : null);
            if (client == null) {
                log.error("No available ZhipuAiClient for multi-modal sync");
                return I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
            }

            // 启用思维模式（如果配置了）
            boolean enableThinking = robot != null && robot.getLlm() != null
                    && Boolean.TRUE.equals(robot.getLlm().getEnableThinking());

            // 构建请求
            ChatCompletionCreateParams req = ChatCompletionCreateParams.builder()
                    .model(model)
                    .messages(msgs)
                    .thinking(ChatThinking.builder().type(enableThinking ? ZAI_THINKING_ENABLED : ZAI_THINKING_DISABLED)
                            .build())
                    .build();

            long start = System.currentTimeMillis();
            ChatCompletionResponse resp = client.chat().createChatCompletion(req);
            boolean success = resp != null && resp.isSuccess();
            String text = success ? extractFinalTextFromResponse(resp) : null;
            if (text == null)
                text = "";
            text = stripThinkTags(text);

            // 记录用量事件（粗略估算）
            long promptTokens = estimateTokens("multi-modal-input");
            long completionTokens = estimateTokens(text);
            tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.ZHIPUAI, model, promptTokens,
                    completionTokens, success,
                    System.currentTimeMillis() - start);

            return text;
        } catch (Exception e) {
            log.error("processMultiModalSyncRequest failed", e);
            return I18Consts.I18N_SERVICE_TEMPORARILY_UNAVAILABLE;
        }
    }

    @Override
    protected void processPromptSse(Prompt prompt, RobotProtobuf robot, MessageProtobuf messageProtobufQuery,
            MessageProtobuf messageProtobufReply, List<RobotContent.SourceReference> sourceReferences,
            SseEmitter emitter) {
        if (robot == null || robot.getLlm() == null) {
            sseMessageHelper.handleSseError(new IllegalArgumentException("robot or llm is null"), messageProtobufQuery,
                    messageProtobufReply, emitter);
            return;
        }
        String model = getModel(robot);
        // 起始提示
        sseMessageHelper.sendStreamStartMessage(messageProtobufQuery, messageProtobufReply, emitter,
                I18Consts.I18N_THINKING);
        long start = System.currentTimeMillis();
        final StringBuilder finalAnswer = new StringBuilder();
        // 如果模型在流式输出的是 RobotContent JSON，而不是纯文本，这里做缓冲，避免被二次包裹导致嵌套
        // 仅按纯文本分片进行推送，RobotContent 的包装统一由 SseMessageHelper 负责
        try {
            List<ChatMessage> zaiMessages = buildZaiMessagesFromPrompt(prompt);
            // 覆盖最新的用户消息为基于 MessageProtobuf 直接解析的多模态内容，避免 BD_MEDIA 标记往返
            try {
                List<MessageContent> directUserContents = buildUserContentsFromMessage(messageProtobufQuery);
                if (directUserContents != null && !directUserContents.isEmpty()) {
                    for (int i = zaiMessages.size() - 1; i >= 0; i--) {
                        ChatMessage m = zaiMessages.get(i);
                        if (ChatMessageRole.USER.value().equals(m.getRole())) {
                            ChatMessage replaced = ChatMessage.builder()
                                    .role(m.getRole())
                                    .content(directUserContents)
                                    .build();
                            zaiMessages.set(i, replaced);
                            break;
                        }
                    }
                }
            } catch (Exception ig) {
                log.debug("sse override user content failed: {}", ig.getMessage());
            }
            ZhipuAiClient client = createDynamicClient(robot.getLlm());
            if (client == null) {
                sseMessageHelper.handleSseError(new IllegalStateException("No available ZhipuAiClient"),
                        messageProtobufQuery,
                        messageProtobufReply, emitter);
                return;
            }
            boolean enableThinking = robot != null && robot.getLlm() != null
                    && Boolean.TRUE.equals(robot.getLlm().getEnableThinking());
            ChatCompletionCreateParams req = ChatCompletionCreateParams.builder()
                    .model(model)
                    .messages(zaiMessages)
                    .stream(true)
                    .thinking(ChatThinking.builder().type(enableThinking ? ZAI_THINKING_ENABLED : ZAI_THINKING_DISABLED)
                            .build())
                    .build();
            log.info("zai stream request: model={}, enableThinking={}, messages={} ", model, enableThinking,
                    zaiMessages.size());
            ChatCompletionResponse response = client.chat().createChatCompletion(req);
            if (response != null && response.isSuccess() && response.getFlowable() != null) {
                response.getFlowable().subscribe(
                        data -> {
                            try {
                                if (data.getChoices() != null && !data.getChoices().isEmpty()) {
                                    Delta delta = data.getChoices().get(0).getDelta();
                                    String piece = extractDeltaText(delta);
                                    // 提取模型推理内容（reasoningContent）
                                    String reasoning = null;
                                    try {
                                        java.lang.reflect.Method getReasoning = delta.getClass()
                                                .getMethod("getReasoningContent");
                                        Object rv = getReasoning.invoke(delta);
                                        if (rv instanceof String rs && !rs.isEmpty()) {
                                            reasoning = rs;
                                        }
                                    } catch (NoSuchMethodException ignore) {
                                        // 某些SDK版本没有该字段
                                    } catch (Exception ignore) {
                                    }
                                    if (piece != null) {
                                        String pieceTrim = piece.trim();
                                        if (pieceTrim.equalsIgnoreCase("null")) {
                                            log.warn("SSE piece is literal 'null', delta={}, data={}", delta, data);
                                            return;
                                        }
                                        if (!pieceTrim.isEmpty()) {
                                            // 正常边流边发，由 SseMessageHelper 统一封装为 RobotContent
                                            finalAnswer.append(pieceTrim);
                                            sseMessageHelper.sendStreamMessage(
                                                    messageProtobufQuery,
                                                    messageProtobufReply,
                                                    emitter,
                                                    pieceTrim,
                                                    reasoning,
                                                    sourceReferences);
                                        } else {
                                            log.debug("SSE piece is empty after trim, delta={}", delta);
                                        }
                                    } else {
                                        log.debug("SSE piece is null, delta={}", delta);
                                    }
                                }
                            } catch (Exception ex) {
                                log.error("SSE send piece error", ex);
                            }
                        },
                        err -> {
                            log.error("zai stream error", err);
                            sseMessageHelper.handleSseError(err, messageProtobufQuery, messageProtobufReply, emitter);
                        },
                        () -> {
                            // 结束时将累计文本移除 <think> 标签作为最终答案，用于用量估算
                            String answer = stripThinkTags(finalAnswer.toString());
                            long promptTokens = estimateTokens(prompt.getContents());
                            long completionTokens = estimateTokens(answer);
                            // 结束并持久化
                            sseMessageHelper.sendStreamEndMessage(messageProtobufQuery, messageProtobufReply, emitter,
                                    promptTokens,
                                    completionTokens, promptTokens + completionTokens, prompt,
                                    LlmProviderConstants.ZHIPUAI, model);
                            // 发布用量事件
                            tokenUsageHelper.recordAiTokenUsage(robot, LlmProviderConstants.ZHIPUAI, model,
                                    promptTokens,
                                    completionTokens, true, System.currentTimeMillis() - start);
                        });
            } else {
                sseMessageHelper.handleSseError(
                        new RuntimeException(response != null ? response.getMsg() : "null response"),
                        messageProtobufQuery, messageProtobufReply, emitter);
            }
        } catch (Exception e) {
            log.error("processPromptSse failed", e);
            sseMessageHelper.handleSseError(e, messageProtobufQuery, messageProtobufReply, emitter);
        }
    }

}
