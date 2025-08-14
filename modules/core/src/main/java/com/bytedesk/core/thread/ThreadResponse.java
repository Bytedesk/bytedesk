/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-21 10:01:27
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-14 15:49:01
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.thread;

import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseResponse;
import com.bytedesk.core.rbac.user.UserProtobuf;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * for agent client
 */
@Data
@SuperBuilder
@Accessors(chain = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ThreadResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    private String topic;

    private String content;

    private String type;

    private String status;

    // transfer status
    @Builder.Default
    private String transferStatus = ThreadTransferStatusEnum.NONE.name();

    // invite status
    @Builder.Default
    private String inviteStatus = ThreadInviteStatusEnum.NONE.name();

    // resolved status
    @Builder.Default
    private String resolvedStatus = ThreadSummaryStatusEnum.PENDING.name();

    private Boolean top;

    private Boolean unread;

    // 给客服端使用，统计未读消息数量
    private Integer unreadCount;

    // 给访客端使用，统计未读消息数量
    private Integer visitorUnreadCount;

    private Boolean mute;

    private Boolean hide;

    private Integer star;

    private Boolean fold;

    private Boolean autoClose;

    private String note;

    private Boolean offline;

    // 标签
    @Builder.Default
    private List<String> tagList = new ArrayList<>();

    private String channel;

    private String extra;

    // 存放被转接客服，存放多个 UserProtobuf 实体转换成的 JSON
    @Builder.Default
    private List<UserProtobuf> transfers = new ArrayList<>();

    // 邀请多个客服参与会话
    @Builder.Default
    private List<UserProtobuf> invites = new ArrayList<>();

    // 多个管理员监听会话
    @Builder.Default
    private List<UserProtobuf> monitors = new ArrayList<>();

    @Builder.Default
    private List<UserProtobuf> assistants = new ArrayList<>();

    // ticket observers
    @Builder.Default
    private List<UserProtobuf> ticketors = new ArrayList<>();

    private UserProtobuf user;
    //
    private UserProtobuf owner;

    // 用于更新robot-agent-llm配置，不能修改为UserProtobuf, 
    // 否则会内容缺失，因为可能为RobotProtobuf类型, 其中含有llm字段
    private String agent;

    public UserProtobuf getAgentProtobuf() {
        if (agent == null) {
            return null;
        }
        return UserProtobuf.fromJson(agent);
    }

    private String robot;

    public UserProtobuf getRobotProtobuf() {
        if (robot == null) {
            return null;
        }
        return UserProtobuf.fromJson(robot);
    }

    private String workgroup;

    public UserProtobuf getWorkgroupProtobuf() {
        if (workgroup == null) {
            return null;
        }
        return UserProtobuf.fromJson(workgroup);
    }

    // 流程实例ID
    private String processInstanceId;
    
    // 流程定义实体UID
    private String processEntityUid;
    
    // 是否机器人转人工
    private Boolean robotToAgent;
    
    // 是否有效会话
    private Boolean valid;
    
    // 消息统计字段
    private Integer allMessageCount;
    private Integer visitorMessageCount;
    private Integer agentMessageCount;
    private Integer systemMessageCount;
    private Integer robotMessageCount;
}
