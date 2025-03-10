/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-27 22:35:30
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-10 15:27:56
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.taboo_message;

import java.time.LocalDateTime;

import com.bytedesk.core.base.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class TabooMessageResponse extends BaseResponse {

    private String content;

    private String type;

    private String level;

    private String platform;

    private String tags;

    private Boolean enabled;

    // 有效开始日期
    private LocalDateTime startDate;

    // 有效结束日期
    private LocalDateTime endDate;

    private String categoryUid;

    private String kbUid; // 对应知识库

    // private LocalDateTime createdAt;

    // private LocalDateTime updatedAt;
}
