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
package com.bytedesk.crm.product;

// import com.bytedesk.core.constant.I18Consts;

// public class ProductInitData {

//     /**
//      * Technical Support Products
//      * 技术支持标签
//      */
//     public static final String[] TECHNICAL_SUPPORT = {
//             // I18Consts.I18N_PREFIX + "thread.product.technical_support", // parent
//             "技术支持", // parent
//     };

//     /**
//      * Service Request Products
//      * 服务请求标签
//      */
//     public static final String[] SERVICE_REQUEST = {
//             // I18Consts.I18N_PREFIX + "thread.product.service_request", // parent
//             "服务请求", // parent
//     };

//     /**
//      * Consultation Products
//      * 咨询标签
//      */
//     public static final String[] CONSULTATION = {
//             // I18Consts.I18N_PREFIX + "thread.product.consultation", // parent
//             "咨询", // parent
//     };

//     /**
//      * Complaint &amp; Suggestion Products
//      * 投诉与建议标签
//      */
//     public static final String[] COMPLAINT_SUGGESTION = {
//             // I18Consts.I18N_PREFIX + "thread.product.complaint_suggestion", // parent
//             "投诉建议", // parent
//     };

//     /**
//      * Operation &amp; Maintenance Products
//      * 运维标签
//      */
//     public static final String[] OPERATION_MAINTENANCE = {
//             // I18Consts.I18N_PREFIX + "thread.product.operation_maintenance", // parent
//             "运维", // parent
//             // 其他
//             // I18Consts.I18N_PREFIX + "thread.product.other",
//             "其他",
//     };

//     /**
//      * Helper method to determine if a product is a parent product
//      * 
//      * @param product The product key to check
//      * @return true if it's a parent product
//      */
//     public static boolean isParentProduct(String product) {
//         return !product.contains(".");
//     }

//     /**
//      * Helper method to get parent product key for a child product
//      * 
//      * @param childProduct The child product key
//      * @return The parent product key
//      */
//     public static String getParentProduct(String childProduct) {
//         if (isParentProduct(childProduct)) {
//             return null;
//         }
//         // 由于已将常量转为中文，此方法可能需要重新实现
//         // 这里仅保留基本结构，具体实现需要根据新的标签体系来调整
//         return null;
//     }

//     /**
//      * Get all products as a single array
//      * 
//      * @return Array containing all products
//      */
//     public static String[] getAllProducts() {
//         int totalLength = TECHNICAL_SUPPORT.length + SERVICE_REQUEST.length +
//                 CONSULTATION.length + COMPLAINT_SUGGESTION.length +
//                 OPERATION_MAINTENANCE.length;

//         String[] allProducts = new String[totalLength];
//         int index = 0;

//         System.arraycopy(TECHNICAL_SUPPORT, 0, allProducts, index, TECHNICAL_SUPPORT.length);
//         index += TECHNICAL_SUPPORT.length;

//         System.arraycopy(SERVICE_REQUEST, 0, allProducts, index, SERVICE_REQUEST.length);
//         index += SERVICE_REQUEST.length;

//         System.arraycopy(CONSULTATION, 0, allProducts, index, CONSULTATION.length);
//         index += CONSULTATION.length;

//         System.arraycopy(COMPLAINT_SUGGESTION, 0, allProducts, index, COMPLAINT_SUGGESTION.length);
//         index += COMPLAINT_SUGGESTION.length;

//         System.arraycopy(OPERATION_MAINTENANCE, 0, allProducts, index, OPERATION_MAINTENANCE.length);

//         return allProducts;
//     }
// }