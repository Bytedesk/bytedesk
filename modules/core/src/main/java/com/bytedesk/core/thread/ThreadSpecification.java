/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-05 22:46:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-26 16:57:43
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
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.rbac.auth.AuthService;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadSpecification extends BaseSpecification<ThreadEntity, ThreadRequest> {

    private static void applyUpdatedAtRange(ThreadRequest request,
            Root<ThreadEntity> root,
            jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder,
            List<Predicate> predicates) {
        if (request == null) {
            return;
        }
        ZonedDateTime startAt = request.getStartAt();
        ZonedDateTime endAt = request.getEndAt();
        if (startAt != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("updatedAt"), startAt));
        }
        if (endAt != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("updatedAt"), endAt));
        }
    }

    private static Predicate buildMessageContentPredicate(ThreadRequest request,
            String keyword,
            Root<ThreadEntity> root,
            jakarta.persistence.criteria.CriteriaQuery<?> query,
            jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder) {
        if (request == null || !StringUtils.hasText(keyword)) {
            return null;
        }

        Subquery<Long> messageThreadIdSubquery = query.subquery(Long.class);
        Root<MessageEntity> messageRoot = messageThreadIdSubquery.from(MessageEntity.class);

        List<Predicate> messagePredicates = new ArrayList<>();
        messagePredicates.add(criteriaBuilder.equal(messageRoot.get("deleted"), false));
        messagePredicates.add(criteriaBuilder.like(messageRoot.get("content"), "%" + keyword + "%"));

        // 如果设置了时间范围，同时约束消息的 createdAt（更贴近“按时间搜索消息”直觉）
        ZonedDateTime startAt = request.getStartAt();
        ZonedDateTime endAt = request.getEndAt();
        if (startAt != null) {
            messagePredicates.add(criteriaBuilder.greaterThanOrEqualTo(messageRoot.get("createdAt"), startAt));
        }
        if (endAt != null) {
            messagePredicates.add(criteriaBuilder.lessThanOrEqualTo(messageRoot.get("createdAt"), endAt));
        }

        messageThreadIdSubquery
                .select(messageRoot.get("thread").get("id"))
                .distinct(true)
                .where(criteriaBuilder.and(messagePredicates.toArray(new Predicate[0])));

        return root.get("id").in(messageThreadIdSubquery);
    }

    /**
     * User(登录) 侧客服会话查询：
     * - 仅返回当前客服“参与”的会话（owner / invites / monitors / assistants / ticketors）
     * - 合并相同 topic，仅取 updatedAt 最新的一条
     * - 与会话列表展示行为对齐：updatedAt 倒序
     */
    public static Specification<ThreadEntity> searchForUser(ThreadRequest request, String userUid, String orgUid) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
            predicates.add(criteriaBuilder.equal(root.get("hide"), false));

            // userUid 为空时不应返回任何数据（避免误查全量）
            if (!StringUtils.hasText(userUid)) {
                return criteriaBuilder.disjunction();
            }

            // 默认限制在当前组织；同时兼容历史数据：orgUid 为空且 level=USER
            if (StringUtils.hasText(orgUid)) {
                predicates.add(
                        criteriaBuilder.or(
                                criteriaBuilder.equal(root.get("orgUid"), orgUid),
                                criteriaBuilder.and(
                                        criteriaBuilder.isNull(root.get("orgUid")),
                                        criteriaBuilder.equal(root.get("level"), LevelEnum.USER.name()))));
            }

            // 当前客服参与会话（owner / invites / monitors / assistants / ticketors）
            Predicate participatedPredicate = criteriaBuilder.or(
                    criteriaBuilder.and(
                            criteriaBuilder.isNotNull(root.get("owner")),
                            criteriaBuilder.equal(root.get("owner").get("uid"), userUid)),
                    criteriaBuilder.like(root.get("invites"), "%" + userUid + "%"),
                    criteriaBuilder.like(root.get("monitors"), "%" + userUid + "%"),
                    criteriaBuilder.like(root.get("assistants"), "%" + userUid + "%"),
                    criteriaBuilder.like(root.get("ticketors"), "%" + userUid + "%"));
            predicates.add(participatedPredicate);

            // 合并相同 topic，仅取参与范围内 updatedAt 最新的一条
            Subquery<ZonedDateTime> maxDateSubquery = query.subquery(ZonedDateTime.class);
            var subRoot = maxDateSubquery.from(ThreadEntity.class);
            Path<ZonedDateTime> updatedAtPath = subRoot.get("updatedAt");
            Expression<ZonedDateTime> maxExpression = criteriaBuilder.greatest(updatedAtPath);

            Predicate subParticipatedPredicate = criteriaBuilder.or(
                    criteriaBuilder.and(
                            criteriaBuilder.isNotNull(subRoot.get("owner")),
                            criteriaBuilder.equal(subRoot.get("owner").get("uid"), userUid)),
                    criteriaBuilder.like(subRoot.get("invites"), "%" + userUid + "%"),
                    criteriaBuilder.like(subRoot.get("monitors"), "%" + userUid + "%"),
                    criteriaBuilder.like(subRoot.get("assistants"), "%" + userUid + "%"),
                    criteriaBuilder.like(subRoot.get("ticketors"), "%" + userUid + "%"));

            List<Predicate> subPredicates = new ArrayList<>();
            subPredicates.add(criteriaBuilder.equal(subRoot.get("deleted"), false));
            subPredicates.add(criteriaBuilder.equal(subRoot.get("hide"), false));
            subPredicates.add(criteriaBuilder.equal(subRoot.get("topic"), root.get("topic")));
            subPredicates.add(subParticipatedPredicate);
            if (StringUtils.hasText(orgUid)) {
                subPredicates.add(
                        criteriaBuilder.or(
                                criteriaBuilder.equal(subRoot.get("orgUid"), orgUid),
                                criteriaBuilder.and(
                                        criteriaBuilder.isNull(subRoot.get("orgUid")),
                                        criteriaBuilder.equal(subRoot.get("level"), LevelEnum.USER.name()))));
            }

            maxDateSubquery.select(maxExpression)
                    .where(criteriaBuilder.and(subPredicates.toArray(new Predicate[0])));
            predicates.add(criteriaBuilder.equal(root.get("updatedAt"), maxDateSubquery));

            // 时间范围过滤（按 updatedAt）
            applyUpdatedAtRange(request, root, criteriaBuilder, predicates);

            // 基础筛选
            if (StringUtils.hasText(request.getType())) {
                predicates.add(criteriaBuilder.equal(root.get("type"), request.getType()));
            }

            if (StringUtils.hasText(request.getStatus())) {
                predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatus()));
            }

            if (StringUtils.hasText(request.getUid())) {
                predicates.add(criteriaBuilder.like(root.get("uid"), "%" + request.getUid() + "%"));
            }

            if (StringUtils.hasText(request.getTopic())) {
                predicates.add(criteriaBuilder.like(root.get("topic"), "%" + request.getTopic() + "%"));
            }

            if (StringUtils.hasText(request.getChannel())) {
                predicates.add(criteriaBuilder.equal(root.get("channel"), request.getChannel()));
            }

            if (StringUtils.hasText(request.getSearchText())) {
                String searchText = request.getSearchText();
                Predicate threadMatch = criteriaBuilder.or(
                        criteriaBuilder.like(root.get("content"), "%" + searchText + "%"),
                        criteriaBuilder.like(root.get("user"), "%" + searchText + "%"),
                        criteriaBuilder.like(root.get("topic"), "%" + searchText + "%"),
                        criteriaBuilder.like(root.get("uid"), "%" + searchText + "%"));
                Predicate messageMatch = buildMessageContentPredicate(request, searchText, root, query, criteriaBuilder);
                predicates.add(messageMatch == null ? threadMatch : criteriaBuilder.or(threadMatch, messageMatch));
            }

            // 兼容：若只传 messageSearchText（旧用法），仅按消息内容过滤
            if (!StringUtils.hasText(request.getSearchText()) && StringUtils.hasText(request.getMessageSearchText())) {
                Predicate messageOnly = buildMessageContentPredicate(request, request.getMessageSearchText(), root, query, criteriaBuilder);
                if (messageOnly != null) {
                    predicates.add(messageOnly);
                }
            }

            query.orderBy(criteriaBuilder.desc(root.get("updatedAt")));
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Visitor(匿名) 侧会话查询：
     * - 不依赖 AuthService（无登录态）
     * - 不做 orgUid/superUser 权限校验
     * - 通过 thread_user(JSON) 中包含 visitorUid 来过滤
     * - 与历史 native query 行为对齐：updatedAt 倒序
     */
    public static Specification<ThreadEntity> searchForVisitor(ThreadRequest request, String uid, String visitorUid) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));

            // 访客侧必须携带 orgUid，并按组织隔离，避免跨组织会话泄露
            if (!StringUtils.hasText(request.getOrgUid())) {
                return criteriaBuilder.disjunction();
            }
            predicates.add(criteriaBuilder.equal(root.get("orgUid"), request.getOrgUid()));

            // uid 与 visitorUid 都为空时不应返回任何数据（避免匿名全量查询）
            if (!StringUtils.hasText(uid) && !StringUtils.hasText(visitorUid)) {
                return criteriaBuilder.disjunction();
            }

            List<Predicate> identityPredicates = new ArrayList<>();
            if (StringUtils.hasText(uid)) {
                identityPredicates.add(criteriaBuilder.like(root.get("user"), "%" + uid + "%"));
            }
            if (StringUtils.hasText(visitorUid)) {
                identityPredicates.add(criteriaBuilder.like(root.get("user"), "%" + visitorUid + "%"));
            }
            predicates.add(criteriaBuilder.or(identityPredicates.toArray(new Predicate[0])));

            if (StringUtils.hasText(request.getType())) {
                predicates.add(criteriaBuilder.equal(root.get("type"), request.getType()));
            }

            if (StringUtils.hasText(request.getTopic())) {
                predicates.add(criteriaBuilder.like(root.get("topic"), "%" + request.getTopic() + "%"));
            }

            if (StringUtils.hasText(request.getChannel())) {
                predicates.add(criteriaBuilder.equal(root.get("channel"), request.getChannel()));
            }

            // 时间范围过滤（按 updatedAt）
            applyUpdatedAtRange(request, root, criteriaBuilder, predicates);

            // visitor 侧 searchText：对齐旧实现（topic 或 uid 模糊匹配）
            if (StringUtils.hasText(request.getSearchText())) {
                String searchText = request.getSearchText();
                Predicate threadMatch = criteriaBuilder.or(
                    criteriaBuilder.like(root.get("topic"), "%" + searchText + "%"),
                    criteriaBuilder.like(root.get("uid"), "%" + searchText + "%"));
                Predicate messageMatch = buildMessageContentPredicate(request, searchText, root, query, criteriaBuilder);
                predicates.add(messageMatch == null ? threadMatch : criteriaBuilder.or(threadMatch, messageMatch));
            }

            query.orderBy(criteriaBuilder.desc(root.get("updatedAt")));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }


    public static Specification<ThreadEntity> search(ThreadRequest request, AuthService authService) {
        // log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 基础过滤：deleted=false + 权限校验 + 组织过滤
            predicates.addAll(getBasicPredicates(root, criteriaBuilder, request, authService));

            // 时间范围过滤（按 updatedAt）
            applyUpdatedAtRange(request, root, criteriaBuilder, predicates);

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
            
            // type
            if (StringUtils.hasText(request.getType())) {
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

            // 状态查询
            if (StringUtils.hasText(request.getStatus())) {
                predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatus()));
            }

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

            // channel
            if (StringUtils.hasText(request.getChannel())) {
                predicates.add(criteriaBuilder.equal(root.get("channel"), request.getChannel()));
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

                // 同时搜索消息内容：message.content 命中则返回关联会话（并集）
                Predicate messageMatch = buildMessageContentPredicate(request, searchText, root, query, criteriaBuilder);
                if (messageMatch != null) {
                    orPredicates.add(messageMatch);
                }

                // 添加拼音搜索
                // orPredicates.add(criteriaBuilder.like(root.get("contentPinyin"), "%" + pinyinText + "%"));
                // orPredicates.add(criteriaBuilder.like(root.get("userPinyin"), "%" + pinyinText + "%"));

                predicates.add(criteriaBuilder.or(orPredicates.toArray(new Predicate[0])));
            }

            // 兼容：若只传 messageSearchText（旧用法），仅按消息内容过滤
            if (!StringUtils.hasText(request.getSearchText()) && StringUtils.hasText(request.getMessageSearchText())) {
                Predicate messageOnly = buildMessageContentPredicate(request, request.getMessageSearchText(), root, query, criteriaBuilder);
                if (messageOnly != null) {
                    predicates.add(messageOnly);
                }
            }
            
            //
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
