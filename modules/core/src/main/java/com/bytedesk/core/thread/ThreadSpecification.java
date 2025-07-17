/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-05 22:46:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-17 18:08:07
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.thread;

import java.time.ZonedDateTime;
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
            // predicates.addAll(getBasicPredicates(root, criteriaBuilder,request.getOrgUid()));
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
            // 如果前端设置了isSuperUser标志，则不需要过滤orgUid
            if (!Boolean.TRUE.equals(request.getIsSuperUser()) && StringUtils.hasText(request.getOrgUid())) {
                predicates.add(criteriaBuilder.equal(root.get("orgUid"), request.getOrgUid()));
            }
            
            // 仅当mergeByTopic为true时才应用topic合并逻辑
            if (Boolean.TRUE.equals(request.getMergeByTopic())) {
                // 创建子查询获取每个topic的最新记录的updatedAt时间
                Subquery<ZonedDateTime> maxDateSubquery = query.subquery(ZonedDateTime.class);
                var subRoot = maxDateSubquery.from(ThreadEntity.class);

                // 明确指定类型为 ZonedDateTime
                Path<ZonedDateTime> updatedAtPath = subRoot.get("updatedAt");
                Expression<ZonedDateTime> maxExpression = criteriaBuilder.greatest(updatedAtPath);

                maxDateSubquery.select(maxExpression)
                        .where(criteriaBuilder.equal(subRoot.get("topic"), root.get("topic")));

                // 如果ownerUid不为空，优先选择owner不为空的记录
                if (StringUtils.hasText(request.getOwnerUid())) {
                    // 创建子查询来获取同一topic中匹配ownerUid的最新记录
                    Subquery<ZonedDateTime> ownerMaxDateSubquery = query.subquery(ZonedDateTime.class);
                    var ownerSubRoot = ownerMaxDateSubquery.from(ThreadEntity.class);

                    Path<ZonedDateTime> ownerUpdatedAtPath = ownerSubRoot.get("updatedAt");
                    Expression<ZonedDateTime> ownerMaxExpression = criteriaBuilder.greatest(ownerUpdatedAtPath);

                    ownerMaxDateSubquery.select(ownerMaxExpression)
                            .where(criteriaBuilder.and(
                                    criteriaBuilder.equal(ownerSubRoot.get("topic"), root.get("topic")),
                                    criteriaBuilder.isNotNull(ownerSubRoot.get("owner")),
                                    criteriaBuilder.equal(ownerSubRoot.get("owner").get("uid"),
                                            request.getOwnerUid())));

                    // 创建子查询来检查同一topic中是否存在匹配ownerUid的记录
                    Subquery<Long> ownerExistsSubquery = query.subquery(Long.class);
                    var ownerExistsSubRoot = ownerExistsSubquery.from(ThreadEntity.class);

                    ownerExistsSubquery.select(criteriaBuilder.count(ownerExistsSubRoot))
                            .where(criteriaBuilder.and(
                                    criteriaBuilder.equal(ownerExistsSubRoot.get("topic"), root.get("topic")),
                                    criteriaBuilder.isNotNull(ownerExistsSubRoot.get("owner")),
                                    criteriaBuilder.equal(ownerExistsSubRoot.get("owner").get("uid"),
                                            request.getOwnerUid())));

                    // 两种情况：
                    // 1. 如果存在匹配ownerUid的记录，取该组记录中updatedAt最新的一条
                    // 2. 如果不存在匹配ownerUid的记录，取所有记录中updatedAt最新的一条
                    predicates.add(
                            criteriaBuilder.or(
                                    criteriaBuilder.and(
                                            criteriaBuilder.greaterThan(ownerExistsSubquery, 0L),
                                            criteriaBuilder.isNotNull(root.get("owner")),
                                            criteriaBuilder.equal(root.get("owner").get("uid"), request.getOwnerUid()),
                                            criteriaBuilder.equal(root.get("updatedAt"), ownerMaxDateSubquery)),
                                    criteriaBuilder.and(
                                            criteriaBuilder.equal(ownerExistsSubquery, 0L),
                                            criteriaBuilder.equal(root.get("updatedAt"), maxDateSubquery))));
                } else {
                    // 如果没有指定ownerUid，则使用原来的逻辑：按updatedAt最新的记录
                    predicates.add(criteriaBuilder.equal(root.get("updatedAt"), maxDateSubquery));
                }
            }

            // 根据组件类型过滤
            if (StringUtils.hasText(request.getComponentType())) {
                if (TypeConsts.COMPONENT_TYPE_TEAM.equals(request.getComponentType())) {
                    predicates.add(criteriaBuilder.or(
                            criteriaBuilder.equal(root.get("type"), ThreadTypeEnum.GROUP.toString()),
                            criteriaBuilder.equal(root.get("type"), ThreadTypeEnum.MEMBER.toString())));
                } else if (TypeConsts.COMPONENT_TYPE_SERVICE.equals(request.getComponentType())) {
                    predicates.add(criteriaBuilder.or(
                            criteriaBuilder.equal(root.get("type"), ThreadTypeEnum.AGENT.toString()),
                            criteriaBuilder.equal(root.get("type"), ThreadTypeEnum.WORKGROUP.toString())));
                } else if (TypeConsts.COMPONENT_TYPE_ROBOT.equals(request.getComponentType())) {
                    predicates.add(criteriaBuilder.equal(root.get("type"), ThreadTypeEnum.ROBOT.toString()));
                } else if (TypeConsts.COMPONENT_TYPE_CHANNEL.equals(request.getComponentType())) {
                    
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
            
            // 主题列表查询 - 支持批量查询指定的主题
            if (request.getTopicList() != null && !request.getTopicList().isEmpty()) {
                List<Predicate> topicPredicates = new ArrayList<>();
                for (String topicItem : request.getTopicList()) {
                    if (StringUtils.hasText(topicItem)) {
                        // 支持模糊匹配，检查topic是否包含指定的字符串
                        topicPredicates.add(criteriaBuilder.like(root.get("topic"), "%" + topicItem + "%"));
                    }
                }
                if (!topicPredicates.isEmpty()) {
                    // 任一主题匹配即可
                    predicates.add(criteriaBuilder.or(topicPredicates.toArray(new Predicate[0])));
                }
            }
            
            // 状态查询
            if (StringUtils.hasText(request.getStatus())) {
                predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatus()));
            }
            
            // 创建一个包含inviteUids、monitorUids和ownerUid的OR条件组
            List<Predicate> filterPredicates = new ArrayList<>();

            // 通过 private List<String> inviteUids 查询 private List<String> invites
            if (request.getInviteUids() != null && !request.getInviteUids().isEmpty()) {
                List<Predicate> invitePredicates = new ArrayList<>();
                for (String inviteUid : request.getInviteUids()) {
                    if (StringUtils.hasText(inviteUid)) {
                        // 使用LIKE查询匹配invites字段中包含特定uid的记录
                        // 匹配JSON格式：\"uid\":\"1688358346555520\"
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
                        // 匹配JSON格式：\"uid\":\"1688358346555520\"
                        monitorPredicates.add(criteriaBuilder.like(root.get("monitors"), "%" + monitorUid + "%"));
                    }
                }
                if (!monitorPredicates.isEmpty()) {
                    // 将所有monitorUids条件合并为一个条件
                    filterPredicates.add(criteriaBuilder.or(monitorPredicates.toArray(new Predicate[0])));
                }
            }

            // ticketorUids
            if (request.getTicketorUids() != null && !request.getTicketorUids().isEmpty()) {
                List<Predicate> ticketorPredicates = new ArrayList<>();
                for (String ticketorUid : request.getTicketorUids()) {
                    if (StringUtils.hasText(ticketorUid)) {
                        // 使用LIKE查询匹配ticketors字段中包含特定uid的记录
                        // 匹配JSON格式：\"uid\":\"1688342408200341\"
                        ticketorPredicates.add(criteriaBuilder.like(root.get("ticketors"), "%" + ticketorUid + "%"));
                    }
                }
                if (!ticketorPredicates.isEmpty()) {
                    // 将所有ticketorUids条件合并为一个条件
                    filterPredicates.add(criteriaBuilder.or(ticketorPredicates.toArray(new Predicate[0])));
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
            // agentNickname
            if (StringUtils.hasText(request.getAgentNickname())) {
                predicates.add(criteriaBuilder.like(root.get("agent"), "%" + request.getAgentNickname() + "%"));
            }
            // robotNickname
            if (StringUtils.hasText(request.getRobotNickname())) {
                predicates.add(criteriaBuilder.like(root.get("robot"), "%" + request.getRobotNickname() + "%"));
            }
            // workgroupNickname
            if (StringUtils.hasText(request.getWorkgroupNickname())) {
                predicates.add(criteriaBuilder.like(root.get("workgroup"), "%" + request.getWorkgroupNickname() + "%"));
            }
            //
            if (StringUtils.hasText(request.getChannel())) {
                predicates.add(criteriaBuilder.equal(root.get("client"), request.getChannel()));
            }
            // content
            if (StringUtils.hasText(request.getContent())) {
                predicates.add(criteriaBuilder.like(root.get("content"), "%" + request.getContent() + "%"));
            }
            //
            if (StringUtils.hasText(request.getSearchText())) {
                List<Predicate> orPredicates = new ArrayList<>();
                String searchText = request.getSearchText();
                // String pinyinText = BdPinyinUtils.toPinYin(searchText);

                orPredicates.add(criteriaBuilder.like(root.get("content"), "%" + searchText + "%"));
                orPredicates.add(criteriaBuilder.like(root.get("user"), "%" + searchText + "%"));

                // 添加拼音搜索
                // orPredicates.add(criteriaBuilder.like(root.get("contentPinyin"), "%" + pinyinText + "%"));
                // orPredicates.add(criteriaBuilder.like(root.get("userPinyin"), "%" + pinyinText + "%"));

                predicates.add(criteriaBuilder.or(orPredicates.toArray(new Predicate[0])));
            }
            // 按更新时间排序
            query.orderBy(criteriaBuilder.desc(root.get("updatedAt")));
            //
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
