/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-05 22:53:57
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-05 11:22:36
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import com.bytedesk.core.topic.TopicUtils;

import jakarta.persistence.criteria.Predicate;

public class MessageSpecification extends BaseSpecification {

    public static Specification<MessageEntity> search(MessageRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            // predicates.addAll(getBasicPredicates(root, criteriaBuilder, request.getOrgUid()));
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
            // 
            if (StringUtils.hasText(request.getOrgUid())) {
                predicates.add(criteriaBuilder.equal(root.get("orgUid"), request.getOrgUid()));
            }
            //
            if (StringUtils.hasText(request.getContent())) {
                predicates.add(criteriaBuilder.like(root.get("content"), "%" + request.getContent() + "%"));
            }
            // 
            String topic = request.getThreadTopic();
            if (StringUtils.hasText(topic)) {
                Predicate topicPredicate = criteriaBuilder.equal(root.get("threadTopic"), topic);
                if (TopicUtils.isOrgMemberTopic(topic)) {
                    String reverseTopic = TopicUtils.getOrgMemberTopicReverse(topic);
                    Predicate reverseTopicPredicate = criteriaBuilder.equal(root.get("threadTopic"), reverseTopic);
                    predicates.add(criteriaBuilder.or(topicPredicate, reverseTopicPredicate));
                } else {
                    predicates.add(topicPredicate);
                }
            }
            //
            if (StringUtils.hasText(request.getClient())) {
                predicates.add(criteriaBuilder.like(root.get("client"), "%" + request.getClient() + "%"));
            }
            //
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
