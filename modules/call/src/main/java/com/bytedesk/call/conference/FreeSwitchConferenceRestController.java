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
package com.bytedesk.call.conference;

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
@RequestMapping("/api/v1/freeswitch/conference")
@AllArgsConstructor
public class FreeSwitchConferenceRestController extends BaseRestController<FreeSwitchConferenceRequest> {

    private final FreeSwitchConferenceRestService freeSwitchConferenceRestService;

    @ActionAnnotation(title = "FreeSwitch会议室", action = "组织查询", description = "query freeswitch conference by org")
    @Override
    public ResponseEntity<?> queryByOrg(FreeSwitchConferenceRequest request) {
        
        Page<FreeSwitchConferenceResponse> conferences = freeSwitchConferenceRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(conferences));
    }

    @ActionAnnotation(title = "FreeSwitch会议室", action = "用户查询", description = "query freeswitch conference by user")
    @Override
    public ResponseEntity<?> queryByUser(FreeSwitchConferenceRequest request) {
        
        Page<FreeSwitchConferenceResponse> conferences = freeSwitchConferenceRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(conferences));
    }

    @ActionAnnotation(title = "FreeSwitch会议室", action = "查询", description = "query freeswitch conference by uid")
    @Override
    public ResponseEntity<?> queryByUid(FreeSwitchConferenceRequest request) {

        FreeSwitchConferenceResponse conference = freeSwitchConferenceRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(conference));
    }

    @ActionAnnotation(title = "FreeSwitch会议室", action = "创建", description = "create freeswitch conference")
    @Override
    public ResponseEntity<?> create(FreeSwitchConferenceRequest request) {

        FreeSwitchConferenceResponse conference = freeSwitchConferenceRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(conference));
    }

    @ActionAnnotation(title = "FreeSwitch会议室", action = "更新", description = "update freeswitch conference")
    @Override
    public ResponseEntity<?> update(FreeSwitchConferenceRequest request) {

        FreeSwitchConferenceResponse conference = freeSwitchConferenceRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(conference));
    }

    @ActionAnnotation(title = "FreeSwitch会议室", action = "删除", description = "delete freeswitch conference")
    @Override
    public ResponseEntity<?> delete(FreeSwitchConferenceRequest request) {

        freeSwitchConferenceRestService.deleteByUid(request.getUid());

        return ResponseEntity.ok(JsonResult.success("删除成功", request.getUid()));
    }

    @ActionAnnotation(title = "FreeSwitch会议室", action = "导出", description = "export freeswitch conference to excel")
    @Override
    @GetMapping("/export")
    public Object export(FreeSwitchConferenceRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            freeSwitchConferenceRestService,
            FreeSwitchConferenceExcel.class,
            "FreeSwitch会议室",
            "freeswitch_conference"
        );
    }

}
