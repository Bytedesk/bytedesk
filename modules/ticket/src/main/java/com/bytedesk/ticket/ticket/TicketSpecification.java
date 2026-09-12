/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-08 12:30:14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-06 10:44:43
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.ticket.ticket_sla_record.TicketSlaRecordEntity;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Subquery;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TicketSpecification extends BaseSpecification<TicketEntity, TicketRequest> {

    private static final char LIKE_ESCAPE_CHAR = '\\';
    private static final String ASSIGNEE_UID_KEYWORD_UNASSIGNED = "UNASSIGNED";

    public static Specification<TicketEntity> search(TicketRequest request, AuthService authService) {
        // log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
            // predicates.addAll(getBasicPredicates(root, criteriaBuilder, request,
            // authService)); // 基础查询条件

            // 组织隔离：按 orgUid 限定查询范围，避免跨组织数据泄露
            if (StringUtils.hasText(request.getOrgUid())) {
                predicates.add(criteriaBuilder.equal(root.get("orgUid"), request.getOrgUid()));
            }

            if (StringUtils.hasText(request.getTitle())) {
                predicates.add(criteriaBuilder.like(root.get("title"), "%" + request.getTitle() + "%"));
            }
            // description
            if (StringUtils.hasText(request.getDescription())) {
                predicates.add(criteriaBuilder.like(root.get("description"), "%" + request.getDescription() + "%"));
            }

            if (StringUtils.hasText(request.getSearchText())) {
                String searchText = request.getSearchText().trim().toLowerCase();
                String searchPattern = "%" + escapeLike(searchText) + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), searchPattern,
                                LIKE_ESCAPE_CHAR),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), searchPattern,
                                LIKE_ESCAPE_CHAR),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("ticketNumber")), searchPattern,
                                LIKE_ESCAPE_CHAR),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("contactName")), searchPattern,
                                LIKE_ESCAPE_CHAR),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("phone")), searchPattern,
                                LIKE_ESCAPE_CHAR),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), searchPattern,
                                LIKE_ESCAPE_CHAR),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("wechat")), searchPattern,
                                LIKE_ESCAPE_CHAR),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("threadTopic")), searchPattern,
                                LIKE_ESCAPE_CHAR),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("visitorThreadTopic")), searchPattern,
                                LIKE_ESCAPE_CHAR),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("reporter")), searchPattern,
                                LIKE_ESCAPE_CHAR),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("assignee")), searchPattern,
                                LIKE_ESCAPE_CHAR)));
            }

            // ticket number
            // 容错匹配：通知/移动端展示为 "#TKxxxx"，允许用户带 '#' 或仅输入部分编号；
            // 统一 trim + 去前导 '#'，转义后按小写模糊匹配（兼容大小写敏感数据库）
            if (StringUtils.hasText(request.getTicketNumber())) {
                String ticketNumber = request.getTicketNumber().trim().replaceFirst("^#+", "");
                if (!ticketNumber.isEmpty()) {
                    String ticketNumberPattern = "%" + escapeLike(ticketNumber.toLowerCase()) + "%";
                    predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("ticketNumber")),
                            ticketNumberPattern, LIKE_ESCAPE_CHAR));
                }
            }
            // type
            if (StringUtils.hasText(request.getType())) {
                predicates.add(criteriaBuilder.equal(root.get("type"), request.getType()));
            }
            // status
            if (StringUtils.hasText(request.getStatus())) {
                predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatus()));
            }
            // priority
            if (StringUtils.hasText(request.getPriority())) {
                predicates.add(criteriaBuilder.equal(root.get("priority"), request.getPriority()));
            }
            // threadUid
            if (StringUtils.hasText(request.getThreadUid())) {
                predicates.add(criteriaBuilder.equal(root.get("threadUid"), request.getThreadUid()));
            }
            // threadTopic
            if (StringUtils.hasText(request.getThreadTopic())) {
                predicates.add(criteriaBuilder.equal(root.get("threadTopic"), request.getThreadTopic()));
            }
            // categoryUid
            if (StringUtils.hasText(request.getCategoryUid())) {
                predicates.add(criteriaBuilder.equal(root.get("categoryUid"), request.getCategoryUid()));
            }
            // workgroupUid
            if (StringUtils.hasText(request.getWorkgroupUid())) {
                predicates.add(criteriaBuilder.equal(root.get("workgroupUid"), request.getWorkgroupUid()));
            }
            // departmentUid
            if (StringUtils.hasText(request.getDepartmentUid())) {
                predicates.add(criteriaBuilder.equal(root.get("departmentUid"), request.getDepartmentUid()));
            }

            // reporterUid (stored in JSON string column: reporter)
            if (StringUtils.hasText(request.getReporterUid())) {
                String reporterUid = escapeLike(request.getReporterUid());
                String reporterPattern = "%\"uid\":\"" + reporterUid + "\"%";
                predicates.add(criteriaBuilder.like(root.get("reporter"), reporterPattern, LIKE_ESCAPE_CHAR));
            }

            // assigneeUid (stored in JSON string column: assignee)
            if (StringUtils.hasText(request.getAssigneeUid())) {
                if (ASSIGNEE_UID_KEYWORD_UNASSIGNED.equalsIgnoreCase(request.getAssigneeUid())) {
                    // 未分配：assignee 为空/null/{} 或不包含 uid 字段
                    Predicate assigneeIsNull = criteriaBuilder.isNull(root.get("assignee"));
                    Predicate assigneeIsEmptyString = criteriaBuilder.equal(root.get("assignee"), "");
                    Predicate assigneeIsEmptyJson = criteriaBuilder.equal(root.get("assignee"),
                            BytedeskConsts.EMPTY_JSON_STRING);
                    Predicate assigneeUidEmpty = criteriaBuilder.like(root.get("assignee"), "%\"uid\":\"\"%",
                            LIKE_ESCAPE_CHAR);
                    Predicate assigneeHasNoUidField = criteriaBuilder.notLike(root.get("assignee"), "%\"uid\":\"%",
                            LIKE_ESCAPE_CHAR);
                    predicates.add(criteriaBuilder.or(
                            assigneeIsNull,
                            assigneeIsEmptyString,
                            assigneeIsEmptyJson,
                            assigneeUidEmpty,
                            assigneeHasNoUidField));
                } else {
                    String assigneeUid = escapeLike(request.getAssigneeUid());
                    String assigneePattern = "%\"uid\":\"" + assigneeUid + "\"%";
                    predicates.add(criteriaBuilder.like(root.get("assignee"), assigneePattern, LIKE_ESCAPE_CHAR));
                }
            }

            // userUid
            if (StringUtils.hasText(request.getUserUid())) {
                predicates.add(criteriaBuilder.equal(root.get("userUid"), request.getUserUid()));
            }

            // level (platform / organization)
            if (StringUtils.hasText(request.getLevel())) {
                predicates.add(criteriaBuilder.equal(root.get("level"), request.getLevel()));
            }

            appendVisibilityPredicates(request, root, criteriaBuilder, predicates);

            if (StringUtils.hasText(request.getSlaStatus())) {
                Subquery<String> slaSubquery = query.subquery(String.class);
                var slaRoot = slaSubquery.from(TicketSlaRecordEntity.class);
                slaSubquery.select(slaRoot.get("ticketUid"));
                slaSubquery.where(
                        criteriaBuilder.equal(slaRoot.get("deleted"), false),
                        criteriaBuilder.equal(slaRoot.get("status"), request.getSlaStatus()),
                        criteriaBuilder.equal(slaRoot.get("ticketUid"), root.get("uid")));
                predicates.add(criteriaBuilder.exists(slaSubquery));
            }

            // 时间范围过滤 - 使用BdDateUtils进行时间解析和转换
            if (StringUtils.hasText(request.getCreatedAtStart())) {
                try {
                    java.time.ZonedDateTime startDateTime = BdDateUtils.parseStartDate(request.getCreatedAtStart());
                    if (startDateTime != null) {
                        predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDateTime));
                    }
                } catch (Exception e) {
                    log.warn("Invalid createdAtStart format: {}", request.getCreatedAtStart());
                }
            }
            if (StringUtils.hasText(request.getCreatedAtEnd())) {
                try {
                    java.time.ZonedDateTime endDateTime = BdDateUtils.parseEndDate(request.getCreatedAtEnd());
                    if (endDateTime != null) {
                        predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDateTime));
                    }
                } catch (Exception e) {
                    log.warn("Invalid createdAtEnd format: {}", request.getCreatedAtEnd());
                }
            }
            //
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static String escapeLike(String value) {
        if (value == null) {
            return null;
        }
        return value
                .replace("\\\\", "\\\\\\\\")
                .replace("%", "\\\\%")
                .replace("_", "\\\\_");
    }

    private static void appendVisibilityPredicates(TicketRequest request,
            jakarta.persistence.criteria.Root<TicketEntity> root,
            jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder,
            List<Predicate> predicates) {
        if (!Boolean.TRUE.equals(request.getVisibilityRestricted())) {
            return;
        }
        if (!StringUtils.hasText(request.getVisibilityCurrentUserUid())) {
            return;
        }

        String assigneePattern = "%\"uid\":\""
                + escapeLike(request.getVisibilityCurrentUserUid())
                + "\"%";
        // 工单 userUid（报告人为成员时）与 assignee JSON 存的是 member uid，与 user uid 不同：双口径匹配，
        // 避免“处理人看不到自己被分配的工单”
        Predicate reporterSelf = StringUtils.hasText(request.getVisibilityCurrentUserMemberUid())
                ? criteriaBuilder.or(
                        criteriaBuilder.equal(root.get("userUid"), request.getVisibilityCurrentUserUid()),
                        criteriaBuilder.equal(root.get("userUid"), request.getVisibilityCurrentUserMemberUid()))
                : criteriaBuilder.equal(root.get("userUid"), request.getVisibilityCurrentUserUid());
        Predicate assigneeSelf = StringUtils.hasText(request.getVisibilityCurrentUserMemberUid())
                ? criteriaBuilder.or(
                        criteriaBuilder.like(root.get("assignee"), assigneePattern, LIKE_ESCAPE_CHAR),
                        criteriaBuilder.like(root.get("assignee"),
                                "%\"uid\":\"" + escapeLike(request.getVisibilityCurrentUserMemberUid()) + "\"%",
                                LIKE_ESCAPE_CHAR))
                : criteriaBuilder.like(root.get("assignee"), assigneePattern, LIKE_ESCAPE_CHAR);
        Predicate noDepartmentAssigned = criteriaBuilder.or(
                criteriaBuilder.isNull(root.get("departmentUid")),
                criteriaBuilder.equal(root.get("departmentUid"), ""));
        Predicate sameDepartment = StringUtils.hasText(request.getVisibilityCurrentUserDepartmentUid())
                ? criteriaBuilder.equal(root.get("departmentUid"), request.getVisibilityCurrentUserDepartmentUid())
                : criteriaBuilder.disjunction();

        // G2 组合谓词：无 type 的混合列表查询按工单类型分别应用 INTERNAL/EXTERNAL 可见性设置，
        // 保证「全部工单」=「内部工单」+「外部工单」口径一致（创建人/受理人仍始终可见）
        if (request.getVisibilityInternalContext() != null || request.getVisibilityExternalContext() != null) {
            Predicate typeInternal = criteriaBuilder.equal(root.get("type"), "INTERNAL");
            Predicate typeExternal = criteriaBuilder.equal(root.get("type"), "EXTERNAL");
            Predicate internalVisible = buildTypeVisibilityPredicate(request.getVisibilityInternalContext(),
                    root, criteriaBuilder, noDepartmentAssigned, sameDepartment,
                    request.getVisibilityCurrentUserDepartmentUid());
            Predicate externalVisible = buildTypeVisibilityPredicate(request.getVisibilityExternalContext(),
                    root, criteriaBuilder, noDepartmentAssigned, sameDepartment,
                    request.getVisibilityCurrentUserDepartmentUid());
            predicates.add(criteriaBuilder.or(
                    reporterSelf, assigneeSelf,
                    criteriaBuilder.and(typeInternal, internalVisible),
                    criteriaBuilder.and(typeExternal, externalVisible)));
            return;
        }

        if ("DEPARTMENT_RESTRICTED".equalsIgnoreCase(request.getVisibilityMode())) {
            predicates.add(criteriaBuilder.or(reporterSelf, assigneeSelf, noDepartmentAssigned, sameDepartment));
            return;
        }

        if ("DEPARTMENT_BASED".equalsIgnoreCase(request.getVisibilityMode())) {
            boolean currentDepartmentAllowed = StringUtils.hasText(request.getVisibilityCurrentUserDepartmentUid())
                    && request.getVisibilityAllowedDepartmentUids() != null
                    && request.getVisibilityAllowedDepartmentUids()
                            .contains(request.getVisibilityCurrentUserDepartmentUid());
            if (!currentDepartmentAllowed) {
                predicates.add(criteriaBuilder.or(reporterSelf, assigneeSelf));
            }
            return;
        }

        if ("CATEGORY_BASED".equalsIgnoreCase(request.getVisibilityMode())) {
            List<String> sameDeptCategories = request.getVisibilityRestrictedCategoryUids();
            Map<String, List<String>> deptBasedCategories = request.getVisibilityRestrictedCategoryDepartmentUids();
            boolean hasSameDept = sameDeptCategories != null && !sameDeptCategories.isEmpty();
            boolean hasDeptBased = deptBasedCategories != null && !deptBasedCategories.isEmpty();
            if (hasSameDept || hasDeptBased) {
                // 命中受限分类且不在其允许可见范围内 => 视为不可见
                List<Predicate> restrictedAndInvisible = new ArrayList<>();
                if (sameDeptCategories != null && !sameDeptCategories.isEmpty()) {
                    for (String categoryUid : sameDeptCategories) {
                        if (!StringUtils.hasText(categoryUid)) {
                            continue;
                        }
                        Predicate categoryMatched = criteriaBuilder.equal(root.get("categoryUid"), categoryUid);
                        Predicate departmentRestrictedInvisible = criteriaBuilder.and(
                                criteriaBuilder.not(noDepartmentAssigned),
                                criteriaBuilder.not(sameDepartment));
                        restrictedAndInvisible.add(criteriaBuilder.and(categoryMatched,
                                departmentRestrictedInvisible));
                    }
                }
                if (deptBasedCategories != null && !deptBasedCategories.isEmpty()) {
                    for (Map.Entry<String, List<String>> entry : deptBasedCategories.entrySet()) {
                        String categoryUid = entry.getKey();
                        List<String> allowedDepartmentUids = entry.getValue();
                        if (!StringUtils.hasText(categoryUid) || allowedDepartmentUids == null
                                || allowedDepartmentUids.isEmpty()) {
                            continue;
                        }
                        Predicate categoryMatched = criteriaBuilder.equal(root.get("categoryUid"), categoryUid);
                        boolean currentDepartmentAllowed = StringUtils.hasText(request.getVisibilityCurrentUserDepartmentUid())
                                && allowedDepartmentUids.contains(request.getVisibilityCurrentUserDepartmentUid());
                        if (!currentDepartmentAllowed) {
                            restrictedAndInvisible.add(categoryMatched);
                        }
                    }
                }
                // 未填写分类的工单（如访客提交/自动创建的外部工单）无法套用任何分类规则：
                // 存在受限分类规则时默认收紧为仅创建人/受理人可见，避免无分类工单全员可见。
                // 管理员/超级管理员已在 enrichVisibilityContext 中跳过本谓词；无任何受限规则时不生效。
                restrictedAndInvisible.add(criteriaBuilder.or(
                        criteriaBuilder.isNull(root.get("categoryUid")),
                        criteriaBuilder.equal(root.get("categoryUid"), "")));
                if (!restrictedAndInvisible.isEmpty()) {
                    Predicate restrictedAndNotVisible = criteriaBuilder.or(
                            restrictedAndInvisible.toArray(new Predicate[0]));
                    Predicate alwaysVisible = criteriaBuilder.or(reporterSelf, assigneeSelf);
                    predicates.add(criteriaBuilder.or(alwaysVisible, criteriaBuilder.not(restrictedAndNotVisible)));
                }
            }
        }
    }

    /**
     * G2：构建单类型（INTERNAL/EXTERNAL）的可见性谓词。
     * 返回 conjunction()（恒真）表示该类型工单全部可见（未配置/ORG_WIDE/无受限规则）。
     * 语义与单类型路径一致：DEPARTMENT_RESTRICTED → 无部门或同部门；
     * CATEGORY_BASED → 未命中受限规则，命中则需部门匹配，未分类工单在有受限规则时收紧。
     */
    private static Predicate buildTypeVisibilityPredicate(TicketVisibilityQueryContext context,
            jakarta.persistence.criteria.Root<TicketEntity> root,
            jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder,
            Predicate noDepartmentAssigned, Predicate sameDepartment, String currentDepartmentUid) {
        if (context == null || !context.isRestricted()) {
            return criteriaBuilder.conjunction();
        }
        if ("DEPARTMENT_RESTRICTED".equalsIgnoreCase(context.getMode())) {
            return criteriaBuilder.or(noDepartmentAssigned, sameDepartment);
        }
        if ("DEPARTMENT_BASED".equalsIgnoreCase(context.getMode())) {
            boolean currentDepartmentAllowed = StringUtils.hasText(currentDepartmentUid)
                    && context.getAllowedDepartmentUids() != null
                    && context.getAllowedDepartmentUids().contains(currentDepartmentUid);
            return currentDepartmentAllowed ? criteriaBuilder.conjunction() : criteriaBuilder.disjunction();
        }
        if ("CATEGORY_BASED".equalsIgnoreCase(context.getMode())) {
            List<String> sameDeptCategories = context.getRestrictedCategoryUids();
            Map<String, List<String>> deptBasedCategories = context.getRestrictedCategoryDepartmentUids();
            boolean hasSameDept = sameDeptCategories != null && !sameDeptCategories.isEmpty();
            boolean hasDeptBased = deptBasedCategories != null && !deptBasedCategories.isEmpty();
            if (!hasSameDept && !hasDeptBased) {
                return criteriaBuilder.conjunction();
            }
            List<Predicate> restrictedAndInvisible = new ArrayList<>();
            if (sameDeptCategories != null && !sameDeptCategories.isEmpty()) {
                for (String categoryUid : sameDeptCategories) {
                    if (!StringUtils.hasText(categoryUid)) {
                        continue;
                    }
                    restrictedAndInvisible.add(criteriaBuilder.and(
                            criteriaBuilder.equal(root.get("categoryUid"), categoryUid),
                            criteriaBuilder.and(
                                    criteriaBuilder.not(noDepartmentAssigned),
                                    criteriaBuilder.not(sameDepartment))));
                }
            }
            if (deptBasedCategories != null && !deptBasedCategories.isEmpty()) {
                for (Map.Entry<String, List<String>> entry : deptBasedCategories.entrySet()) {
                    String categoryUid = entry.getKey();
                    List<String> allowedDepartmentUids = entry.getValue();
                    if (!StringUtils.hasText(categoryUid) || allowedDepartmentUids == null
                            || allowedDepartmentUids.isEmpty()) {
                        continue;
                    }
                    boolean currentDepartmentAllowed = StringUtils.hasText(currentDepartmentUid)
                            && allowedDepartmentUids.contains(currentDepartmentUid);
                    if (!currentDepartmentAllowed) {
                        restrictedAndInvisible.add(
                                criteriaBuilder.equal(root.get("categoryUid"), categoryUid));
                    }
                }
            }
            // 未分类工单收紧（与单类型路径同口径）：存在受限规则时仅创建人/受理人可见
            restrictedAndInvisible.add(criteriaBuilder.or(
                    criteriaBuilder.isNull(root.get("categoryUid")),
                    criteriaBuilder.equal(root.get("categoryUid"), "")));
            if (restrictedAndInvisible.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.not(criteriaBuilder.or(restrictedAndInvisible.toArray(new Predicate[0])));
        }
        return criteriaBuilder.conjunction();
    }
}
