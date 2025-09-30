/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-27 22:35:15
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-02 08:24:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.taboo_message;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.ArrayList;

import com.bytedesk.core.base.BaseRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.experimental.Accessors;

@Getter
@Setter
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class TabooMessageRequest extends BaseRequest {
    private static final long serialVersionUID = 1L;

    
    // private String content;

    // private String type;

    @Builder.Default
    private List<String> tagList = new ArrayList<>();
    
    private ZonedDateTime startDate;

    private ZonedDateTime endDate;

    private String categoryUid;

    private String kbUid; // 对应知识库
}
