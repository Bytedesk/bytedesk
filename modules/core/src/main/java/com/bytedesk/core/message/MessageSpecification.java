/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-05 22:53:57
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-08 09:10:36
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

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class MessageSpecification extends BaseSpecification {

    public static Specification<MessageEntity> search(MessageRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            // predicates.addAll(getBasicPredicates(root, criteriaBuilder, request.getOrgUid()));
            
            // 获取thread关联，用于访问thread的属性
            Join<Object, Object> threadJoin = root.join("thread", JoinType.LEFT);
            
            if (StringUtils.hasText(request.getComponentType())) {
                // 
                if (TypeConsts.COMPONENT_TYPE_TEAM.equals(request.getComponentType())) {
                    // thread.topic like '%group%' or thread.topic like '%member%'
                    predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(threadJoin.get("topic"), "%group%"),
                        criteriaBuilder.like(threadJoin.get("topic"), "%member%")
                    ));
                } else if (TypeConsts.COMPONENT_TYPE_SERVICE.equals(request.getComponentType())) {
                    // thread.topic like '%agent%' or thread.topic like '%workgroup%'
                    predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(threadJoin.get("topic"), "%agent%"),
                        criteriaBuilder.like(threadJoin.get("topic"), "%workgroup%")
                    ));
                } else if (TypeConsts.COMPONENT_TYPE_ROBOT.equals(request.getComponentType())) {
                    // thread.topic like '%robot%'
                    predicates.add(criteriaBuilder.like(threadJoin.get("topic"), "%robot%"));
                } else if (TypeConsts.COMPONENT_TYPE_VISITOR.equals(request.getComponentType())) {
                    // 访客端查询消息：过滤掉一些消息类型，比如：TRANSFER, TRANSFER_ACCEPT, TRANSFER_REJECT
                    predicates.add(criteriaBuilder.notEqual(root.get("type"), MessageTypeEnum.TRANSFER.name()));
                    predicates.add(criteriaBuilder.notEqual(root.get("type"), MessageTypeEnum.TRANSFER_ACCEPT.name()));
                    predicates.add(criteriaBuilder.notEqual(root.get("type"), MessageTypeEnum.TRANSFER_REJECT.name()));
                    predicates.add(criteriaBuilder.notEqual(root.get("type"), MessageTypeEnum.TRANSFER_TIMEOUT.name()));
                    predicates.add(criteriaBuilder.notEqual(root.get("type"), MessageTypeEnum.TRANSFER_CANCEL.name()));
                    // 过滤掉 INVITE, INVITE_ACCEPT, INVITE_REJECT
                    predicates.add(criteriaBuilder.notEqual(root.get("type"), MessageTypeEnum.INVITE.name()));
                    predicates.add(criteriaBuilder.notEqual(root.get("type"), MessageTypeEnum.INVITE_ACCEPT.name()));
                    predicates.add(criteriaBuilder.notEqual(root.get("type"), MessageTypeEnum.INVITE_REJECT.name()));
                    predicates.add(criteriaBuilder.notEqual(root.get("type"), MessageTypeEnum.INVITE_TIMEOUT.name()));
                    predicates.add(criteriaBuilder.notEqual(root.get("type"), MessageTypeEnum.INVITE_CANCEL.name()));
                    // MESSAGE_TYPE_NOTIFICATION_AGENT_REPLY_TIMEOUT
                    predicates.add(criteriaBuilder.notEqual(root.get("type"), MessageTypeEnum.NOTIFICATION_AGENT_REPLY_TIMEOUT.name()));
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
                Predicate topicPredicate = criteriaBuilder.equal(threadJoin.get("topic"), topic);
                if (TopicUtils.isOrgMemberTopic(topic)) {
                    String reverseTopic = TopicUtils.getOrgMemberTopicReverse(topic);
                    Predicate reverseTopicPredicate = criteriaBuilder.equal(threadJoin.get("topic"), reverseTopic);
                    predicates.add(criteriaBuilder.or(topicPredicate, reverseTopicPredicate));
                } else {
                    predicates.add(topicPredicate);
                }
            }
            // threadUid 替换为 thread.uid
            if (StringUtils.hasText(request.getThreadUid())) {
                predicates.add(criteriaBuilder.equal(threadJoin.get("uid"), request.getThreadUid()));
            }
            // threadType 替换为 thread.type
            if (StringUtils.hasText(request.getThreadType())) {
                predicates.add(criteriaBuilder.equal(threadJoin.get("type"), request.getThreadType()));
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
