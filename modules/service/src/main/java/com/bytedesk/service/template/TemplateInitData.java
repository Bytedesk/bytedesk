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
package com.bytedesk.service.template;

// import com.bytedesk.core.constant.I18Consts;

public class TemplateInitData {

    /**
     * Technical Support Templates
     * 技术支持标签
     */
    public static final String[] TECHNICAL_SUPPORT = {
            // I18Consts.I18N_PREFIX + "thread.template.technical_support", // parent
            "技术支持", // parent
    };

    /**
     * Service Request Templates
     * 服务请求标签
     */
    public static final String[] SERVICE_REQUEST = {
            // I18Consts.I18N_PREFIX + "thread.template.service_request", // parent
            "服务请求", // parent
    };

    /**
     * Consultation Templates
     * 咨询标签
     */
    public static final String[] CONSULTATION = {
            // I18Consts.I18N_PREFIX + "thread.template.consultation", // parent
            "咨询", // parent
    };

    /**
     * Complaint & Suggestion Templates
     * 投诉与建议标签
     */
    public static final String[] COMPLAINT_SUGGESTION = {
            // I18Consts.I18N_PREFIX + "thread.template.complaint_suggestion", // parent
            "投诉建议", // parent
    };

    /**
     * Operation & Maintenance Templates
     * 运维标签
     */
    public static final String[] OPERATION_MAINTENANCE = {
            // I18Consts.I18N_PREFIX + "thread.template.operation_maintenance", // parent
            "运维", // parent
            // 其他
            // I18Consts.I18N_PREFIX + "thread.template.other",
            "其他",
    };

    /**
     * Helper method to determine if a template is a parent template
     * 
     * @param template The template key to check
     * @return true if it's a parent template
     */
    public static boolean isParentTemplate(String template) {
        return !template.contains(".");
    }

    /**
     * Helper method to get parent template key for a child template
     * 
     * @param childTemplate The child template key
     * @return The parent template key
     */
    public static String getParentTemplate(String childTemplate) {
        if (isParentTemplate(childTemplate)) {
            return null;
        }
        // 由于已将常量转为中文，此方法可能需要重新实现
        // 这里仅保留基本结构，具体实现需要根据新的标签体系来调整
        return null;
    }

    /**
     * Get all templates as a single array
     * 
     * @return Array containing all templates
     */
    public static String[] getAllTemplates() {
        int totalLength = TECHNICAL_SUPPORT.length + SERVICE_REQUEST.length +
                CONSULTATION.length + COMPLAINT_SUGGESTION.length +
                OPERATION_MAINTENANCE.length;

        String[] allTemplates = new String[totalLength];
        int index = 0;

        System.arraycopy(TECHNICAL_SUPPORT, 0, allTemplates, index, TECHNICAL_SUPPORT.length);
        index += TECHNICAL_SUPPORT.length;

        System.arraycopy(SERVICE_REQUEST, 0, allTemplates, index, SERVICE_REQUEST.length);
        index += SERVICE_REQUEST.length;

        System.arraycopy(CONSULTATION, 0, allTemplates, index, CONSULTATION.length);
        index += CONSULTATION.length;

        System.arraycopy(COMPLAINT_SUGGESTION, 0, allTemplates, index, COMPLAINT_SUGGESTION.length);
        index += COMPLAINT_SUGGESTION.length;

        System.arraycopy(OPERATION_MAINTENANCE, 0, allTemplates, index, OPERATION_MAINTENANCE.length);

        return allTemplates;
    }
}