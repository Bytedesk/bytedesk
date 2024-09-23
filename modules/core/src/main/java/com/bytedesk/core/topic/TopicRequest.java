/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-13 16:15:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-20 09:29:45
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.topic;

import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseRequest;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class TopicRequest extends BaseRequest {
    
    private String topic;
    
    @NotBlank
    private String userUid;

    /** AT_MOST_ONCE(0),AT_LEAST_ONCE(1), EXACTLY_ONCE(2), */
    // @Builder.Default
    // private int qos = 1;

    // private boolean subscribed;

    // private boolean wildcard;

    /**
     * current online clientIds
     */
    @Builder.Default
    private List<String> clientIds = new ArrayList<>();
}
