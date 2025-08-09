/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-08 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-08 18:49:05
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.cdr;

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
@RequestMapping("/api/v1/freeswitch/cdr")
@AllArgsConstructor
public class CallCdrRestController extends BaseRestController<CallCdrRequest> {

    private final CallCdrRestService freeSwitchCdrRestService;

    @ActionAnnotation(title = "Call通话详单", action = "组织查询", description = "query freeswitch cdr by org")
    @Override
    public ResponseEntity<?> queryByOrg(CallCdrRequest request) {
        
        Page<CallCdrResponse> cdrs = freeSwitchCdrRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(cdrs));
    }

    @ActionAnnotation(title = "Call通话详单", action = "用户查询", description = "query freeswitch cdr by user")
    @Override
    public ResponseEntity<?> queryByUser(CallCdrRequest request) {
        
        Page<CallCdrResponse> cdrs = freeSwitchCdrRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(cdrs));
    }

    @ActionAnnotation(title = "Call通话详单", action = "查询", description = "query freeswitch cdr by uid")
    @Override
    public ResponseEntity<?> queryByUid(CallCdrRequest request) {

        CallCdrResponse cdr = freeSwitchCdrRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(cdr));
    }

    @ActionAnnotation(title = "Call通话详单", action = "创建", description = "create freeswitch cdr")
    @Override
    public ResponseEntity<?> create(CallCdrRequest request) {

        CallCdrResponse cdr = freeSwitchCdrRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(cdr));
    }

    @ActionAnnotation(title = "Call通话详单", action = "更新", description = "update freeswitch cdr")
    @Override
    public ResponseEntity<?> update(CallCdrRequest request) {

        CallCdrResponse cdr = freeSwitchCdrRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(cdr));
    }

    @ActionAnnotation(title = "Call通话详单", action = "删除", description = "delete freeswitch cdr")
    @Override
    public ResponseEntity<?> delete(CallCdrRequest request) {

        freeSwitchCdrRestService.deleteByUid(request.getUid());

        return ResponseEntity.ok(JsonResult.success("删除成功", request.getUid()));
    }

    @ActionAnnotation(title = "Call通话详单", action = "导出", description = "export freeswitch cdr to excel")
    @Override
    @GetMapping("/export")
    public Object export(CallCdrRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            freeSwitchCdrRestService,
            CallCdrExcel.class,
            "Call通话详单",
            "freeswitch_cdr"
        );
    }

}
