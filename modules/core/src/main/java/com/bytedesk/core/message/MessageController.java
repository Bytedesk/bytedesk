/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-14 12:02:03
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

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

/**
 * 
 * http://127.0.0.1:9003/swagger-ui/index.html
 */
// @Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/message")
public class MessageController {

    private final MessageService messageService;

    /**
     * 管理后台 根据 orgUids 查询
     * 
     * @param messageRequest
     * @return
     */
    @GetMapping("/org")
    public ResponseEntity<?> queryAll(MessageRequest messageRequest) {

        Page<MessageResponse> messagePage = messageService.queryAll(messageRequest);
        //
        return ResponseEntity.ok(JsonResult.success(messagePage));
    }

    /**
     * TODO:
     * 1. 判断是否有更新，如果有，则读取缓存
     * 2. 如果没有更新，则返回提示：无需更新
     * 
     * @return json
     */
    @GetMapping("/query")
    public ResponseEntity<?> query(MessageRequest messageRequest) {

        Page<MessageResponse> messagePage = messageService.query(messageRequest);
        //
        return ResponseEntity.ok(JsonResult.success(messagePage));
    }

}
