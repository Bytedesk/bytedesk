/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-05 14:15:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-01 22:11:44
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
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
import com.bytedesk.core.utils.IpUtils;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * for testing
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/visitor/api/v1/ip")
@Tag(name = "IP Management", description = "IP address management and location services APIs")
public class IpRestControllerVisitor {

    private final IpService ipService;

    /**
     * http://127.0.0.1:9003/visitor/api/v1/ip/
     *
     * @return json
     */
    @Operation(summary = "Get Client IP", description = "Get the client's IP address")
    @GetMapping({ "", "/" })
    public JsonResult<?> ip(HttpServletRequest request) {
        return new JsonResult<>("your ip", 200, IpUtils.getClientIp(request));
    }

    /**
     * http://127.0.0.1:9003/visitor/api/v1/ip/location
     * https://api.weiyuai.cn/visitor/api/v1/ip/location
     * location: "国家|区域|省份|城市|ISP"
     * location: "中国|0|湖北省|武汉市|联通"，缺省的地域信息默认是0。
     * 
     * @param request
     * @return
     */
    @Operation(summary = "Get IP Location", description = "Get location information for the client's IP address")
    @GetMapping("/location")
    public JsonResult<?> location(HttpServletRequest request) {

        String ip = IpUtils.getClientIp(request);
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
     * http://127.0.0.1:9003/visitor/api/v1/ip/ip/location?ip=202.106.212.226
     * 
     * @param request
     * @return
     */
    @Operation(summary = "Get Specific IP Location", description = "Get location information for a specific IP address")
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

    // http://127.0.0.1:9003/visitor/api/v1/ip/ip/range?ip=192.168.1.100
    @Operation(summary = "Check IP in Range", description = "Check if an IP address is within a specific range")
    @GetMapping("/ip/range")
    public JsonResult<?> ipInRange(@RequestParam String ip) {

        boolean isInRange = IpUtils.testIsIpInRange(ip);
        //
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ip", ip);
        jsonObject.put("isInRange", isInRange);

        return new JsonResult<>("ip isInRange", 200, jsonObject);
    }

    // for testing
    // http://127.0.0.1:9003/visitor/api/v1/ip/ip/province?ip=202.106.212.226
    @Operation(summary = "Get IP Province", description = "Get province information for a specific IP address")
    @GetMapping("/ip/province")
    public JsonResult<?> ipProvince(@RequestParam String ip) {

        // location: "国家|区域|省份|城市|ISP"
        // location: "中国|0|湖北省|武汉市|联通"
        // 0|0|0|内网IP|内网IP
        String location = ipService.getIpLocation(ip);
        //
        String[] locals = location.split("\\|");
        log.info("location {} locals {}", location, (Object[]) locals); // Cast to Object[] to confirm the non-varargs
                                                                        // invocation
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
     * http://127.0.0.1:9003/api/v1/ip/server
     * 
     * @return
     */
    // @GetMapping("/server")
    // public JsonResult<?> ipServer() {

    // String serverIp = IpUtils.getServerIp();
    // String serverHostname = IpUtils.hostname();
    // //
    // JSONObject jsonObject = new JSONObject();
    // jsonObject.put("ip", serverIp);
    // jsonObject.put("hostname", serverHostname);
    // //
    // return new JsonResult<>("server info", 200, jsonObject);
    // }

}
