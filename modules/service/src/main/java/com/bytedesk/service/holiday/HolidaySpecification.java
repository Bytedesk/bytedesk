/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-09 22:19:21
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-03 14:25:07
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.holiday;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import com.bytedesk.core.rbac.auth.AuthService;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HolidaySpecification extends BaseSpecification<HolidayEntity, HolidayRequest> {
    
    public static Specification<HolidayEntity> search(HolidayRequest request, AuthService authService) {
        log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.addAll(getBasicPredicates(root, criteriaBuilder, request, authService));
            if (Boolean.TRUE.equals(request.getSuperUser()) && StringUtils.hasText(request.getOrgUid())) {
                predicates.add(criteriaBuilder.equal(root.get("orgUid"), request.getOrgUid()));
            }
            // 
            if (StringUtils.hasText(request.getUserUid())) {
                predicates.add(criteriaBuilder.equal(root.get("userUid"), request.getUserUid()));
            }
            // 字段级过滤：支持前端按名称/类型/年份/国家/标识/是否休息/是否官方/具体日期 搜索
            if (StringUtils.hasText(request.getName())) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + request.getName().toLowerCase() + "%"));
            }

            if (StringUtils.hasText(request.getType())) {
                predicates.add(criteriaBuilder.equal(root.get("type"), request.getType()));
            }

            if (request.getHolidayYear() != null) {
                predicates.add(criteriaBuilder.equal(root.get("holidayYear"), request.getHolidayYear()));
            }

            if (StringUtils.hasText(request.getCountryCode())) {
                predicates.add(criteriaBuilder.equal(root.get("countryCode"), request.getCountryCode()));
            }

            if (StringUtils.hasText(request.getHolidayKey())) {
                predicates.add(criteriaBuilder.equal(root.get("holidayKey"), request.getHolidayKey()));
            }

            if (request.getOffDay() != null) {
                predicates.add(criteriaBuilder.equal(root.get("offDay"), request.getOffDay()));
            }

            if (request.getOfficial() != null) {
                predicates.add(criteriaBuilder.equal(root.get("official"), request.getOfficial()));
            }

            if (request.getHolidayDate() != null) {
                predicates.add(criteriaBuilder.equal(root.get("holidayDate"), request.getHolidayDate()));
            }

            // 通用搜索文本，匹配 name/description/holidayKey
            if (StringUtils.hasText(request.getSearchText())) {
                String kw = "%" + request.getSearchText().toLowerCase() + "%";
                Predicate pName = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), kw);
                Predicate pDesc = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), kw);
                Predicate pKey = criteriaBuilder.like(criteriaBuilder.lower(root.get("holidayKey")), kw);
                predicates.add(criteriaBuilder.or(pName, pDesc, pKey));
            }
            //
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
