/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-20 17:04:33
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-26 15:46:33
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import com.bytedesk.ticket.consts.TicketConsts;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.utils.BdPinyinUtils;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TicketSpecification extends BaseSpecification {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Specification<TicketEntity> search(TicketRequest request) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            
            List<Predicate> predicates = new ArrayList<>();
            predicates.addAll(getBasicPredicates(root, criteriaBuilder, request.getOrgUid()));
            
            if (StringUtils.hasText(request.getTitle())) {
                predicates.add(criteriaBuilder.like(root.get("title"), "%" + request.getTitle() + "%"));
            }
            if (StringUtils.hasText(request.getDescription())) {
                predicates.add(criteriaBuilder.like(root.get("description"), "%" + request.getDescription() + "%"));
            }
            if (StringUtils.hasText(request.getSearchText())) {
                String searchText = request.getSearchText();
                String pinyinText = BdPinyinUtils.toPinYin(searchText);
                
                predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(root.get("uid"), "%" + searchText + "%"),
                    criteriaBuilder.like(root.get("title"), "%" + searchText + "%"),
                    criteriaBuilder.like(root.get("description"), "%" + searchText + "%"),
                    criteriaBuilder.like(root.get("titlePinyin"), "%" + pinyinText + "%"),
                    criteriaBuilder.like(root.get("descriptionPinyin"), "%" + pinyinText + "%")
                ));
            }
            if (StringUtils.hasText(request.getStatus())) {
                predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatus()));
            }
            if (StringUtils.hasText(request.getPriority())) {
                predicates.add(criteriaBuilder.equal(root.get("priority"), request.getPriority()));
            }
            if (StringUtils.hasText(request.getCategoryUid())) {
                predicates.add(criteriaBuilder.equal(root.get("categoryUid"), request.getCategoryUid()));
            }
            if (StringUtils.hasText(request.getThreadUid())) {
                predicates.add(criteriaBuilder.equal(root.get("threadUid"), request.getThreadUid()));
            }
            if (StringUtils.hasText(request.getTopic())) {
                predicates.add(criteriaBuilder.equal(root.get("topic"), request.getTopic()));
            }
            if (StringUtils.hasText(request.getDepartmentUid())) {
                predicates.add(criteriaBuilder.equal(root.get("departmentUid"), request.getDepartmentUid()));
            }
            // 处理 ALL 查询 - 我创建的或待我处理的
            if (StringUtils.hasText(request.getReporterJson()) || StringUtils.hasText(request.getAssigneeJson())) {
                if (Boolean.TRUE.equals(request.getAssignmentAll())) {
                    List<Predicate> orPredicates = new ArrayList<>();
                    
                    // 处理 reporter 条件
                    if (StringUtils.hasText(request.getReporterJson())) {
                        orPredicates.add(criteriaBuilder.like(root.get("reporter"), 
                            "%" + "\"uid\":\"" + request.getReporter().getUid() + "\"" + "%"));
                    }
                    
                    // 处理 assignee 条件
                    if (StringUtils.hasText(request.getAssigneeJson())) {
                        if (TicketConsts.TICKET_FILTER_UNASSIGNED.equals(request.getAssignee().getUid())) {
                            orPredicates.add(criteriaBuilder.or(
                                criteriaBuilder.isNull(root.get("assignee")),
                                criteriaBuilder.equal(root.get("assignee"), BytedeskConsts.EMPTY_JSON_STRING)
                            ));
                        } else {
                            orPredicates.add(criteriaBuilder.like(root.get("assignee"), 
                                "%" + "\"uid\":\"" + request.getAssignee().getUid() + "\"" + "%"));
                        }
                    }
                    
                    // 组合 OR 条件
                    if (!orPredicates.isEmpty()) {
                        predicates.add(criteriaBuilder.or(orPredicates.toArray(new Predicate[0])));
                    }
                } else {
                    // 单一条件查询
                    if (StringUtils.hasText(request.getReporterJson())) {
                        predicates.add(criteriaBuilder.like(root.get("reporter"), 
                            "%" + "\"uid\":\"" + request.getReporter().getUid() + "\"" + "%"));
                    }
                    if (StringUtils.hasText(request.getAssigneeJson())) {
                        if (TicketConsts.TICKET_FILTER_UNASSIGNED.equals(request.getAssignee().getUid())) {
                            predicates.add(criteriaBuilder.or(
                                criteriaBuilder.isNull(root.get("assignee")),
                                criteriaBuilder.equal(root.get("assignee"), BytedeskConsts.EMPTY_JSON_STRING)
                            ));
                        } else {
                            predicates.add(criteriaBuilder.like(root.get("assignee"), 
                                "%" + "\"uid\":\"" + request.getAssignee().getUid() + "\"" + "%"));
                        }
                    }
                }
            }
            // 处理日期范围查询
            if (StringUtils.hasText(request.getStartDate())) {
                LocalDateTime startDate = LocalDateTime.parse(request.getStartDate(), formatter);
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDate));
            }
            if (StringUtils.hasText(request.getEndDate())) {
                LocalDateTime endDate = LocalDateTime.parse(request.getEndDate(), formatter);
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDate));
            }

            // 添加排序
            query.orderBy(criteriaBuilder.desc(root.get("updatedAt")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<TicketEntity> equalOrgUid(String orgUid) {
        return (root, query, cb) -> cb.equal(root.get("orgUid"), orgUid);
    }
    
    public static Specification<TicketEntity> equalPriority(String priority) {
        return (root, query, cb) -> cb.equal(root.get("priority"), priority);
    }
    
    public static Specification<TicketEntity> equalStatus(String status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }
    
    public static Specification<TicketEntity> betweenCreatedAt(Date startTime, Date endTime) {
        return (root, query, cb) -> cb.between(root.get("createdAt"), startTime, endTime);
    }
}
