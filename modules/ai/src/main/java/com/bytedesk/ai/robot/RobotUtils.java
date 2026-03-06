package com.bytedesk.ai.robot;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.ai.segment.SegmentService;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.llm.LlmProviderConstants;
import com.bytedesk.core.thread.ThreadEntity;

public final class RobotUtils {

    private RobotUtils() {
    }

    public static String extractWorkgroupUidFromTopic(String topic) {
        if (!StringUtils.hasText(topic)) {
            return null;
        }
        String[] parts = topic.split("/");
        if (parts.length < 3) {
            return null;
        }
        if (!"workgroup".equalsIgnoreCase(parts[1])) {
            return null;
        }
        return parts[2];
    }

    public static String extractTextQuery(String query) {
        if (!StringUtils.hasText(query)) {
            return query;
        }
        String trimmed = query.trim();
        if (!(trimmed.startsWith("{") && trimmed.endsWith("}"))) {
            return trimmed;
        }
        try {
            JSONObject json = JSON.parseObject(trimmed);
            String text = json.getString("text");
            if (StringUtils.hasText(text)) {
                return text.trim();
            }
            String content = json.getString("content");
            if (StringUtils.hasText(content)) {
                return content.trim();
            }
            return trimmed;
        } catch (Exception ex) {
            return trimmed;
        }
    }

    public static List<String> preprocessAndSegment(String content, SegmentService segmentService) {
        if (content == null || content.isBlank() || segmentService == null) {
            return List.of();
        }
        List<String> words = segmentService.segmentWords(content);
        return segmentService.filterWords(words, true, 1);
    }

    public static String buildExpandedQuery(String base, List<String> tokens) {
        if (tokens == null || tokens.isEmpty()) {
            return base;
        }
        List<String> uniq = tokens.stream().distinct().limit(8).collect(Collectors.toList());
        String extra = String.join(" ", uniq);
        if (extra.isBlank()) {
            return base;
        }
        return base + " " + extra;
    }

    public static boolean shouldBypassRobotReply(ThreadEntity thread) {
        if (thread == null || !thread.isWorkgroupType()) {
            return false;
        }
        if (hasAgentAssigned(thread)) {
            return true;
        }
        return !thread.isRoboting();
    }

    public static boolean hasAgentAssigned(ThreadEntity thread) {
        if (thread == null) {
            return false;
        }
        String agentJson = thread.getAgent();
        return StringUtils.hasText(agentJson) && !BytedeskConsts.EMPTY_JSON_STRING.equals(agentJson);
    }

    public static String getAIProviderName(RobotProtobuf robot) {
        String provider = LlmProviderConstants.ZHIPUAI;
        if (robot.getLlm() != null) {
            provider = robot.getLlm().getTextProvider().toLowerCase();
        }
        return provider;
    }
}
