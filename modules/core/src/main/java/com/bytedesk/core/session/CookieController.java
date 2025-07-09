/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-04 23:09:29
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-05 11:04:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.session;

import java.util.Arrays;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.core.utils.Utils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

/**
 * https://dzone.com/articles/how-to-use-cookies-in-spring-boot
 */
@RestController
@AllArgsConstructor
@RequestMapping("/cookie")
@Tag(name = "Cookie Management", description = "Cookie management APIs for visitor tracking and session management")
public class CookieController {

    /**
     * v_vid = visitor_vid
     * http://127.0.0.1:9003/cookie/
     * 
     * @param username
     * @return
     */
    @Operation(summary = "Get Cookie", description = "Retrieve visitor ID from cookie")
    @GetMapping({"", "/"})
    public ResponseEntity<?> getCookie(@CookieValue(value = "v_vid", defaultValue = "no vid") String vid) {

        return ResponseEntity.ok(JsonResult.success("get cookie", 200, vid));
    }

    /**
     * http://127.0.0.1:9003/cookie/set
     * 
     * @param response
     * @return
     */
    @Operation(summary = "Set Cookie", description = "Set visitor ID cookie")
    @GetMapping("/set")
    public ResponseEntity<?> setCookie(HttpServletResponse response) {
        // create a cookie
        Cookie cookie = new Cookie("v_vid", Utils.getUid());
        cookie.setMaxAge(365 * 24 * 60 * 60); // expires in 365 days
        // cookie.setSecure(true);
        // cookie.setHttpOnly(true);
        cookie.setPath("/");

        // add cookie to response
        response.addCookie(cookie);

        return ResponseEntity.ok(JsonResult.success("visitor vid is changed"));
    }

    /**
     * http://127.0.0.1:9003/cookie/all
     * 
     * @param httpRequest The HTTP request object
     * @return
     */
    @Operation(summary = "Get All Cookies", description = "Retrieve all cookies from the request")
    @GetMapping("/all")
    public ResponseEntity<?> getAllCookies(HttpServletRequest httpRequest) {

        Cookie[] cookies = httpRequest.getCookies();
        if (cookies != null) {
            JSONObject jsonObject = new JSONObject();

            Arrays.stream(cookies).forEach(c -> {
                jsonObject.put(c.getName(), c.getValue());
            });
            ;

            return ResponseEntity.ok(JsonResult.success(jsonObject));
        }

        return ResponseEntity.ok(JsonResult.success("no cookies"));
    }

    /**
     * http://127.0.0.1:9003/cookie/delete
     * 
     * @param response
     * @return
     */
    @Operation(summary = "Delete Cookie", description = "Delete visitor ID cookie")
    @GetMapping("/delete")
    public ResponseEntity<?> deleteCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("v_vid", null);
        cookie.setMaxAge(0);
        cookie.setSecure(false);
        cookie.setHttpOnly(false);
        response.addCookie(cookie);

        return ResponseEntity.ok(JsonResult.success("cookie is deleted!"));
    }

}
