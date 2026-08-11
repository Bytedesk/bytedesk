package com.bytedesk.ai.rag_rewrite;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import com.bytedesk.core.rbac.auth.AuthService;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RagRewriteSpecification extends BaseSpecification<RagRewriteEntity, RagRewriteRequest> {

    public static Specification<RagRewriteEntity> search(RagRewriteRequest request, AuthService authService) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.addAll(getBasicPredicatesWithLevel(root, criteriaBuilder, request, authService,
                    RagRewritePermissions.MODULE_NAME));
            if (StringUtils.hasText(request.getRobotUid())) {
                predicates.add(criteriaBuilder.equal(root.get("robotUid"), request.getRobotUid()));
            }
            if (StringUtils.hasText(request.getThreadTopic())) {
                predicates.add(criteriaBuilder.equal(root.get("threadTopic"), request.getThreadTopic()));
            }
            if (StringUtils.hasText(request.getMessageUid())) {
                predicates.add(criteriaBuilder.equal(root.get("messageUid"), request.getMessageUid()));
            }
            if (StringUtils.hasText(request.getRewriteType())) {
                predicates.add(criteriaBuilder.equal(root.get("rewriteType"), request.getRewriteType()));
            }
            if (StringUtils.hasText(request.getStatus())) {
                predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatus()));
            }
            if (StringUtils.hasText(request.getOriginalQuery())) {
                predicates.add(criteriaBuilder.like(root.get("originalQuery"), "%" + request.getOriginalQuery() + "%"));
            }
            if (request.getFallbackUsed() != null) {
                predicates.add(criteriaBuilder.equal(root.get("fallbackUsed"), request.getFallbackUsed()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
