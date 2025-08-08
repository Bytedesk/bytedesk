/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-06 10:05:59
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-10 21:01:04
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.auto_reply.keyword;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AutoReplyKeywordSpecification extends BaseSpecification<AutoReplyKeywordEntity, AutoReplyKeywordRequest> {
    
    public static Specification<AutoReplyKeywordEntity> search(AutoReplyKeywordRequest request) {
        // log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.addAll(getBasicPredicates(root, criteriaBuilder, request.getOrgUid()));
            // 
            List<String> keywords = request.getKeywordList();
            for (String keyword : keywords) {
                if (StringUtils.hasText(keyword)) {
                    predicates.add(criteriaBuilder.like(root.get("keywordList").as(String.class), "%" + keyword + "%"));
                }
            }
            // replyList
            List<String> replies = request.getReplyList();
            for (String reply : replies) {
                if (StringUtils.hasText(reply)) {
                    predicates.add(criteriaBuilder.like(root.get("replyList").as(String.class), "%" + reply + "%"));
                }
            }
            // matchType
            if (StringUtils.hasText(request.getMatchType())) {
                predicates.add(criteriaBuilder.equal(root.get("matchType"), request.getMatchType()));
            }
            // contentType
            if (StringUtils.hasText(request.getContentType())) {
                predicates.add(criteriaBuilder.equal(root.get("contentType"), request.getContentType()));
            }
            // 
            if (StringUtils.hasText(request.getCategoryUid())) {
                predicates.add(criteriaBuilder.equal(root.get("categoryUid"), request.getCategoryUid()));
            }
            if (StringUtils.hasText(request.getKbUid())) {
                predicates.add(criteriaBuilder.equal(root.get("kbUid"), request.getKbUid()));
            }
            //
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
