/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-03 13:42:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-04 17:03:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.category;

// import com.bytedesk.core.constant.I18Consts;

public class CategoryInitData {

    /**
     * Technical Support Categories
     * 技术支持类
     */
    public static final String[] TECHNICAL_SUPPORT = {
            // I18Consts.I18N_PREFIX + "thread.category.technical_support", // parent
            "技术支持", // parent
    };

    /**
     * Service Request Categories
     * 服务请求类
     */
    public static final String[] SERVICE_REQUEST = {
            // I18Consts.I18N_PREFIX + "thread.category.service_request", // parent
            "服务请求", // parent
    };

    /**
     * Consultation Categories
     * 咨询类
     */
    public static final String[] CONSULTATION = {
            // I18Consts.I18N_PREFIX + "thread.category.consultation", // parent
            "咨询", // parent
    };

    /**
     * Complaint &amp; Suggestion Categories
     * 投诉与建议类
     */
    public static final String[] COMPLAINT_SUGGESTION = {
            // I18Consts.I18N_PREFIX + "thread.category.complaint_suggestion", // parent
            "投诉建议", // parent
    };

    /**
     * Operation &amp; Maintenance Categories
     * 运维类
     */
    public static final String[] OPERATION_MAINTENANCE = {
            // I18Consts.I18N_PREFIX + "thread.category.operation_maintenance", // parent
            "运维", // parent
            // 其他
            // I18Consts.I18N_PREFIX + "thread.category.other",
            "其他",
    };

    /**
     * Helper method to determine if a category is a parent category
     * 
     * @param category The category key to check
     * @return true if it's a parent category
     */
    public static boolean isParentCategory(String category) {
        return !category.contains(".");
    }

    /**
     * Helper method to get parent category key for a child category
     * 
     * @param childCategory The child category key
     * @return The parent category key
     */
    public static String getParentCategory(String childCategory) {
        if (isParentCategory(childCategory)) {
            return null;
        }
        // 由于已将常量转为中文，此方法可能需要重新实现
        // 这里仅保留基本结构，具体实现需要根据新的分类体系来调整
        return null;
    }

    /**
     * Get all categories as a single array
     * 
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
