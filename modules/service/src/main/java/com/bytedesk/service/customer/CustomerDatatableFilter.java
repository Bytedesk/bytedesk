/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-03 21:18:44
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-03 21:53:33
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.customer;

import java.util.ArrayList;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;


/**
 * https://github.com/tucanoo/crm_spring_boot
 * https://tucanoo.com/build-a-crm-with-spring-boot-and-thymeleaf/
 */
public class CustomerDatatableFilter implements Specification<Customer> {

    String userQuery;

    public CustomerDatatableFilter(String queryString) {
        this.userQuery = queryString;
    }

    @Override
    public Predicate toPredicate(Root<Customer> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        ArrayList<Predicate> predicates = new ArrayList<>();

        if (userQuery != null && userQuery != "") {
            predicates.add(criteriaBuilder.like(root.get("name"), '%' + userQuery + '%'));
            // predicates.add(criteriaBuilder.like(root.get("city"), '%' + userQuery + '%'));
            // predicates.add(criteriaBuilder.like(root.get("email"), '%' + userQuery + '%'));
            // predicates.add(criteriaBuilder.like(root.get("mobile"), '%' + userQuery + '%'));
            // predicates.add(criteriaBuilder.like(root.get("country"), '%' + userQuery + '%'));
        }

        return (! predicates.isEmpty() ? criteriaBuilder.or(predicates.toArray(new Predicate[predicates.size()])) : null);
    }
    
}
