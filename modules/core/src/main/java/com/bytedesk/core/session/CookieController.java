/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-04 23:09:29
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-05 11:04:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
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
public class CookieController {

    /**
     * v_vid = visitor_vid
     * http://localhost:9003/cookie/
     * 
     * @param username
     * @return
     */
    @GetMapping("/")
    public ResponseEntity<?> getCookie(@CookieValue(value = "v_vid", defaultValue = "no vid") String vid) {
        
        return ResponseEntity.ok(JsonResult.success("get cookie", 200, vid));
    }

    /**
     * http://localhost:9003/cookie/set
     * 
     * @param response
     * @return
     */
    @GetMapping("/set")
    public ResponseEntity<?> setCookie(HttpServletResponse response) {
        // create a cookie
        Cookie cookie = new Cookie("v_vid", Utils.getUid());
        cookie.setMaxAge(365 * 24 * 60 * 60); // expires in 365 days
        // cookie.setSecure(true);
        // cookie.setHttpOnly(true);
        cookie.setPath("/");

        //add cookie to response
        response.addCookie(cookie);

        return ResponseEntity.ok(JsonResult.success("visitor vid is changed"));
    }

    /**
     * http://localhost:9003/cookie/all
     * 
     * @param request
     * @return
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllCookies(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            JSONObject jsonObject = new JSONObject();

            Arrays.stream(cookies).forEach(c -> {
                jsonObject.put(c.getName(), c.getValue());
            });;
            
            return ResponseEntity.ok(JsonResult.success(jsonObject));
        }

        return ResponseEntity.ok(JsonResult.success("no cookies"));
    }


    /**
     * http://localhost:9003/cookie/delete
     * 
     * @param request
     * @param response
     * @return
     */
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
