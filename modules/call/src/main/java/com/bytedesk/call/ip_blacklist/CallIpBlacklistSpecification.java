package com.bytedesk.call.ip_blacklist;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import com.bytedesk.core.rbac.auth.AuthService;

import jakarta.persistence.criteria.Predicate;

public class CallIpBlacklistSpecification extends BaseSpecification<CallIpBlacklistEntity, CallIpBlacklistRequest> {

    private CallIpBlacklistSpecification() {
    }

    public static Specification<CallIpBlacklistEntity> search(CallIpBlacklistRequest request, AuthService authService) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.addAll(getBasicPredicates(root, criteriaBuilder, request, authService));

            if (!Boolean.TRUE.equals(request.getSuperUser()) && StringUtils.hasText(request.getOrgUid())) {
                predicates.add(criteriaBuilder.equal(root.get("orgUid"), request.getOrgUid().trim()));
            }

            if (StringUtils.hasText(request.getIpAddress())) {
                predicates.add(criteriaBuilder.like(root.get("ipAddress"), "%" + request.getIpAddress().trim() + "%"));
            }

            if (StringUtils.hasText(request.getSourceEslEventUid())) {
                predicates.add(criteriaBuilder.equal(root.get("sourceEslEventUid"), request.getSourceEslEventUid().trim()));
            }

            if (StringUtils.hasText(request.getEventName())) {
                predicates.add(criteriaBuilder.like(root.get("eventName"), "%" + request.getEventName().trim() + "%"));
            }

            if (StringUtils.hasText(request.getCallerNumber())) {
                predicates.add(criteriaBuilder.like(root.get("callerNumber"), "%" + request.getCallerNumber().trim() + "%"));
            }

            if (StringUtils.hasText(request.getReason())) {
                predicates.add(criteriaBuilder.like(root.get("reason"), "%" + request.getReason().trim() + "%"));
            }

            if (StringUtils.hasText(request.getSearchText())) {
                String keyword = "%" + request.getSearchText().trim() + "%";
                predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(root.get("ipAddress"), keyword),
                    criteriaBuilder.like(root.get("sourceEslEventUid"), keyword),
                    criteriaBuilder.like(root.get("eventName"), keyword),
                    criteriaBuilder.like(root.get("callerNumber"), keyword),
                    criteriaBuilder.like(root.get("reason"), keyword)
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}