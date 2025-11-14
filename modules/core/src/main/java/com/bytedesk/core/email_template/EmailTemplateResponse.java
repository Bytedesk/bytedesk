/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-28 10:41:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-01 10:42:52
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.email_template;

import java.util.List;

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
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class EmailTemplateResponse extends BaseResponse {
 
    private static final long serialVersionUID = 1L;

    private String name;

    private String subject;

    private String preheader;

    private String content;

    private String plainText;

    private String contentType;

    private String templateType;

    private String status;

    private String language;

    private String description;

    private List<String> tagList;

    private Boolean enabled;

    private Boolean defaultTemplate;

    private Integer templateVersion;

    private Long useCount;
}
