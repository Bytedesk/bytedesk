/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-11 10:56:48
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-11 17:24:22
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  技术/商务联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.workflow.flow;

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.enums.LevelEnum;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowRequest extends BaseRequest {

    private String name;

    private String description;

    private String icon;

    @Builder.Default
    private String groups = "[]";

    @Builder.Default
    private String events = "[]";

    @Builder.Default
    private String variables = "[]";

    @Builder.Default
    private String edges = "[]";

    @Builder.Default
    private String theme = "{}";

    private String selectedThemeTemplateId;

    @Builder.Default
    private String settings = "{}";

    private String publicId;

    private String customDomain;

    @Builder.Default
    private String resultsTablePreferences = "{}";

    @Builder.Default
    private boolean isArchived = false;

    @Builder.Default
    private boolean isClosed = false;

    private String whatsAppCredentialsId;

    private Integer riskLevel;

    @Builder.Default
    private String level = LevelEnum.PLATFORM.name();
}
