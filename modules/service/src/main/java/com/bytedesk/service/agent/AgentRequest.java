/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-06 10:16:30
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-13 22:29:10
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

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.BdConstants;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class AgentRequest extends BaseRequest {

    // private String uid;

    private String nickname;

    private String password;

    @Builder.Default
    private String avatar = AvatarConsts.DEFAULT_AGENT_AVATAR_URL;

    private String mobile;

    private String email;

    @Builder.Default
    private String description = BdConstants.DEFAULT_AGENT_DESCRIPTION;

    // organization oid
    private String orgUid;

}
