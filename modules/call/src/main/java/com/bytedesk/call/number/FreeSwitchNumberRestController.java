/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-08 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-14 10:43:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.number;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/freeswitch/number")
@AllArgsConstructor
public class FreeSwitchNumberRestController extends BaseRestController<FreeSwitchNumberRequest> {

    private final FreeSwitchNumberRestService freeSwitchNumberRestService;

    @ActionAnnotation(title = "FreeSwitch用户", action = "组织查询", description = "query freeswitch number by org")
    @Override
    public ResponseEntity<?> queryByOrg(FreeSwitchNumberRequest request) {
        
        Page<FreeSwitchNumberResponse> numbers = freeSwitchNumberRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(numbers));
    }

    @ActionAnnotation(title = "FreeSwitch用户", action = "用户查询", description = "query freeswitch number by number")
    @Override
    public ResponseEntity<?> queryByUser(FreeSwitchNumberRequest request) {
        
        Page<FreeSwitchNumberResponse> numbers = freeSwitchNumberRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(numbers));
    }

    @ActionAnnotation(title = "FreeSwitch用户", action = "查询", description = "query freeswitch number by uid")
    @Override
    public ResponseEntity<?> queryByUid(FreeSwitchNumberRequest request) {

        FreeSwitchNumberResponse number = freeSwitchNumberRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(number));
    }

    @ActionAnnotation(title = "FreeSwitch用户", action = "创建", description = "create freeswitch number")
    @Override
    public ResponseEntity<?> create(FreeSwitchNumberRequest request) {

        FreeSwitchNumberResponse number = freeSwitchNumberRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(number));
    }

    @ActionAnnotation(title = "FreeSwitch用户", action = "更新", description = "update freeswitch number")
    @Override
    public ResponseEntity<?> update(FreeSwitchNumberRequest request) {

        FreeSwitchNumberResponse number = freeSwitchNumberRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(number));
    }

    @ActionAnnotation(title = "FreeSwitch用户", action = "删除", description = "delete freeswitch number")
    @Override
    public ResponseEntity<?> delete(FreeSwitchNumberRequest request) {

        freeSwitchNumberRestService.deleteByUid(request.getUid());

        return ResponseEntity.ok(JsonResult.success("删除成功", request.getUid()));
    }

    @ActionAnnotation(title = "FreeSwitch用户", action = "导出", description = "export freeswitch number to excel")
    @Override
    @GetMapping("/export")
    public Object export(FreeSwitchNumberRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            freeSwitchNumberRestService,
            FreeSwitchNumberExcel.class,
            "FreeSwitch用户",
            "freeswitch_number"
        );
    }

}
