/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-22 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-09-22 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is breach of the terms of the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.push_android_device;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import com.bytedesk.core.rbac.auth.AuthService;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PushDeviceSpecification extends BaseSpecification<PushDeviceEntity, PushDeviceRequest> {

    public static Specification<PushDeviceEntity> search(PushDeviceRequest request, AuthService authService) {
        // log.info("request: {} orgUid: {} pageNumber: {} pageSize: {}",
        //     request, request.getOrgUid(), request.getPageNumber(), request.getPageSize());
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 带层级过滤的基础条件（deleted/orgUid/superUser）
            predicates.addAll(getBasicPredicatesWithLevel(root, criteriaBuilder, request, authService, PushDevicePermissions.MODULE_NAME));
            // provider
            if (StringUtils.hasText(request.getProvider())) {
                predicates.add(criteriaBuilder.equal(root.get("provider"), request.getProvider()));
            }
            // deviceId（精确）
            if (StringUtils.hasText(request.getDeviceId())) {
                predicates.add(criteriaBuilder.equal(root.get("deviceId"), request.getDeviceId()));
            }
            // account（精确，绑定账号 = userUid）
            if (StringUtils.hasText(request.getAccount())) {
                predicates.add(criteriaBuilder.equal(root.get("account"), request.getAccount()));
            }
            // type（绑定类型：ANDROID_ALIYUN）
            if (StringUtils.hasText(request.getType())) {
                predicates.add(criteriaBuilder.equal(root.get("type"), request.getType()));
            }
            // device（设备平台）
            if (StringUtils.hasText(request.getDevice())) {
                predicates.add(criteriaBuilder.equal(root.get("device"), request.getDevice()));
            }
            // level - 如果指定了level则精确过滤
            if (StringUtils.hasText(request.getLevel())) {
                predicates.add(criteriaBuilder.equal(root.get("level"), request.getLevel()));
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
