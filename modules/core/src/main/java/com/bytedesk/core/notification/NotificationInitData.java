/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-11 08:54:35
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-04 17:12:37
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.notification;

// import com.bytedesk.core.constant.I18Consts;

public class NotificationInitData {

    /**
     * Technical Support Notifications
     * 技术支持标签
     */
    public static final String[] TECHNICAL_SUPPORT = {
            // I18Consts.I18N_PREFIX + "thread.notification.technical_support", // parent
            "技术支持", // parent
    };

    /**
     * Service Request Notifications
     * 服务请求标签
     */
    public static final String[] SERVICE_REQUEST = {
            // I18Consts.I18N_PREFIX + "thread.notification.service_request", // parent
            "服务请求", // parent
    };

    /**
     * Consultation Notifications
     * 咨询标签
     */
    public static final String[] CONSULTATION = {
            // I18Consts.I18N_PREFIX + "thread.notification.consultation", // parent
            "咨询", // parent
    };

    /**
     * Complaint &amp; Suggestion Notifications
     * 投诉与建议标签
     */
    public static final String[] COMPLAINT_SUGGESTION = {
            // I18Consts.I18N_PREFIX + "thread.notification.complaint_suggestion", // parent
            "投诉建议", // parent
    };

    /**
     * Operation &amp; Maintenance Notifications
     * 运维标签
     */
    public static final String[] OPERATION_MAINTENANCE = {
            // I18Consts.I18N_PREFIX + "thread.notification.operation_maintenance", // parent
            "运维", // parent
            // 其他
            // I18Consts.I18N_PREFIX + "thread.notification.other",
            "其他",
    };

    /**
     * Helper method to determine if a notification is a parent notification
     * 
     * @param notification The notification key to check
     * @return true if it's a parent notification
     */
    public static boolean isParentNotification(String notification) {
        return !notification.contains(".");
    }

    /**
     * Helper method to get parent notification key for a child notification
     * 
     * @param childNotification The child notification key
     * @return The parent notification key
     */
    public static String getParentNotification(String childNotification) {
        if (isParentNotification(childNotification)) {
            return null;
        }
        // 由于已将常量转为中文，此方法可能需要重新实现
        // 这里仅保留基本结构，具体实现需要根据新的标签体系来调整
        return null;
    }

    /**
     * Get all notifications as a single array
     * 
     * @return Array containing all notifications
     */
    public static String[] getAllNotifications() {
        int totalLength = TECHNICAL_SUPPORT.length + SERVICE_REQUEST.length +
                CONSULTATION.length + COMPLAINT_SUGGESTION.length +
                OPERATION_MAINTENANCE.length;

        String[] allNotifications = new String[totalLength];
        int index = 0;

        System.arraycopy(TECHNICAL_SUPPORT, 0, allNotifications, index, TECHNICAL_SUPPORT.length);
        index += TECHNICAL_SUPPORT.length;

        System.arraycopy(SERVICE_REQUEST, 0, allNotifications, index, SERVICE_REQUEST.length);
        index += SERVICE_REQUEST.length;

        System.arraycopy(CONSULTATION, 0, allNotifications, index, CONSULTATION.length);
        index += CONSULTATION.length;

        System.arraycopy(COMPLAINT_SUGGESTION, 0, allNotifications, index, COMPLAINT_SUGGESTION.length);
        index += COMPLAINT_SUGGESTION.length;

        System.arraycopy(OPERATION_MAINTENANCE, 0, allNotifications, index, OPERATION_MAINTENANCE.length);

        return allNotifications;
    }
}