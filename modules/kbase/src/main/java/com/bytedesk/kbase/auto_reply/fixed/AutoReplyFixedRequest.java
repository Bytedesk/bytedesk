/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-27 22:40:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-10 15:04:03
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.auto_reply.fixed;

import java.time.LocalDateTime;

import com.bytedesk.core.base.BaseRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class AutoReplyFixedRequest extends BaseRequest {
    
    private String content;

    @Builder.Default
    private String tags = "[]";

    // 回复次数
    @Builder.Default
    private Integer replyCount = 0;

    @Builder.Default
    private Boolean enabled = true;
    
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String categoryUid;

    private String kbUid; // 对应知识库

    // user uid
    private String userUid;
}
