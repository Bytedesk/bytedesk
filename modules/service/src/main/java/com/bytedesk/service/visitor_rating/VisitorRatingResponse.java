/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:01:30
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-31 22:42:34
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor_rating;

import com.bytedesk.core.base.BaseResponse;
import com.bytedesk.core.rbac.user.UserProtobuf;
// import com.bytedesk.core.thread.VisitorResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper=true)
@AllArgsConstructor
@NoArgsConstructor
public class VisitorRatingResponse extends BaseResponse {

    // private VisitorRatingTypeEnum type;

    private Integer score;

    private String comment;

    private String threadUid;

    private String topic;

    private UserProtobuf agent;

    private UserProtobuf user;

    // rate message
    // private VisitorRatingResultEnum result;

    // rate (robot) message
    // private Message message;

    // rate thread
    // many rates to one thread
    // private String threadTopic;
    // private VisitorResponse thread;


    // private LocalDateTime createdAt;

}
