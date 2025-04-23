/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-09 22:19:21
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-23 18:28:15
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm.file;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileSpecification extends BaseSpecification {
    
    public static Specification<FileEntity> search(FileRequest request) {
        log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.addAll(getBasicPredicates(root, criteriaBuilder, request.getOrgUid()));

            // fileName
            if (StringUtils.hasText(request.getFileName())) {
                predicates.add(criteriaBuilder.like(root.get("fileName"), "%" + request.getFileName() + "%"));
            }

            // categoryUid
            if (StringUtils.hasText(request.getCategoryUid())) {
                predicates.add(criteriaBuilder.equal(root.get("categoryUid"), request.getCategoryUid()));
            }
            // kbUid
            if (StringUtils.hasText(request.getKbUid())) {
                // 修改为通过kbaseEntity关联对象的uid进行查询，而不是直接查询kbUid字段
                predicates.add(criteriaBuilder.equal(root.get("kbase").get("uid"), request.getKbUid()));
            }
            
            //
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
