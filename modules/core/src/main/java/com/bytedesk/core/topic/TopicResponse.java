/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-13 16:15:22
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-13 18:34:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.topic;

import java.util.HashSet;
import java.util.Set;

import com.bytedesk.core.base.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TopicResponse extends BaseResponse {
        
    @Builder.Default
    private Set<String> topics = new HashSet<>();

    @Builder.Default
    private Set<String> monitorTopics = new HashSet<>();
    /**
     * current online clientIds
     */
    @Builder.Default
    private Set<String> clientIds = new HashSet<>();

    // 
    private String userUid;
}
