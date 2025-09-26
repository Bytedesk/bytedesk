/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-22 15:52:39
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-01 23:34:24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor_token;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import com.bytedesk.core.rbac.auth.AuthService;

import jakarta.persistence.criteria.Predicate;

public class VisitorTokenSpecification extends BaseSpecification<VisitorTokenEntity, VisitorTokenRequest> {

    public static Specification<VisitorTokenEntity> search(VisitorTokenRequest request, AuthService authService) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.addAll(getBasicPredicates(root, criteriaBuilder, request, authService));
            // name
            if (StringUtils.hasText(request.getName())) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + request.getName() + "%"));
            }
            // description
            if (StringUtils.hasText(request.getDescription())) {
                predicates.add(criteriaBuilder.like(root.get("description"), "%" + request.getDescription() + "%"));
            }
            // type
            if (StringUtils.hasText(request.getType())) {
                predicates.add(criteriaBuilder.equal(root.get("type"), request.getType()));
            }
            // userUid
            if (StringUtils.hasText(request.getUserUid())) {
                predicates.add(criteriaBuilder.equal(root.get("userUid"), request.getUserUid()));
            }
            // Add more predicates based on request fields
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
    
}
