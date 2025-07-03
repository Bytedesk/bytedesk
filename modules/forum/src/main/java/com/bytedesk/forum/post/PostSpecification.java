package com.bytedesk.forum.post;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PostSpecification {
    
    public static Specification<PostEntity> searchByCriteria(PostSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 关键词搜索（标题和内容）
            if (criteria.getKeyword() != null && !criteria.getKeyword().isEmpty()) {
                String keyword = "%" + criteria.getKeyword() + "%";
                predicates.add(cb.or(
                    cb.like(root.get("title"), keyword),
                    cb.like(root.get("content"), keyword)
                ));
            }
            
            // 分类搜索
            if (criteria.getCategoryId() != null) {
                predicates.add(cb.equal(root.get("category").get("id"), criteria.getCategoryId()));
            }
            
            // 用户搜索
            if (criteria.getUserId() != null) {
                predicates.add(cb.equal(root.get("userId"), criteria.getUserId()));
            }
            
            // 状态搜索
            if (criteria.getStatus() != null && !criteria.getStatus().isEmpty()) {
                predicates.add(cb.equal(root.get("status"), criteria.getStatus()));
            }
            
            // 日期范围搜索
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            if (criteria.getStartDate() != null && !criteria.getStartDate().isEmpty()) {
                ZonedDateTime startDate = ZonedDateTime.parse(criteria.getStartDate(), formatter);
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), startDate));
            }
            
            if (criteria.getEndDate() != null && !criteria.getEndDate().isEmpty()) {
                ZonedDateTime endDate = ZonedDateTime.parse(criteria.getEndDate(), formatter);
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), endDate));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
} 