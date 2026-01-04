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
package com.bytedesk.service.workgroup_routing;

// import com.bytedesk.core.constant.I18Consts;

public class WorkgroupRoutingInitData {

    /**
     * Technical Support WorkgroupRoutings
     * 技术支持标签
     */
    public static final String[] TECHNICAL_SUPPORT = {
            // I18Consts.I18N_PREFIX + "thread.workgroup_routing.technical_support", // parent
            "技术支持", // parent
    };

    /**
     * Service Request WorkgroupRoutings
     * 服务请求标签
     */
    public static final String[] SERVICE_REQUEST = {
            // I18Consts.I18N_PREFIX + "thread.workgroup_routing.service_request", // parent
            "服务请求", // parent
    };

    /**
     * Consultation WorkgroupRoutings
     * 咨询标签
     */
    public static final String[] CONSULTATION = {
            // I18Consts.I18N_PREFIX + "thread.workgroup_routing.consultation", // parent
            "咨询", // parent
    };

    /**
     * Complaint &amp; Suggestion WorkgroupRoutings
     * 投诉与建议标签
     */
    public static final String[] COMPLAINT_SUGGESTION = {
            // I18Consts.I18N_PREFIX + "thread.workgroup_routing.complaint_suggestion", // parent
            "投诉建议", // parent
    };

    /**
     * Operation &amp; Maintenance WorkgroupRoutings
     * 运维标签
     */
    public static final String[] OPERATION_MAINTENANCE = {
            // I18Consts.I18N_PREFIX + "thread.workgroup_routing.operation_maintenance", // parent
            "运维", // parent
            // 其他
            // I18Consts.I18N_PREFIX + "thread.workgroup_routing.other",
            "其他",
    };

    /**
     * Helper method to determine if a workgroup_routing is a parent workgroup_routing
     * 
     * @param workgroup_routing The workgroup_routing key to check
     * @return true if it's a parent workgroup_routing
     */
    public static boolean isParentWorkgroupRouting(String workgroup_routing) {
        return !workgroup_routing.contains(".");
    }

    /**
     * Helper method to get parent workgroup_routing key for a child workgroup_routing
     * 
     * @param childWorkgroupRouting The child workgroup_routing key
     * @return The parent workgroup_routing key
     */
    public static String getParentWorkgroupRouting(String childWorkgroupRouting) {
        if (isParentWorkgroupRouting(childWorkgroupRouting)) {
            return null;
        }
        // 由于已将常量转为中文，此方法可能需要重新实现
        // 这里仅保留基本结构，具体实现需要根据新的标签体系来调整
        return null;
    }

    /**
     * Get all workgroup_routings as a single array
     * 
     * @return Array containing all workgroup_routings
     */
    public static String[] getAllWorkgroupRoutings() {
        int totalLength = TECHNICAL_SUPPORT.length + SERVICE_REQUEST.length +
                CONSULTATION.length + COMPLAINT_SUGGESTION.length +
                OPERATION_MAINTENANCE.length;

        String[] allWorkgroupRoutings = new String[totalLength];
        int index = 0;

        System.arraycopy(TECHNICAL_SUPPORT, 0, allWorkgroupRoutings, index, TECHNICAL_SUPPORT.length);
        index += TECHNICAL_SUPPORT.length;

        System.arraycopy(SERVICE_REQUEST, 0, allWorkgroupRoutings, index, SERVICE_REQUEST.length);
        index += SERVICE_REQUEST.length;

        System.arraycopy(CONSULTATION, 0, allWorkgroupRoutings, index, CONSULTATION.length);
        index += CONSULTATION.length;

        System.arraycopy(COMPLAINT_SUGGESTION, 0, allWorkgroupRoutings, index, COMPLAINT_SUGGESTION.length);
        index += COMPLAINT_SUGGESTION.length;

        System.arraycopy(OPERATION_MAINTENANCE, 0, allWorkgroupRoutings, index, OPERATION_MAINTENANCE.length);

        return allWorkgroupRoutings;
    }
}