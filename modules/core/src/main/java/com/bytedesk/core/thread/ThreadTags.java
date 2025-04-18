/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-11 08:54:35
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-11 08:54:37
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.thread;

import com.bytedesk.core.constant.I18Consts;

public class ThreadTags {

    /**
     * Technical Support Tags
     * 技术支持标签
     */
    public static final String[] TECHNICAL_SUPPORT = {
            I18Consts.I18N_PREFIX + "thread.tag.technical_support", // parent
            // I18Consts.I18N_PREFIX + "thread.tag.technical.system_error", // children
            // I18Consts.I18N_PREFIX + "thread.tag.technical.software_issue",
            // I18Consts.I18N_PREFIX + "thread.tag.technical.hardware_issue",
            // I18Consts.I18N_PREFIX + "thread.tag.technical.network_issue",
            // I18Consts.I18N_PREFIX + "thread.tag.technical.account_permission"
    };

    /**
     * Service Request Tags
     * 服务请求标签
     */
    public static final String[] SERVICE_REQUEST = {
            I18Consts.I18N_PREFIX + "thread.tag.service_request", // parent
            // I18Consts.I18N_PREFIX + "thread.tag.service.new_account", // children
            // I18Consts.I18N_PREFIX + "thread.tag.service.permission_change",
            // I18Consts.I18N_PREFIX + "thread.tag.service.device_request",
            // I18Consts.I18N_PREFIX + "thread.tag.service.environment_setup",
            // I18Consts.I18N_PREFIX + "thread.tag.service.data_recovery"
    };

    /**
     * Consultation Tags
     * 咨询标签
     */
    public static final String[] CONSULTATION = {
            I18Consts.I18N_PREFIX + "thread.tag.consultation", // parent
            // I18Consts.I18N_PREFIX + "thread.tag.consult.product_usage", // children
            // I18Consts.I18N_PREFIX + "thread.tag.consult.technical_solution",
            // I18Consts.I18N_PREFIX + "thread.tag.consult.business_process"
    };

    /**
     * Complaint & Suggestion Tags
     * 投诉与建议标签
     */
    public static final String[] COMPLAINT_SUGGESTION = {
            I18Consts.I18N_PREFIX + "thread.tag.complaint_suggestion", // parent
            // I18Consts.I18N_PREFIX + "thread.tag.complaint.service_quality", // children
            // I18Consts.I18N_PREFIX + "thread.tag.complaint.system_improvement",
            // I18Consts.I18N_PREFIX + "thread.tag.complaint.process_optimization"
    };

    /**
     * Operation & Maintenance Tags
     * 运维标签
     */
    public static final String[] OPERATION_MAINTENANCE = {
            I18Consts.I18N_PREFIX + "thread.tag.operation_maintenance", // parent
            // I18Consts.I18N_PREFIX + "thread.tag.operation.system_maintenance", //
            // children
            // I18Consts.I18N_PREFIX + "thread.tag.operation.data_backup",
            // I18Consts.I18N_PREFIX + "thread.tag.operation.monitoring_alert",
            // I18Consts.I18N_PREFIX + "thread.tag.operation.security_issue",
            // I18Consts.I18N_PREFIX + "thread.tag.operation.performance_optimization"

            // 其他
            I18Consts.I18N_PREFIX + "thread.tag.other",
    };

    /**
     * Helper method to determine if a tag is a parent tag
     * 
     * @param tag The tag key to check
     * @return true if it's a parent tag
     */
    public static boolean isParentTag(String tag) {
        return !tag.contains(".");
    }

    /**
     * Helper method to get parent tag key for a child tag
     * 
     * @param childTag The child tag key
     * @return The parent tag key
     */
    public static String getParentTag(String childTag) {
        if (isParentTag(childTag)) {
            return null;
        }
        String[] parts = childTag.split("\\.");
        return I18Consts.I18N_PREFIX + "thread.tag." + parts[2];
    }

    /**
     * Get all tags as a single array
     * 
     * @return Array containing all tags
     */
    public static String[] getAllTags() {
        int totalLength = TECHNICAL_SUPPORT.length + SERVICE_REQUEST.length +
                CONSULTATION.length + COMPLAINT_SUGGESTION.length +
                OPERATION_MAINTENANCE.length;

        String[] allTags = new String[totalLength];
        int index = 0;

        System.arraycopy(TECHNICAL_SUPPORT, 0, allTags, index, TECHNICAL_SUPPORT.length);
        index += TECHNICAL_SUPPORT.length;

        System.arraycopy(SERVICE_REQUEST, 0, allTags, index, SERVICE_REQUEST.length);
        index += SERVICE_REQUEST.length;

        System.arraycopy(CONSULTATION, 0, allTags, index, CONSULTATION.length);
        index += CONSULTATION.length;

        System.arraycopy(COMPLAINT_SUGGESTION, 0, allTags, index, COMPLAINT_SUGGESTION.length);
        index += COMPLAINT_SUGGESTION.length;

        System.arraycopy(OPERATION_MAINTENANCE, 0, allTags, index, OPERATION_MAINTENANCE.length);

        return allTags;
    }
}