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
package com.bytedesk.core.push.apns_p12;

// import com.bytedesk.core.constant.I18Consts;

// public class ApnsP12InitData {

//     /**
//      * Technical Support ApnsP12s
//      * 技术支持标签
//      */
//     public static final String[] TECHNICAL_SUPPORT = {
//             // I18Consts.I18N_PREFIX + "thread.apns_p12.technical_support", // parent
//             "技术支持", // parent
//     };

//     /**
//      * Service Request ApnsP12s
//      * 服务请求标签
//      */
//     public static final String[] SERVICE_REQUEST = {
//             // I18Consts.I18N_PREFIX + "thread.apns_p12.service_request", // parent
//             "服务请求", // parent
//     };

//     /**
//      * Consultation ApnsP12s
//      * 咨询标签
//      */
//     public static final String[] CONSULTATION = {
//             // I18Consts.I18N_PREFIX + "thread.apns_p12.consultation", // parent
//             "咨询", // parent
//     };

//     /**
//      * Complaint &amp; Suggestion ApnsP12s
//      * 投诉与建议标签
//      */
//     public static final String[] COMPLAINT_SUGGESTION = {
//             // I18Consts.I18N_PREFIX + "thread.apns_p12.complaint_suggestion", // parent
//             "投诉建议", // parent
//     };

//     /**
//      * Operation &amp; Maintenance ApnsP12s
//      * 运维标签
//      */
//     public static final String[] OPERATION_MAINTENANCE = {
//             // I18Consts.I18N_PREFIX + "thread.apns_p12.operation_maintenance", // parent
//             "运维", // parent
//             // 其他
//             // I18Consts.I18N_PREFIX + "thread.apns_p12.other",
//             "其他",
//     };

//     /**
//      * Helper method to determine if a apns_p12 is a parent apns_p12
//      * 
//      * @param apns_p12 The apns_p12 key to check
//      * @return true if it's a parent apns_p12
//      */
//     public static boolean isParentApnsP12(String apns_p12) {
//         return !apns_p12.contains(".");
//     }

//     /**
//      * Helper method to get parent apns_p12 key for a child apns_p12
//      * 
//      * @param childApnsP12 The child apns_p12 key
//      * @return The parent apns_p12 key
//      */
//     public static String getParentApnsP12(String childApnsP12) {
//         if (isParentApnsP12(childApnsP12)) {
//             return null;
//         }
//         // 由于已将常量转为中文，此方法可能需要重新实现
//         // 这里仅保留基本结构，具体实现需要根据新的标签体系来调整
//         return null;
//     }

//     /**
//      * Get all apns_p12s as a single array
//      * 
//      * @return Array containing all apns_p12s
//      */
//     public static String[] getAllApnsP12s() {
//         int totalLength = TECHNICAL_SUPPORT.length + SERVICE_REQUEST.length +
//                 CONSULTATION.length + COMPLAINT_SUGGESTION.length +
//                 OPERATION_MAINTENANCE.length;

//         String[] allApnsP12s = new String[totalLength];
//         int index = 0;

//         System.arraycopy(TECHNICAL_SUPPORT, 0, allApnsP12s, index, TECHNICAL_SUPPORT.length);
//         index += TECHNICAL_SUPPORT.length;

//         System.arraycopy(SERVICE_REQUEST, 0, allApnsP12s, index, SERVICE_REQUEST.length);
//         index += SERVICE_REQUEST.length;

//         System.arraycopy(CONSULTATION, 0, allApnsP12s, index, CONSULTATION.length);
//         index += CONSULTATION.length;

//         System.arraycopy(COMPLAINT_SUGGESTION, 0, allApnsP12s, index, COMPLAINT_SUGGESTION.length);
//         index += COMPLAINT_SUGGESTION.length;

//         System.arraycopy(OPERATION_MAINTENANCE, 0, allApnsP12s, index, OPERATION_MAINTENANCE.length);

//         return allApnsP12s;
//     }
// }