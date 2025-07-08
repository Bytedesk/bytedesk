/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-08 17:03:58
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.thread;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserTypeEnum;
import com.bytedesk.core.utils.ConvertUtils;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * Conversation thread entity for managing chat sessions
 * Represents a series of related messages arranged chronologically to form a coherent communication thread
 * Used for online forums, email threads, social media, and customer service conversations
 * 
 * Database Table: bytedesk_core_thread
 * Purpose: Stores conversation threads, participant information, and message history
 * 
 * Note: Each visitor to agent thread should be unique, with history records stored in thread_log table
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true, exclude = { "messages" })
@NoArgsConstructor
@EntityListeners({ ThreadEntityListener.class })
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "messages"})
@Table(name = "bytedesk_core_thread", indexes = {
    @Index(name = "idx_thread_topic", columnList = "thread_topic")
}
)
public class ThreadEntity extends AbstractThreadEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Messages associated with this conversation thread
     * One-to-many relationship: one thread can have multiple messages
     */
    @Builder.Default
    @OneToMany(mappedBy = "thread", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<MessageEntity> messages = new ArrayList<>();

    public Boolean isNew() {
        return ThreadProcessStatusEnum.NEW.name().equals(getStatus());
    }

    // ROBOTING
    public Boolean isRoboting() {
        return ThreadProcessStatusEnum.ROBOTING.name().equals(getStatus());
    }

    // LLMING
    public Boolean isLlmIng() {
        return ThreadProcessStatusEnum.LLMING.name().equals(getStatus());
    }

    // queuing
    public Boolean isQueuing() {
        return ThreadProcessStatusEnum.QUEUING.name().equals(getStatus());
    }

    // is offline
    public Boolean isOffline() {
        return ThreadProcessStatusEnum.OFFLINE.name().equals(getStatus());
    }

    public Boolean isChatting() {
        return ThreadProcessStatusEnum.CHATTING.name().equals(getStatus());
    }

    // TIMEOUT
    public Boolean isTimeout() {
        return ThreadProcessStatusEnum.TIMEOUT.name().equals(getStatus());
    }

    //
    public Boolean isClosed() {
        return ThreadProcessStatusEnum.CLOSED.name().equals(getStatus());
    }
    
    public Boolean isCustomerService() {
        return ThreadTypeEnum.AGENT.name().equals(getType())
                || ThreadTypeEnum.WORKGROUP.name().equals(getType())
                || ThreadTypeEnum.ROBOT.name().equals(getType())
                || ThreadTypeEnum.UNIFIED.name().equals(getType());
    }

    public Boolean isRobotType() {
        return ThreadTypeEnum.ROBOT.name().equals(getType());
    }

    public Boolean isWorkgroupType() {
        return ThreadTypeEnum.WORKGROUP.name().equals(getType());
    }

    public Boolean isAgentType() {
        return ThreadTypeEnum.AGENT.name().equals(getType());
    }

    public Boolean isUnifiedType() {
        return ThreadTypeEnum.UNIFIED.name().equals(getType());
    }

    public Boolean isWeChatMp() {
        return ClientEnum.WECHAT_MP.name().equals(getClient());
    }

    public Boolean isWeChatMini() {
        return ClientEnum.WECHAT_MINI.name().equals(getClient());
    }

    // ---------------------------

    public ThreadEntity setRoboting() {
        setStatus(ThreadProcessStatusEnum.ROBOTING.name());
        return this;
    }

    public ThreadEntity setLlmIng() {
        setStatus(ThreadProcessStatusEnum.LLMING.name());
        return this;
    }

    public ThreadEntity setOffline() {
        setStatus(ThreadProcessStatusEnum.OFFLINE.name());
        return this;
    }

    public ThreadEntity setQueuing() {
        setStatus(ThreadProcessStatusEnum.QUEUING.name());
        return this;
    }

    public ThreadEntity setChatting() {
        setStatus(ThreadProcessStatusEnum.CHATTING.name());
        return this;
    }

    public ThreadEntity setTimeout() {
        setStatus(ThreadProcessStatusEnum.TIMEOUT.name());
        return this;
    }

    public ThreadEntity setClose() {
        setStatus(ThreadProcessStatusEnum.CLOSED.name());
        return this;
    }

    //--------------------------

    public ThreadProtobuf toProtobuf() {
        return ConvertUtils.convertToThreadProtobuf(this);
    }

    public UserProtobuf getUserProtobuf() {
        return JSON.parseObject(getUser(), UserProtobuf.class);
    }

    public UserProtobuf getAgentProtobuf() {
        return JSON.parseObject(getAgent(), UserProtobuf.class);
    }

    public UserProtobuf getRobotProtobuf() {
        return JSON.parseObject(getRobot(), UserProtobuf.class);
    }

    public UserProtobuf getWorkgroupProtobuf() {
        return JSON.parseObject(getWorkgroup(), UserProtobuf.class);
    }

    public ThreadExtra getThreadExtra() {
        return JSON.parseObject(getExtra(), ThreadExtra.class);
    }

    // 判断是否机器人转人工
    // 如果robot.type == UserTypeEnum.ROBOT.name() && agent.type == UserTypeEnum.AGENT.name()
    public Boolean isRobotToAgent() {
        return getRobotProtobuf() != null && getAgentProtobuf() != null
                && UserTypeEnum.ROBOT.name().equals(getRobotProtobuf().getType())
                && UserTypeEnum.AGENT.name().equals(getAgentProtobuf().getType());
    }

    // 获取全部消息数量
    public Integer getAllMessageCount() {
        return messages.size();
    }

    // 获取访客消息数量
    public Integer getVisitorMessageCount() {
        // 遍历消息列表，统计访客消息数量
        int count = 0;
        for (MessageEntity message : messages) {
            if (message.isFromVisitor()) {
                count++;
            }
        }
        return count;
    }

    // 获取客服消息数量
    public Integer getAgentMessageCount() {
        // 遍历消息列表，统计客服消息数量
        int count = 0;
        for (MessageEntity message : messages) {
            if (message.isFromAgent()) {
                count++;
            }
        }
        return count;
    }

    // 获取系统消息数量
    public Integer getSystemMessageCount() {
        // 遍历消息列表，统计系统消息数量
        int count = 0;
        for (MessageEntity message : messages) {
            if (message.isFromSystem()) {
                count++;
            }
        }
        return count;
    }

    // 获取机器人消息数量
    public Integer getRobotMessageCount() {
        // 遍历消息列表，统计机器人消息数量
        int count = 0;
        for (MessageEntity message : messages) {
            if (message.isFromRobot()) {
                count++;
            }
        }
        return count;
    }

    // 是否有效会话
    // 至少包含一条用户消息 + 一条客服消息
    public Boolean isValid() {
        return getVisitorMessageCount() > 0 && getAgentMessageCount() > 0;
    }

    /**
     * 重写toString方法避免循环引用
     */
    @Override
    public String toString() {
        return "ThreadEntity{" +
                "id=" + getId() +
                ", uid='" + getUid() + '\'' +
                ", topic='" + getTopic() + '\'' +
                ", type='" + getType() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", createdAt=" + getCreatedAt() +
                '}';
    }

}