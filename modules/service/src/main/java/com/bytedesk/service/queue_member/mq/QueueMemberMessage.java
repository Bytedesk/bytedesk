/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-30 11:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-30 11:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue_member.mq;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 队列成员消息
 * 用于在消息队列中传递队列成员更新信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueueMemberMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 队列成员唯一标识
     */
    private String memberUid;
    
    /**
     * 操作类型：update, delete
     */
    private String operationType;
    
    /**
     * 需要更新的字段
     */
    private Map<String, Object> updates;
    
    /**
     * 是否需要更新统计信息
     */
    private Boolean updateStats;
    
    /**
     * 消息计数更新
     */
    private Integer visitorMessageCount;
    private Integer agentMessageCount;
    private Integer robotMessageCount;
    private Integer systemMessageCount;
    
    /**
     * 时间戳更新
     */
    private LocalDateTime visitorLastMessageAt;
    private LocalDateTime agentLastResponseAt;
    private LocalDateTime robotLastResponseAt;
    private LocalDateTime workgroupLastResponseAt;
    
    /**
     * 状态更新
     */
    private String agentAcceptType;
    private String robotAcceptType;
    private String workgroupAcceptType;
    private Boolean agentOffline;
    private Boolean robotToAgent;
    private Boolean rated;
    private Integer rateLevel;
    private Boolean resolved;
    private Boolean qualityChecked;
    private String qualityCheckResult;
    private Boolean messageLeave;
    private String leaveMsg;
    private Boolean summarized;
    private String transferStatus;
    private String inviteStatus;
    private String intentionType;
    private String emotionType;
} 