/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-06 09:42:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-26 15:45:14
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor;

// import java.util.ArrayList;
// import java.util.List;
// import java.util.stream.Collectors;

// // import org.springframework.util.StringUtils;

// /**
//  * 访客数据转换器
//  */
// public class VisitorExcelConverter {
    
//     /**
//      * 将单个 VisitorResponse 转换为 VisitorExcel
//      * 
//      * @param response VisitorResponse 对象
//      * @return VisitorExcel 对象
//      */
//     public static VisitorExcel convert(VisitorResponse response) {
//         if (response == null) {
//             return null;
//         }
        
//         VisitorExcel excel = new VisitorExcel();
//         excel.setUid(response.getUid());
//         excel.setNickname(response.getNickname());
//         // excel.setAvatar(response.getAvatar());
//         // excel.setLang(response.getLang());
//         // // 设备信息处理
//         // if (response.getDevice() != null) {
//         //     excel.setDevice(response.getDevice().toString());
//         // }
//         excel.setMobile(response.getMobile());
//         excel.setEmail(response.getEmail());
//         excel.setNote(response.getNote());
//         // 客户端信息处理
//         if (response.getChannel() != null) {
//             excel.setChannel(response.getChannel().name());
//         }
//         // excel.setStatus(response.getStatus());
//         // 标签列表转换为字符串
//         // if (response.getTagList() != null &amp;amp;& !response.getTagList().isEmpty()) {
//         //     excel.setTags(String.join(", ", response.getTagList()));
//         // }
//         // excel.setExtra(response.getExtra());
//         excel.setIp(response.getIp());
//         excel.setIpLocation(response.getIpLocation());
//         excel.setVipLevel(response.getVipLevel());
        
//         return excel;
//     }
    
//     /**
//      * 将 VisitorResponse 列表转换为 VisitorExcel 列表
//      * 
//      * @param responseList VisitorResponse 列表
//      * @return VisitorExcel 列表
//      */
//     public static List<VisitorExcel> convertList(List<VisitorResponse> responseList) {
//         if (responseList == null || responseList.isEmpty()) {
//             return new ArrayList<>();
//         }
        
//         return responseList.stream()
//                 .map(VisitorExcelConverter::convert)
//                 .collect(Collectors.toList());
//     }
// }