/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-25 17:08:19
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-26 18:22:40
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LlmModelSpecification extends BaseSpecification {

    public static Specification<LlmModelEntity> search(LlmModelRequest request) {
        log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            // predicates.addAll(getBasicPredicates(root, criteriaBuilder, request.getOrgUid()));
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
            // 
            if (StringUtils.hasText(request.getOrgUid())) {
                predicates.add(criteriaBuilder.equal(root.get("orgUid"), request.getOrgUid()));
            }
            // 
            if (StringUtils.hasText(request.getLevel())) {
                predicates.add(criteriaBuilder.equal(root.get("level"), request.getLevel()));
            }
            // 
            if (StringUtils.hasText(request.getProviderUid())) {
                predicates.add(criteriaBuilder.equal(root.get("providerUid"), request.getProviderUid()));
            }
            // providerName
            if (StringUtils.hasText(request.getProviderName())) {
                predicates.add(criteriaBuilder.equal(root.get("providerName"), request.getProviderName()));
            }
            //
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
