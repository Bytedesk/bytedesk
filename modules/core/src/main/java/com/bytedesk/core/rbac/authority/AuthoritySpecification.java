/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-04 16:14:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-01 23:33:52
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.authority;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.bytedesk.core.base.BaseSpecification;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthoritySpecification extends BaseSpecification<AuthorityEntity, AuthorityRequest> {
    
    public static Specification<AuthorityEntity> search(AuthorityRequest request) {
        log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            // predicates.addAll(getBasicPredicates(root, criteriaBuilder, request.getOrgUid()));
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
            // 
            if (request.getLevel()!= null) {
                predicates.add(criteriaBuilder.equal(root.get("level"), request.getLevel()));
            }
            // if (StringUtils.hasText(request.getOrgUid())) {
            //     predicates.add(criteriaBuilder.equal(root.get("orgUid"), request.getOrgUid()));
            // }
            //
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
