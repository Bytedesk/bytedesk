/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-08 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-08 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.user;

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
@RequestMapping("/api/v1/freeswitch/user")
@AllArgsConstructor
public class FreeSwitchUserRestController extends BaseRestController<FreeSwitchUserRequest> {

    private final FreeSwitchUserRestService freeSwitchUserRestService;

    @ActionAnnotation(title = "FreeSwitch用户", action = "组织查询", description = "query freeswitch user by org")
    @Override
    public ResponseEntity<?> queryByOrg(FreeSwitchUserRequest request) {
        
        Page<FreeSwitchUserResponse> users = freeSwitchUserRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(users));
    }

    @ActionAnnotation(title = "FreeSwitch用户", action = "用户查询", description = "query freeswitch user by user")
    @Override
    public ResponseEntity<?> queryByUser(FreeSwitchUserRequest request) {
        
        Page<FreeSwitchUserResponse> users = freeSwitchUserRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(users));
    }

    @ActionAnnotation(title = "FreeSwitch用户", action = "查询", description = "query freeswitch user by uid")
    @Override
    public ResponseEntity<?> queryByUid(FreeSwitchUserRequest request) {

        FreeSwitchUserResponse user = freeSwitchUserRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(user));
    }

    @ActionAnnotation(title = "FreeSwitch用户", action = "创建", description = "create freeswitch user")
    @Override
    public ResponseEntity<?> create(FreeSwitchUserRequest request) {

        FreeSwitchUserResponse user = freeSwitchUserRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(user));
    }

    @ActionAnnotation(title = "FreeSwitch用户", action = "更新", description = "update freeswitch user")
    @Override
    public ResponseEntity<?> update(FreeSwitchUserRequest request) {

        FreeSwitchUserResponse user = freeSwitchUserRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(user));
    }

    @ActionAnnotation(title = "FreeSwitch用户", action = "删除", description = "delete freeswitch user")
    @Override
    public ResponseEntity<?> delete(FreeSwitchUserRequest request) {

        freeSwitchUserRestService.deleteByUid(request.getUid());

        return ResponseEntity.ok(JsonResult.success("删除成功", request.getUid()));
    }

    @ActionAnnotation(title = "FreeSwitch用户", action = "导出", description = "export freeswitch user to excel")
    @Override
    @GetMapping("/export")
    public Object export(FreeSwitchUserRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            freeSwitchUserRestService,
            FreeSwitchUserExcel.class,
            "FreeSwitch用户",
            "freeswitch_user"
        );
    }

}
