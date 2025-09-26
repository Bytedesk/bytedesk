/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-09 22:19:21
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-15 15:18:45
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.voc.feedback;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import com.bytedesk.core.rbac.auth.AuthService;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FeedbackSpecification extends BaseSpecification<FeedbackEntity, FeedbackRequest> {
    
    public static Specification<FeedbackEntity> search(FeedbackRequest request, AuthService authService) {
        // log.info("request: {} orgUid: {} pageNumber: {} pageSize: {}", 
        //     request, request.getOrgUid(), request.getPageNumber(), request.getPageSize());
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.addAll(getBasicPredicates(root, criteriaBuilder, request, authService));
            // name
            if (StringUtils.hasText(request.getTitle())) {
                predicates.add(criteriaBuilder.like(root.get("title"), "%" + request.getTitle() + "%"));
            }
            // type
            if (StringUtils.hasText(request.getType())) {
                predicates.add(criteriaBuilder.equal(root.get("type"), request.getType()));
            }
            // 
            if (StringUtils.hasText(request.getUserUid())) {
                predicates.add(criteriaBuilder.equal(root.get("userUid"), request.getUserUid()));
            }
            //
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
