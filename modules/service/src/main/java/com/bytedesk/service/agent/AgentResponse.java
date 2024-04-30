/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-06 10:17:01
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-16 17:07:56
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent;

import com.bytedesk.core.utils.BaseResponse;

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
public class AgentResponse extends BaseResponse {

    private static final long serialVersionUID = 94072119L;    

    private String uid;

    private String nickname;

    private String avatar;

    private String mobile;

    private String email;

    private String description;

    private String acceptStatus;

    private boolean connected;
    
    private Integer maxThreadCount;

    private String welcomeTip;
}
