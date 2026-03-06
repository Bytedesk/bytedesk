/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-27 22:35:15
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-21 09:17:55
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.taboo;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.ArrayList;

import com.bytedesk.core.base.BaseRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class TabooRequest extends BaseRequest {
    
    private static final long serialVersionUID = 1L;

    // private String content;

    @Builder.Default
    private List<String> synonymList = new ArrayList<>();

    private String reply;

    // private String type;

    @Builder.Default
    private List<String> tagList = new ArrayList<>();

    private Integer matchCount;
    
    private ZonedDateTime startDate;

    private ZonedDateTime endDate;

    private Boolean enabled;

    private String categoryUid;

    private String kbUid; // 对应知识库
}
