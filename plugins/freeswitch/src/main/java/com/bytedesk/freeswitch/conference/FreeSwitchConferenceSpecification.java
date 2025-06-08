/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-09 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-09 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.conference;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * FreeSwitch会议室查询规范
 */
public class FreeSwitchConferenceSpecification {

    /**
     * 构建查询条件
     */
    public static Specification<FreeSwitchConferenceEntity> build(FreeSwitchConferenceRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 按会议室名称搜索
            if (StringUtils.hasText(request.getConferenceName())) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("conferenceName")),
                    "%" + request.getConferenceName().toLowerCase() + "%"
                ));
            }

            // 按描述搜索
            if (StringUtils.hasText(request.getDescription())) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("description")),
                    "%" + request.getDescription().toLowerCase() + "%"
                ));
            }

            // 按创建者搜索
            if (StringUtils.hasText(request.getCreator())) {
                predicates.add(criteriaBuilder.equal(root.get("creator"), request.getCreator()));
            }

            // 按启用状态过滤
            if (request.getEnabled() != null) {
                predicates.add(criteriaBuilder.equal(root.get("enabled"), request.getEnabled()));
            }

            // 按录音状态过滤
            if (request.getRecordEnabled() != null) {
                predicates.add(criteriaBuilder.equal(root.get("recordEnabled"), request.getRecordEnabled()));
            }

            // 按最大成员数过滤
            if (request.getMaxMembers() != null) {
                predicates.add(criteriaBuilder.equal(root.get("maxMembers"), request.getMaxMembers()));
            }

            // 是否有密码保护
            if (request.getPasswordProtected() != null) {
                if (request.getPasswordProtected()) {
                    predicates.add(criteriaBuilder.and(
                        criteriaBuilder.isNotNull(root.get("password")),
                        criteriaBuilder.notEqual(root.get("password"), "")
                    ));
                } else {
                    predicates.add(criteriaBuilder.or(
                        criteriaBuilder.isNull(root.get("password")),
                        criteriaBuilder.equal(root.get("password"), "")
                    ));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * 按会议室名称搜索
     */
    public static Specification<FreeSwitchConferenceEntity> conferenceName(String conferenceName) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(conferenceName)) {
                return null;
            }
            return criteriaBuilder.like(
                criteriaBuilder.lower(root.get("conferenceName")),
                "%" + conferenceName.toLowerCase() + "%"
            );
        };
    }

    /**
     * 按启用状态过滤
     */
    public static Specification<FreeSwitchConferenceEntity> enabled(Boolean enabled) {
        return (root, query, criteriaBuilder) -> {
            if (enabled == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("enabled"), enabled);
        };
    }

    /**
     * 按录音状态过滤
     */
    public static Specification<FreeSwitchConferenceEntity> recordEnabled(Boolean recordEnabled) {
        return (root, query, criteriaBuilder) -> {
            if (recordEnabled == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("recordEnabled"), recordEnabled);
        };
    }

    /**
     * 按创建者过滤
     */
    public static Specification<FreeSwitchConferenceEntity> creator(String creator) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(creator)) {
                return null;
            }
            return criteriaBuilder.equal(root.get("creator"), creator);
        };
    }

    /**
     * 按最大成员数范围过滤
     */
    public static Specification<FreeSwitchConferenceEntity> maxMembersBetween(Integer minMembers, Integer maxMembers) {
        return (root, query, criteriaBuilder) -> {
            if (minMembers == null && maxMembers == null) {
                return null;
            }
            
            List<Predicate> predicates = new ArrayList<>();
            
            if (minMembers != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("maxMembers"), minMembers));
            }
            
            if (maxMembers != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("maxMembers"), maxMembers));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * 按密码保护状态过滤
     */
    public static Specification<FreeSwitchConferenceEntity> passwordProtected(Boolean passwordProtected) {
        return (root, query, criteriaBuilder) -> {
            if (passwordProtected == null) {
                return null;
            }
            
            if (passwordProtected) {
                return criteriaBuilder.and(
                    criteriaBuilder.isNotNull(root.get("password")),
                    criteriaBuilder.notEqual(root.get("password"), "")
                );
            } else {
                return criteriaBuilder.or(
                    criteriaBuilder.isNull(root.get("password")),
                    criteriaBuilder.equal(root.get("password"), "")
                );
            }
        };
    }
}
