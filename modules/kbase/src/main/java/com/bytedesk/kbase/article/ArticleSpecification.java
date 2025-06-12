/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-08 12:30:14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-12 13:04:20
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.article;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ArticleSpecification extends BaseSpecification {

    public static Specification<ArticleEntity> search(ArticleRequest request) {
        log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            // predicates.addAll(getBasicPredicates(root, criteriaBuilder, request.getOrgUid()));
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
            if (StringUtils.hasText(request.getOrgUid())) {
                predicates.add(criteriaBuilder.equal(root.get("orgUid"), request.getOrgUid()));
            }
            if (StringUtils.hasText(request.getTitle())) {
                predicates.add(criteriaBuilder.like(root.get("title"), "%" + request.getTitle() + "%"));
            }
            // summary
            if (StringUtils.hasText(request.getSummary())) {
                predicates.add(criteriaBuilder.like(root.get("summary"), "%" + request.getSummary() + "%"));
            }
            if (StringUtils.hasText(request.getContent())) {
                predicates.add(criteriaBuilder.like(root.get("contentHtml"), "%" + request.getContent() + "%"));
            }
            if (StringUtils.hasText(request.getContent())) {
                predicates.add(criteriaBuilder.like(root.get("contentMarkdown"), "%" + request.getContent() + "%"));
            }
            if (StringUtils.hasText(request.getCategoryUid())) {
                predicates.add(criteriaBuilder.equal(root.get("categoryUid"), request.getCategoryUid()));
            }
            if (StringUtils.hasText(request.getKbUid())) {
                predicates.add(criteriaBuilder.equal(root.get("kbase").get("uid"), request.getKbUid()));
            }
            if (StringUtils.hasText(request.getUserUid())) {
                predicates.add(criteriaBuilder.equal(root.get("userUid"), request.getUserUid()));
            }
            // searchText
            if (StringUtils.hasText(request.getSearchText())) {
                String searchText = "%" + request.getSearchText() + "%";
                Predicate titlePredicate = criteriaBuilder.like(root.get("title"), searchText);
                Predicate contentHtmlPredicate = criteriaBuilder.like(root.get("contentHtml"), searchText);
                Predicate contentMarkdownPredicate = criteriaBuilder.like(root.get("contentMarkdown"), searchText);
                // Predicate shortCutPredicate = criteriaBuilder.like(root.get("shortCut"), searchText);
                predicates.add(criteriaBuilder.or(titlePredicate, contentHtmlPredicate, contentMarkdownPredicate));
            }
            //
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
