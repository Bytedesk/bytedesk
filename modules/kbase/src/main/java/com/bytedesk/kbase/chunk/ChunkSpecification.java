/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-09 22:19:21
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-14 11:33:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.chunk;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChunkSpecification extends BaseSpecification {
    
    public static Specification<ChunkEntity> search(ChunkRequest request) {
        log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.addAll(getBasicPredicates(root, criteriaBuilder, request.getOrgUid()));
            // 
            if (StringUtils.hasText(request.getName())) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + request.getName() + "%"));
            }
            // content
            if (StringUtils.hasText(request.getContent())) {
                predicates.add(criteriaBuilder.like(root.get("content"), "%" + request.getContent() + "%"));
            }
            // type
            if (StringUtils.hasText(request.getType())) {
                predicates.add(criteriaBuilder.equal(root.get("type"), request.getType()));
            }
            // level
            if (StringUtils.hasText(request.getLevel())) {
                predicates.add(criteriaBuilder.equal(root.get("level"), request.getLevel()));
            }
            // platform
            if (StringUtils.hasText(request.getPlatform())) {
                predicates.add(criteriaBuilder.equal(root.get("platform"), request.getPlatform()));
            }
            // docId
            if (StringUtils.hasText(request.getDocId())) {  
                predicates.add(criteriaBuilder.equal(root.get("docId"), request.getDocId()));
            }
            // typeUid
            // if (StringUtils.hasText(request.getTypeUid())) {
            //     predicates.add(criteriaBuilder.equal(root.get("typeUid"), request.getTypeUid()));
            // }
            // categoryUid
            if (StringUtils.hasText(request.getCategoryUid())) {
                predicates.add(criteriaBuilder.equal(root.get("categoryUid"), request.getCategoryUid()));
            }
            // kbUid
            if (StringUtils.hasText(request.getKbUid())) {
                // 修改为通过kbaseEntity关联对象的uid进行查询，而不是直接查询kbUid字段
                predicates.add(criteriaBuilder.equal(root.get("kbase").get("uid"), request.getKbUid()));
            }
            // userUid
            if (StringUtils.hasText(request.getUserUid())) {
                predicates.add(criteriaBuilder.equal(root.get("userUid"), request.getUserUid()));
            }
            //
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
