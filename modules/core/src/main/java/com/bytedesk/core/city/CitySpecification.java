/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-09 22:19:21
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-11-29 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.city;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import com.bytedesk.core.rbac.auth.AuthService;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CitySpecification extends BaseSpecification<CityEntity, CityRequest> {
    
    public static Specification<CityEntity> search(CityRequest request, AuthService authService) {
        // log.info("request: {} orgUid: {} pageNumber: {} pageSize: {}", 
        //     request, request.getOrgUid(), request.getPageNumber(), request.getPageSize());
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 使用带层级过滤的基础条件
            // predicates.addAll(getBasicPredicatesWithLevel(root, criteriaBuilder, request, authService,
            //         "CITY"));
            // name
            if (StringUtils.hasText(request.getName())) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + request.getName() + "%"));
            }
            // code
            if (StringUtils.hasText(request.getCode())) {
                predicates.add(criteriaBuilder.equal(root.get("code"), request.getCode()));
            }
            // cap
            if (StringUtils.hasText(request.getCap())) {
                predicates.add(criteriaBuilder.equal(root.get("cap"), request.getCap()));
            }
            // type
            if (StringUtils.hasText(request.getType())) {
                predicates.add(criteriaBuilder.equal(root.get("type"), request.getType()));
            }
            // parentId
            if (request.getParentId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("parentId"), request.getParentId()));
            }
            // pinyin
            if (StringUtils.hasText(request.getPinyin())) {
                predicates.add(criteriaBuilder.like(root.get("pinyin"), "%" + request.getPinyin() + "%"));
            }
            // hot
            if (request.getHot() != null) {
                predicates.add(criteriaBuilder.equal(root.get("hot"), request.getHot()));
            }
            // open
            if (request.getOpen() != null) {
                predicates.add(criteriaBuilder.equal(root.get("open"), request.getOpen()));
            }
            // level - 如果指定了level则精确过滤
            if (StringUtils.hasText(request.getLevel())) {
                predicates.add(criteriaBuilder.equal(root.get("level"), request.getLevel()));
            }
            // 
            if (StringUtils.hasText(request.getUserUid())) {
                predicates.add(criteriaBuilder.equal(root.get("userUid"), request.getUserUid()));
            }
            //
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
