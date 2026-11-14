/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-11-12
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *   selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms under the license.
 *   Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *   contact: 270580156@qq.com
 *   联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.core.jobtitle;

import org.springframework.context.annotation.Description;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 职务字典管理：组织级职务选项 CRUD，供成员 jobTitle 快速选择。
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/jobtitle")
@AllArgsConstructor
@Tag(name = "JobTitle Management", description = "Job title dictionary management APIs for member job title selection")
@Description("JobTitle Management Controller - organization-level job title dictionary")
public class JobTitleRestController extends BaseRestController<JobTitleRequest, JobTitleRestService> {

    private final JobTitleRestService jobTitleRestService;

    @ActionAnnotation(title = I18Consts.I18N_JOBTITLE, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query job titles by org")
    @Operation(summary = "Query Job Titles by Organization", description = "Retrieve job title dictionary for the current organization")
    @PreAuthorize(JobTitlePermissions.HAS_JOBTITLE_READ)
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(JobTitleRequest request) {
        Page<JobTitleResponse> jobTitles = jobTitleRestService.queryByOrg(request);
        return ResponseEntity.ok(JsonResult.success(jobTitles));
    }

    @ActionAnnotation(title = I18Consts.I18N_JOBTITLE, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query job title by uid")
    @Operation(summary = "Query Job Title by UID", description = "Retrieve a specific job title by its unique identifier")
    @PreAuthorize(JobTitlePermissions.HAS_JOBTITLE_READ)
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(JobTitleRequest request) {
        JobTitleResponse jobTitle = jobTitleRestService.queryByUid(request);
        return ResponseEntity.ok(JsonResult.success(jobTitle));
    }

    @ActionAnnotation(title = I18Consts.I18N_JOBTITLE, action = I18Consts.I18N_ACTION_CREATE, description = "create job title")
    @Operation(summary = "Create Job Title", description = "Create a new job title in the current organization")
    @Override
    @PreAuthorize(JobTitlePermissions.HAS_JOBTITLE_CREATE)
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody JobTitleRequest request) {
        JobTitleResponse jobTitle = jobTitleRestService.create(request);
        return ResponseEntity.ok(JsonResult.success(jobTitle));
    }

    @ActionAnnotation(title = I18Consts.I18N_JOBTITLE, action = I18Consts.I18N_ACTION_UPDATE, description = "update job title")
    @Operation(summary = "Update Job Title", description = "Update an existing job title")
    @Override
    @PreAuthorize(JobTitlePermissions.HAS_JOBTITLE_UPDATE)
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody JobTitleRequest request) {
        JobTitleResponse jobTitle = jobTitleRestService.update(request);
        return ResponseEntity.ok(JsonResult.success(jobTitle));
    }

    @ActionAnnotation(title = I18Consts.I18N_JOBTITLE, action = I18Consts.I18N_ACTION_INIT, description = "init default job titles")
    @Operation(summary = "Init Default Job Titles", description = "Create missing default job titles for the current organization; existing ones are kept")
    @PreAuthorize(JobTitlePermissions.HAS_JOBTITLE_CREATE)
    @PostMapping("/init")
    public ResponseEntity<?> init() {
        int created = jobTitleRestService.initDefaultJobTitlesManually();
        return ResponseEntity.ok(JsonResult.success(
                I18Consts.withArgs(I18Consts.I18N_JOBTITLE_INIT_SUCCESS, created), created));
    }

    @ActionAnnotation(title = I18Consts.I18N_JOBTITLE, action = I18Consts.I18N_ACTION_DELETE, description = "delete job title")
    @Operation(summary = "Delete Job Title", description = "Soft delete a job title; members keep their stored job title strings")
    @Override
    @PreAuthorize(JobTitlePermissions.HAS_JOBTITLE_DELETE)
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody JobTitleRequest request) {
        jobTitleRestService.delete(request);
        return ResponseEntity.ok(JsonResult.success());
    }
}
