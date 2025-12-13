/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-12-11
 * @Description: Specification builder for quick button queries
 */
package com.bytedesk.kbase.quick_button;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import com.bytedesk.core.rbac.auth.AuthService;

import jakarta.persistence.criteria.Predicate;

public class QuickButtonSpecification extends BaseSpecification<QuickButtonEntity, QuickButtonRequest> {

    public static Specification<QuickButtonEntity> search(QuickButtonRequest request, AuthService authService) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.addAll(getBasicPredicates(root, cb, request, authService));

            if (StringUtils.hasText(request.getTitle())) {
                predicates.add(cb.like(root.get("title"), "%" + request.getTitle() + "%"));
            }
            if (StringUtils.hasText(request.getType())) {
                predicates.add(cb.equal(root.get("type"), request.getType()));
            }
            if (request.getEnabled() != null) {
                predicates.add(cb.equal(root.get("enabled"), request.getEnabled()));
            }
            if (StringUtils.hasText(request.getKbUid())) {
                predicates.add(cb.equal(root.get("kbUid"), request.getKbUid()));
            }
            if (StringUtils.hasText(request.getSearchText())) {
                String search = "%" + request.getSearchText() + "%";
                predicates.add(cb.or(
                    cb.like(root.get("title"), search),
                    cb.like(root.get("description"), search),
                    cb.like(root.get("content"), search)
                ));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
