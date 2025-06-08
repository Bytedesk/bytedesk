/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-09 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-08 18:46:01
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.gateway;

import com.bytedesk.core.base.BaseSpecification;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * FreeSwitch网关查询规格
 */
@Slf4j
public class FreeSwitchGatewaySpecification extends BaseSpecification {

    /**
     * 查询规格
     */
    public static Specification<FreeSwitchGatewayEntity> search(FreeSwitchGatewayRequest request) {
        
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 网关名称模糊查询
            if (StringUtils.hasText(request.getGatewayName())) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("gatewayName")), 
                    "%" + request.getGatewayName().toLowerCase() + "%"
                ));
            }

            // 代理地址模糊查询
            if (StringUtils.hasText(request.getProxy())) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("proxy")), 
                    "%" + request.getProxy().toLowerCase() + "%"
                ));
            }

            // 用户名模糊查询
            if (StringUtils.hasText(request.getUsername())) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("username")), 
                    "%" + request.getUsername().toLowerCase() + "%"
                ));
            }

            // 状态精确查询
            if (StringUtils.hasText(request.getStatus())) {
                predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatus()));
            }

            // 启用状态查询
            if (!ObjectUtils.isEmpty(request.getEnabled())) {
                predicates.add(criteriaBuilder.equal(root.get("enabled"), request.getEnabled()));
            }

            // 注册状态查询
            if (!ObjectUtils.isEmpty(request.getRegister())) {
                predicates.add(criteriaBuilder.equal(root.get("register"), request.getRegister()));
            }

            // 传输协议查询
            if (StringUtils.hasText(request.getRegisterTransport())) {
                predicates.add(criteriaBuilder.equal(root.get("registerTransport"), request.getRegisterTransport()));
            }

            // 删除状态查询（未删除的记录）
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));

            // 添加组织查询条件
            if (StringUtils.hasText(request.getOrgUid())) {
                predicates.add(criteriaBuilder.equal(root.get("orgUid"), request.getOrgUid()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
