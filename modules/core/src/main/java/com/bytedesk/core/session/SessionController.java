/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-05 00:15:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-05 10:28:03
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.session;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * https://docs.spring.io/spring-session/reference/guides/boot-redis.html
 * 
 * @description
 * @author jackning
 * @date 2024-04-05 00:15:55
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/session")
@Tag(name = "Session Management", description = "Session management APIs for user session handling")
public class SessionController {

    /**
     * http://127.0.0.1:9003/session/
     * 
     * @param session
     * @return
     */
    @Operation(summary = "Get Session", description = "Retrieve current session information")
    @GetMapping({"", "/"})
    public ResponseEntity<?> getSession(HttpSession session) {
        log.info("sessionId:[{}]", session.getId());
        //
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return ResponseEntity.ok(JsonResult.error("session is null"));
        }
        //
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", username);
        jsonObject.put("sessionId", session.getId());
        //
        return ResponseEntity.ok(JsonResult.success(jsonObject));
    }

    /**
     * http://127.0.0.1:9003/session/set?username=chrome
     * 
     * @param username
     * @param session
     * @return
     */
    @Operation(summary = "Set Session", description = "Set username in session")
    @GetMapping("/set")
    public ResponseEntity<?> setSession(@RequestParam String username, HttpSession session) {
        log.info("sessionId:[{}]", session.getId());
        //
        session.setAttribute("username", username);
        //
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", username);
        jsonObject.put("sessionId", session.getId());

        return ResponseEntity.ok(JsonResult.success(jsonObject));
    }

}
