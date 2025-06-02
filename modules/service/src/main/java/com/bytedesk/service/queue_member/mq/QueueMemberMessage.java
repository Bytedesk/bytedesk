/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-30 11:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-02 10:20:00
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

import com.alibaba.fastjson2.annotation.JSONField;

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
    @JSONField(ordinal = 1)
    private String memberUid;
    
    /**
     * 操作类型：update, delete
     */
    @JSONField(ordinal = 2)
    private String operationType;
    
    /**
     * 需要更新的字段
     */
    @JSONField(ordinal = 3)
    private Map<String, Object> updates;
    
    /**
     * 是否需要更新统计信息
     */
    @JSONField(ordinal = 4)
    private Boolean updateStats;
    
    /**
     * 消息计数更新
     */
    @JSONField(ordinal = 5)
    private Integer visitorMessageCount;
    
    @JSONField(ordinal = 6)
    private Integer agentMessageCount;
    
    @JSONField(ordinal = 7)
    private Integer robotMessageCount;
    
    @JSONField(ordinal = 8)
    private Integer systemMessageCount;
    
    /**
     * 时间戳更新
     */
    @JSONField(ordinal = 9, format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime visitorLastMessageAt;
    
    @JSONField(ordinal = 10, format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime agentLastResponseAt;
    
    @JSONField(ordinal = 11, format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime robotLastResponseAt;
    
    @JSONField(ordinal = 12, format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime workgroupLastResponseAt;
    
    /**
     * 状态更新
     */
    @JSONField(ordinal = 13)
    private String agentAcceptType;
    
    @JSONField(ordinal = 14)
    private String robotAcceptType;
    
    @JSONField(ordinal = 15)
    private String workgroupAcceptType;
    
    @JSONField(ordinal = 16)
    private Boolean agentOffline;
    
    @JSONField(ordinal = 17)
    private Boolean robotToAgent;
    
    @JSONField(ordinal = 18)
    private Boolean rated;
    
    @JSONField(ordinal = 19)
    private Integer rateLevel;
    
    @JSONField(ordinal = 20)
    private Boolean resolved;
    
    @JSONField(ordinal = 21)
    private Boolean qualityChecked;
    
    @JSONField(ordinal = 22)
    private String qualityCheckResult;
    
    @JSONField(ordinal = 23)
    private Boolean messageLeave;
    
    @JSONField(ordinal = 24)
    private String leaveMsg;
    
    @JSONField(ordinal = 25)
    private Boolean summarized;
    
    @JSONField(ordinal = 26)
    private String transferStatus;
    
    @JSONField(ordinal = 27)
    private String inviteStatus;
    
    @JSONField(ordinal = 28)
    private String intentionType;
    
    @JSONField(ordinal = 29)
    private String emotionType;
    
    /**
     * 自动接受会话标志
     */
    @JSONField(ordinal = 30)
    private Boolean agentAutoAcceptThread;
    
    /**
     * 消息类型标记，便于接收端识别
     */
    @JSONField(ordinal = 0)
    @Builder.Default
    private String _type = "queueMemberMessage";
}