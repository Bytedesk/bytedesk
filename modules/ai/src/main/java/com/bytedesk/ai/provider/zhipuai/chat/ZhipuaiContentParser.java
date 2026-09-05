package com.bytedesk.ai.provider.zhipuai.chat;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import com.bytedesk.ai.utils.AIFileUtils;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.content.AudioContent;
import com.bytedesk.core.message.content.FileContent;
import com.bytedesk.core.message.content.ImageContent;
import com.bytedesk.core.message.content.RobotContent;
import com.bytedesk.core.message.content.VideoContent;
import com.bytedesk.core.message.enums.MessageTypeEnum;

import ai.z.openapi.service.model.FileUrl;
import ai.z.openapi.service.model.ImageUrl;
import ai.z.openapi.service.model.MessageContent;
import ai.z.openapi.service.model.VideoUrl;
import lombok.extern.slf4j.Slf4j;

/**
 * zai-sdk 多模态消息内容构建工具。
 *
 * <p>2026-09-04（规划 Phase 5）：从 {@code ZhipuaiService} 抽取为静态工具，
 * 供 {@link ZhipuaiChatModel}（ChatClient 链路）与 {@code ZhipuaiService}
 * （多模态同步直调路径）共用，消除双份维护。</p>
 *
 * <ul>
 *   <li>{@link #parseUserContents(String)}：从文本解析媒体 JSON
 *       （ImageContent/VideoContent/FileContent/AudioContent），本地回环 URL 自动转 base64；解析失败退化为纯文本。</li>
 *   <li>{@link #contentsFromMessage(MessageProtobuf)}：直接根据 MessageProtobuf 类型构建多模态内容。</li>
 * </ul>
 */
@Slf4j
public final class ZhipuaiContentParser {

    // zai-sdk 消息内容类型常量
    public static final String ZAI_TEXT = "text";
    public static final String ZAI_IMAGE_URL = "image_url";
    public static final String ZAI_VIDEO_URL = "video_url";
    public static final String ZAI_FILE_URL = "file_url";

    private ZhipuaiContentParser() {
    }

    /**
     * 根据文本尝试解析媒体 JSON（ImageContent/VideoContent/FileContent/AudioContent），
     * 否则退化为纯文本。逻辑与原 ZhipuaiService.buildUserContents 一致。
     */
    public static List<MessageContent> parseUserContents(String text) {
        List<MessageContent> contents = new ArrayList<>();
        if (!StringUtils.hasText(text)) {
            contents.add(textMessage(""));
            return contents;
        }
        String trimmed = text.trim();

        try {
            ImageContent ic = ImageContent.fromJson(trimmed);
            if (ic != null && ic.getUrl() != null && !ic.getUrl().isEmpty()) {
                contents.add(MessageContent.builder().type(ZAI_IMAGE_URL)
                        .imageUrl(ImageUrl.builder().url(resolveSendableUrl(ic.getUrl())).build()).build());
                addLabelIfPresent(contents, ic.getLabel());
                return contents;
            }
        } catch (Exception ignore) {
        }

        try {
            VideoContent vc = VideoContent.fromJson(trimmed);
            if (vc != null && vc.getUrl() != null && !vc.getUrl().isEmpty()) {
                contents.add(MessageContent.builder().type(ZAI_VIDEO_URL)
                        .videoUrl(VideoUrl.builder().url(vc.getUrl()).build()).build());
                addLabelIfPresent(contents, vc.getLabel());
                return contents;
            }
        } catch (Exception ignore) {
        }

        try {
            FileContent fc = FileContent.fromJson(trimmed);
            if (fc != null && fc.getUrl() != null && !fc.getUrl().isEmpty()) {
                contents.add(MessageContent.builder().type(ZAI_FILE_URL)
                        .fileUrl(FileUrl.builder().url(fc.getUrl()).build()).build());
                addLabelIfPresent(contents, fc.getLabel());
                return contents;
            }
        } catch (Exception ignore) {
        }

        try {
            AudioContent ac = AudioContent.fromJson(trimmed);
            if (ac != null && ac.getUrl() != null && !ac.getUrl().isEmpty()) {
                contents.add(MessageContent.builder().type(ZAI_FILE_URL)
                        .fileUrl(FileUrl.builder().url(ac.getUrl()).build()).build());
                addLabelIfPresent(contents, ac.getLabel());
                return contents;
            }
        } catch (Exception ignore) {
        }

        // 以上均无法解析为媒体 JSON，则按纯文本
        contents.add(textMessage(text));
        return contents;
    }

    /**
     * 直接根据原始 MessageProtobuf（而不是通过 BD_MEDIA 标记）构建用户多模态内容。
     * 逻辑与原 ZhipuaiService.buildUserContentsFromMessage 一致。
     */
    public static List<MessageContent> contentsFromMessage(MessageProtobuf messageProtobufQuery) {
        List<MessageContent> contents = new ArrayList<>();
        if (messageProtobufQuery == null) {
            return contents;
        }
        MessageTypeEnum type = messageProtobufQuery.getType();
        String raw = messageProtobufQuery.getContent();
        try {
            switch (type) {
                case IMAGE -> {
                    ImageContent ic = ImageContent.fromJson(raw);
                    String url = ic != null ? ic.getUrl() : null;
                    if (StringUtils.hasText(url)) {
                        contents.add(MessageContent.builder().type(ZAI_IMAGE_URL)
                                .imageUrl(ImageUrl.builder().url(resolveSendableUrl(url)).build()).build());
                        if (ic != null) {
                            addLabelIfPresent(contents, ic.getLabel());
                        }
                    }
                }
                case VIDEO -> {
                    VideoContent vc = VideoContent.fromJson(raw);
                    String url = vc != null ? vc.getUrl() : null;
                    if (StringUtils.hasText(url)) {
                        contents.add(MessageContent.builder().type(ZAI_VIDEO_URL)
                                .videoUrl(VideoUrl.builder().url(url).build()).build());
                        if (vc != null) {
                            addLabelIfPresent(contents, vc.getLabel());
                        }
                    }
                }
                case FILE, AUDIO -> {
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
                    if (StringUtils.hasText(url)) {
                        contents.add(MessageContent.builder().type(ZAI_FILE_URL)
                                .fileUrl(FileUrl.builder().url(url).build()).build());
                        if (fc != null) {
                            addLabelIfPresent(contents, fc.getLabel());
                        }
                        if (ac != null) {
                            addLabelIfPresent(contents, ac.getLabel());
                        }
                    }
                }
                case ROBOT_STREAM -> {
                    // 仅提取历史机器人流式消息中的 answer 文本，避免把整段 JSON 作为文本传给模型
                    try {
                        RobotContent rc = RobotContent.fromJson(raw, RobotContent.class);
                        String answer = rc != null ? rc.getAnswer() : null;
                        if (StringUtils.hasText(answer)) {
                            contents.add(textMessage(stripThinkTags(answer)));
                        }
                    } catch (Exception ignore) {
                        // 忽略解析失败，保持不追加，避免发送原始 JSON
                    }
                }
                default -> contents.add(textMessage(raw != null ? raw : ""));
            }
        } catch (Exception e) {
            log.warn("contentsFromMessage parse failed, fallback to text: {}", e.getMessage());
            contents.add(textMessage(raw != null ? raw : ""));
        }
        return contents;
    }

    /** 从 ChatMessage.content（List&lt;MessageContent&gt; 或 String）提取纯文本（拼接 text 片段）。 */
    public static String extractTextFromContent(Object content) {
        if (content == null) {
            return "";
        }
        if (content instanceof String s) {
            return s;
        }
        if (content instanceof List<?> list) {
            StringBuilder sb = new StringBuilder();
            for (Object o : list) {
                if (o instanceof MessageContent mc && ZAI_TEXT.equalsIgnoreCase(mc.getType())
                        && mc.getText() != null) {
                    sb.append(mc.getText());
                } else if (o != null && !(o instanceof MessageContent)) {
                    sb.append(o);
                }
            }
            return sb.toString();
        }
        return String.valueOf(content);
    }

    /** 本地回环 URL 自动转 base64（原 ZhipuaiService 内联逻辑），失败回退原 url。 */
    private static String resolveSendableUrl(String url) {
        if (url != null && AIFileUtils.isLocalLoopbackHttpUrl(url)) {
            try {
                String b64 = AIFileUtils.fetchHttpAsBase64(url, 8 * 1024 * 1024);
                if (b64 != null && !b64.isEmpty()) {
                    return b64;
                }
            } catch (Exception ce) {
                log.warn("Convert local image to base64 error, fallback to original url: {} - {}", url,
                        ce.getMessage());
            }
        }
        return url;
    }

    private static void addLabelIfPresent(List<MessageContent> contents, String label) {
        if (StringUtils.hasText(label)) {
            contents.add(textMessage(label));
        }
    }

    private static MessageContent textMessage(String text) {
        return MessageContent.builder().type(ZAI_TEXT).text(text != null ? text : "").build();
    }

    /** 构造 text 类型 MessageContent（供 ChatModel 构建非用户消息内容）。 */
    public static MessageContent textContent(String text) {
        return textMessage(text);
    }

    /** 统一移除 &lt;think&gt;...&lt;/think&gt;。 */
    public static String stripThinkTags(String text) {
        if (text == null) {
            return null;
        }
        if (text.contains("<think>")) {
            return text.replaceAll("(?s)<think>.*?</think>", "");
        }
        return text;
    }
}
