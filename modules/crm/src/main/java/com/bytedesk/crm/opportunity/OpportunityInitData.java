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
package com.bytedesk.crm.opportunity;

// // import com.bytedesk.core.constant.I18Consts;

// public class OpportunityInitData {

//     /**
//      * Technical Support Opportunitys
//      * 技术支持标签
//      */
//     public static final String[] TECHNICAL_SUPPORT = {
//             // I18Consts.I18N_PREFIX + "thread.opportunity.technical_support", // parent
//             "技术支持", // parent
//     };

//     /**
//      * Service Request Opportunitys
//      * 服务请求标签
//      */
//     public static final String[] SERVICE_REQUEST = {
//             // I18Consts.I18N_PREFIX + "thread.opportunity.service_request", // parent
//             "服务请求", // parent
//     };

//     /**
//      * Consultation Opportunitys
//      * 咨询标签
//      */
//     public static final String[] CONSULTATION = {
//             // I18Consts.I18N_PREFIX + "thread.opportunity.consultation", // parent
//             "咨询", // parent
//     };

//     /**
//      * Complaint &amp; Suggestion Opportunitys
//      * 投诉与建议标签
//      */
//     public static final String[] COMPLAINT_SUGGESTION = {
//             // I18Consts.I18N_PREFIX + "thread.opportunity.complaint_suggestion", // parent
//             "投诉建议", // parent
//     };

//     /**
//      * Operation &amp; Maintenance Opportunitys
//      * 运维标签
//      */
//     public static final String[] OPERATION_MAINTENANCE = {
//             // I18Consts.I18N_PREFIX + "thread.opportunity.operation_maintenance", // parent
//             "运维", // parent
//             // 其他
//             // I18Consts.I18N_PREFIX + "thread.opportunity.other",
//             "其他",
//     };

//     /**
//      * Helper method to determine if a opportunity is a parent opportunity
//      * 
//      * @param opportunity The opportunity key to check
//      * @return true if it's a parent opportunity
//      */
//     public static boolean isParentOpportunity(String opportunity) {
//         return !opportunity.contains(".");
//     }

//     /**
//      * Helper method to get parent opportunity key for a child opportunity
//      * 
//      * @param childOpportunity The child opportunity key
//      * @return The parent opportunity key
//      */
//     public static String getParentOpportunity(String childOpportunity) {
//         if (isParentOpportunity(childOpportunity)) {
//             return null;
//         }
//         // 由于已将常量转为中文，此方法可能需要重新实现
//         // 这里仅保留基本结构，具体实现需要根据新的标签体系来调整
//         return null;
//     }

//     /**
//      * Get all opportunitys as a single array
//      * 
//      * @return Array containing all opportunitys
//      */
//     public static String[] getAllOpportunitys() {
//         int totalLength = TECHNICAL_SUPPORT.length + SERVICE_REQUEST.length +
//                 CONSULTATION.length + COMPLAINT_SUGGESTION.length +
//                 OPERATION_MAINTENANCE.length;

//         String[] allOpportunitys = new String[totalLength];
//         int index = 0;

//         System.arraycopy(TECHNICAL_SUPPORT, 0, allOpportunitys, index, TECHNICAL_SUPPORT.length);
//         index += TECHNICAL_SUPPORT.length;

//         System.arraycopy(SERVICE_REQUEST, 0, allOpportunitys, index, SERVICE_REQUEST.length);
//         index += SERVICE_REQUEST.length;

//         System.arraycopy(CONSULTATION, 0, allOpportunitys, index, CONSULTATION.length);
//         index += CONSULTATION.length;

//         System.arraycopy(COMPLAINT_SUGGESTION, 0, allOpportunitys, index, COMPLAINT_SUGGESTION.length);
//         index += COMPLAINT_SUGGESTION.length;

//         System.arraycopy(OPERATION_MAINTENANCE, 0, allOpportunitys, index, OPERATION_MAINTENANCE.length);

//         return allOpportunitys;
//     }
// }