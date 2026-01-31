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
package com.bytedesk.crm.lead_follow;

// import com.bytedesk.core.constant.I18Consts;

// public class LeadFollowInitData {

//     /**
//      * Technical Support LeadFollows
//      * 技术支持标签
//      */
//     public static final String[] TECHNICAL_SUPPORT = {
//             // I18Consts.I18N_PREFIX + "thread.lead_follow.technical_support", // parent
//             "技术支持", // parent
//     };

//     /**
//      * Service Request LeadFollows
//      * 服务请求标签
//      */
//     public static final String[] SERVICE_REQUEST = {
//             // I18Consts.I18N_PREFIX + "thread.lead_follow.service_request", // parent
//             "服务请求", // parent
//     };

//     /**
//      * Consultation LeadFollows
//      * 咨询标签
//      */
//     public static final String[] CONSULTATION = {
//             // I18Consts.I18N_PREFIX + "thread.lead_follow.consultation", // parent
//             "咨询", // parent
//     };

//     /**
//      * Complaint &amp; Suggestion LeadFollows
//      * 投诉与建议标签
//      */
//     public static final String[] COMPLAINT_SUGGESTION = {
//             // I18Consts.I18N_PREFIX + "thread.lead_follow.complaint_suggestion", // parent
//             "投诉建议", // parent
//     };

//     /**
//      * Operation &amp; Maintenance LeadFollows
//      * 运维标签
//      */
//     public static final String[] OPERATION_MAINTENANCE = {
//             // I18Consts.I18N_PREFIX + "thread.lead_follow.operation_maintenance", // parent
//             "运维", // parent
//             // 其他
//             // I18Consts.I18N_PREFIX + "thread.lead_follow.other",
//             "其他",
//     };

//     /**
//      * Helper method to determine if a lead_follow is a parent lead_follow
//      * 
//      * @param lead_follow The lead_follow key to check
//      * @return true if it's a parent lead_follow
//      */
//     public static boolean isParentLeadFollow(String lead_follow) {
//         return !lead_follow.contains(".");
//     }

//     /**
//      * Helper method to get parent lead_follow key for a child lead_follow
//      * 
//      * @param childLeadFollow The child lead_follow key
//      * @return The parent lead_follow key
//      */
//     public static String getParentLeadFollow(String childLeadFollow) {
//         if (isParentLeadFollow(childLeadFollow)) {
//             return null;
//         }
//         // 由于已将常量转为中文，此方法可能需要重新实现
//         // 这里仅保留基本结构，具体实现需要根据新的标签体系来调整
//         return null;
//     }

//     /**
//      * Get all lead_follows as a single array
//      * 
//      * @return Array containing all lead_follows
//      */
//     public static String[] getAllLeadFollows() {
//         int totalLength = TECHNICAL_SUPPORT.length + SERVICE_REQUEST.length +
//                 CONSULTATION.length + COMPLAINT_SUGGESTION.length +
//                 OPERATION_MAINTENANCE.length;

//         String[] allLeadFollows = new String[totalLength];
//         int index = 0;

//         System.arraycopy(TECHNICAL_SUPPORT, 0, allLeadFollows, index, TECHNICAL_SUPPORT.length);
//         index += TECHNICAL_SUPPORT.length;

//         System.arraycopy(SERVICE_REQUEST, 0, allLeadFollows, index, SERVICE_REQUEST.length);
//         index += SERVICE_REQUEST.length;

//         System.arraycopy(CONSULTATION, 0, allLeadFollows, index, CONSULTATION.length);
//         index += CONSULTATION.length;

//         System.arraycopy(COMPLAINT_SUGGESTION, 0, allLeadFollows, index, COMPLAINT_SUGGESTION.length);
//         index += COMPLAINT_SUGGESTION.length;

//         System.arraycopy(OPERATION_MAINTENANCE, 0, allLeadFollows, index, OPERATION_MAINTENANCE.length);

//         return allLeadFollows;
//     }
// }