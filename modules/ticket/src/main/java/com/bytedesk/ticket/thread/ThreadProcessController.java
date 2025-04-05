/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-04 13:26:31
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-05 09:52:12
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.thread;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.thread.ThreadRequest;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.ticket.thread.dto.ThreadHistoryActivityResponse;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/thread/process")
@AllArgsConstructor
public class ThreadProcessController {

    private final ThreadProcessService threadProcessService;

    /**
     * 查询会话活动历史
     */
    @GetMapping("/history/activity")
    public ResponseEntity<?> queryThreadActivityHistory(ThreadRequest request) {

        List<ThreadHistoryActivityResponse> activities = threadProcessService.queryThreadActivityHistory(request);

        return ResponseEntity.ok(JsonResult.success(activities));
    }
    
}
