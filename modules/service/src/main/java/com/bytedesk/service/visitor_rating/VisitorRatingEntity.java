/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-07 11:37:48
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-02 17:22:22
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor_rating;

import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.converter.StringListConverter;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

// "Rate" is a verb and "VisitorRating" is a noun
/**
 * 满意度双向评价机制之：客服评价访客
 */
@Data
@Entity
@SuperBuilder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_service_visitor_rating")
@EqualsAndHashCode(callSuper = true)
public class VisitorRatingEntity extends BaseEntity {
    
    @Builder.Default
    @Column(nullable = false)
    private int score = 5;  // 1-5星评分
    
    private String comment;  // 评价内容

    // 支持图片
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> images = new ArrayList<>();

    @Column(name = "thread_uid", nullable = false)
    private String threadUid;
    
    @Column(name = "thread_topic", nullable = false)
    private String topic;

    // rate agent
    @Builder.Default
    @Column(name = "rating_agent", length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String agent = BytedeskConsts.EMPTY_JSON_STRING;

    @Builder.Default
    @Column(name = "rating_user", length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String user = BytedeskConsts.EMPTY_JSON_STRING;
    
    // private Integer responseTimeVisitorRating;  // 响应时间评分
    
    // private Integer solutionVisitorRating;  // 解决方案评分
    
    // private Integer attitudeVisitorRating;  // 服务态度评分

    // private String additionalFeedback;  // 额外反馈

    // private boolean anonymous;  // 匿名
} 