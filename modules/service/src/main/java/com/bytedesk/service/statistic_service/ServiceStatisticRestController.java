/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-23 14:20:33
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.statistic_service;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.rbac.role.RolePermissions;
import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/ServiceStatistic")
@AllArgsConstructor
public class ServiceStatisticRestController extends BaseRestController<ServiceStatisticRequest> {

    private final ServiceStatisticRestService ServiceStatisticService;

    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(ServiceStatisticRequest request) {
        
        Page<ServiceStatisticResponse> ServiceStatistics = ServiceStatisticService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(ServiceStatistics));
    }

    @Override
    public ResponseEntity<?> queryByUser(ServiceStatisticRequest request) {
        
        Page<ServiceStatisticResponse> ServiceStatistics = ServiceStatisticService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(ServiceStatistics));
    }

    @Override
    public ResponseEntity<?> create(ServiceStatisticRequest request) {
        
        ServiceStatisticResponse ServiceStatistic = ServiceStatisticService.create(request);

        return ResponseEntity.ok(JsonResult.success(ServiceStatistic));
    }

    @Override
    public ResponseEntity<?> update(ServiceStatisticRequest request) {
        
        ServiceStatisticResponse ServiceStatistic = ServiceStatisticService.update(request);

        return ResponseEntity.ok(JsonResult.success(ServiceStatistic));
    }

    @Override
    public ResponseEntity<?> delete(ServiceStatisticRequest request) {
        
        ServiceStatisticService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }
    
}