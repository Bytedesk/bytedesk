/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-08 12:30:14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-12 11:46:46
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.quick_reply;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.enums.LevelEnum;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QuickReplySpecification extends BaseSpecification {

    public static Specification<QuickReplyEntity> search(QuickReplyRequest request) {
        log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            // predicates.addAll(getBasicPredicates(root, criteriaBuilder, request.getOrgUid()));
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
            //
            // type
            if (StringUtils.hasText(request.getType())) {
                predicates.add(criteriaBuilder.equal(root.get("type"), request.getType()));
            }
            if (StringUtils.hasText(request.getTitle())) {
                predicates.add(criteriaBuilder.like(root.get("title"), "%" + request.getTitle() + "%"));
            }
            if (StringUtils.hasText(request.getContent())) {
                predicates.add(criteriaBuilder.like(root.get("content"), "%" + request.getContent() + "%"));
            }
            if (StringUtils.hasText(request.getCategoryUid())) {
                predicates.add(criteriaBuilder.equal(root.get("categoryUid"), request.getCategoryUid()));
            }
            if (StringUtils.hasText(request.getKbUid())) {
                predicates.add(criteriaBuilder.equal(root.get("kbUid"), request.getKbUid()));
            }
            if (TypeConsts.COMPONENT_TYPE_SERVICE.equals(request.getComponentType())) {
                // 包含下面两种情况的所有结果：
                // 如果 agentUid 不为空，则必须满足 agentUid 等于 request.getAgentUid()，而且level === Level agent
                // 如果 orgUid 不为空，则必须满足 orgUid === request.getOrgUid()，而且 level === Level organization
                if (StringUtils.hasText(request.getAgentUid())) {
                    predicates.add(criteriaBuilder.equal(root.get("agentUid"), request.getAgentUid()));
                    predicates.add(criteriaBuilder.equal(root.get("level"), LevelEnum.AGENT.name()));
                }
                if (StringUtils.hasText(request.getOrgUid())) {
                    predicates.add(criteriaBuilder.equal(root.get("orgUid"), request.getOrgUid()));
                    predicates.add(criteriaBuilder.equal(root.get("level"), LevelEnum.ORGANIZATION.name()));
                }
            } else {
                predicates.add(criteriaBuilder.equal(root.get("orgUid"), request.getOrgUid()));
            }
            // searchText
            if (StringUtils.hasText(request.getSearchText())) {
                String searchText = "%" + request.getSearchText() + "%";
                Predicate titlePredicate = criteriaBuilder.like(root.get("title"), searchText);
                Predicate contentPredicate = criteriaBuilder.like(root.get("content"), searchText);
                // Predicate shortCutPredicate = criteriaBuilder.like(root.get("shortCut"), searchText);
                predicates.add(criteriaBuilder.or(titlePredicate, contentPredicate));
            }
            //
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
