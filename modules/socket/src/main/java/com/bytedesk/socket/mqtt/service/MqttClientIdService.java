/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-16 09:33:13
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.socket.mqtt.service;

// import java.util.ArrayList;
// import java.util.Collections;
// import java.util.List;
// import java.util.Map;
// import java.util.concurrent.ConcurrentHashMap;

// import org.springframework.stereotype.Service;

// import com.bytedesk.socket.mqtt.service.MqttClientIdService;
// import lombok.AllArgsConstructor;
// // import lombok.extern.slf4j.Slf4j;

// /**
//  * 
//  */
// // @Slf4j
// @Service
// @AllArgsConstructor
// public class MqttClientIdService {

//     private Map<String, List<String>> clientIdCache = new ConcurrentHashMap<>();

//     public void put(String clientId) {
//         final String uid = clientId.split("/")[0];
//         List<String> clientIdList = clientIdCache.get(uid);
//         if (clientIdList == null) {
//             // 加锁
//             clientIdList = Collections.synchronizedList(new ArrayList<>());
//         }
//         if (!clientIdList.contains(clientId)) {
//             clientIdList.add(clientId);
//             clientIdCache.put(uid, clientIdList);
//         }
//     }

//     public List<String> get(String uid) {
//         List<String> clientIdList = clientIdCache.get(uid);
//         if (clientIdList == null) {
//             return new ArrayList<>();
//         }
//         return clientIdList;
//     }

//     public void remove(String clientId) {
//         final String uid = clientId.split("/")[0];
//         List<String> clientIdList = clientIdCache.get(uid);
//         if (clientIdList != null && clientIdList.contains(clientId)) {
//             clientIdList.remove(clientId);
//         }
//     }

//     public boolean contains(String clientId) {
//         final String uid = clientId.split("/")[0];
//         List<String> clientIdList = clientIdCache.get(uid);
//         if (clientIdList != null && clientIdList.contains(clientId)) {
//             return true;
//         }
//         return false;
//     }

// }