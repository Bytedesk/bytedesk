/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-17 15:21:47
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-29 23:33:22
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.ip_black;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;

import jakarta.persistence.criteria.Predicate;

public class IpBlackSpecification extends BaseSpecification {
        
    public static Specification<IpBlackEntity> search(IpBlackRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            // predicates.addAll(getBasicPredicates(root, criteriaBuilder, request.getOrgUid()));
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
            // 方便超级管理员super查询
            if (StringUtils.hasText(request.getOrgUid())) {
                predicates.add(criteriaBuilder.equal(root.get("orgUid"), request.getOrgUid()));
            }
            // ip
            if (StringUtils.hasText(request.getIp())) {
                predicates.add(criteriaBuilder.like(root.get("ip"), "%" + request.getIp() + "%"));
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
            //
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
