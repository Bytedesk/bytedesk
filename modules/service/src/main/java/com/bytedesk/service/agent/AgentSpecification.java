/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-07 11:44:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-12 15:00:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;

import jakarta.persistence.criteria.Predicate;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
public class AgentSpecification extends BaseSpecification<AgentEntity, AgentRequest> {

    public static Specification<AgentEntity> search(AgentRequest request) {
        // log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.addAll(getBasicPredicates(root, criteriaBuilder, request.getOrgUid()));
            // nickname
            if (StringUtils.hasText(request.getNickname())) {
                predicates.add(criteriaBuilder.like(root.get("nickname"), "%" + request.getNickname() + "%"));
            }
            // mobile
            if (StringUtils.hasText(request.getMobile())) {
                predicates.add(criteriaBuilder.like(root.get("mobile"), "%" + request.getMobile() + "%"));
            }
            // email
            if (StringUtils.hasText(request.getEmail())) {
                predicates.add(criteriaBuilder.like(root.get("email"), "%" + request.getEmail() + "%"));
            }
            // searchText
            if (StringUtils.hasText(request.getSearchText())) {
                predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(root.get("nickname"), "%" + request.getSearchText() + "%"),
                    criteriaBuilder.like(root.get("mobile"), "%" + request.getSearchText() + "%"),
                    criteriaBuilder.like(root.get("email"), "%" + request.getSearchText() + "%")
                ));
            }
            //
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
