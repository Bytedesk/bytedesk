/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-17 17:08:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-21 17:35:49
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.statistic;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/service/statistic")
@RequiredArgsConstructor
public class ServiceStatisticRestController extends BaseRestController<ServiceStatisticRequest> {

    private final ServiceStatisticRestService serviceStatisticRestService;

    private final ServiceStatisticService serviceStatisticService;

    @Override
    public ResponseEntity<JsonResult<?>> queryByOrg(ServiceStatisticRequest request) {
        
        Page<ServiceStatisticResponse> page = serviceStatisticRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUser(ServiceStatisticRequest request) {

        Page<ServiceStatisticResponse> page = serviceStatisticRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> create(ServiceStatisticRequest request) {
        
        ServiceStatisticResponse response = serviceStatisticRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    public ResponseEntity<?> update(ServiceStatisticRequest request) {
        
        ServiceStatisticResponse response = serviceStatisticRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    public ResponseEntity<?> delete(ServiceStatisticRequest request) {
        
        serviceStatisticRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    // 查询某时间段统计
    @GetMapping("/query/date")
    public ResponseEntity<?> queryByDate(ServiceStatisticRequest request) {

        ServiceStatisticResponse response = serviceStatisticService.queryByDate(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    // 计算今日统计
    @PostMapping("/calculate")
    public ResponseEntity<?> calculateTodayStatistics() {

        serviceStatisticService.calculateTodayStatistics();

        return ResponseEntity.ok(JsonResult.success());
    }
    
    
}
