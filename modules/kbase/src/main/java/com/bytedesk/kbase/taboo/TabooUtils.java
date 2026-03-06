package com.bytedesk.kbase.taboo;

import java.util.List;

import org.springframework.util.StringUtils;

import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageExtra;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageStatusEnum;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.BdDateUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TabooUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String extractPlainTextContent(String rawContent) {
        if (!StringUtils.hasText(rawContent)) {
            return rawContent;
        }
        String trimmed = rawContent.trim();
        if (!(trimmed.startsWith("{") && trimmed.endsWith("}"))) {
            return rawContent;
        }

        try {
            JsonNode node = OBJECT_MAPPER.readTree(trimmed);
            JsonNode textNode = node.get("text");
            if (textNode != null && textNode.isTextual()) {
                return textNode.asText();
            }
        } catch (Exception e) {
            // Ignore parse errors and fallback to raw content.
        }
        return rawContent;
    }

    public static String maskTabooContent(String plainTextContent, List<String> words) {
        if (!StringUtils.hasText(plainTextContent) || words == null || words.isEmpty()) {
            return plainTextContent;
        }

        String masked = plainTextContent;
        for (String word : words) {
            if (!StringUtils.hasText(word)) {
                continue;
            }
            String replacement = "*".repeat(word.length());
            masked = masked.replace(word, replacement);
        }
        return masked;
    }

    public static MessageEntity buildTabooVisitorMessage(
            MessageProtobuf source,
            ThreadEntity thread,
            String orgUid,
            String filteredText) {
        MessageExtra messageExtra = MessageExtra.fromJson(source.getExtra());
        if (!StringUtils.hasText(messageExtra.getOrgUid())) {
            messageExtra.setOrgUid(orgUid);
        }

        String sourceContent = source.getContent();
        String sourcePlainText = extractPlainTextContent(sourceContent);
        if (StringUtils.hasText(sourcePlainText)) {
            messageExtra.setTabooOriginalContent(sourcePlainText);
        } else if (StringUtils.hasText(sourceContent)) {
            messageExtra.setTabooOriginalContent(sourceContent);
        }

        String nextContent = replaceTextFieldOrRaw(sourceContent, filteredText);

        String sourceType = source.getType() != null ? source.getType().name() : MessageTypeEnum.TEXT.name();
        String sourceUserJson = source.getUser() != null ? source.getUser().toJson() : thread.getUser();
        String sourceUserUid = source.getUser() != null ? source.getUser().getUid() : null;
        String channel = source.getChannel() != null ? source.getChannel().name() : thread.getChannel();

        return MessageEntity.builder()
                .uid(source.getUid())
                .content(nextContent)
                .type(sourceType)
                .status(MessageStatusEnum.SUCCESS.name())
                .channel(channel)
                .user(sourceUserJson)
                .userUid(sourceUserUid)
                .orgUid(thread.getOrgUid())
                .createdAt(BdDateUtils.now())
                .updatedAt(BdDateUtils.now())
                .thread(thread)
                .extra(messageExtra.toJson())
                .build();
    }

    public static MessageEntity buildTabooReplyMessage(
            ThreadEntity thread,
            String orgUid,
            String replyText) {
        MessageExtra messageExtra = MessageExtra.fromOrgUid(orgUid);

        String replyUser = thread.getRobot();
        if (!StringUtils.hasText(replyUser) && StringUtils.hasText(thread.getAgent())) {
            replyUser = thread.getAgent();
        }
        if (!StringUtils.hasText(replyUser) && StringUtils.hasText(thread.getWorkgroup())) {
            replyUser = thread.getWorkgroup();
        }
        if (!StringUtils.hasText(replyUser)) {
            replyUser = UserProtobuf.getSystemUser().toJson();
        }

        String replyContent = toTextContentJson(replyText);

        return MessageEntity.builder()
                .uid(UidUtils.getInstance().getUid())
                .content(replyContent)
                .type(MessageTypeEnum.TEXT.name())
                .status(MessageStatusEnum.SUCCESS.name())
                .channel(thread.getChannel())
                .user(replyUser)
                .orgUid(thread.getOrgUid())
                .createdAt(BdDateUtils.now())
                .updatedAt(BdDateUtils.now())
                .thread(thread)
                .extra(messageExtra.toJson())
                .build();
    }

    private static String replaceTextFieldOrRaw(String rawContent, String filteredText) {
        if (!StringUtils.hasText(rawContent)) {
            return filteredText;
        }
        String trimmed = rawContent.trim();
        if (!(trimmed.startsWith("{") && trimmed.endsWith("}"))) {
            return filteredText;
        }
        try {
            JsonNode node = OBJECT_MAPPER.readTree(trimmed);
            if (node.isObject()) {
                ((ObjectNode) node).put("text", filteredText);
                return OBJECT_MAPPER.writeValueAsString(node);
            }
        } catch (Exception e) {
            // Ignore parse errors and fallback to plain text.
        }
        return filteredText;
    }

    private static String toTextContentJson(String text) {
        try {
            ObjectNode node = OBJECT_MAPPER.createObjectNode();
            node.put("text", text);
            return OBJECT_MAPPER.writeValueAsString(node);
        } catch (Exception e) {
            return text;
        }
    }
}
