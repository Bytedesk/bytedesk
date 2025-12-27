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
package com.bytedesk.service.agent_next;

// import com.bytedesk.core.constant.I18Consts;

public class AgentNextInitData {

    /**
     * Technical Support AgentNexts
     * 技术支持标签
     */
    public static final String[] TECHNICAL_SUPPORT = {
            // I18Consts.I18N_PREFIX + "thread.agent_next.technical_support", // parent
            "技术支持", // parent
    };

    /**
     * Service Request AgentNexts
     * 服务请求标签
     */
    public static final String[] SERVICE_REQUEST = {
            // I18Consts.I18N_PREFIX + "thread.agent_next.service_request", // parent
            "服务请求", // parent
    };

    /**
     * Consultation AgentNexts
     * 咨询标签
     */
    public static final String[] CONSULTATION = {
            // I18Consts.I18N_PREFIX + "thread.agent_next.consultation", // parent
            "咨询", // parent
    };

    /**
     * Complaint &amp; Suggestion AgentNexts
     * 投诉与建议标签
     */
    public static final String[] COMPLAINT_SUGGESTION = {
            // I18Consts.I18N_PREFIX + "thread.agent_next.complaint_suggestion", // parent
            "投诉建议", // parent
    };

    /**
     * Operation &amp; Maintenance AgentNexts
     * 运维标签
     */
    public static final String[] OPERATION_MAINTENANCE = {
            // I18Consts.I18N_PREFIX + "thread.agent_next.operation_maintenance", // parent
            "运维", // parent
            // 其他
            // I18Consts.I18N_PREFIX + "thread.agent_next.other",
            "其他",
    };

    /**
     * Helper method to determine if a agent_next is a parent agent_next
     * 
     * @param agent_next The agent_next key to check
     * @return true if it's a parent agent_next
     */
    public static boolean isParentAgentNext(String agent_next) {
        return !agent_next.contains(".");
    }

    /**
     * Helper method to get parent agent_next key for a child agent_next
     * 
     * @param childAgentNext The child agent_next key
     * @return The parent agent_next key
     */
    public static String getParentAgentNext(String childAgentNext) {
        if (isParentAgentNext(childAgentNext)) {
            return null;
        }
        // 由于已将常量转为中文，此方法可能需要重新实现
        // 这里仅保留基本结构，具体实现需要根据新的标签体系来调整
        return null;
    }

    /**
     * Get all agent_nexts as a single array
     * 
     * @return Array containing all agent_nexts
     */
    public static String[] getAllAgentNexts() {
        int totalLength = TECHNICAL_SUPPORT.length + SERVICE_REQUEST.length +
                CONSULTATION.length + COMPLAINT_SUGGESTION.length +
                OPERATION_MAINTENANCE.length;

        String[] allAgentNexts = new String[totalLength];
        int index = 0;

        System.arraycopy(TECHNICAL_SUPPORT, 0, allAgentNexts, index, TECHNICAL_SUPPORT.length);
        index += TECHNICAL_SUPPORT.length;

        System.arraycopy(SERVICE_REQUEST, 0, allAgentNexts, index, SERVICE_REQUEST.length);
        index += SERVICE_REQUEST.length;

        System.arraycopy(CONSULTATION, 0, allAgentNexts, index, CONSULTATION.length);
        index += CONSULTATION.length;

        System.arraycopy(COMPLAINT_SUGGESTION, 0, allAgentNexts, index, COMPLAINT_SUGGESTION.length);
        index += COMPLAINT_SUGGESTION.length;

        System.arraycopy(OPERATION_MAINTENANCE, 0, allAgentNexts, index, OPERATION_MAINTENANCE.length);

        return allAgentNexts;
    }
}