package com.bytedesk.ticket.util;

public class TicketUtils {

    public static String getStatusBadgeClass(String status) {
        return switch (status) {
            case "open" -> "danger";
            case "in_progress" -> "warning";
            case "resolved" -> "success";
            case "closed" -> "secondary";
            default -> "info";
        };
    }

    public static String getStatusText(String status) {
        return switch (status) {
            case "open" -> "待处理";
            case "in_progress" -> "处理中";
            case "resolved" -> "已解决";
            case "closed" -> "已关闭";
            default -> status;
        };
    }

    public static String getPriorityBadgeClass(String priority) {
        return switch (priority) {
            case "urgent" -> "danger";
            case "high" -> "warning";
            case "normal" -> "info";
            case "low" -> "secondary";
            default -> "info";
        };
    }

    public static String getPriorityText(String priority) {
        return switch (priority) {
            case "urgent" -> "紧急";
            case "high" -> "高";
            case "normal" -> "中";
            case "low" -> "低";
            default -> priority;
        };
    }
} 