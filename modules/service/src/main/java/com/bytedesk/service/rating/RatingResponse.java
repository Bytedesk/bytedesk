/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:01:30
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-01 15:02:07
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.rating;

import java.time.LocalDateTime;

import com.bytedesk.core.base.BaseResponse;
import com.bytedesk.core.thread.ThreadResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
@NoArgsConstructor
public class RatingResponse extends BaseResponse {

    private RatingTypeEnum type;

    private Integer score;

    private String comment;

    // rate message
    private RatingResultEnum result;

    // rate (robot) message
    // private Message message;

    // rate thread
    // many rates to one thread
    // private String threadTopic;
    private ThreadResponse thread;

    private String user;

    private LocalDateTime createdAt;

}
