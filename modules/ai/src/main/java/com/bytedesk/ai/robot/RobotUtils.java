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

    /**
     * 从会话 topic 中提取工作组 uid。
     * 仅识别形如 /workgroup/{uid}/... 的 topic，其他格式返回 null。
     */
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

    /**
     * 提取用于问答检索的纯文本查询。
     * 如果 query 是 JSON，则优先读取 text，其次读取 content；否则直接返回裁剪后的原始字符串。
     */
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

    /**
     * 对文本做分词和停用词过滤，供后续查询扩展使用。
     */
    public static List<String> preprocessAndSegment(String content, SegmentService segmentService) {
        if (content == null || content.isBlank() || segmentService == null) {
            return List.of();
        }
        List<String> words = segmentService.segmentWords(content);
        return segmentService.filterWords(words, true, 1);
    }

    /**
     * 将分词结果追加到原始查询后面，构造一个轻量的扩展查询。
     * 仅保留去重后的前 8 个 token，避免查询膨胀过大。
     */
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

    /**
     * 判断当前会话是否应该跳过机器人回复。
     *
     * 这里的目标不是判断“会话能不能被机器人处理”，而是拦截“本次机器人回复是否还应该继续发出去”。
     * 对工作组会话来说，只要已经分配坐席，或者会话状态已经不再是 ROBOTING，就说明它处于
     * 转人工、排队或人工接管链路中，此时应立即停止机器人回复，避免出现“已转人工但机器人仍继续输出”的竞态。
     */
    public static boolean shouldBypassRobotReply(ThreadEntity thread) {
        if (thread == null || !thread.isWorkgroupType()) {
            return false;
        }
        if (hasAgentAssigned(thread)) {
            return true;
        }
        return !thread.isRoboting();
    }

    /**
     * 判断会话是否已经绑定了有效的坐席信息。
     * 空字符串或空 JSON 均视为未分配。
     */
    public static boolean hasAgentAssigned(ThreadEntity thread) {
        if (thread == null) {
            return false;
        }
        String agentJson = thread.getAgent();
        return StringUtils.hasText(agentJson) && !BytedeskConsts.EMPTY_JSON_STRING.equals(agentJson);
    }

    /**
     * 获取机器人当前配置的文本模型提供商名称。
     * 未配置时默认回退到智谱。
     */
    public static String getAIProviderName(RobotProtobuf robot) {
        String provider = LlmProviderConstants.ZHIPUAI;
        if (robot.getLlm() != null) {
            provider = robot.getLlm().getTextProvider().toLowerCase();
        }
        return provider;
    }
}
