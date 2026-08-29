/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-28 10:41:23
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-28 10:41:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.email_template;

import com.bytedesk.core.base.BaseSpecification;
import com.bytedesk.core.rbac.auth.AuthService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;

public class EmailTemplateSpecification extends BaseSpecification<EmailTemplateEntity, EmailTemplateRequest> {
	
	public static Specification<EmailTemplateEntity> search(EmailTemplateRequest request, AuthService authService) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			predicates.addAll(getBasicPredicates(root, cb, request, authService));
			// name
			if (StringUtils.hasText(request.getName())) {
				predicates.add(cb.like(root.get("name"), "%" + request.getName() + "%"));
			}
			// subject 邮件主题
			if (StringUtils.hasText(request.getSubject())) {
				predicates.add(cb.like(root.get("subject"), "%" + request.getSubject() + "%"));
			}
			// templateType 模板类型
			if (StringUtils.hasText(request.getTemplateType())) {
				predicates.add(cb.equal(root.get("templateType"), request.getTemplateType()));
			}
			// status 状态
			if (StringUtils.hasText(request.getStatus())) {
				predicates.add(cb.equal(root.get("status"), request.getStatus()));
			}
			// searchText across name/content
			if (StringUtils.hasText(request.getSearchText())) {
				String kw = request.getSearchText();
				List<Predicate> orPreds = new ArrayList<>();
				orPreds.add(cb.like(root.get("name"), "%" + kw + "%"));
				orPreds.add(cb.like(root.get("content"), "%" + kw + "%"));
				predicates.add(cb.or(orPreds.toArray(new Predicate[0])));
			}
			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}
}
