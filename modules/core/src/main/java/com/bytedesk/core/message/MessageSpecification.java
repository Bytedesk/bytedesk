/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-05 22:53:57
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-27 14:53:47
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
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.topic.TopicUtils;

import jakarta.persistence.criteria.Predicate;

public class MessageSpecification extends BaseSpecification {

    public static Specification<MessageEntity> search(MessageRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            // predicates.addAll(getBasicPredicates(root, criteriaBuilder, request.getOrgUid()));
            // 
            if (StringUtils.hasText(request.getComponentType())) {
                // 
                if (TypeConsts.COMPONENT_TYPE_TEAM.equals(request.getComponentType())) {
                    // topic like '%group%' or topic like '%member%'
                    predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(root.get("topic"), "%group%"),
                        criteriaBuilder.like(root.get("topic"), "%member%")
                    ));
                } else if (TypeConsts.COMPONENT_TYPE_SERVICE.equals(request.getComponentType())) {
                    // topic like '%agent%' or topic like '%workgroup%'
                    predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(root.get("topic"), "%agent%"),
                        criteriaBuilder.like(root.get("topic"), "%workgroup%")
                    ));
                } else if (TypeConsts.COMPONENT_TYPE_ROBOT.equals(request.getComponentType())) {
                    // topic like '%robot%'
                    predicates.add(criteriaBuilder.like(root.get("topic"), "%robot%"));
                } else if (TypeConsts.COMPONENT_TYPE_VISITOR.equals(request.getComponentType())) {
                    // 访客端查询消息：过滤掉一些消息类型，比如：TRANSFER, TRANSFER_ACCEPT, TRANSFER_REJECT
                    predicates.add(criteriaBuilder.notEqual(root.get("type"), MessageTypeEnum.TRANSFER.name()));
                    predicates.add(criteriaBuilder.notEqual(root.get("type"), MessageTypeEnum.TRANSFER_ACCEPT.name()));
                    predicates.add(criteriaBuilder.notEqual(root.get("type"), MessageTypeEnum.TRANSFER_REJECT.name()));
                    // 过滤掉 INVITE, INVITE_ACCEPT, INVITE_REJECT
                    predicates.add(criteriaBuilder.notEqual(root.get("type"), MessageTypeEnum.INVITE.name()));
                    predicates.add(criteriaBuilder.notEqual(root.get("type"), MessageTypeEnum.INVITE_ACCEPT.name()));
                    predicates.add(criteriaBuilder.notEqual(root.get("type"), MessageTypeEnum.INVITE_REJECT.name()));
                }
            }
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
            String topic = request.getTopic();
            if (StringUtils.hasText(topic)) {
                Predicate topicPredicate = criteriaBuilder.equal(root.get("topic"), topic);
                if (TopicUtils.isOrgMemberTopic(topic)) {
                    String reverseTopic = TopicUtils.getOrgMemberTopicReverse(topic);
                    Predicate reverseTopicPredicate = criteriaBuilder.equal(root.get("topic"), reverseTopic);
                    predicates.add(criteriaBuilder.or(topicPredicate, reverseTopicPredicate));
                } else {
                    predicates.add(topicPredicate);
                }
            }
            // threadUid
            if (StringUtils.hasText(request.getThreadUid())) {
                predicates.add(criteriaBuilder.equal(root.get("threadUid"), request.getThreadUid()));
            }
            // user.nickname
            if (StringUtils.hasText(request.getNickname())) {
                predicates.add(criteriaBuilder.like(root.get("user"), "%" + request.getNickname() + "%"));
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
