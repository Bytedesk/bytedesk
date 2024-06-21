/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-27 16:41:32
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-14 17:15:35
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.socket.controller;

// import java.util.Map;

// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.bytedesk.core.utils.JsonResult;
// import com.bytedesk.core.socket.stomp.service.StompMqService;

// import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// /**
// *
// * http://127.0.0.1:9003/swagger-ui/index.html
// */
// @Slf4j
// @RestController
// @AllArgsConstructor
// @RequestMapping("/api/v1/message/rest")
// public class MessageRestController {

// private final StompMqService stompMqService;

// /**
// * send offline message
// *
// * @param map map
// * @return json
// */
// @PostMapping("/send")
// public ResponseEntity<?> sendOfflineMessage(@RequestBody Map<String, String>
// map) {

// String json = (String) map.get("json");
// log.debug("json {}", json);
// stompMqService.sendMessageToMq(json);
// //
// return ResponseEntity.ok(JsonResult.success(json));
// }

// }
