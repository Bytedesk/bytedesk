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
package com.bytedesk.webrtc.participant;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import com.bytedesk.core.rbac.auth.AuthService;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ParticipantSpecification extends BaseSpecification<ParticipantEntity, ParticipantRequest> {
    
    public static Specification<ParticipantEntity> search(ParticipantRequest request, AuthService authService) {
        // log.info("request: {} orgUid: {} pageNumber: {} pageSize: {}", 
        //     request, request.getOrgUid(), request.getPageNumber(), request.getPageSize());
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 使用带层级过滤的基础条件
            predicates.addAll(getBasicPredicatesWithLevel(root, criteriaBuilder, request, authService, ParticipantPermissions.MODULE_NAME));
            // name
            if (StringUtils.hasText(request.getName())) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + request.getName() + "%"));
            }
            // description
            if (StringUtils.hasText(request.getDescription())) {
                predicates.add(criteriaBuilder.like(root.get("description"), "%" + request.getDescription() + "%"));
            }
            // type - 支持逗号分隔多值（如 AUDIO_SERVICE,VIDEO_SERVICE,MEMBER_CALL），与 RoomSpecification 一致
            if (StringUtils.hasText(request.getType())) {
                String[] typeValues = request.getType().split(",");
                if (typeValues.length > 1) {
                    CriteriaBuilder.In<Object> typeIn = criteriaBuilder.in(root.get("type"));
                    for (String typeValue : typeValues) {
                        if (StringUtils.hasText(typeValue)) {
                            typeIn.value(typeValue.trim());
                        }
                    }
                    predicates.add(typeIn);
                } else {
                    predicates.add(criteriaBuilder.equal(root.get("type"), request.getType().trim()));
                }
            }
            // level - 如果指定了level则精确过滤
            if (StringUtils.hasText(request.getLevel())) {
                predicates.add(criteriaBuilder.equal(root.get("level"), request.getLevel()));
            }
            // 
            if (StringUtils.hasText(request.getUserUid())) {
                predicates.add(criteriaBuilder.equal(root.get("userUid"), request.getUserUid()));
            }
            // 会议室：按 roomUid 过滤参会记录
            if (StringUtils.hasText(request.getRoomUid())) {
                predicates.add(criteriaBuilder.equal(root.get("roomUid"), request.getRoomUid()));
            }
            //
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
