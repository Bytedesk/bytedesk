/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-21 10:01:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-07 14:29:49
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

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.rbac.user.UserProtobuf;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ThreadRequest extends BaseRequest {
    // 
    private String topic;

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

    @Builder.Default
    private Boolean top = false;

    @Builder.Default
    private Boolean unread = false;

    @Builder.Default
    private Integer unreadCount = 0;

    @Builder.Default
    private Boolean mute = false;

    @Builder.Default
    private Boolean hide = false;

    @Builder.Default
    private Integer star = 0;

    @Builder.Default
    private Boolean fold = false;

    @Builder.Default
    private Boolean autoClose = false;

    // 备注
    private String note;

    // offline
    @Builder.Default
    private Boolean offline = false;

    // 标签
    @Builder.Default
    private List<String> tagList = new ArrayList<>();

    // 邀请多个客服参与会话
    @Builder.Default
    private List<String> inviteUids = new ArrayList<>();

    // 多个管理员监听会话
    @Builder.Default
    private List<String> monitorUids = new ArrayList<>();

    // assistants: monitoring agent、quality check agent、robot agent
    @Builder.Default
    private List<String> assistantUids = new ArrayList<>();

    // ticketors: ticket observers
    @Builder.Default
    private List<String> ticketorUids = new ArrayList<>();

    // group member uids
    @Builder.Default
    private List<String> memberUids = new ArrayList<>();

    // used for client query
    private String componentType;

    // 强制重新创建新会话
    @Builder.Default
    private Boolean forceNew = false;

    // 在客服端需要将同一个访客所有的会话合并为一条会话显示，但在管理后台需要显示所有会话，所以需要区分
    // 在客服端需要设置 mergeByTopic = true，在管理后台需要设置 mergeByTopic = false
    // 是否合并相同topic的记录
    @Builder.Default
    private Boolean mergeByTopic = false;

    private UserProtobuf user;
    
    private String userNickname;

    private String agentNickname;

    private String robotNickname;

    private String workgroupNickname;

    private String ownerNickname;

    private String ownerUid;

    // 用于更新robot-agent-llm配置，不能修改为UserProtobuf,否则会序列化出错
    private String agent;

    private String robot;

    private String workgroup;

    // 流程实例ID
    private String processInstanceId;
    
    // 流程定义实体UID
    private String processEntityUid;

    // 是否取消订阅topic
    private Boolean unsubscribe;
}
