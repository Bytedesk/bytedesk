/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-09 22:19:21
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-24 21:26:28
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.server;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerSpecification extends BaseSpecification {
    
    public static Specification<ServerEntity> search(ServerRequest request) {
        log.info("request: {} userUid: {} pageNumber: {} pageSize: {}", 
            request, request.getUserUid(), request.getPageNumber(), request.getPageSize());
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 只查询未删除的记录
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
            
            // 服务器名称搜索
            if (StringUtils.hasText(request.getServerName())) {
                predicates.add(criteriaBuilder.like(root.get("serverName"), "%" + request.getServerName() + "%"));
            }
            
            // 服务器类型过滤
            if (StringUtils.hasText(request.getServerTypeFilter())) {
                predicates.add(criteriaBuilder.equal(root.get("type"), request.getServerTypeFilter()));
            }
            
            // 服务器状态过滤
            if (StringUtils.hasText(request.getServerStatusFilter())) {
                predicates.add(criteriaBuilder.equal(root.get("status"), request.getServerStatusFilter()));
            }
            
            // 环境过滤
            if (StringUtils.hasText(request.getEnvironmentFilter())) {
                predicates.add(criteriaBuilder.equal(root.get("environment"), request.getEnvironmentFilter()));
            }
            
            // 位置过滤
            if (StringUtils.hasText(request.getLocationFilter())) {
                predicates.add(criteriaBuilder.like(root.get("location"), "%" + request.getLocationFilter() + "%"));
            }
            
            // 高资源使用率过滤
            if (Boolean.TRUE.equals(request.getHighUsageFilter())) {
                predicates.add(criteriaBuilder.or(
                    criteriaBuilder.greaterThanOrEqualTo(root.get("cpuUsage"), 80.0),
                    criteriaBuilder.greaterThanOrEqualTo(root.get("memoryUsage"), 80.0),
                    criteriaBuilder.greaterThanOrEqualTo(root.get("diskUsage"), 85.0)
                ));
            }
            
            // 离线服务器过滤
            if (Boolean.TRUE.equals(request.getOfflineFilter())) {
                predicates.add(criteriaBuilder.equal(root.get("status"), "OFFLINE"));
            }
            
            // 心跳超时过滤
            if (request.getHeartbeatThresholdMinutes() != null && request.getHeartbeatThresholdMinutes() > 0) {
                // 这里需要在应用层处理，因为需要计算时间差
                // 暂时不在这里实现，可以在Service层处理
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
