/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-14 09:40:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-08 22:35:34
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.quartz;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

/**
 * 
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/quartz")
@Tag(name = "Quartz Job Management", description = "Quartz job scheduling and management APIs")
public class QuartzRestController extends BaseRestController<QuartzRequest> {

    private QuartzRestService quartzRestService;

    @Operation(summary = "Query Jobs by Organization", description = "Retrieve Quartz jobs for the current organization")
    @Override
    public ResponseEntity<?> queryByOrg(QuartzRequest request) {

        Page<QuartzResponse> pageResult = quartzRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(pageResult));
    }

    @Operation(summary = "Query Jobs by User", description = "Retrieve Quartz jobs for the current user")
    @Override
    public ResponseEntity<?> queryByUser(QuartzRequest request) {

        Page<QuartzResponse> pageResult = quartzRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(pageResult));
    }

    @Operation(summary = "Create Job", description = "Create a new Quartz job")
    @Override
    public ResponseEntity<?> create(@RequestBody QuartzRequest request) {

        QuartzResponse response = quartzRestService.create(request);
        if (response == null) {
            return ResponseEntity.badRequest().body(JsonResult.error("jobName already exists"));
        }

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Operation(summary = "Update Job", description = "Update an existing Quartz job")
    @Override
    public ResponseEntity<?> update(@RequestBody QuartzRequest request) {

        QuartzResponse response = quartzRestService.update(request);
        if (response == null) {
            return ResponseEntity.badRequest().body(JsonResult.error("uid not exists"));
        }

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Operation(summary = "Delete Job", description = "Delete a Quartz job")
    @Override
    public ResponseEntity<?> delete(@RequestBody QuartzRequest request) {

        quartzRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    //
    @Operation(summary = "Start Job", description = "Start a Quartz job")
    @PostMapping("/startJob")
    public ResponseEntity<?> startJob(@RequestBody QuartzRequest request) {
        quartzRestService.startJob(request);
        return ResponseEntity.ok(JsonResult.success(request));
    }

    @Operation(summary = "Pause Job", description = "Pause a running Quartz job")
    @PostMapping("/pauseJob")
    public ResponseEntity<?> pauseJob(@RequestBody QuartzRequest request) {
        quartzRestService.pauseJob(request);
        return ResponseEntity.ok(JsonResult.success(request));
    }

    @Operation(summary = "Resume Job", description = "Resume a paused Quartz job")
    @PostMapping("/resumeJob")
    public ResponseEntity<?> resumeJob(@RequestBody QuartzRequest request) {
        quartzRestService.resumeJob(request);
        return ResponseEntity.ok(JsonResult.success(request));
    }

    @Operation(summary = "Delete Job from Scheduler", description = "Remove a job from the Quartz scheduler")
    @PostMapping("/deleteJob")
    public ResponseEntity<?> deleteJob(@RequestBody QuartzRequest request) {
        quartzRestService.deleteJob(request);
        return ResponseEntity.ok(JsonResult.success(request));
    }

    @Override
    public Object export(QuartzRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Override
    public ResponseEntity<?> queryByUid(QuartzRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

}
