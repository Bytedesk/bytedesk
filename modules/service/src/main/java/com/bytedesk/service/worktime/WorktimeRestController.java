/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-18 14:46:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 17:19:53
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.worktime;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Description;

import com.bytedesk.core.base.BaseRestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@Tag(name = "工作时间管理", description = "工作时间管理相关接口")
@RestController
@RequestMapping("/api/v1/worktime")
@AllArgsConstructor
@Description("Worktime Management Controller - Agent working hours and schedule management APIs")
public class WorktimeRestController extends BaseRestController<WorktimeRequest, WorktimeRestService> {
    
}
