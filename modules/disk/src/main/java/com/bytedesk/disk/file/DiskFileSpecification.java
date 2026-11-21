package com.bytedesk.disk.file;

import com.bytedesk.core.base.BaseSpecification;
import com.bytedesk.core.rbac.auth.AuthService;

import jakarta.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class DiskFileSpecification extends BaseSpecification<DiskFileEntity, DiskFileRequest> {

    public static Specification<DiskFileEntity> search(DiskFileRequest request, AuthService authService) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            predicates.addAll(getBasicPredicates(root, cb, request, authService));

            // 回收站查询传 deleted=true，其余只查活跃节点
            if (Boolean.TRUE.equals(request.getDeleted())) {
                predicates.add(cb.isTrue(root.get("deleted")));
            } else {
                predicates.add(cb.isFalse(root.get("deleted")));
            }

            if (request.getSpaceUid() != null && !request.getSpaceUid().isEmpty()) {
                predicates.add(cb.equal(root.get("spaceUid"), request.getSpaceUid()));
            }

            if (StringUtils.hasText(request.getKeyword())) {
                predicates.add(cb.like(root.get("name"), "%" + request.getKeyword() + "%"));
            }

            // 类型过滤：分类编码（如 IMAGE）展开为该分类全部扩展名，也兼容单个/逗号分隔扩展名
            List<String> extensions = DiskFileTypeEnum.resolveExtensions(request.getFileType());
            if (!extensions.isEmpty()) {
                predicates.add(root.get("fileType").in(extensions));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
