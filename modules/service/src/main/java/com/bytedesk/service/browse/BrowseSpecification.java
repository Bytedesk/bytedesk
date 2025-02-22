package com.bytedesk.service.browse;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.bytedesk.core.base.BaseSpecification;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BrowseSpecification extends BaseSpecification {
    
    public static Specification<BrowseEntity> search(BrowseRequest request) {
        log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.addAll(getBasicPredicates(root, criteriaBuilder, request.getOrgUid()));
            // 

            // 
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

