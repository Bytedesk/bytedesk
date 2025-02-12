/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-05 22:46:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-12 18:35:04
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.thread;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadSpecification extends BaseSpecification {

    public static Specification<ThreadEntity> search(ThreadRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.addAll(getBasicPredicates(root, criteriaBuilder, request.getOrgUid()));
            // 
            if (StringUtils.hasText(request.getUid())) {
                predicates.add(criteriaBuilder.like(root.get("uid"), "%" + request.getUid() + "%"));
            }
            if (StringUtils.hasText(request.getTopic())) {
                predicates.add(criteriaBuilder.like(root.get("topic"), "%" + request.getTopic() + "%"));
            }
            if (StringUtils.hasText(request.getType())) {
                predicates.add(criteriaBuilder.equal(root.get("type"), request.getType()));
            }
            if (StringUtils.hasText(request.getOwnerUid())) {
                // 个人查询时，不显示隐藏的会话
                predicates.add(criteriaBuilder.equal(root.get("hide"), false));
                predicates.add(criteriaBuilder.equal(root.get("owner").get("uid"), request.getOwnerUid()));
            }
            if (StringUtils.hasText(request.getOwnerNickname())) {
                predicates.add(criteriaBuilder.like(root.get("owner").get("nickname"),
                        "%" + request.getOwnerNickname() + "%"));
            }
            if (StringUtils.hasText(request.getClient())) {
                predicates.add(criteriaBuilder.equal(root.get("client"), request.getClient()));
            }
            if (StringUtils.hasText(request.getSearchText())) {
                List<Predicate> orPredicates = new ArrayList<>();
                // content
                orPredicates.add(criteriaBuilder.like(root.get("content"), "%" + request.getSearchText() + "%"));
                // user json字符串 中的 nickname
                orPredicates.add(criteriaBuilder.like(root.get("user"), "%" + request.getSearchText() + "%"));
                // 组合or条件
                predicates.add(criteriaBuilder.or(orPredicates.toArray(new Predicate[0])));
            }
            //
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
