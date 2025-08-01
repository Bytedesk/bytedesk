/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-27 12:21:44
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-01 23:29:38
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.black;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;

import jakarta.persistence.criteria.Predicate;

public class BlackSpecification extends BaseSpecification<BlackEntity, BlackRequest> {

     public static Specification<BlackEntity> search(BlackRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            // predicates.addAll(getBasicPredicates(root, criteriaBuilder, request.getOrgUid()));
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
            // 方便超级管理员super查询
            if (StringUtils.hasText(request.getOrgUid())) {
                predicates.add(criteriaBuilder.equal(root.get("orgUid"), request.getOrgUid()));
            }
            // blackNickname
            if (StringUtils.hasText(request.getBlackNickname())) {
                predicates.add(criteriaBuilder.like(root.get("blackNickname"), "%" + request.getBlackNickname() + "%"));
            }
            // reason
            if (StringUtils.hasText(request.getReason())) {
                predicates.add(criteriaBuilder.like(root.get("reason"), "%" + request.getReason() + "%"));
            }
            // userNickname
            if (StringUtils.hasText(request.getUserNickname())) {
                predicates.add(criteriaBuilder.like(root.get("userNickname"), "%" + request.getUserNickname() + "%"));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
