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
package com.bytedesk.demos.goods;

// import com.bytedesk.core.constant.I18Consts;

public class GoodsInitData {

    /**
     * Technical Support Goodss
     * 技术支持标签
     */
    public static final String[] TECHNICAL_SUPPORT = {
            // I18Consts.I18N_PREFIX + "thread.goods.technical_support", // parent
            "技术支持", // parent
    };

    /**
     * Service Request Goodss
     * 服务请求标签
     */
    public static final String[] SERVICE_REQUEST = {
            // I18Consts.I18N_PREFIX + "thread.goods.service_request", // parent
            "服务请求", // parent
    };

    /**
     * Consultation Goodss
     * 咨询标签
     */
    public static final String[] CONSULTATION = {
            // I18Consts.I18N_PREFIX + "thread.goods.consultation", // parent
            "咨询", // parent
    };

    /**
     * Complaint &amp; Suggestion Goodss
     * 投诉与建议标签
     */
    public static final String[] COMPLAINT_SUGGESTION = {
            // I18Consts.I18N_PREFIX + "thread.goods.complaint_suggestion", // parent
            "投诉建议", // parent
    };

    /**
     * Operation &amp; Maintenance Goodss
     * 运维标签
     */
    public static final String[] OPERATION_MAINTENANCE = {
            // I18Consts.I18N_PREFIX + "thread.goods.operation_maintenance", // parent
            "运维", // parent
            // 其他
            // I18Consts.I18N_PREFIX + "thread.goods.other",
            "其他",
    };

    /**
     * Helper method to determine if a goods is a parent goods
     * 
     * @param goods The goods key to check
     * @return true if it's a parent goods
     */
    public static boolean isParentGoods(String goods) {
        return !goods.contains(".");
    }

    /**
     * Helper method to get parent goods key for a child goods
     * 
     * @param childGoods The child goods key
     * @return The parent goods key
     */
    public static String getParentGoods(String childGoods) {
        if (isParentGoods(childGoods)) {
            return null;
        }
        // 由于已将常量转为中文，此方法可能需要重新实现
        // 这里仅保留基本结构，具体实现需要根据新的标签体系来调整
        return null;
    }

    /**
     * Get all goodss as a single array
     * 
     * @return Array containing all goodss
     */
    public static String[] getAllGoodss() {
        int totalLength = TECHNICAL_SUPPORT.length + SERVICE_REQUEST.length +
                CONSULTATION.length + COMPLAINT_SUGGESTION.length +
                OPERATION_MAINTENANCE.length;

        String[] allGoodss = new String[totalLength];
        int index = 0;

        System.arraycopy(TECHNICAL_SUPPORT, 0, allGoodss, index, TECHNICAL_SUPPORT.length);
        index += TECHNICAL_SUPPORT.length;

        System.arraycopy(SERVICE_REQUEST, 0, allGoodss, index, SERVICE_REQUEST.length);
        index += SERVICE_REQUEST.length;

        System.arraycopy(CONSULTATION, 0, allGoodss, index, CONSULTATION.length);
        index += CONSULTATION.length;

        System.arraycopy(COMPLAINT_SUGGESTION, 0, allGoodss, index, COMPLAINT_SUGGESTION.length);
        index += COMPLAINT_SUGGESTION.length;

        System.arraycopy(OPERATION_MAINTENANCE, 0, allGoodss, index, OPERATION_MAINTENANCE.length);

        return allGoodss;
    }
}