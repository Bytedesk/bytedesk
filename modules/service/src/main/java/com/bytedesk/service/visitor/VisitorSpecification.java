/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-29 13:19:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-06 10:42:12
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VisitorSpecification extends BaseSpecification<VisitorEntity, VisitorRequest> {
    
    public static Specification<VisitorEntity> search(VisitorRequest request) {
        // log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            // predicates.addAll(getBasicPredicates(root, criteriaBuilder, request.getOrgUid()));
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
            // ip
            if (StringUtils.hasText(request.getIp())) {
                predicates.add(criteriaBuilder.like(root.get("ip"), "%" + request.getIp() + "%"));
            }
            // ipLocation
            if (StringUtils.hasText(request.getIpLocation())) {
                predicates.add(criteriaBuilder.like(root.get("ipLocation"), "%" + request.getIpLocation() + "%"));
            }
            // 方便超级管理员super查询
            if (StringUtils.hasText(request.getOrgUid())) {
                predicates.add(criteriaBuilder.equal(root.get("orgUid"), request.getOrgUid()));
            }
            if (StringUtils.hasText(request.getNickname())) {
                predicates.add(criteriaBuilder.like(root.get("nickname"), "%" + request.getNickname() + "%"));
            }
            // vipLevel
            // if (StringUtils.hasText(request.getVipLevel())) {
            //     predicates.add(criteriaBuilder.equal(root.get("vipLevel"), request.getVipLevel()));
            // }
            //
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
