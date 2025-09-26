/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-02 14:20:34
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-06 10:49:15
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.message_leave;

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
public class MessageLeaveSpecification extends BaseSpecification<MessageLeaveEntity, MessageLeaveRequest> {

    public static Specification<MessageLeaveEntity> search(MessageLeaveRequest request, AuthService authService) {
        // log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.addAll(getBasicPredicates(root, criteriaBuilder, request, authService));
            // nickname
            if (StringUtils.hasText(request.getNickname())) {
                predicates.add(criteriaBuilder.like(root.get("nickname"), "%" + request.getNickname() + "%"));
            }
            if (StringUtils.hasText(request.getContact())) {
                predicates.add(criteriaBuilder.like(root.get("contact"), "%" + request.getContact() + "%"));
            }
            if (StringUtils.hasText(request.getContent())) {
                predicates.add(criteriaBuilder.like(root.get("content"), "%" + request.getContent() + "%"));
            }
            if (StringUtils.hasText(request.getStatus())) {
                predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatus()));
            }
            if (StringUtils.hasText(request.getType())) {
                predicates.add(criteriaBuilder.equal(root.get("type"), request.getType()));
            }
            // user
            if (StringUtils.hasText(request.getUid())) {
                predicates.add(criteriaBuilder.like(root.get("user"), "%" + request.getUid() + "%"));
            }
            // searchText
            if (StringUtils.hasText(request.getSearchText())) {
                String searchText = "%" + request.getSearchText() + "%";
                Predicate contactPredicate = criteriaBuilder.like(root.get("contact"), searchText);
                Predicate contentPredicate = criteriaBuilder.like(root.get("content"), searchText);
                predicates.add(criteriaBuilder.or(contactPredicate, contentPredicate));
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
