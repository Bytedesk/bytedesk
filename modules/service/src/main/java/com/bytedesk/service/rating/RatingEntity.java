/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-07 11:37:48
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-21 15:11:28
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.rating;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.thread.ThreadEntity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

// "Rating" is a verb and "rating" is a noun
@Data
@Entity
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_service_rating")
@EqualsAndHashCode(callSuper = true)
public class RatingEntity extends BaseEntity {

    // @Column(name = "thread_uid", nullable = false)
    // private String threadUid;
    
    // @Column(name = "agent_uid", nullable = false)
    // private String agentUid;
    
    // @Column(name = "visitor_uid", nullable = false)
    // private String visitorUid;
    
    @Builder.Default
    @Column(nullable = false)
    private int score = 5;  // 1-5星评分
    
    private String comment;  // 评价内容
    
    // @Column(name = "response_time_rating")
    // private Integer responseTimeRating;  // 响应时间评分
    
    // @Column(name = "solution_rating")
    // private Integer solutionRating;  // 解决方案评分
    
    // @Column(name = "attitude_rating")
    // private Integer attitudeRating;  // 服务态度评分

    @Builder.Default
    // @Enumerated(EnumType.STRING)
    @Column(name = "rate_type", nullable = false)
    // private RatingTypeEnum type = RatingTypeEnum.THREAD;
    private String type = RatingTypeEnum.THREAD.name();

    // TODO: 可配置总分
    // // rate thread
    // @Builder.Default
    // private int score = 5;

    // @Builder.Default
    // private String content = BytedeskConsts.EMPTY_STRING;

    // rate message
    @Builder.Default
    private String result = RatingResultEnum.UP.name();

    // rate (robot) message
    @OneToOne
    private MessageEntity message;

    // rate thread
    // many rates to one thread
    // private String threadTopic;
    @ManyToOne
    private ThreadEntity thread;

    @Builder.Default
    @Column(name = "rate_user", length = 512)
    // @JdbcTypeCode(SqlTypes.JSON)
    private String user = BytedeskConsts.EMPTY_JSON_STRING;

    // 评价时间, baseEntity 自带 createTime
    // private LocalDateTime createTime;
} 