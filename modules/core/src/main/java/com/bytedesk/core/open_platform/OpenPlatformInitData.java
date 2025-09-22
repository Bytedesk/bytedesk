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
package com.bytedesk.core.open_platform;

// import com.bytedesk.core.constant.I18Consts;

public class OpenPlatformInitData {

    /**
     * Technical Support OpenPlatforms
     * 技术支持标签
     */
    public static final String[] TECHNICAL_SUPPORT = {
            // I18Consts.I18N_PREFIX + "thread.openPlatform.technical_support", // parent
            "技术支持", // parent
    };

    /**
     * Service Request OpenPlatforms
     * 服务请求标签
     */
    public static final String[] SERVICE_REQUEST = {
            // I18Consts.I18N_PREFIX + "thread.openPlatform.service_request", // parent
            "服务请求", // parent
    };

    /**
     * Consultation OpenPlatforms
     * 咨询标签
     */
    public static final String[] CONSULTATION = {
            // I18Consts.I18N_PREFIX + "thread.openPlatform.consultation", // parent
            "咨询", // parent
    };

    /**
     * Complaint &amp; Suggestion OpenPlatforms
     * 投诉与建议标签
     */
    public static final String[] COMPLAINT_SUGGESTION = {
            // I18Consts.I18N_PREFIX + "thread.openPlatform.complaint_suggestion", // parent
            "投诉建议", // parent
    };

    /**
     * Operation &amp; Maintenance OpenPlatforms
     * 运维标签
     */
    public static final String[] OPERATION_MAINTENANCE = {
            // I18Consts.I18N_PREFIX + "thread.openPlatform.operation_maintenance", // parent
            "运维", // parent
            // 其他
            // I18Consts.I18N_PREFIX + "thread.openPlatform.other",
            "其他",
    };

    /**
     * Helper method to determine if a openPlatform is a parent openPlatform
     * 
     * @param openPlatform The openPlatform key to check
     * @return true if it's a parent openPlatform
     */
    public static boolean isParentOpenPlatform(String openPlatform) {
        return !openPlatform.contains(".");
    }

    /**
     * Helper method to get parent openPlatform key for a child openPlatform
     * 
     * @param childOpenPlatform The child openPlatform key
     * @return The parent openPlatform key
     */
    public static String getParentOpenPlatform(String childOpenPlatform) {
        if (isParentOpenPlatform(childOpenPlatform)) {
            return null;
        }
        // 由于已将常量转为中文，此方法可能需要重新实现
        // 这里仅保留基本结构，具体实现需要根据新的标签体系来调整
        return null;
    }

    /**
     * Get all openPlatforms as a single array
     * 
     * @return Array containing all openPlatforms
     */
    public static String[] getAllOpenPlatforms() {
        int totalLength = TECHNICAL_SUPPORT.length + SERVICE_REQUEST.length +
                CONSULTATION.length + COMPLAINT_SUGGESTION.length +
                OPERATION_MAINTENANCE.length;

        String[] allOpenPlatforms = new String[totalLength];
        int index = 0;

        System.arraycopy(TECHNICAL_SUPPORT, 0, allOpenPlatforms, index, TECHNICAL_SUPPORT.length);
        index += TECHNICAL_SUPPORT.length;

        System.arraycopy(SERVICE_REQUEST, 0, allOpenPlatforms, index, SERVICE_REQUEST.length);
        index += SERVICE_REQUEST.length;

        System.arraycopy(CONSULTATION, 0, allOpenPlatforms, index, CONSULTATION.length);
        index += CONSULTATION.length;

        System.arraycopy(COMPLAINT_SUGGESTION, 0, allOpenPlatforms, index, COMPLAINT_SUGGESTION.length);
        index += COMPLAINT_SUGGESTION.length;

        System.arraycopy(OPERATION_MAINTENANCE, 0, allOpenPlatforms, index, OPERATION_MAINTENANCE.length);

        return allOpenPlatforms;
    }
}