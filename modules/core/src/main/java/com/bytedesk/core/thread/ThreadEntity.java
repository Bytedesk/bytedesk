/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-08 18:31:19
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
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserTypeEnum;
import com.bytedesk.core.utils.ConvertUtils;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * thread: 指的是一系列相关的信息或讨论按照时间顺序排列形成的一个连贯的交流脉络.
 * 强调信息的连贯性和关联性，就像一条线将不同的消息或回复串联在一起，通常用于在线论坛、电子邮件、社交媒体等平台上，
 * 指围绕特定主题展开的一系列连续的消息交流
 * conversation: 更侧重于指人与人之间面对面或通过某种通信方式进行的较为直接和实时的语言交流互动，强调交流的过程和行为本身
 * 通常用于描述两个或多个人之间的口头或书面的交流活动，更强调交流的互动性和即时性，使用的场景较为广泛，包括日常对话、商务谈判、电话交流等
 * 综上考虑，此处使用 thread 表示会话更为合适
 * 
 * every visitor to agent thread should only be one,
 * history records are stored in thread_log table
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@EntityListeners({ ThreadEntityListener.class })
@Table(name = "bytedesk_core_thread", uniqueConstraints = {
    // @UniqueConstraint(columnNames = {"topic", "owner_id"}) // 同一个用户，针对某service thread 创建多个ticket，并对应多个ticket thread
})
public class ThreadEntity extends AbstractThreadEntity {

    private static final long serialVersionUID = 1L;

    // 一对多关系，一个thread可以对应多个message
    // cascade = CascadeType.ALL, orphanRemoval = true, 
    @Builder.Default
    @OneToMany(mappedBy = "thread", fetch = FetchType.LAZY)
    private List<MessageEntity> messages = new ArrayList<>();

    //
    public Boolean isClosed() {
        return getStatus().equals(ThreadProcessStatusEnum.CLOSED.name());
    }

    public Boolean isNew() {
        return getStatus().equals(ThreadProcessStatusEnum.NEW.name());
    }

    public Boolean isChatting() {
        return getStatus().equals(ThreadProcessStatusEnum.CHATTING.name());
    }

    // is transfer pending
    public Boolean isTransferPending() {
        return getTransferStatus().equals(ThreadTransferStatusEnum.TRANSFER_PENDING.name());
    }

    // is transfer accepted
    public Boolean isTransferAccepted() {
        return getTransferStatus().equals(ThreadTransferStatusEnum.TRANSFER_ACCEPTED.name());
    }

    public Boolean isTransferRejected() {
        return getTransferStatus().equals(ThreadTransferStatusEnum.TRANSFER_REJECTED.name());
    }

    public Boolean isTransferTimeout() {
        return getTransferStatus().equals(ThreadTransferStatusEnum.TRANSFER_TIMEOUT.name());
    }

    public Boolean isTransferCanceled() {
        return getTransferStatus().equals(ThreadTransferStatusEnum.TRANSFER_CANCELED.name());
    }

    // is invite pending
    public Boolean isInvitePending() {
        return getInviteStatus().equals(ThreadInviteStatusEnum.INVITE_PENDING.name());
    }

    // is invite accepted
    public Boolean isInviteAccepted() {
        return getInviteStatus().equals(ThreadInviteStatusEnum.INVITE_ACCEPTED.name());
    }

    public Boolean isInviteRejected() {
        return getInviteStatus().equals(ThreadInviteStatusEnum.INVITE_REJECTED.name());
    }

    public Boolean isInviteTimeout() {
        return getInviteStatus().equals(ThreadInviteStatusEnum.INVITE_TIMEOUT.name());
    }

    public Boolean isInviteCanceled() {
        return getInviteStatus().equals(ThreadInviteStatusEnum.INVITE_CANCELED.name());
    }

    public Boolean isQueuing() {
        return getStatus().equals(ThreadProcessStatusEnum.QUEUING.name());
    }

    public Boolean isCustomerService() {
        return getType().equals(ThreadTypeEnum.AGENT.name())
                || getType().equals(ThreadTypeEnum.WORKGROUP.name())
                || getType().equals(ThreadTypeEnum.ROBOT.name())
                || getType().equals(ThreadTypeEnum.UNIFIED.name());
    }

    public Boolean isRobotType() {
        return getType().equals(ThreadTypeEnum.ROBOT.name());
    }

    public Boolean isWorkgroupType() {
        return getType().equals(ThreadTypeEnum.WORKGROUP.name());
    }

    public Boolean isAgentType() {
        return getType().equals(ThreadTypeEnum.AGENT.name());
    }

    public Boolean isUnifiedType() {
        return getType().equals(ThreadTypeEnum.UNIFIED.name());
    }

    public Boolean isWeChatMp() {
        return getClient().equals(ClientEnum.WECHAT_MP.name());
    }

    public Boolean isWeChatMini() {
        return getClient().equals(ClientEnum.WECHAT_MINI.name());
    }

    /**
     * 检查会话是否曾经处于离线状态
     */
    // public Boolean isOffline() {
    //     ThreadExtra extra = getThreadExtra();
    //     return extra != null && extra.isOffline();
    // }

    /**
     * 将当前会话标记为离线状态
     */
    public ThreadEntity setOffline() {
        // setStatus(ThreadProcessStatusEnum.OFFLINE.name());
        setOffline(true);
        return this;
    }

    public ThreadEntity setOnline() {
        // setStatus(ThreadProcessStatusEnum.OFFLINE.name());
        setOffline(false);
        return this;
    }

    public ThreadEntity setChatting() {
        setStatus(ThreadProcessStatusEnum.CHATTING.name());
        return this;
    }

    public ThreadEntity setClose() {
        setStatus(ThreadProcessStatusEnum.CLOSED.name());
        return this;
    }

    public ThreadEntity setQueuing() {
        setStatus(ThreadProcessStatusEnum.QUEUING.name());
        return this;
    }

    public ThreadProtobuf toProtobuf() {
        return ConvertUtils.convertToThreadProtobuf(this);
    }

    public UserProtobuf getUserProtobuf() {
        return JSON.parseObject(getUser(), UserProtobuf.class);
    }

    public UserProtobuf getAgentProtobuf() {
        return JSON.parseObject(getAgent(), UserProtobuf.class);
    }

    public Boolean isAgentRobot() {
        return getAgentProtobuf() != null && getAgentProtobuf().getType().equals(UserTypeEnum.ROBOT.name());
    }

    public UserProtobuf getWorkgroupProtobuf() {
        return JSON.parseObject(getWorkgroup(), UserProtobuf.class);
    }

    public ThreadExtra getThreadExtra() {
        return JSON.parseObject(getExtra(), ThreadExtra.class);
    }

    // 判断是否机器人转人工
    // 如果robot和agent都不是BytedeskConsts.EMPTY_JSON_STRING，说明是机器人转人工
    public Boolean isRobotToAgent() {
        return !getRobot().equals(BytedeskConsts.EMPTY_JSON_STRING)
                && !getAgent().equals(BytedeskConsts.EMPTY_JSON_STRING);
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


}