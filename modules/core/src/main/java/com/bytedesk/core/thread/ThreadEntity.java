/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-19 15:18:32
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

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.utils.ConvertUtils;

import jakarta.persistence.*;
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

    // public ThreadEntity reInit(Boolean isRobot) {
    //     if (isRobot) {
    //         setState(ThreadStateEnum.STARTED.name());
    //     } else {
    //         setState(ThreadStateEnum.QUEUING.name());
    //     }
    //     setRated(false);
    //     setHide(false);
    //     setAutoClose(false);
    //     setRobot(false);
    //     return this;
    // }
    //
    public Boolean isClosed() {
        return getState().equals(ThreadStateEnum.CLOSED.name());
    }

    public Boolean isStarted() {
        // return getState().equals(ThreadStateEnum.STARTED.name());
        return getState().equals(ThreadStateEnum.STARTED.name())
             || getState().equals(ThreadStateEnum.TRANSFER_PENDING.name())
             || getState().equals(ThreadStateEnum.TRANSFER_ACCEPTED.name())
             || getState().equals(ThreadStateEnum.TRANSFER_REJECTED.name())
             || getState().equals(ThreadStateEnum.TRANSFER_TIMEOUT.name())
             || getState().equals(ThreadStateEnum.TRANSFER_CANCELED.name())
             || getState().equals(ThreadStateEnum.INVITE_PENDING.name())
             || getState().equals(ThreadStateEnum.INVITE_ACCEPTED.name())
             || getState().equals(ThreadStateEnum.INVITE_REJECTED.name())
             || getState().equals(ThreadStateEnum.INVITE_TIMEOUT.name())
             || getState().equals(ThreadStateEnum.INVITE_CANCELED.name());
    }

    // is transfer pending
    public Boolean isTransferPending() {
        return getState().equals(ThreadStateEnum.TRANSFER_PENDING.name());
    }

    // is transfer accepted
    public Boolean isTransferAccepted() {
        return getState().equals(ThreadStateEnum.TRANSFER_ACCEPTED.name());
    }

    public Boolean isTransferRejected() {
        return getState().equals(ThreadStateEnum.TRANSFER_REJECTED.name());
    }

    public Boolean isTransferTimeout() {
        return getState().equals(ThreadStateEnum.TRANSFER_TIMEOUT.name());
    }

    public Boolean isTransferCanceled() {
        return getState().equals(ThreadStateEnum.TRANSFER_CANCELED.name());
    }

    // is invite pending
    public Boolean isInvitePending() {
        return getState().equals(ThreadStateEnum.INVITE_PENDING.name());
    }

    // is invite accepted
    public Boolean isInviteAccepted() {
        return getState().equals(ThreadStateEnum.INVITE_ACCEPTED.name());
    }

    public Boolean isInviteRejected() {
        return getState().equals(ThreadStateEnum.INVITE_REJECTED.name());
    }

    public Boolean isInviteTimeout() {
        return getState().equals(ThreadStateEnum.INVITE_TIMEOUT.name());
    }

    public Boolean isInviteCanceled() {
        return getState().equals(ThreadStateEnum.INVITE_CANCELED.name());
    }

    public Boolean isOffline() {
        return getState().equals(ThreadStateEnum.OFFLINE.name());
    }

    public Boolean isQueuing() {
        return getState().equals(ThreadStateEnum.QUEUING.name());
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

    public ThreadProtobuf toProtobuf() {
        return ConvertUtils.convertToThreadProtobuf(this);
    }

    // public UserProtobuf getAgentProtobuf() {
    // return JSON.parseObject(this.agent, UserProtobuf.class);
    // }

    public UserProtobuf getUserProtobuf() {
        return JSON.parseObject(getUser(), UserProtobuf.class);
    }

    public Boolean isWeChatMp() {
        return getClient().equals(ClientEnum.WECHAT_MP.name());
    }

    public Boolean isWeChatMini() {
        return getClient().equals(ClientEnum.WECHAT_MINI.name());
    }

    public ThreadEntity setOffline() {
        setState(ThreadStateEnum.OFFLINE.name());
        return this;
    }

    public ThreadEntity setStarted() {
        setState(ThreadStateEnum.STARTED.name());
        return this;
    }

    public ThreadEntity setClose() {
        setState(ThreadStateEnum.CLOSED.name());
        return this;
    }

    public ThreadEntity setQueuing() {
        setState(ThreadStateEnum.QUEUING.name());
        return this;
    }

}
