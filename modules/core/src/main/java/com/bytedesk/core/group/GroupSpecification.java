/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-28 10:46:47
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-14 13:12:18
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.group;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GroupSpecification extends BaseSpecification<GroupEntity, GroupRequest> {
    
    public static Specification<GroupEntity> search(GroupRequest request) {
        log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.addAll(getBasicPredicates(root, criteriaBuilder, request.getOrgUid()));
            // 
            if (StringUtils.hasText(request.getName())) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + request.getName() + "%"));
            }
            //
            // 如果userUid不为空，则查询该用户所在的群组
            if (StringUtils.hasText(request.getUserUid())) {
                predicates.add(criteriaBuilder.equal(root.join("members").get("user").get("uid"), request.getUserUid()));
            }
            //
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
