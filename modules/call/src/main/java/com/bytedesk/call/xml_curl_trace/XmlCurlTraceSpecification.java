/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-09 22:19:21
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-11-29 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.xml_curl_trace;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import com.bytedesk.core.rbac.auth.AuthService;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XmlCurlTraceSpecification extends BaseSpecification<XmlCurlTraceEntity, XmlCurlTraceRequest> {
    
    public static Specification<XmlCurlTraceEntity> search(XmlCurlTraceRequest request, AuthService authService) {
        // log.info("request: {} orgUid: {} pageNumber: {} pageSize: {}", 
        //     request, request.getOrgUid(), request.getPageNumber(), request.getPageSize());
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 使用带层级过滤的基础条件
            predicates.addAll(getBasicPredicatesWithLevel(root, criteriaBuilder, request, authService, XmlCurlTracePermissions.MODULE_NAME));
            if (StringUtils.hasText(request.getSection())) {
                predicates.add(criteriaBuilder.equal(root.get("section"), request.getSection().trim().toLowerCase()));
            }
            if (StringUtils.hasText(request.getCategory())) {
                predicates.add(criteriaBuilder.equal(root.get("category"), request.getCategory().trim().toLowerCase()));
            }
            if (StringUtils.hasText(request.getRemote())) {
                predicates.add(criteriaBuilder.like(root.get("remote"), "%" + request.getRemote() + "%"));
            }
            if (StringUtils.hasText(request.getMethod())) {
                predicates.add(criteriaBuilder.equal(root.get("method"), request.getMethod().trim().toUpperCase()));
            }
            if (StringUtils.hasText(request.getUri())) {
                predicates.add(criteriaBuilder.like(root.get("uri"), "%" + request.getUri() + "%"));
            }
            if (StringUtils.hasText(request.getQuery())) {
                predicates.add(criteriaBuilder.like(root.get("query"), "%" + request.getQuery() + "%"));
            }
            if (request.getFound() != null) {
                predicates.add(criteriaBuilder.equal(root.get("found"), request.getFound()));
            }
            if (request.getResponseSize() != null) {
                predicates.add(criteriaBuilder.equal(root.get("responseSize"), request.getResponseSize()));
            }
            if (request.getCostMs() != null) {
                predicates.add(criteriaBuilder.equal(root.get("costMs"), request.getCostMs()));
            }
            if (StringUtils.hasText(request.getKeyFields())) {
                predicates.add(criteriaBuilder.like(root.get("keyFields"), "%" + request.getKeyFields() + "%"));
            }
            // level - 如果指定了level则精确过滤
            if (StringUtils.hasText(request.getLevel())) {
                predicates.add(criteriaBuilder.equal(root.get("level"), request.getLevel()));
            }
            // 
            if (StringUtils.hasText(request.getUserUid())) {
                predicates.add(criteriaBuilder.equal(root.get("userUid"), request.getUserUid()));
            }
            //
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
