/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-09 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 17:31:46
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.gateway;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

/**
 * Call网关REST控制器
 */
@RestController
@RequestMapping("/api/v1/call/gateway")
@AllArgsConstructor
public class CallGatewayRestController extends BaseRestController<CallGatewayRequest, CallGatewayRestService> {

    private final CallGatewayRestService gatewayRestService;

    /**
     * 查询网关列表
     */
    @GetMapping("/query/org")
    @ActionAnnotation(title = "网关", action = "查询", description = "查询网关列表")
    @Override
    public ResponseEntity<?> queryByOrg(CallGatewayRequest request) {
        
        Page<CallGatewayResponse> gatewayPage = gatewayRestService.queryByOrg(request);
        
        return ResponseEntity.ok(JsonResult.success(gatewayPage));
    }

    /**
     * 查询网关列表
     */
    @GetMapping("/query/user")
    @ActionAnnotation(title = "网关", action = "查询", description = "查询用户网关列表")
    @Override
    public ResponseEntity<?> queryByUser(CallGatewayRequest request) {
        
        Page<CallGatewayResponse> gatewayPage = gatewayRestService.queryByUser(request);
        
        return ResponseEntity.ok(JsonResult.success(gatewayPage));
    }

    /**
     * 根据uid查询网关详情
     */
    @GetMapping("/query/uid")
    @ActionAnnotation(title = "网关", action = "详情", description = "查询网关详情")
    @Override
    public ResponseEntity<?> queryByUid(CallGatewayRequest request) {
        
        CallGatewayResponse gateway = gatewayRestService.queryByUid(request);
        
        return ResponseEntity.ok(JsonResult.success(gateway));
    }

    /**
     * 查询网关详情（兼容旧接口）
     */
    @GetMapping("/query")
    @ActionAnnotation(title = "网关", action = "详情", description = "查询网关详情")
    public ResponseEntity<JsonResult<CallGatewayResponse>> query(CallGatewayRequest request) {
        
        CallGatewayResponse gateway = gatewayRestService.queryByUid(request);
        
        return ResponseEntity.ok(JsonResult.success(gateway));
    }

    /**
     * 创建网关
     */
    @PostMapping("/create")
    @ActionAnnotation(title = "网关", action = "创建", description = "创建网关")
    @Override
    public ResponseEntity<?> create(@RequestBody CallGatewayRequest request) {
        
        CallGatewayResponse gateway = gatewayRestService.create(request);
        
        return ResponseEntity.ok(JsonResult.success(gateway));
    }

    /**
     * 更新网关
     */
    @PostMapping("/update")
    @ActionAnnotation(title = "网关", action = "更新", description = "更新网关")
    @Override
    public ResponseEntity<?> update(@RequestBody CallGatewayRequest request) {
        
        CallGatewayResponse gateway = gatewayRestService.update(request);
        
        return ResponseEntity.ok(JsonResult.success(gateway));
    }

    /**
     * 删除网关
     */
    @PostMapping("/delete")
    @ActionAnnotation(title = "网关", action = "删除", description = "删除网关")
    @Override
    public ResponseEntity<?> delete(@RequestBody CallGatewayRequest request) {
        
        gatewayRestService.deleteByUid(request.getUid());
        
        return ResponseEntity.ok(JsonResult.success());
    }

    /**
     * 导出网关列表
     */
    @GetMapping("/export")
    @ActionAnnotation(title = "网关", action = "导出", description = "导出网关列表")
    @Override
    public Object export(CallGatewayRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            gatewayRestService,
            CallGatewayExcel.class,
            "Call网关",
            "freeswitch_gateway"
        );
    }
}
