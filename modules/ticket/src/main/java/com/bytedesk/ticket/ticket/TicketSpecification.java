/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-08 12:30:14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-06 10:44:43
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.utils.BdDateUtils;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TicketSpecification extends BaseSpecification<TicketEntity, TicketRequest> {

    public static Specification<TicketEntity> search(TicketRequest request, AuthService authService) {
        log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            // predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
            predicates.addAll(getBasicPredicates(root, criteriaBuilder, request, authService));
            // 使用基类方法处理超级管理员权限和组织过滤
            // addOrgFilterIfNotSuperUser(root, criteriaBuilder, predicates, request, authService);
            
            if (StringUtils.hasText(request.getSummary())) {
                predicates.add(criteriaBuilder.like(root.get("summary"), "%" + request.getSummary() + "%"));
            }
            // description
            if (StringUtils.hasText(request.getDescription())) {
                predicates.add(criteriaBuilder.like(root.get("description"), "%" + request.getDescription() + "%"));
            }
            // topic
            if (StringUtils.hasText(request.getTopic())) {
                predicates.add(criteriaBuilder.like(root.get("topic"), "%" + request.getTopic() + "%"));
            }
            // ticket number
            if (StringUtils.hasText(request.getTicketNumber())) {
                predicates.add(criteriaBuilder.equal(root.get("ticketNumber"), request.getTicketNumber()));
            }
            // status
            if (StringUtils.hasText(request.getStatus())) {
                predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatus()));
            }
            // priority
            if (StringUtils.hasText(request.getPriority())) {
                predicates.add(criteriaBuilder.equal(root.get("priority"), request.getPriority()));
            }
            // threadUid
            if (StringUtils.hasText(request.getThreadUid())) {
                predicates.add(criteriaBuilder.equal(root.get("threadUid"), request.getThreadUid()));
            }
            // categoryUid
            if (StringUtils.hasText(request.getCategoryUid())) {
                predicates.add(criteriaBuilder.equal(root.get("categoryUid"), request.getCategoryUid()));
            }
            // workgroupUid
            if (StringUtils.hasText(request.getWorkgroupUid())) {
                predicates.add(criteriaBuilder.equal(root.get("workgroupUid"), request.getWorkgroupUid()));
            }
            // departmentUid
            if (StringUtils.hasText(request.getDepartmentUid())) {
                predicates.add(criteriaBuilder.equal(root.get("departmentUid"), request.getDepartmentUid()));
            }
            //
            if (StringUtils.hasText(request.getUserUid())) {
                predicates.add(criteriaBuilder.equal(root.get("userUid"), request.getUserUid()));
            }
            // 时间范围过滤 - 使用BdDateUtils进行时间解析和转换
            if (StringUtils.hasText(request.getCreatedAtStart())) {
                try {
                    java.time.ZonedDateTime startDateTime = BdDateUtils.parseStartDate(request.getCreatedAtStart());
                    if (startDateTime != null) {
                        predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDateTime));
                    }
                } catch (Exception e) {
                    log.warn("Invalid createdAtStart format: {}", request.getCreatedAtStart());
                }
            }
            if (StringUtils.hasText(request.getCreatedAtEnd())) {
                try {
                    java.time.ZonedDateTime endDateTime = BdDateUtils.parseEndDate(request.getCreatedAtEnd());
                    if (endDateTime != null) {
                        predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDateTime));
                    }
                } catch (Exception e) {
                    log.warn("Invalid createdAtEnd format: {}", request.getCreatedAtEnd());
                }
            }
            // 
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
