/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-25 13:50:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-16 15:58:18
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.provider;

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.constant.BytedeskConsts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class LlmProviderRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;
    
    private String nickname;

    // https://cdn.weiyuai.cn/assets/images/llm/model/baichuan.png
    private String logo;
    //
    @Builder.Default
    private String description = BytedeskConsts.EMPTY_STRING;

    private String type;

    //
    private String baseUrl;
    private String apiKey;
    private String webUrl;

    // 关联的Coze Bot
    private String cozeBotId;
    
    // 
    private String status;

    private Boolean enabled;    

    private Boolean systemEnabled;
}
