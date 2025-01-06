/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-28 10:46:47
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-28 12:58:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.team.group;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import com.bytedesk.core.base.BaseSpecification;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GroupSpecification extends BaseSpecification {
    
    public static Specification<GroupEntity> search(GroupRequest request) {
        log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.addAll(getBasicPredicates(root, criteriaBuilder, request.getOrgUid()));
            // 
            if (request.getName()!= null) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + request.getName() + "%"));
            }
            //
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
