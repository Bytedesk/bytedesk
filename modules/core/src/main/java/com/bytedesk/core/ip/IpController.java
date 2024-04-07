/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-05 14:15:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-05 14:37:36
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.ip;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

/**
 * 
 * http://localhost:9003/swagger-ui/index.html
 */
// @Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/ip/api/v1")
public class IpController {

    private final IpService ipService;
    
    /**
     * http://localhost:9003/ip/api/v1/
     *
     * @return json
     */
    @GetMapping("/")
    public JsonResult<?> ip(HttpServletRequest request) {

        return new JsonResult<>("your ip", 200, IpUtils.clientIp(request));
    }

    /**
     * http://localhost:9003/ip/api/v1/location
     * 
     * @param request
     * @return
     */
    @GetMapping("/location")
    public JsonResult<?> location(HttpServletRequest request) {

        String ip = IpUtils.clientIp(request);
        String location = ipService.getIpLocation(ip);
        // 
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ip", ip);
        jsonObject.put("location", location);
        // 
        return new JsonResult<>("your ip location", 200, jsonObject);
    }

    /**
     * http://localhost:9003/ip/api/v1/ip/location?ip=103.46.244.251
     * 
     * @param request
     * @return
     */
    @GetMapping("/ip/location")
    public JsonResult<?> ipLocation(@RequestParam String ip) {

        String location = ipService.getIpLocation(ip);
        // 
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ip", ip);
        jsonObject.put("location", location);

        return new JsonResult<>("ip location", 200, jsonObject);
    }

    /**
     * comment out for safety reason
     * server host info
     * http://localhost:9003/ip/api/v1/server
     * @return
     */
    // @GetMapping("/server")
    // public JsonResult<?> ipServer() {

    //     String serverIp = IpUtils.getServerIp();
    //     String serverHostname = IpUtils.hostname();
    //     // 
    //     JSONObject jsonObject = new JSONObject();
    //     jsonObject.put("ip", serverIp);
    //     jsonObject.put("hostname", serverHostname);
    //     // 
    //     return new JsonResult<>("server info", 200, jsonObject);
    // }


}
