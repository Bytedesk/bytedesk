/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-12 09:07:53
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-26 16:18:40
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import com.bytedesk.core.rbac.auth.AuthService;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RobotSpecification extends BaseSpecification<RobotEntity, RobotRequest> {
    
    public static Specification<RobotEntity> search(RobotRequest request, AuthService authService) {
        log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            // predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
            predicates.addAll(getBasicPredicates(root, criteriaBuilder, request, authService));
            // 使用基类方法处理超级管理员权限和组织过滤
            // addOrgFilterIfNotSuperUser(root, criteriaBuilder, predicates, request, authService);
            // name
            if (StringUtils.hasText(request.getName())) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + request.getName() + "%"));
            }
            // nickname
            if (StringUtils.hasText(request.getNickname())) {
                predicates.add(criteriaBuilder.like(root.get("nickname"), "%" + request.getNickname() + "%"));
            }
            // description
            if (StringUtils.hasText(request.getDescription())) {
                predicates.add(criteriaBuilder.like(root.get("description"), "%" + request.getDescription() + "%"));
            }
            // 
            if (StringUtils.hasText(request.getOrgUid())) {
                predicates.add(criteriaBuilder.equal(root.get("orgUid"), request.getOrgUid()));
            }
            if (StringUtils.hasText(request.getLevel())) {
                predicates.add(criteriaBuilder.equal(root.get("level"), request.getLevel()));
            }
            if (StringUtils.hasText(request.getCategoryUid())) {
                predicates.add(criteriaBuilder.equal(root.get("categoryUid"), request.getCategoryUid()));
            }
            // 连接 Settings 以支持 LLM 提示词搜索等
            jakarta.persistence.criteria.Join<Object, Object> settingsJoin = null;
            try {
                settingsJoin = root.join("settings", jakarta.persistence.criteria.JoinType.LEFT);
            } catch (Exception ex) {
                log.debug("RobotSpecification join 'settings' failed", ex);
            }
            if (StringUtils.hasText(request.getType())) {
                predicates.add(criteriaBuilder.equal(root.get("type"), request.getType()));
            }
            // 方便前端搜索
            if (StringUtils.hasText(request.getPrompt())) {
                if (settingsJoin == null) {
                    settingsJoin = root.join("settings", jakarta.persistence.criteria.JoinType.LEFT);
                }
                if (settingsJoin != null) {
                    jakarta.persistence.criteria.Join<Object, Object> llmJoin = null;
                    try {
                        llmJoin = settingsJoin.join("llm", jakarta.persistence.criteria.JoinType.LEFT);
                    } catch (Exception ex) {
                        log.debug("RobotSpecification join 'llm' failed", ex);
                    }
                    if (llmJoin != null) {
                        predicates.add(criteriaBuilder.like(llmJoin.get("prompt"), "%" + request.getPrompt() + "%"));
                    }
                }
            }
            
            // searchText
            if (StringUtils.hasText(request.getSearchText())) {
                List<Predicate> orPredicates = new ArrayList<>();
                String searchText = request.getSearchText();
                
                orPredicates.add(criteriaBuilder.like(root.get("name"), "%" + searchText + "%"));
                orPredicates.add(criteriaBuilder.like(root.get("nickname"), "%" + searchText + "%"));
                orPredicates.add(criteriaBuilder.like(root.get("description"), "%" + searchText + "%"));

                predicates.add(criteriaBuilder.or(orPredicates.toArray(new Predicate[0])));
            }
            // 
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
