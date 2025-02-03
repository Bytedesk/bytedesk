/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-03 13:42:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-03 13:54:56
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
     * Category structure:
     * 1. Technical Support
     *   - System Error
     *   - Software Issue
     *   - Hardware Issue
     *   - Network Issue
     *   - Account & Permission
     * 2. Service Request
     *   - New Account
     *   - Permission Change
     *   - Device Request
     *   - Environment Setup
     *   - Data Recovery
     * 3. Consultation
     *   - Product Usage
     *   - Technical Solution
     *   - Business Process
     * 4. Complaint & Suggestion
     *   - Service Quality
     *   - System Improvement
     *   - Process Optimization
     * 5. Operation & Maintenance
     *   - System Maintenance
     *   - Data Backup
     *   - Monitoring Alert
     *   - Security Issue
     *   - Performance Optimization
     */
    public static final String[] CATEGORIES = {
        I18Consts.I18N_PREFIX + "ticket.category.1.technical_support",              // parent
        I18Consts.I18N_PREFIX + "ticket.category.1.1.system_error",                // child
        I18Consts.I18N_PREFIX + "ticket.category.1.2.software_issue",
        I18Consts.I18N_PREFIX + "ticket.category.1.3.hardware_issue",
        I18Consts.I18N_PREFIX + "ticket.category.1.4.network_issue",
        I18Consts.I18N_PREFIX + "ticket.category.1.5.account_permission",
        
        I18Consts.I18N_PREFIX + "ticket.category.2.service_request",               // parent
        I18Consts.I18N_PREFIX + "ticket.category.2.1.new_account",                 // child
        I18Consts.I18N_PREFIX + "ticket.category.2.2.permission_change",
        I18Consts.I18N_PREFIX + "ticket.category.2.3.device_request",
        I18Consts.I18N_PREFIX + "ticket.category.2.4.environment_setup",
        I18Consts.I18N_PREFIX + "ticket.category.2.5.data_recovery",
        
        I18Consts.I18N_PREFIX + "ticket.category.3.consultation",                  // parent
        I18Consts.I18N_PREFIX + "ticket.category.3.1.product_usage",               // child
        I18Consts.I18N_PREFIX + "ticket.category.3.2.technical_solution",
        I18Consts.I18N_PREFIX + "ticket.category.3.3.business_process",
        
        I18Consts.I18N_PREFIX + "ticket.category.4.complaint_suggestion",          // parent
        I18Consts.I18N_PREFIX + "ticket.category.4.1.service_quality",             // child
        I18Consts.I18N_PREFIX + "ticket.category.4.2.system_improvement",
        I18Consts.I18N_PREFIX + "ticket.category.4.3.process_optimization",
        
        I18Consts.I18N_PREFIX + "ticket.category.5.operation_maintenance",         // parent
        I18Consts.I18N_PREFIX + "ticket.category.5.1.system_maintenance",          // child
        I18Consts.I18N_PREFIX + "ticket.category.5.2.data_backup",
        I18Consts.I18N_PREFIX + "ticket.category.5.3.monitoring_alert",
        I18Consts.I18N_PREFIX + "ticket.category.5.4.security_issue",
        I18Consts.I18N_PREFIX + "ticket.category.5.5.performance_optimization"
    };

    /**
     * Helper method to determine if a category is a parent category
     * @param category The category key to check
     * @return true if it's a parent category
     */
    public static boolean isParentCategory(String category) {
        return category.matches("i18n\\.ticket\\.category\\.[1-5]\\.[a-z_]+$");
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
        // Extract the first number from the category key (e.g., "1" from "1.1")
        String parentNumber = childCategory.split("\\.")[2];
        return "i18n.ticket.category." + parentNumber + "." + 
               childCategory.split(parentNumber + "\\.[0-9]\\.", 2)[1].split("_")[0];
    }
}
