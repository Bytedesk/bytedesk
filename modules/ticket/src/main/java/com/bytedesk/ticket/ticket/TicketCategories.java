/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-03 13:42:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-10 15:30:04
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket;

import com.bytedesk.core.constant.I18Consts;

public class TicketCategories {

    /**
     * Technical Support Categories
     * 技术支持类
     */
    public static final String[] TECHNICAL_SUPPORT = {
        I18Consts.I18N_PREFIX + "ticket.category.technical_support",              // parent
        // I18Consts.I18N_PREFIX + "ticket.category.technical.system_error",         // children
        // I18Consts.I18N_PREFIX + "ticket.category.technical.software_issue",
        // I18Consts.I18N_PREFIX + "ticket.category.technical.hardware_issue",
        // I18Consts.I18N_PREFIX + "ticket.category.technical.network_issue",
        // I18Consts.I18N_PREFIX + "ticket.category.technical.account_permission"
    };

    /**
     * Service Request Categories
     * 服务请求类
     */
    public static final String[] SERVICE_REQUEST = {
        I18Consts.I18N_PREFIX + "ticket.category.service_request",               // parent
        // I18Consts.I18N_PREFIX + "ticket.category.service.new_account",          // children
        // I18Consts.I18N_PREFIX + "ticket.category.service.permission_change",
        // I18Consts.I18N_PREFIX + "ticket.category.service.device_request",
        // I18Consts.I18N_PREFIX + "ticket.category.service.environment_setup",
        // I18Consts.I18N_PREFIX + "ticket.category.service.data_recovery"
    };

    /**
     * Consultation Categories
     * 咨询类
     */
    public static final String[] CONSULTATION = {
        I18Consts.I18N_PREFIX + "ticket.category.consultation",                  // parent
        // I18Consts.I18N_PREFIX + "ticket.category.consult.product_usage",        // children
        // I18Consts.I18N_PREFIX + "ticket.category.consult.technical_solution",
        // I18Consts.I18N_PREFIX + "ticket.category.consult.business_process"
    };

    /**
     * Complaint &amp; Suggestion Categories
     * 投诉与建议类
     */
    public static final String[] COMPLAINT_SUGGESTION = {
        I18Consts.I18N_PREFIX + "ticket.category.complaint_suggestion",          // parent
        // I18Consts.I18N_PREFIX + "ticket.category.complaint.service_quality",     // children
        // I18Consts.I18N_PREFIX + "ticket.category.complaint.system_improvement",
        // I18Consts.I18N_PREFIX + "ticket.category.complaint.process_optimization"
    };

    /**
     * Operation &amp; Maintenance Categories
     * 运维类
     */
    public static final String[] OPERATION_MAINTENANCE = {
        I18Consts.I18N_PREFIX + "ticket.category.operation_maintenance",         // parent
        // I18Consts.I18N_PREFIX + "ticket.category.operation.system_maintenance",  // children
        // I18Consts.I18N_PREFIX + "ticket.category.operation.data_backup",
        // I18Consts.I18N_PREFIX + "ticket.category.operation.monitoring_alert",
        // I18Consts.I18N_PREFIX + "ticket.category.operation.security_issue",
        // I18Consts.I18N_PREFIX + "ticket.category.operation.performance_optimization"

        // 其他
        I18Consts.I18N_PREFIX + "ticket.category.other",
    };

    /**
     * Helper method to determine if a category is a parent category
     * @param category The category key to check
     * @return true if it's a parent category
     */
    public static boolean isParentCategory(String category) {
        return !category.contains(".");
    }

    /**
     * Helper method to get parent category key for a child category
     * @param childCategory The child category key
     * @return The parent category key
     */
    public static String getParentCategory(String childCategory) {
        if (isParentCategory(childCategory)) {
            return null;
        }
        String[] parts = childCategory.split("\\.");
        return I18Consts.I18N_PREFIX + "ticket.category." + parts[2];
    }

    /**
     * Get all categories as a single array
     * @return Array containing all categories
     */
    public static String[] getAllCategories() {
        int totalLength = TECHNICAL_SUPPORT.length + SERVICE_REQUEST.length + 
                         CONSULTATION.length + COMPLAINT_SUGGESTION.length + 
                         OPERATION_MAINTENANCE.length;
        
        String[] allCategories = new String[totalLength];
        int index = 0;
        
        System.arraycopy(TECHNICAL_SUPPORT, 0, allCategories, index, TECHNICAL_SUPPORT.length);
        index += TECHNICAL_SUPPORT.length;
        
        System.arraycopy(SERVICE_REQUEST, 0, allCategories, index, SERVICE_REQUEST.length);
        index += SERVICE_REQUEST.length;
        
        System.arraycopy(CONSULTATION, 0, allCategories, index, CONSULTATION.length);
        index += CONSULTATION.length;
        
        System.arraycopy(COMPLAINT_SUGGESTION, 0, allCategories, index, COMPLAINT_SUGGESTION.length);
        index += COMPLAINT_SUGGESTION.length;
        
        System.arraycopy(OPERATION_MAINTENANCE, 0, allCategories, index, OPERATION_MAINTENANCE.length);
        
        return allCategories;
    }
}
