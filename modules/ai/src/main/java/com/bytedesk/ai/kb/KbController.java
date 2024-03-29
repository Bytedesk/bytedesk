/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 16:46:14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-03-26 16:49:05
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.kb;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.BaseRequest;
import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;

/**
 * 
 */
@RestController
@RequestMapping("/api/v1/kb")
@AllArgsConstructor
public class KbController {

    private final KbService kbService;

    /**
     * query
     * 
     * @param kbRequest
     * @return
     */
    @GetMapping("/query")
    public JsonResult<?> query(KbRequest kbRequest) {

        return JsonResult.success(kbService.query(kbRequest));
    }

    /**
     * create
     *
     * @param kbRequest kb
     * @return json
     */
    @PostMapping("/create")
    public JsonResult<?> create(@RequestBody KbRequest kbRequest) {

        return kbService.create(kbRequest);
    }

    /**
     * update
     *
     * @param kbRequest kb
     * @return json
     */
    @PostMapping("/update")
    public JsonResult<?> update(@RequestBody KbRequest kbRequest) {

        //
        return new JsonResult<>("update success", 200, false);
    }

    /**
     * delete
     *
     * @param kbRequest kb
     * @return json
     */
    @PostMapping("/delete")
    public JsonResult<?> delete(@RequestBody KbRequest kbRequest) {

        //

        return new JsonResult<>("delete success", 200, true);
    }

    /**
     * filter
     *
     * @return json
     */
    @GetMapping("/filter")
    public JsonResult<?> filter(BaseRequest filterParam) {

        //
        
        //
        return new JsonResult<>("filter success", 200, false);
    }

}
