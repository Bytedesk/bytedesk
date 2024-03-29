/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 16:48:26
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-03-27 17:23:25
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.file;

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
@RequestMapping("/api/v1/kb/file")
@AllArgsConstructor
public class KbFileController {

    private final KbFileService kbService;

    /**
     * query
     * 
     * @param kbFileRequest
     * @return
     */
    @GetMapping("/query")
    public JsonResult<?> query(KbFileRequest kbFileRequest) {

        return JsonResult.success(kbService.query(kbFileRequest));
    }

    /**
     * create
     *
     * @param kbFileRequest kb
     * @return json
     */
    @PostMapping("/create")
    public JsonResult<?> create(@RequestBody KbFileRequest kbFileRequest) {

        // return kbService.create(kbFileRequest);
        return JsonResult.success();
    }

    /**
     * update
     *
     * @param kbFileRequest kb
     * @return json
     */
    @PostMapping("/update")
    public JsonResult<?> update(@RequestBody KbFileRequest kbFileRequest) {

        //
        return new JsonResult<>("update success", 200, false);
    }

    /**
     * delete
     *
     * @param kbFileRequest kb
     * @return json
     */
    @PostMapping("/delete")
    public JsonResult<?> delete(@RequestBody KbFileRequest kbFileRequest) {

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
