/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-08 12:30:14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-02 08:13:47
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.crm.customer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import com.bytedesk.core.rbac.auth.AuthService;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomerSpecification extends BaseSpecification<CustomerEntity, CustomerRequest> {

    public static Specification<CustomerEntity> search(CustomerRequest request, AuthService authService) {
        // log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            // predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
            predicates.addAll(getBasicPredicates(root, criteriaBuilder, request, authService));
            // 使用基类方法处理超级管理员权限和组织过滤
            // addOrgFilterIfNotSuperUser(root, criteriaBuilder, predicates, request, authService);
            
            if (StringUtils.hasText(request.getName())) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + request.getName() + "%"));
            }
            if (StringUtils.hasText(request.getCompanyName())) {
                predicates.add(criteriaBuilder.like(root.get("companyName"), "%" + request.getCompanyName() + "%"));
            }
            if (StringUtils.hasText(request.getMobile())) {
                predicates.add(criteriaBuilder.like(root.get("mobile"), "%" + request.getMobile() + "%"));
            }
            if (StringUtils.hasText(request.getEmail())) {
                predicates.add(criteriaBuilder.like(root.get("email"), "%" + request.getEmail() + "%"));
            }
            if (StringUtils.hasText(request.getStatus())) {
                predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatus()));
            }
            if (StringUtils.hasText(request.getOwnerUserUid())) {
                predicates.add(criteriaBuilder.like(root.get("ownerUserUid"), "%" + request.getOwnerUserUid() + "%"));
            }
            if (StringUtils.hasText(request.getSource())) {
                predicates.add(criteriaBuilder.equal(root.get("source"), request.getSource()));
            }
            if (StringUtils.hasText(request.getDescription())) {
                predicates.add(criteriaBuilder.like(root.get("description"), "%" + request.getDescription() + "%"));
            }

            if (StringUtils.hasText(request.getShopInfo())) {
                predicates.add(criteriaBuilder.like(root.get("shopInfo"), "%" + request.getShopInfo() + "%"));
            }

            if (StringUtils.hasText(request.getConsultContent())) {
                predicates.add(criteriaBuilder.like(root.get("consultContent"), "%" + request.getConsultContent() + "%"));
            }

            if (request.getNeedFollowUp() != null) {
                predicates.add(criteriaBuilder.equal(root.get("needFollowUp"), request.getNeedFollowUp()));
            }
            if (StringUtils.hasText(request.getUserUid())) {
                predicates.add(criteriaBuilder.equal(root.get("userUid"), request.getUserUid()));
            }

            // visitorUid（绑定访客 uid）
            if (StringUtils.hasText(request.getVisitorUid())) {
                predicates.add(criteriaBuilder.like(root.get("visitorUid"), "%" + request.getVisitorUid() + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
