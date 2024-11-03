/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-05 14:15:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-30 15:53:08
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

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/ip/api/v1")
public class IpRestController {

    private final IpService ipService;

    @PostMapping("/block")
    public ResponseEntity<?> blockIp(@RequestBody IpRequest request) {
        ipService.blockIp(request);
        return ResponseEntity.ok(JsonResult.success());
    }

    @PostMapping("/unblock")
    public ResponseEntity<?> unblockIp(@RequestBody IpRequest request) {
        return ResponseEntity.ok(JsonResult.success());
    }

    @PostMapping("/white")
    public ResponseEntity<?> whiteIp(@RequestBody IpRequest request) {
        return ResponseEntity.ok(JsonResult.success());
    }

    @PostMapping("/unwhite")
    public ResponseEntity<?> unwhiteIp(@RequestBody IpRequest request) {
        return ResponseEntity.ok(JsonResult.success());
    }
    
    /**
     * http://127.0.0.1:9003/ip/api/v1/
     *
     * @return json
     */
    @GetMapping("/")
    public JsonResult<?> ip(HttpServletRequest request) {
        return new JsonResult<>("your ip", 200, IpUtils.clientIp(request));
    }

    /**
     * http://127.0.0.1:9003/ip/api/v1/location
     * https://api.weiyuai.cn/ip/api/v1/location
     * location: "国家|区域|省份|城市|ISP"
     * location: "中国|0|湖北省|武汉市|联通"
     * 
     * @param request
     * @return
     */
    @GetMapping("/location")
    public JsonResult<?> location(HttpServletRequest request) {

        String ip = IpUtils.clientIp(request);
        // location: "中国|0|湖北省|武汉市|联通"
        String location = ipService.getIpLocation(ip);
        // 
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ip", ip);
        jsonObject.put("location", location);
        // 
        return new JsonResult<>("your ip location", 200, jsonObject);
    }

    /**
     * http://127.0.0.1:9003/ip/api/v1/ip/location?ip=202.106.212.226
     * 
     * @param request
     * @return
     */
    @GetMapping("/ip/location")
    public JsonResult<?> ipLocation(@RequestParam String ip) {

        // location: "国家|区域|省份|城市|ISP"
        // location: "中国|0|湖北省|武汉市|联通"
        String location = ipService.getIpLocation(ip);
        // 
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ip", ip);
        jsonObject.put("location", location);

        return new JsonResult<>("ip location", 200, jsonObject);
    }

    // http://127.0.0.1:9003/ip/api/v1/ip/inrange?ip=192.168.1.100
    @GetMapping("/ip/inrange")
    public JsonResult<?> ipInRange(@RequestParam String ip) {

        boolean isInRange = IpService.testIsIpInRange(ip);
        //
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ip", ip);
        jsonObject.put("isInRange", isInRange);

        return new JsonResult<>("ip isInRange", 200, jsonObject);
    }

    // for testing
    // http://127.0.0.1:9003/ip/api/v1/ip/province?ip=202.106.212.226
    @GetMapping("/ip/province")
    public JsonResult<?> ipProvince(@RequestParam String ip) {

        // location: "国家|区域|省份|城市|ISP"
        // location: "中国|0|湖北省|武汉市|联通"
        // 0|0|0|内网IP|内网IP
        String location = ipService.getIpLocation(ip);
        // 
        String[] locals = location.split("\\|");
        log.info("location {} locals {}", location, (Object[]) locals); // Cast to Object[] to confirm the non-varargs invocation
        String province = "";
        if (locals.length > 2) {
            if (locals[2].equals("0")) {
                province = "Local";
            } else {
                province = locals[2];
            }
        }
        //
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ip", ip);
        jsonObject.put("province", province);

        return new JsonResult<>("ip nickname", 200, jsonObject);
    }

    /**
     * comment out for safety reason
     * server host info
     * http://127.0.0.1:9003/ip/api/v1/server
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
