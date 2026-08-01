package com.bytedesk.kbase.translation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import com.bytedesk.core.rbac.auth.AuthService;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KbaseTranslationSpecification extends BaseSpecification<KbaseTranslationEntity, KbaseTranslationRequest> {

    public static Specification<KbaseTranslationEntity> search(KbaseTranslationRequest request, AuthService authService) {
        log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.addAll(getBasicPredicates(root, criteriaBuilder, request, authService));

            if (StringUtils.hasText(request.getKbUid())) {
                predicates.add(criteriaBuilder.equal(root.get("kbase").get("uid"), request.getKbUid()));
            }
            if (StringUtils.hasText(request.getSourceUid())) {
                predicates.add(criteriaBuilder.equal(root.get("sourceUid"), request.getSourceUid()));
            }
            if (StringUtils.hasText(request.getSourceType())) {
                predicates.add(criteriaBuilder.equal(root.get("sourceType"), request.getSourceType()));
            }
            if (request.getSourceTypes() != null && !request.getSourceTypes().isEmpty()) {
                List<String> normalizedSourceTypes = request.getSourceTypes().stream()
                        .filter(StringUtils::hasText)
                        .map(value -> value.trim().toUpperCase(Locale.ROOT))
                        .toList();
                if (!normalizedSourceTypes.isEmpty()) {
                    predicates.add(root.get("sourceType").in(normalizedSourceTypes));
                }
            }
            if (StringUtils.hasText(request.getSourceLanguage())) {
                predicates.add(criteriaBuilder.equal(root.get("sourceLanguage"), request.getSourceLanguage()));
            }
            if (StringUtils.hasText(request.getTargetLanguage())) {
                predicates.add(criteriaBuilder.equal(root.get("targetLanguage"), request.getTargetLanguage()));
            }
            if (StringUtils.hasText(request.getTitle())) {
                predicates.add(criteriaBuilder.like(root.get("title"), "%" + request.getTitle() + "%"));
            }
            if (StringUtils.hasText(request.getContent())) {
                predicates.add(criteriaBuilder.like(root.get("content"), "%" + request.getContent() + "%"));
            }
            if (StringUtils.hasText(request.getTranslateStatus())) {
                predicates.add(criteriaBuilder.equal(root.get("translateStatus"), request.getTranslateStatus()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}