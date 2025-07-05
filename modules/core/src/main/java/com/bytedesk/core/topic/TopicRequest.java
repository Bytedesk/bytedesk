/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-13 16:15:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-05 10:04:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.topic;

import java.util.Set;
import java.util.HashSet;

import com.bytedesk.core.base.BaseRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class TopicRequest extends BaseRequest {
    
    private String topic;

    @Builder.Default
    private Set<String> topics = new HashSet<>();

    // @Builder.Default
    // private Integer qos = 1;

    @Builder.Default
    private Set<String> clientIds = new HashSet<>();
    
}
