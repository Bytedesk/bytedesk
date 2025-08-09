/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-08 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-08 19:52:25
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.cdr;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CallCdrSpecification extends BaseSpecification<CallCdrEntity, CallCdrRequest> {
    
    public static Specification<CallCdrEntity> search(CallCdrRequest request) {
        log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.addAll(getBasicPredicates(root, criteriaBuilder, request.getOrgUid()));
            
            // uuid
            if (StringUtils.hasText(request.getUuid())) {
                predicates.add(criteriaBuilder.equal(root.get("uuid"), request.getUuid()));
            }
            
            // callerIdNumber
            if (StringUtils.hasText(request.getCallerIdNumber())) {
                predicates.add(criteriaBuilder.like(root.get("callerIdNumber"), "%" + request.getCallerIdNumber() + "%"));
            }
            
            // destinationNumber
            if (StringUtils.hasText(request.getDestinationNumber())) {
                predicates.add(criteriaBuilder.like(root.get("destinationNumber"), "%" + request.getDestinationNumber() + "%"));
            }
            
            // hangupCause
            if (StringUtils.hasText(request.getHangupCause())) {
                predicates.add(criteriaBuilder.equal(root.get("hangupCause"), request.getHangupCause()));
            }
            
            // networkAddr
            if (StringUtils.hasText(request.getNetworkAddr())) {
                predicates.add(criteriaBuilder.like(root.get("networkAddr"), "%" + request.getNetworkAddr() + "%"));
            }
            
            // duration range
            if (request.getMinDuration() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("duration"), request.getMinDuration()));
            }
            if (request.getMaxDuration() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("duration"), request.getMaxDuration()));
            }
            
            // date range
            if (request.getStartDate() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("startStamp"), request.getStartDate()));
            }
            if (request.getEndDate() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("endStamp"), request.getEndDate()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
