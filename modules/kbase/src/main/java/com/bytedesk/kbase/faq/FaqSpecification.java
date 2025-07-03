/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-08 12:30:14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-12 13:04:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.faq;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FaqSpecification extends BaseSpecification {

    public static Specification<FaqEntity> search(FaqRequest request) {
        // log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.addAll(getBasicPredicates(root, criteriaBuilder, request.getOrgUid()));
            // type
            if (StringUtils.hasText(request.getType())) {
                predicates.add(criteriaBuilder.equal(root.get("type"), request.getType()));
            }
            //
            if (StringUtils.hasText(request.getQuestion())) {
                predicates.add(criteriaBuilder.like(root.get("question"), "%" + request.getQuestion() + "%"));
            }
            if (StringUtils.hasText(request.getAnswer())) {
                predicates.add(criteriaBuilder.like(root.get("answer"), "%" + request.getAnswer() + "%"));
            }
            if (StringUtils.hasText(request.getCategoryUid())) {
                predicates.add(criteriaBuilder.equal(root.get("categoryUid"), request.getCategoryUid()));
            }
            if (StringUtils.hasText(request.getKbUid())) {
                // 修改为通过kbaseEntity关联对象的uid进行查询，而不是直接查询kbUid字段
                predicates.add(criteriaBuilder.equal(root.get("kbase").get("uid"), request.getKbUid()));
            }
            // onlyLoadValid
            if (request.getOnlyLoadValid() != null && request.getOnlyLoadValid()) {
                predicates.add(criteriaBuilder.equal(root.get("enabled"), true));
                // 当前时间 > startDate && 当前时间 < endDate
                ZonedDateTime now = ZonedDateTime.now();
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), now));
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("endDate"), now));
            }
            // searchText
            if (StringUtils.hasText(request.getSearchText())) {
                String searchText = "%" + request.getSearchText() + "%";
                Predicate titlePredicate = criteriaBuilder.like(root.get("question"), searchText);
                Predicate contentPredicate = criteriaBuilder.like(root.get("answer"), searchText);
                // Predicate shortCutPredicate = criteriaBuilder.like(root.get("shortCut"), searchText);
                predicates.add(criteriaBuilder.or(titlePredicate, contentPredicate));
            }
            //
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
