/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-06 07:21:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-18 16:18:42
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue_member;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QueueMemberSpecification extends BaseSpecification {
    
    public static Specification <QueueMemberEntity> search(QueueMemberRequest request) {
        log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.addAll(getBasicPredicates(root, criteriaBuilder, request.getOrgUid()));
            // 根据visitorNickname查询
            if (StringUtils.hasText(request.getVisitorNickname())) {
                predicates.add(criteriaBuilder.like(root.get("visitorNickname"), "%" + request.getVisitorNickname() + "%"));
            }
            // 根据agentNickname查询
            if (StringUtils.hasText(request.getAgentNickname())) {
                predicates.add(criteriaBuilder.like(root.get("agentNickname"), "%" + request.getAgentNickname() + "%"));
            }
            // 根据client查询
            if (StringUtils.hasText(request.getClient())) {
                predicates.add(criteriaBuilder.like(root.get("client"), "%" + request.getClient() + "%"));
            }
            //
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    // public static Specification<QueueMemberEntity> hasUserUid(String uid, String dbType) {
    //     return (Root<QueueMemberEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
    //         String jsonExtractFunction;
    //         switch (dbType.toLowerCase()) {
    //             case "mysql":
    //                 jsonExtractFunction = "JSON_EXTRACT";
    //                 break;
    //             case "postgresql":
    //                 jsonExtractFunction = "JSONB_EXTRACT_PATH_TEXT";
    //                 break;
    //             case "oracle":
    //                 jsonExtractFunction = "JSON_VALUE";
    //                 break;
    //             default:
    //                 throw new IllegalArgumentException("Unsupported database type: " + dbType);
    //         }

    //         String jsonPath = "$.uid";
    //         String jsonQuery = String.format("%s(v.user, '%s')", jsonExtractFunction, jsonPath);

    //         return criteriaBuilder.equal(criteriaBuilder.function(jsonQuery, String.class), uid);
    //     };
    // }
}
