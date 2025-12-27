package com.bytedesk.ticket.utils;

import java.util.Locale;

/**
 * Flowable / BPMN 对 processDefinitionKey（即 <process id="...">）的要求较严格：
 * - 必须是 XML NCName（不能以数字开头）
 * - 仅允许字母/数字/下划线/连字符/点
 */
public final class FlowableIdUtils {

    private FlowableIdUtils() {
    }

    /**
     * 将业务侧的 processUid 映射为可用于 Flowable 的 processDefinitionKey。
     * 该映射需要“稳定且可逆（至少可重复计算）”，以便启动/查询时使用同一 key。
     */
    public static String toProcessDefinitionKey(String processUid) {
        if (processUid == null || processUid.isBlank()) {
            throw new IllegalArgumentException("processUid cannot be null/blank");
        }

        String sanitized = processUid.replaceAll("[^a-zA-Z0-9_\\-\\.]", "_");

        // NCName 不能以数字/连字符/点开头；用统一前缀兜底
        if (!sanitized.matches("^[A-Za-z_].*")) {
            sanitized = "p_" + sanitized;
        }

        // NCName 不允许以 'xml'（任意大小写）开头
        if (sanitized.toLowerCase(Locale.ROOT).startsWith("xml")) {
            sanitized = "p_" + sanitized;
        }

        return sanitized;
    }
}
