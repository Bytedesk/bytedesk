/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-25 12:20:34
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-23 13:47:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.model;

import java.util.List;

import com.bytedesk.core.base.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class LlmModelResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;


    private String name;

    private String nickname;

    private String description;

    private String type;

    private String providerUid;

    private String providerType;

    private List<String> tagList;

    private Boolean free;

    private Boolean enabled;

    private Boolean systemEnabled;
}
