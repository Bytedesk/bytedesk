/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-09 21:41:31
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-01 23:33:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.push;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import com.bytedesk.core.rbac.auth.AuthService;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PushSpecification extends BaseSpecification<PushEntity, PushRequest> {
    
    public static Specification<PushEntity> search(PushRequest request, AuthService authService) {
        log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.addAll(getBasicPredicates(root, criteriaBuilder, request, authService));
            // 
            // searchText
            String searchText = request.getSearchText();
            if (StringUtils.hasText(searchText)) {
                Predicate p1 = criteriaBuilder.like(root.get("sender"), "%" + searchText + "%");
                Predicate p2 = criteriaBuilder.like(root.get("content"), "%" + searchText + "%");
                Predicate p3 = criteriaBuilder.like(root.get("receiver"), "%" + searchText + "%");
                predicates.add(criteriaBuilder.or(p1, p2, p3));
            }
            // type
            if (StringUtils.hasText(request.getType())) {
                predicates.add(criteriaBuilder.equal(root.get("type"), request.getType()));
            }
            // status
            if (StringUtils.hasText(request.getStatus())) {
                predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatus()));
            }
            //
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
