/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-20 17:04:33
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-05 15:37:48
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TicketSpecification extends BaseSpecification {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Specification<TicketEntity> search(TicketRequest request) {
        log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.addAll(getBasicPredicates(root, criteriaBuilder, request.getOrgUid()));
            
            if (StringUtils.hasText(request.getTitle())) {
                predicates.add(criteriaBuilder.like(root.get("title"), "%" + request.getTitle() + "%"));
            }
            if (StringUtils.hasText(request.getDescription())) {
                predicates.add(criteriaBuilder.like(root.get("description"), "%" + request.getDescription() + "%"));
            }
            if (StringUtils.hasText(request.getSearchText())) {
                predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(root.get("title"), "%" + request.getSearchText() + "%"),
                    criteriaBuilder.like(root.get("description"), "%" + request.getSearchText() + "%")
                ));
            }
            if (StringUtils.hasText(request.getStatus())) {
                predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatus()));
            }
            if (StringUtils.hasText(request.getPriority())) {
                predicates.add(criteriaBuilder.equal(root.get("priority"), request.getPriority()));
            }
            if (StringUtils.hasText(request.getCategoryUid())) {
                predicates.add(criteriaBuilder.equal(root.get("category").get("uid"), request.getCategoryUid()));
            }
            if (StringUtils.hasText(request.getThreadTopic())) {
                predicates.add(criteriaBuilder.equal(root.get("thread").get("topic"), request.getThreadTopic()));
            }
            if (StringUtils.hasText(request.getWorkgroupUid())) {
                predicates.add(criteriaBuilder.equal(root.get("workgroup").get("uid"), request.getWorkgroupUid()));
            }
            if (StringUtils.hasText(request.getAssigneeUid())) {
                predicates.add(criteriaBuilder.equal(root.get("assignee").get("uid"), request.getAssigneeUid()));
            }
            if (StringUtils.hasText(request.getReporterUid())) {
                predicates.add(criteriaBuilder.equal(root.get("reporter").get("uid"), request.getReporterUid()));
            }
            
            // 处理日期范围查询
            if (StringUtils.hasText(request.getStartDate())) {
                LocalDateTime startDate = LocalDateTime.parse(request.getStartDate(), formatter);
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDate));
            }
            if (StringUtils.hasText(request.getEndDate())) {
                LocalDateTime endDate = LocalDateTime.parse(request.getEndDate(), formatter);
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
