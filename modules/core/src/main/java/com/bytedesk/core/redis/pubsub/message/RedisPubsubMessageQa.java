/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-30 16:09:19
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-31 10:19:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.redis.pubsub.message;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RedisPubsubMessageQa {

    private Integer id;

    @NotEmpty
    private String uid;

    private String threadTopic;

    // 
    private String kbUid;

    private String fileUid;
    
    // 
    private String question;

    private String answer;

    private String model;

    private Integer created;

    private String finishReason;

    // 
    private String promptTokens;

    private String completionTokens;

    private String totalTokens;

}
