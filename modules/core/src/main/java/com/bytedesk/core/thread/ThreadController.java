/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-02-22 11:30:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.thread;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.BaseRequest;
import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;

/**
 * 
 * http://localhost:9003/swagger-ui/index.html
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/thread")
public class ThreadController {

    private final ThreadService threadService;

    /**
     * 
     * @return json
     */
    @GetMapping("/query")
    public JsonResult<?> query(BaseRequest pageParam) {

        Page<Thread> threadPage = threadService.query(pageParam);
        //
        return new JsonResult<>("query thread success", 200, threadPage);
    }

    @RequestMapping("/create")
    public JsonResult<?> create(@RequestBody ThreadRequest threadRequest) {
         
        Thread thread = threadService.create(threadRequest);
        if (thread == null) {
            return new JsonResult<>("create thread failed", -1, null);
        }
        return new JsonResult<>("create thread success", 200, thread);
    }


}
