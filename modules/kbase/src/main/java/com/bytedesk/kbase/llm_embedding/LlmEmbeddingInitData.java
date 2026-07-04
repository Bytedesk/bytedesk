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
package com.bytedesk.kbase.llm_embedding;

// import com.bytedesk.core.constant.I18Consts;

// public class LlmEmbeddingInitData {

//     /**
//      * Technical Support LlmEmbeddings
//      * 技术支持标签
//      */
//     public static final String[] TECHNICAL_SUPPORT = {
//             // I18Consts.I18N_PREFIX + "thread.llm_embedding.technical_support", // parent
//             "技术支持", // parent
//     };

//     /**
//      * Service Request LlmEmbeddings
//      * 服务请求标签
//      */
//     public static final String[] SERVICE_REQUEST = {
//             // I18Consts.I18N_PREFIX + "thread.llm_embedding.service_request", // parent
//             "服务请求", // parent
//     };

//     /**
//      * Consultation LlmEmbeddings
//      * 咨询标签
//      */
//     public static final String[] CONSULTATION = {
//             // I18Consts.I18N_PREFIX + "thread.llm_embedding.consultation", // parent
//             "咨询", // parent
//     };

//     /**
//      * Complaint &amp; Suggestion LlmEmbeddings
//      * 投诉与建议标签
//      */
//     public static final String[] COMPLAINT_SUGGESTION = {
//             // I18Consts.I18N_PREFIX + "thread.llm_embedding.complaint_suggestion", // parent
//             "投诉建议", // parent
//     };

//     /**
//      * Operation &amp; Maintenance LlmEmbeddings
//      * 运维标签
//      */
//     public static final String[] OPERATION_MAINTENANCE = {
//             // I18Consts.I18N_PREFIX + "thread.llm_embedding.operation_maintenance", // parent
//             "运维", // parent
//             // 其他
//             // I18Consts.I18N_PREFIX + "thread.llm_embedding.other",
//             "其他",
//     };

//     /**
//      * Helper method to determine if a llm_embedding is a parent llm_embedding
//      * 
//      * @param llm_embedding The llm_embedding key to check
//      * @return true if it's a parent llm_embedding
//      */
//     public static boolean isParentLlmEmbedding(String llm_embedding) {
//         return !llm_embedding.contains(".");
//     }

//     /**
//      * Helper method to get parent llm_embedding key for a child llm_embedding
//      * 
//      * @param childLlmEmbedding The child llm_embedding key
//      * @return The parent llm_embedding key
//      */
//     public static String getParentLlmEmbedding(String childLlmEmbedding) {
//         if (isParentLlmEmbedding(childLlmEmbedding)) {
//             return null;
//         }
//         // 由于已将常量转为中文，此方法可能需要重新实现
//         // 这里仅保留基本结构，具体实现需要根据新的标签体系来调整
//         return null;
//     }

//     /**
//      * Get all llm_embeddings as a single array
//      * 
//      * @return Array containing all llm_embeddings
//      */
//     public static String[] getAllLlmEmbeddings() {
//         int totalLength = TECHNICAL_SUPPORT.length + SERVICE_REQUEST.length +
//                 CONSULTATION.length + COMPLAINT_SUGGESTION.length +
//                 OPERATION_MAINTENANCE.length;

//         String[] allLlmEmbeddings = new String[totalLength];
//         int index = 0;

//         System.arraycopy(TECHNICAL_SUPPORT, 0, allLlmEmbeddings, index, TECHNICAL_SUPPORT.length);
//         index += TECHNICAL_SUPPORT.length;

//         System.arraycopy(SERVICE_REQUEST, 0, allLlmEmbeddings, index, SERVICE_REQUEST.length);
//         index += SERVICE_REQUEST.length;

//         System.arraycopy(CONSULTATION, 0, allLlmEmbeddings, index, CONSULTATION.length);
//         index += CONSULTATION.length;

//         System.arraycopy(COMPLAINT_SUGGESTION, 0, allLlmEmbeddings, index, COMPLAINT_SUGGESTION.length);
//         index += COMPLAINT_SUGGESTION.length;

//         System.arraycopy(OPERATION_MAINTENANCE, 0, allLlmEmbeddings, index, OPERATION_MAINTENANCE.length);

//         return allLlmEmbeddings;
//     }
// }