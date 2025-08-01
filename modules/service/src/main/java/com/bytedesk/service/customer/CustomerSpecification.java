/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-07 11:45:30
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-25 08:32:29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.customer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomerSpecification extends BaseSpecification<CustomerEntity, CustomerRequest> {

    public static Specification<CustomerEntity> search(CustomerRequest request) {
        log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.addAll(getBasicPredicates(root, criteriaBuilder, request.getOrgUid()));
            // nickname
            if (StringUtils.hasText(request.getNickname())) {
                predicates.add(criteriaBuilder.like(root.get("nickname"), "%" + request.getNickname() + "%"));
            }
            // email
            if (StringUtils.hasText(request.getEmail())) {
                predicates.add(criteriaBuilder.like(root.get("email"), "%" + request.getEmail() + "%"));
            }
            // mobile
            if (StringUtils.hasText(request.getMobile())) {
                predicates.add(criteriaBuilder.like(root.get("mobile"), "%" + request.getMobile() + "%"));
            }
            // tagList
            // if (request.getTagList() != null && !request.getTagList().isEmpty()) {
            //     predicates.add(root.get("tagList").in(request.getTagList()));
            // }
            // extra
            // if (StringUtils.hasText(request.getExtra())) {
            //     predicates.add(criteriaBuilder.like(root.get("extra"), "%" + request.getExtra() + "%"));
            // }
            // notes
            if (StringUtils.hasText(request.getNotes())) {
                predicates.add(criteriaBuilder.like(root.get("notes"), "%" + request.getNotes() + "%"));
            }
            // visitorUid
            if (StringUtils.hasText(request.getVisitorUid())) {
                predicates.add(criteriaBuilder.equal(root.get("visitorUid"), request.getVisitorUid()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
