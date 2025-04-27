/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-22 16:11:42
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-09 09:04:48
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.message_leave;

import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.converter.StringListConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 留言
 * TODO：留言转工单
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_service_message_leave")
public class MessageLeaveEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    // 联系方式
    private String contact;

    // 留言内容
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String content;

    // 支持图片
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> images = new ArrayList<>();

    // 回复内容
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String reply;

    @Builder.Default
    private String status = MessageLeaveStatusEnum.PENDING.name();
    
    // 留言分类（如：咨询、投诉、建议、其他）
    private String categoryUid;
    
    // 留言优先级（如：低、中、高、紧急）
    @Builder.Default
    private String priority = "中";

    // 处理时间
    private Long handleTime;
    
    // 处理备注
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String handleRemark;
    
    // 关联工单ID（如果生成了工单）
    private String ticketUid;
    
    // 关联消息uID（用于更新提示留言消息状态）
    private String messageUid;
    // 关联会话uID（本身对应的会话）
    private String threadUid;
    // 关联的会话
    // private String threadTopic;
    // private ThreadEntity thread;

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
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> tagList = new ArrayList<>();

    @Builder.Default
    @Column(name = "leavemsg_user", length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String user = BytedeskConsts.EMPTY_JSON_STRING;

    // 处理人
    @Builder.Default
    @Column(name = "handler_user", length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String handler = BytedeskConsts.EMPTY_JSON_STRING;
    // private AgentEntity handler;
}
