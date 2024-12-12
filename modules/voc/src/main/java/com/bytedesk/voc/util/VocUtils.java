package com.bytedesk.voc.util;

import java.util.UUID;

public class VocUtils {

    public static String generateUid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String getStatusBadgeClass(String status) {
        return switch (status) {
            case "pending" -> "warning";
            case "processing" -> "info";
            case "resolved" -> "success";
            case "closed" -> "secondary";
            default -> "primary";
        };
    }

    public static String getTypeBadgeClass(String type) {
        return switch (type) {
            case "bug" -> "danger";
            case "suggestion" -> "primary";
            case "complaint" -> "warning";
            case "feature" -> "info";
            default -> "secondary";
        };
    }

    public static String getStatusText(String status) {
        return switch (status) {
            case "pending" -> "待处理";
            case "processing" -> "处理中";
            case "resolved" -> "已解决";
            case "closed" -> "已关闭";
            default -> status;
        };
    }

    public static String getTypeText(String type) {
        return switch (type) {
            case "bug" -> "缺陷";
            case "suggestion" -> "建议";
            case "complaint" -> "投诉";
            case "feature" -> "需求";
            default -> "其他";
        };
    }
} 