/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-19 11:16:21
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message;

import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * Message entity for storing individual chat messages
 * Represents a single message within a conversation thread
 * 
 * Database Table: bytedesk_core_message
 * Purpose: Stores message content, sender information, and message metadata
 */
@Entity
@Data
@SuperBuilder
@NoArgsConstructor // 添加无参构造函数
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@EntityListeners({ MessageEntityListener.class })
@Table(name = "bytedesk_core_message")
public class MessageEntity extends AbstractMessageEntity {

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
