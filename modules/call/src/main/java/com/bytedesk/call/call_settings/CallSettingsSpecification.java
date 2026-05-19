package com.bytedesk.call.call_settings;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import com.bytedesk.core.rbac.auth.AuthService;

import jakarta.persistence.criteria.Predicate;

public class CallSettingsSpecification extends BaseSpecification<CallSettingsEntity, CallSettingsRequest> {

    private CallSettingsSpecification() {
    }

    public static Specification<CallSettingsEntity> search(CallSettingsRequest request, AuthService authService) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.addAll(getBasicPredicates(root, criteriaBuilder, request, authService));

            if (StringUtils.hasText(request.getOrgUid())) {
                predicates.add(criteriaBuilder.equal(root.get("orgUid"), request.getOrgUid()));
            }

            if (StringUtils.hasText(request.getAgentUid())) {
                predicates.add(criteriaBuilder.equal(root.get("agentUid"), request.getAgentUid().trim()));
            }

            if (request.getEnabled() != null) {
                predicates.add(criteriaBuilder.equal(root.get("enabled"), request.getEnabled()));
            }

            if (StringUtils.hasText(request.getNumber())) {
                predicates.add(criteriaBuilder.like(root.get("number"), "%" + request.getNumber().trim() + "%"));
            }

            if (StringUtils.hasText(request.getDisplayName())) {
                predicates.add(criteriaBuilder.like(root.get("displayName"), "%" + request.getDisplayName().trim() + "%"));
            }

            if (StringUtils.hasText(request.getTarget())) {
                predicates.add(criteriaBuilder.like(root.get("target"), "%" + request.getTarget().trim() + "%"));
            }

            if (StringUtils.hasText(request.getSearchText())) {
                String keyword = "%" + request.getSearchText().trim() + "%";
                predicates.add(
                    criteriaBuilder.or(
                        criteriaBuilder.like(root.get("agentUid"), keyword),
                        criteriaBuilder.like(root.get("number"), keyword),
                        criteriaBuilder.like(root.get("displayName"), keyword),
                        criteriaBuilder.like(root.get("target"), keyword)
                    )
                );
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
