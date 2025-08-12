/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-28 17:15:48
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-08 16:34:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message_unread;

import com.bytedesk.core.message.AbstractMessageEntity;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 存储未读消息，减少message表查询压力
 * 缓存每个用户该接收的消息，自己发送的消息自己不缓存
 * 收到客户端的回执receipt后，删除该条缓存记录
 * 客户端拉取之后，从表中删除该条记录
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Accessors(chain = true)
@NoArgsConstructor
@Table(name = "bytedesk_core_message_unread")
public class MessageUnreadEntity extends AbstractMessageEntity  {

    private static final long serialVersionUID = 1L;

    /**
     * Associated conversation thread containing this message
     * Many-to-one relationship: multiple messages can belong to one thread
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thread_id", referencedColumnName = "id")
    @JsonBackReference
    private ThreadEntity thread;

    // 可以在这里添加 MessageEntity 特有的字段（如果有的话）
    public UserProtobuf getUserProtobuf() {
        return UserProtobuf.fromJson(getUser());
    }

    // 通过解析user字段中的type字段来判断 type=robot则为机器人，否则为访客
    public Boolean isFromRobot() {
        return getUserProtobuf().isRobot();
    }

    // 通过解析user字段中的type字段来判断 type=visitor则为访客，否则为客服
    public Boolean isFromVisitor() {
        return getUserProtobuf().isVisitor();
    }

    public Boolean isFromUser() {
        return getUserProtobuf().isUser();
    }

    public Boolean isFromMember() {
        return getUserProtobuf().isMember();
    }

    // 是否系统消息
    public Boolean isFromSystem() {
        return getUserProtobuf().isSystem();
    }

    // 是否客服消息
    public Boolean isFromAgent() {
        return getUserProtobuf().isAgent();
    }

    /**
     * 重写toString方法避免循环引用
     */
    @Override
    public String toString() {
        return "MessageEntity{" +
                "id=" + getId() +
                ", uid='" + getUid() + '\'' +
                ", type='" + getType() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", createdAt=" + getCreatedAt() +
                ", userUid='" + getUserUid() + '\'' +
                '}';
    }
}
