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
package com.bytedesk.core.push.apns_push;

// import com.bytedesk.core.constant.I18Consts;

// public class ApnsPushInitData {

//     /**
//      * Technical Support ApnsPushs
//      * 技术支持标签
//      */
//     public static final String[] TECHNICAL_SUPPORT = {
//             // I18Consts.I18N_PREFIX + "thread.apns_push.technical_support", // parent
//             "技术支持", // parent
//     };

//     /**
//      * Service Request ApnsPushs
//      * 服务请求标签
//      */
//     public static final String[] SERVICE_REQUEST = {
//             // I18Consts.I18N_PREFIX + "thread.apns_push.service_request", // parent
//             "服务请求", // parent
//     };

//     /**
//      * Consultation ApnsPushs
//      * 咨询标签
//      */
//     public static final String[] CONSULTATION = {
//             // I18Consts.I18N_PREFIX + "thread.apns_push.consultation", // parent
//             "咨询", // parent
//     };

//     /**
//      * Complaint &amp; Suggestion ApnsPushs
//      * 投诉与建议标签
//      */
//     public static final String[] COMPLAINT_SUGGESTION = {
//             // I18Consts.I18N_PREFIX + "thread.apns_push.complaint_suggestion", // parent
//             "投诉建议", // parent
//     };

//     /**
//      * Operation &amp; Maintenance ApnsPushs
//      * 运维标签
//      */
//     public static final String[] OPERATION_MAINTENANCE = {
//             // I18Consts.I18N_PREFIX + "thread.apns_push.operation_maintenance", // parent
//             "运维", // parent
//             // 其他
//             // I18Consts.I18N_PREFIX + "thread.apns_push.other",
//             "其他",
//     };

//     /**
//      * Helper method to determine if a apns_push is a parent apns_push
//      * 
//      * @param apns_push The apns_push key to check
//      * @return true if it's a parent apns_push
//      */
//     public static boolean isParentApnsPush(String apns_push) {
//         return !apns_push.contains(".");
//     }

//     /**
//      * Helper method to get parent apns_push key for a child apns_push
//      * 
//      * @param childApnsPush The child apns_push key
//      * @return The parent apns_push key
//      */
//     public static String getParentApnsPush(String childApnsPush) {
//         if (isParentApnsPush(childApnsPush)) {
//             return null;
//         }
//         // 由于已将常量转为中文，此方法可能需要重新实现
//         // 这里仅保留基本结构，具体实现需要根据新的标签体系来调整
//         return null;
//     }

//     /**
//      * Get all apns_pushs as a single array
//      * 
//      * @return Array containing all apns_pushs
//      */
//     public static String[] getAllApnsPushs() {
//         int totalLength = TECHNICAL_SUPPORT.length + SERVICE_REQUEST.length +
//                 CONSULTATION.length + COMPLAINT_SUGGESTION.length +
//                 OPERATION_MAINTENANCE.length;

//         String[] allApnsPushs = new String[totalLength];
//         int index = 0;

//         System.arraycopy(TECHNICAL_SUPPORT, 0, allApnsPushs, index, TECHNICAL_SUPPORT.length);
//         index += TECHNICAL_SUPPORT.length;

//         System.arraycopy(SERVICE_REQUEST, 0, allApnsPushs, index, SERVICE_REQUEST.length);
//         index += SERVICE_REQUEST.length;

//         System.arraycopy(CONSULTATION, 0, allApnsPushs, index, CONSULTATION.length);
//         index += CONSULTATION.length;

//         System.arraycopy(COMPLAINT_SUGGESTION, 0, allApnsPushs, index, COMPLAINT_SUGGESTION.length);
//         index += COMPLAINT_SUGGESTION.length;

//         System.arraycopy(OPERATION_MAINTENANCE, 0, allApnsPushs, index, OPERATION_MAINTENANCE.length);

//         return allApnsPushs;
//     }
// }