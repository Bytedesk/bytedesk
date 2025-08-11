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
package com.bytedesk.call.users;

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
public class CallUserRestController extends BaseRestController<CallUserRequest> {

    private final CallUserRestService freeSwitchNumberRestService;

    @ActionAnnotation(title = "Call用户", action = "组织查询", description = "query freeswitch number by org")
    @Override
    public ResponseEntity<?> queryByOrg(CallUserRequest request) {
        
        Page<CallUserResponse> numbers = freeSwitchNumberRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(numbers));
    }

    @ActionAnnotation(title = "Call用户", action = "用户查询", description = "query freeswitch number by number")
    @Override
    public ResponseEntity<?> queryByUser(CallUserRequest request) {
        
        Page<CallUserResponse> numbers = freeSwitchNumberRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(numbers));
    }

    @ActionAnnotation(title = "Call用户", action = "查询", description = "query freeswitch number by uid")
    @Override
    public ResponseEntity<?> queryByUid(CallUserRequest request) {

        CallUserResponse number = freeSwitchNumberRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(number));
    }

    @ActionAnnotation(title = "Call用户", action = "创建", description = "create freeswitch number")
    @Override
    public ResponseEntity<?> create(CallUserRequest request) {

        CallUserResponse number = freeSwitchNumberRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(number));
    }

    @ActionAnnotation(title = "Call用户", action = "更新", description = "update freeswitch number")
    @Override
    public ResponseEntity<?> update(CallUserRequest request) {

        CallUserResponse number = freeSwitchNumberRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(number));
    }

    @ActionAnnotation(title = "Call用户", action = "删除", description = "delete freeswitch number")
    @Override
    public ResponseEntity<?> delete(CallUserRequest request) {

        freeSwitchNumberRestService.deleteByUid(request.getUid());

        return ResponseEntity.ok(JsonResult.success("删除成功", request.getUid()));
    }

    @ActionAnnotation(title = "Call用户", action = "导出", description = "export freeswitch number to excel")
    @Override
    @GetMapping("/export")
    public Object export(CallUserRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            freeSwitchNumberRestService,
            CallUserExcel.class,
            "Call用户",
            "freeswitch_number"
        );
    }

}
