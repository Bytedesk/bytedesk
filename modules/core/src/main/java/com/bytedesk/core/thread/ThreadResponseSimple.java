/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-21 10:01:27
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-16 18:28:36
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
import com.bytedesk.core.thread.enums.ThreadInviteStatusEnum;
import com.bytedesk.core.thread.enums.ThreadTransferStatusEnum;
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
public class ThreadResponseSimple extends BaseResponse {

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

    // 关闭来源类型
    private String closeType;

    private String note;

    private Boolean offline;

    // 标签
    @Builder.Default
    private List<String> tagList = new ArrayList<>();

    private String channel;

    private String extra;

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
}
