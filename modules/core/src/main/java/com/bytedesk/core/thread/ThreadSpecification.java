/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-05 22:46:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-17 15:07:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.thread;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import com.bytedesk.core.constant.TypeConsts;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Subquery;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadSpecification extends BaseSpecification {

    public static Specification<ThreadEntity> search(ThreadRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.addAll(getBasicPredicates(root, criteriaBuilder, request.getOrgUid()));

            // 创建子查询获取每个topic的最新记录的updatedAt时间
            Subquery<LocalDateTime> maxDateSubquery = query.subquery(LocalDateTime.class);
            var subRoot = maxDateSubquery.from(ThreadEntity.class);
            
            // 明确指定类型为 LocalDateTime
            Path<LocalDateTime> updatedAtPath = subRoot.get("updatedAt");
            Expression<LocalDateTime> maxExpression = criteriaBuilder.greatest(updatedAtPath);
            
            maxDateSubquery.select(maxExpression)
                .where(criteriaBuilder.equal(subRoot.get("topic"), root.get("topic")));

            // 添加条件：当前记录的updatedAt等于该topic下的最大updatedAt
            predicates.add(criteriaBuilder.equal(root.get("updatedAt"), maxDateSubquery));
            
            // 根据组件类型过滤
            if (StringUtils.hasText(request.getComponentType())) {
                if (TypeConsts.COMPONENT_TYPE_TEAM.equals(request.getComponentType())) {
                    predicates.add(criteriaBuilder.or(
                        criteriaBuilder.equal(root.get("type"), ThreadTypeEnum.GROUP.toString()),
                        criteriaBuilder.equal(root.get("type"), ThreadTypeEnum.MEMBER.toString())
                    ));
                } else if (TypeConsts.COMPONENT_TYPE_SERVICE.equals(request.getComponentType())) {  
                    predicates.add(criteriaBuilder.or(
                        criteriaBuilder.equal(root.get("type"), ThreadTypeEnum.AGENT.toString()),
                        criteriaBuilder.equal(root.get("type"), ThreadTypeEnum.WORKGROUP.toString())
                    ));
                } else if (TypeConsts.COMPONENT_TYPE_ROBOT.equals(request.getComponentType())) {
                    predicates.add(criteriaBuilder.equal(root.get("type"), ThreadTypeEnum.ROBOT.toString()));
                }
            } else if (StringUtils.hasText(request.getType())) {
                predicates.add(criteriaBuilder.equal(root.get("type"), request.getType()));
            }

            // 其他条件
            if (StringUtils.hasText(request.getUid())) {
                predicates.add(criteriaBuilder.like(root.get("uid"), "%" + request.getUid() + "%"));
            }
            // 
            if (StringUtils.hasText(request.getTopic())) {
                predicates.add(criteriaBuilder.like(root.get("topic"), "%" + request.getTopic() + "%"));
            }
            // 创建一个包含inviteUids、monitorUids和ownerUid的OR条件组
            List<Predicate> filterPredicates = new ArrayList<>();
            
            // 通过 private List<String> inviteUids 查询 private List<String> invites
            if (request.getInviteUids() != null && !request.getInviteUids().isEmpty()) {
                List<Predicate> invitePredicates = new ArrayList<>();
                for (String inviteUid : request.getInviteUids()) {
                    if (StringUtils.hasText(inviteUid)) {
                        // 使用LIKE查询匹配invites字段中包含特定uid的记录
                        invitePredicates.add(criteriaBuilder.like(root.get("invites"), "%" + inviteUid + "%"));
                    }
                }
                if (!invitePredicates.isEmpty()) {
                    // 将所有inviteUids条件合并为一个条件
                    filterPredicates.add(criteriaBuilder.or(invitePredicates.toArray(new Predicate[0])));
                }
            }
            
            // monitorUids
            if (request.getMonitorUids() != null && !request.getMonitorUids().isEmpty()) {
                List<Predicate> monitorPredicates = new ArrayList<>();
                for (String monitorUid : request.getMonitorUids()) {
                    if (StringUtils.hasText(monitorUid)) {
                        // 使用LIKE查询匹配monitors字段中包含特定uid的记录
                        monitorPredicates.add(criteriaBuilder.like(root.get("monitors"), "%" + monitorUid + "%"));
                    }
                }
                if (!monitorPredicates.isEmpty()) {
                    // 将所有monitorUids条件合并为一个条件
                    filterPredicates.add(criteriaBuilder.or(monitorPredicates.toArray(new Predicate[0])));
                }
            }
            
            // ownerUid
            if (StringUtils.hasText(request.getOwnerUid())) {
                List<Predicate> ownerPredicates = new ArrayList<>();
                ownerPredicates.add(criteriaBuilder.equal(root.get("hide"), false));
                ownerPredicates.add(criteriaBuilder.equal(root.get("owner").get("uid"), request.getOwnerUid()));
                // 将ownerUid相关条件合并为一个条件
                filterPredicates.add(criteriaBuilder.and(ownerPredicates.toArray(new Predicate[0])));
            }
            
            // 将三组条件之间用OR连接（只要满足其中一组条件即可）
            if (!filterPredicates.isEmpty()) {
                predicates.add(criteriaBuilder.or(filterPredicates.toArray(new Predicate[0])));
            }
            //
            // user 使用 string 存储，此处暂时用like查询
            if (StringUtils.hasText(request.getUserNickname())) {
                predicates.add(criteriaBuilder.like(root.get("user"), "%" + request.getUserNickname() + "%"));
            }
            // 
            if (StringUtils.hasText(request.getClient())) {
                predicates.add(criteriaBuilder.equal(root.get("client"), request.getClient()));
            }
            // content
            if (StringUtils.hasText(request.getContent())) {
                predicates.add(criteriaBuilder.like(root.get("content"), "%" + request.getContent() + "%"));
            }
            // 
            if (StringUtils.hasText(request.getSearchText())) {
                List<Predicate> orPredicates = new ArrayList<>();
                orPredicates.add(criteriaBuilder.like(root.get("content"), "%" + request.getSearchText() + "%"));
                orPredicates.add(criteriaBuilder.like(root.get("user"), "%" + request.getSearchText() + "%"));
                predicates.add(criteriaBuilder.or(orPredicates.toArray(new Predicate[0])));
            }
            // 按更新时间排序
            query.orderBy(criteriaBuilder.desc(root.get("updatedAt")));
            // 
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
