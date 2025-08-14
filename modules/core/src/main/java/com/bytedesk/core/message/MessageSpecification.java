/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-05 22:53:57
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-14 17:17:34
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
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.topic.TopicUtils;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import com.bytedesk.core.rbac.user.UserEntity;

public class MessageSpecification extends BaseSpecification<MessageEntity, MessageRequest> {

    public static Specification<MessageEntity> search(MessageRequest request, AuthService authService) {
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
                    predicates.add(criteriaBuilder.notEqual(root.get("type"), MessageTypeEnum.NOTICE.name()));
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
                    predicates.add(criteriaBuilder.notEqual(root.get("type"), MessageTypeEnum.NOTIFICATION_RATE_SUBMITTED.name()));
                }
            }
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
            // 使用基类方法处理超级管理员权限和组织过滤
            addOrgFilterIfNotSuperUser(root, criteriaBuilder, predicates, request, authService);
            //
            if (StringUtils.hasText(request.getContent())) {
                predicates.add(criteriaBuilder.like(root.get("content"), "%" + request.getContent() + "%"));
            }
            // 
            if (StringUtils.hasText(request.getTopic())) {
                String topic = request.getTopic();
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
            // messageType
            if (StringUtils.hasText(request.getType())) {
                predicates.add(criteriaBuilder.equal(root.get("type"), request.getType()));
            }
            // searchText
            if (StringUtils.hasText(request.getSearchText())) {
                predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(root.get("content"), "%" + request.getSearchText() + "%"),
                    criteriaBuilder.like(root.get("user"), "%" + request.getSearchText() + "%")
                ));
            }
            //
            if (StringUtils.hasText(request.getChannel())) {
                predicates.add(criteriaBuilder.like(root.get("client"), "%" + request.getChannel() + "%"));
            }
            //
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * 查询未读消息的 Specification
     * 参考 ThreadEntity.getUnreadCount 的逻辑
     * 
     * @param request 请求对象
     * @param user 当前用户
     * @return Specification对象
     */
    public static Specification<MessageEntity> searchUnread(MessageRequest request, UserEntity user) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 获取 thread 关联，用于访问 thread 的属性
            Join<Object, Object> threadJoin = root.join("thread", JoinType.LEFT);
            
            // 基础条件：未删除的消息
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
            
            // 根据 topic 过滤
            if (StringUtils.hasText(request.getTopic())) {
                String topic = request.getTopic();
                Predicate topicPredicate = criteriaBuilder.equal(threadJoin.get("topic"), topic);
                predicates.add(topicPredicate);
            }
            
            // 未读消息条件：根据消息状态判断
            // 参考 MessageEntity.isUnread() 方法
            predicates.add(criteriaBuilder.or(
                criteriaBuilder.equal(root.get("status"), MessageStatusEnum.SENDING.name()),
                criteriaBuilder.equal(root.get("status"), MessageStatusEnum.SUCCESS.name()),
                criteriaBuilder.equal(root.get("status"), MessageStatusEnum.DELIVERED.name())
            ));
            
            // 根据用户类型和会话类型过滤未读消息
            // 参考 ThreadEntity.getUnreadCount 的逻辑
            String threadType = getThreadTypeFromTopic(request.getTopic());
            if (isCustomerServiceType(threadType)) {
                // 客服端：查询访客发送的未读消息
                predicates.add(criteriaBuilder.like(root.get("user"), "%\"type\":\"VISITOR\"%"));
            } else if (isMemberType(threadType)) {
                // 成员端：查询其他成员发送的未读消息（排除自己）, TODO: 需要修改逻辑
                predicates.add(criteriaBuilder.like(root.get("user"), "%\"type\":\"MEMBER\"%"));
                predicates.add(criteriaBuilder.not(criteriaBuilder.like(root.get("user"), "%" + user.getUid() + "%")));
            } else if (isGroupType(threadType)) {
                // 群聊：查询其他成员发送的未读消息（排除自己）, TODO: 需要修改逻辑
                predicates.add(criteriaBuilder.not(criteriaBuilder.like(root.get("user"), "%" + user.getUid() + "%")));
            }
            
            // 组织过滤
            if (StringUtils.hasText(request.getOrgUid())) {
                predicates.add(criteriaBuilder.equal(root.get("orgUid"), request.getOrgUid()));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * 从 topic 推断会话类型
     */
    private static String getThreadTypeFromTopic(String topic) {
        if (topic == null) {
            return null;
        }
        
        if (topic.contains("group")) {
            return "GROUP";
        } else if (topic.contains("member")) {
            return "MEMBER";
        } else if (topic.contains("agent") || topic.contains("workgroup") || topic.contains("robot")) {
            return "AGENT";
        }
        
        return null;
    }

    /**
     * 判断是否为客服类型会话
     */
    private static boolean isCustomerServiceType(String threadType) {
        return "AGENT".equals(threadType) || "WORKGROUP".equals(threadType) || 
               "ROBOT".equals(threadType) || "UNIFIED".equals(threadType);
    }

    /**
     * 判断是否为成员类型会话
     */
    private static boolean isMemberType(String threadType) {
        return "MEMBER".equals(threadType);
    }

    /**
     * 判断是否为群聊类型会话
     */
    private static boolean isGroupType(String threadType) {
        return "GROUP".equals(threadType);
    }
}
