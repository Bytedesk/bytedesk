/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-21 10:01:27
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-09 12:49:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.thread;

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

    // private Integer queueNumber;

    private Integer unreadCount;

    private Boolean mute;

    private Boolean hide;

    private Integer star;

    private Boolean folded;

    private Boolean autoClose;

    // 备注
    private String note;

    private Boolean offline;

    // 标签
    private List<String> tagList;

    private String client;

    private String extra;

    // 邀请多个客服参与会话
    private List<UserProtobuf> invites;

    // 多个管理员监听会话
    private List<UserProtobuf> monitors;

    // assistants: monitoring agent、quality check agent、robot agent
    private List<UserProtobuf> assistants;

    // ticket observers
    private List<UserProtobuf> ticketors;

    private UserProtobuf user;
    //
    private UserProtobuf owner;

    // 用于更新robot-agent-llm配置，不能修改为UserProtobuf, 
    // 否则会内容缺失，因为可能为RobotProtobuf类型, 其中含有llm字段
    private String agent;

    private UserProtobuf agentProtobuf;

    private UserProtobuf robot;

    private UserProtobuf robotProtobuf;

    private UserProtobuf workgroup;

    // 流程实例ID
    private String processInstanceId;
    
    // 流程定义实体UID
    private String processEntityUid;
}
