/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-08 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-08 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.users;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import com.bytedesk.core.rbac.auth.AuthService;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CallUserSpecification extends BaseSpecification<CallUserEntity, CallUserRequest> {
    
    public static Specification<CallUserEntity> search(CallUserRequest request, AuthService authService) {
        log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.addAll(getBasicPredicates(root, criteriaBuilder, request, authService));
            
            // username
            if (StringUtils.hasText(request.getUsername())) {
                predicates.add(criteriaBuilder.like(root.get("username"), "%" + request.getUsername() + "%"));
            }
            
            // domain
            if (StringUtils.hasText(request.getDomain())) {
                predicates.add(criteriaBuilder.equal(root.get("domain"), request.getDomain()));
            }
            
            // displayName
            if (StringUtils.hasText(request.getDisplayName())) {
                predicates.add(criteriaBuilder.like(root.get("displayName"), "%" + request.getDisplayName() + "%"));
            }
            
            // email
            if (StringUtils.hasText(request.getEmail())) {
                predicates.add(criteriaBuilder.like(root.get("email"), "%" + request.getEmail() + "%"));
            }
            
            // accountcode
            if (StringUtils.hasText(request.getAccountcode())) {
                predicates.add(criteriaBuilder.equal(root.get("accountcode"), request.getAccountcode()));
            }
            
            // enabled
            if (request.getEnabled() != null) {
                predicates.add(criteriaBuilder.equal(root.get("enabled"), request.getEnabled()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
