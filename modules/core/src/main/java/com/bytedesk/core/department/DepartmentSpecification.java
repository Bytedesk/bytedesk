/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-11 21:26:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-07 11:41:06
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.department;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import com.bytedesk.core.base.BaseSpecification;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DepartmentSpecification extends BaseSpecification {

    public static Specification<DepartmentEntity> search(DepartmentRequest request) {
        log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.addAll(getBasicPredicates(root, criteriaBuilder, request.getOrgUid()));
            // parent == null
            predicates.add(criteriaBuilder.isNull(root.get("parent")));
            // 过滤掉children中deleted==true的数据
            // predicates.add(
            //         criteriaBuilder.isFalse(root.join("children").get("deleted").as(Boolean.class)));
            //
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
