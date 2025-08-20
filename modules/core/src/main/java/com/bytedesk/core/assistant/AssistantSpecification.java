/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-20 16:23:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 16:41:55
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.assistant;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.bytedesk.core.base.BaseSpecification;
import com.bytedesk.core.rbac.auth.AuthService;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class AssistantSpecification extends BaseSpecification<AssistantEntity, AssistantRequest> {
    
    public static Specification<AssistantEntity> search(AssistantRequest request, AuthService authService) {
        log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
