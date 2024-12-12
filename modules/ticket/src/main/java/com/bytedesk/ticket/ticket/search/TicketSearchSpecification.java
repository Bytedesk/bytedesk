package com.bytedesk.ticket.ticket.search;

import com.bytedesk.ticket.ticket.TicketEntity;
import com.bytedesk.ticket.ticket.dto.TicketSearchRequest;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import java.util.ArrayList;
import java.util.List;

public class TicketSearchSpecification {

    public static Specification<TicketEntity> buildSpecification(TicketSearchRequest request) {
        return new Specification<TicketEntity>() {
            @Override
            public Predicate toPredicate(Root<TicketEntity> root, CriteriaQuery<?> query, 
                    CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                
                // 关键词搜索
                if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
                    String keyword = "%" + request.getKeyword().toLowerCase() + "%";
                    predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), keyword),
                        cb.like(cb.lower(root.get("content")), keyword),
                        cb.like(cb.lower(root.get("tags")), keyword)
                    ));
                }
                
                // 分类过滤
                if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
                    predicates.add(root.get("categoryId").in(request.getCategoryIds()));
                }
                
                // 状态过滤
                if (request.getStatuses() != null && !request.getStatuses().isEmpty()) {
                    predicates.add(root.get("status").in(request.getStatuses()));
                }
                
                // 优先级过滤
                if (request.getPriorities() != null && !request.getPriorities().isEmpty()) {
                    predicates.add(root.get("priority").in(request.getPriorities()));
                }
                
                // 处理人过滤
                if (request.getAssignedTo() != null) {
                    predicates.add(cb.equal(root.get("assignedTo"), request.getAssignedTo()));
                }
                
                // 提交人过滤
                if (request.getUserId() != null) {
                    predicates.add(cb.equal(root.get("userId"), request.getUserId()));
                }
                
                // 时间范围过滤
                if (request.getStartTime() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(
                        root.get("createdAt"), request.getStartTime()));
                }
                if (request.getEndTime() != null) {
                    predicates.add(cb.lessThanOrEqualTo(
                        root.get("createdAt"), request.getEndTime()));
                }
                
                // 标签过滤
                if (request.getTags() != null && !request.getTags().isEmpty()) {
                    List<Predicate> tagPredicates = new ArrayList<>();
                    for (String tag : request.getTags()) {
                        tagPredicates.add(cb.like(root.get("tags"), "%" + tag + "%"));
                    }
                    predicates.add(cb.or(tagPredicates.toArray(new Predicate[0])));
                }
                
                // 逾期过滤
                if (Boolean.TRUE.equals(request.getOverdue())) {
                    predicates.add(cb.and(
                        cb.isNotNull(root.get("dueDate")),
                        cb.lessThan(root.get("dueDate"), cb.currentTimestamp()),
                        cb.notEqual(root.get("status"), "closed")
                    ));
                }
                
                // 未分配过滤
                if (Boolean.TRUE.equals(request.getUnassigned())) {
                    predicates.add(cb.isNull(root.get("assignedTo")));
                }
                
                // 组合所有条件
                return cb.and(predicates.toArray(new Predicate[0]));
            }
        };
    }
} 