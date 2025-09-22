/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-08 12:49:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-22 13:28:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.category;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import com.bytedesk.core.rbac.auth.AuthService;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CategorySpecification extends BaseSpecification<CategoryEntity, CategoryRequest> {

    public static Specification<CategoryEntity> search(CategoryRequest request, AuthService authService) {
        // log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 基础条件：只查找根分类
            predicates.add(criteriaBuilder.isNull(root.get("parent")));
            
            predicates.addAll(getBasicPredicates(root, criteriaBuilder, request.getOrgUid()));
            //
            if (StringUtils.hasText(request.getName())) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + request.getName() + "%"));
            }
            if (StringUtils.hasText(request.getType())) {
                predicates.add(criteriaBuilder.like(root.get("type"), "%" + request.getType() + "%"));
            }
            if (StringUtils.hasText(request.getKbUid())) {
                predicates.add(criteriaBuilder.equal(root.get("kbUid"), request.getKbUid()));
            }
            if (StringUtils.hasText(request.getUserUid())) {
                predicates.add(criteriaBuilder.equal(root.get("userUid"), request.getUserUid()));
            }
            if (StringUtils.hasText(request.getLevel())) {
                predicates.add(criteriaBuilder.equal(root.get("level"), request.getLevel()));
            }
            //
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * 专门用于查询根分类的 Specification（parent 为 null 的分类）
     * 
     * @param request 查询请求参数
     * @param authService 认证服务
     * @return 根分类查询规范
     */
    // public static Specification<CategoryEntity> searchRootOnly(CategoryRequest request, AuthService authService) {
    //     return (root, query, criteriaBuilder) -> {
    //         List<Predicate> predicates = new ArrayList<>();
            
    //         // 基础条件：只查找根分类
    //         predicates.add(criteriaBuilder.isNull(root.get("parent")));
            
    //         // 添加基础查询条件（deleted = false, orgUid 等）
    //         predicates.addAll(getBasicPredicates(root, criteriaBuilder, request.getOrgUid()));
            
    //         // 添加其他搜索条件
    //         if (StringUtils.hasText(request.getName())) {
    //             predicates.add(criteriaBuilder.like(root.get("name"), "%" + request.getName() + "%"));
    //         }
    //         if (StringUtils.hasText(request.getType())) {
    //             predicates.add(criteriaBuilder.like(root.get("type"), "%" + request.getType() + "%"));
    //         }
    //         if (StringUtils.hasText(request.getKbUid())) {
    //             predicates.add(criteriaBuilder.equal(root.get("kbUid"), request.getKbUid()));
    //         }
    //         if (StringUtils.hasText(request.getUserUid())) {
    //             predicates.add(criteriaBuilder.equal(root.get("userUid"), request.getUserUid()));
    //         }
    //         if (StringUtils.hasText(request.getLevel())) {
    //             predicates.add(criteriaBuilder.equal(root.get("level"), request.getLevel()));
    //         }
            
    //         // 确保查询结果唯一
    //         query.distinct(true);
    //         return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    //     };
    // }
}
