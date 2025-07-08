/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:05:14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-08 15:47:02
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.message_leave;

import java.time.ZonedDateTime;
import java.util.List;

import com.bytedesk.core.base.BaseResponse;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.utils.BdDateUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class MessageLeaveResponse extends BaseResponse {

    // 联系方式
    private String contact;

    // 留言内容
    private String content;

    // 支持图片
    private List<String> images;

    // 支持附件
    private List<String> attachments;

    // 回复内容
    private String reply;

    // 回复图片
    private List<String> replyImages;

    // 回复附件
    private List<String> replyAttachments;

    // 回复时间
    private ZonedDateTime repliedAt;

    // 
    private String status;
    
    // 留言分类（如：咨询、投诉、建议、其他）
    private String categoryUid;
    
    // 留言优先级（如：低、中、高、紧急）
    private String priority;
    
    // 关联工单ID（如果生成了工单）
    private String ticketUid;
    
    // 关联消息uID（用于更新提示留言消息状态）
    private String messageUid;
    // 关联会话uID（本身对应的会话）
    private String threadUid;
    // 关联的会话
    // private String threadTopic;
    // private ThreadResponse thread;

    // 客户来源渠道（如：网站、APP、小程序等）
    private String client;
    
    // 设备信息（浏览器、APP版本等）
    private String deviceInfo;
    
    // 客户IP地址
    private String ipAddress;
    
    // 地理位置信息
    private String location;
    
    // 留言暂时不需要评价
    // 满意度评价（处理后可以让用户评价）
    // private Integer satisfaction;
    
    // 标签，用于分类和检索
    private List<String> tagList;

    private UserProtobuf user;

    private UserProtobuf replyUser;

    // 已读相关字段
    private UserProtobuf readUser;
    private ZonedDateTime readAt;
    
    // 转接相关字段
    private UserProtobuf transferUser;
    private ZonedDateTime transferredAt;
    private String targetAgentUid; // 转接目标客服UID
    private UserProtobuf targetAgent; // 转接目标客服信息
    
    // 关闭相关字段
    private UserProtobuf closeUser;
    private ZonedDateTime closedAt;
    
    // 垃圾留言相关字段
    private UserProtobuf spamUser;
    private ZonedDateTime spamAt;

    public String getRepliedAt() {
        return BdDateUtils.formatDatetimeToString(repliedAt);
    }

    public String getReadAt() {
        return BdDateUtils.formatDatetimeToString(readAt);
    }

    public String getTransferredAt() {
        return BdDateUtils.formatDatetimeToString(transferredAt);
    }

    public String getClosedAt() {
        return BdDateUtils.formatDatetimeToString(closedAt);
    }

    public String getSpamAt() {
        return BdDateUtils.formatDatetimeToString(spamAt);
    }
}
