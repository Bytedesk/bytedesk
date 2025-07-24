/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-24 22:20:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-24 22:47:42
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.server_metrics;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerMetricsSpecification extends BaseSpecification {
    
    public static Specification<ServerMetricsEntity> search(ServerMetricsRequest request) {
        log.info("request: {} userUid: {} pageNumber: {} pageSize: {}", 
            request, request.getUserUid(), request.getPageNumber(), request.getPageSize());
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 只查询未删除的记录
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
            
            // 服务器UID过滤
            if (StringUtils.hasText(request.getServerUidFilter())) {
                predicates.add(criteriaBuilder.equal(root.get("serverUid"), request.getServerUidFilter()));
            }
            
            // 时间范围过滤
            if (request.getStartTime() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("timestamp"), request.getStartTime()));
            }
            
            if (request.getEndTime() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("timestamp"), request.getEndTime()));
            }
            
            // 高资源使用率过滤
            if (Boolean.TRUE.equals(request.getHighUsageFilter())) {
                predicates.add(criteriaBuilder.or(
                    criteriaBuilder.greaterThanOrEqualTo(root.get("cpuUsage"), 80.0),
                    criteriaBuilder.greaterThanOrEqualTo(root.get("memoryUsage"), 80.0),
                    criteriaBuilder.greaterThanOrEqualTo(root.get("diskUsage"), 85.0)
                ));
            }
            
            // CPU使用率阈值过滤
            if (request.getCpuThreshold() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("cpuUsage"), request.getCpuThreshold()));
            }
            
            // 内存使用率阈值过滤
            if (request.getMemoryThreshold() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("memoryUsage"), request.getMemoryThreshold()));
            }
            
            // 磁盘使用率阈值过滤
            if (request.getDiskThreshold() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("diskUsage"), request.getDiskThreshold()));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
} 