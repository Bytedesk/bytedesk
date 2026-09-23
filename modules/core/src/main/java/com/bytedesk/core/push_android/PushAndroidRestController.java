/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-23 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-09-23 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.core.push_android;

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
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

/**
 * Android 推送投递记录接口（仅查询+删除+导出，推送记录不可编辑）
 *
 * <p>记录由 PushAndroidRecordService 在 PushDeviceRouter 调用供应商后系统写入。
 *
 * <p>见 docs/plans/2026-09-23-android-push-record-entity-plan.md §3.6
 */
@RestController
@RequestMapping("/api/v1/push/android")
@AllArgsConstructor
@Tag(name = "Push Android", description = "Android push delivery record management APIs")
@Description("Push Android Controller - android push delivery record query/delete/export APIs")
public class PushAndroidRestController extends BaseRestController<PushAndroidRequest, PushAndroidRestService> {

    private final PushAndroidRestService pushAndroidRestService;

    @ActionAnnotation(title = I18Consts.I18N_PUSH_ANDROID, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query push android by org")
    @Operation(summary = "Query Push Android Records by Organization", description = "Retrieve android push delivery records for the current organization")
    @PreAuthorize(PushAndroidPermissions.HAS_PUSH_ANDROID_READ)
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(PushAndroidRequest request) {

        Page<PushAndroidResponse> pushAndroids = pushAndroidRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(pushAndroids));
    }

    @ActionAnnotation(title = I18Consts.I18N_PUSH_ANDROID, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query push android by user")
    @Operation(summary = "Query Push Android Records by User", description = "Retrieve android push delivery records for the current user")
    @PreAuthorize(PushAndroidPermissions.HAS_PUSH_ANDROID_READ)
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(PushAndroidRequest request) {

        Page<PushAndroidResponse> pushAndroids = pushAndroidRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(pushAndroids));
    }

    @ActionAnnotation(title = I18Consts.I18N_PUSH_ANDROID, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query push android by uid")
    @Operation(summary = "Query Push Android Record by UID", description = "Retrieve a specific android push delivery record by its unique identifier")
    @PreAuthorize(PushAndroidPermissions.HAS_PUSH_ANDROID_READ)
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(PushAndroidRequest request) {

        PushAndroidResponse pushAndroid = pushAndroidRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(pushAndroid));
    }

    @ActionAnnotation(title = I18Consts.I18N_PUSH_ANDROID, action = I18Consts.I18N_ACTION_DELETE, description = "delete push android record")
    @Operation(summary = "Delete Push Android Record", description = "Soft-delete an android push delivery record")
    @Override
    @PreAuthorize(PushAndroidPermissions.HAS_PUSH_ANDROID_DELETE)
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody PushAndroidRequest request) {

        pushAndroidRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_PUSH_ANDROID, action = I18Consts.I18N_ACTION_EXPORT, description = "export push android records")
    @Operation(summary = "Export Push Android Records", description = "Export android push delivery records to Excel format")
    @Override
    @PreAuthorize(PushAndroidPermissions.HAS_PUSH_ANDROID_EXPORT)
    @GetMapping("/export")
    public Object export(PushAndroidRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            pushAndroidRestService,
            PushAndroidExcel.class,
            "PushAndroid",
            "push_android"
        );
    }

}
