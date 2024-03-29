/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-03-01 17:26:34
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * http://localhost:9003/swagger-ui/index.html
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/message")
public class MessageController {

    private final MessageService messageService;

    /**
     * TODO: 
     * 1. 判断是否有更新，如果有，则读取缓存
     * 2. 如果没有更新，则返回提示：无需更新
     * @return json
     */
    @GetMapping("/query")
    public JsonResult<?> query(MessageRequest messageRequest) {

        Page<MessageResponse> messagePage = messageService.query(messageRequest);
        //
        return new JsonResult<>("query message success", 200, messagePage);
    }


    /**
     * send offline message
     * 
     * @param map map
     * @return json
     */
    @PostMapping("/send")
    public JsonResult<?> sendOfflineMessage(@RequestBody Map<String, String> map) {

        String json = (String) map.get("json");
        log.debug("json {}", json);
        // stompMqService.sendMessageToMq(json);
        //
        return new JsonResult<>("send offline message success", 200, json);
    }

}
