/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-21 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-09-22 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is breach of the terms of the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.push_android_device;

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
 * 设备推送绑定接口（由 push 包迁入 push_device 包，接口契约不变）
 *
 * <p>两组入口：
 * 1. 移动端（Android 阿里云推送）登录后上报 bind、登出前 unbind——真实绑定在
 *    阿里云侧由 aliyun_push.bindAccount(userUid) 完成，本接口维护后端对账记录；
 * 2. 管理后台 query/create/update/delete/export 维护绑定信息。
 */
@RestController
@RequestMapping("/api/v1/push/device")
@AllArgsConstructor
@Tag(name = "Push Device", description = "Device push binding reconciliation and management APIs")
@Description("Push Device Controller - device push bind/unbind reconciliation and admin management")
public class PushDeviceRestController extends BaseRestController<PushDeviceRequest, PushDeviceRestService> {

    private final PushDeviceRestService pushDeviceRestService;

    // ==================================================================================
    // 移动端对账入口（契约不可变）
    // ==================================================================================

    @ActionAnnotation(title = I18Consts.I18N_PUSH_DEVICE, action = I18Consts.I18N_ACTION_CREATE, description = "bind current user push device")
    @Operation(summary = "Bind Current User Push Device", description = "Create or update the push device binding (provider=aliyun, account=userUid, deviceId) for the current authenticated user. Account ownership is enforced server-side.")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/bind")
    public ResponseEntity<?> bind(@RequestBody PushDeviceRequest request) {
        PushDeviceEntity entity = pushDeviceRestService.bind(request);
        return ResponseEntity.ok(JsonResult.success(entity.getUid()));
    }

    @ActionAnnotation(title = I18Consts.I18N_PUSH_DEVICE, action = I18Consts.I18N_ACTION_DELETE, description = "unbind current user push device")
    @Operation(summary = "Unbind Current User Push Device", description = "Soft-delete the push device binding for the current authenticated user. The real unbind happens on Aliyun side via unbindAccount.")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/unbind")
    public ResponseEntity<?> unbind(@RequestBody PushDeviceRequest request) {
        pushDeviceRestService.unbind(request);
        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_PUSH_DEVICE, action = I18Consts.I18N_ACTION_UPDATE, description = "test push device binding")
    @Operation(summary = "Test Push Device Binding", description = "Send a custom test notification to the account bound to the given push device binding, via the currently active provider (bytedesk.push.provider). Accepted by provider does not guarantee delivery.")
    @PreAuthorize(PushDevicePermissions.HAS_PUSH_DEVICE_UPDATE)
    @PostMapping("/test")
    public ResponseEntity<?> test(@RequestBody PushDeviceRequest request) {
        com.bytedesk.core.push_android_device.service.PushDeviceResult result = pushDeviceRestService.test(request);
        if (result == null || !result.isSuccess()) {
            String error = result != null ? result.getError() : "unknown";
            return ResponseEntity.ok(JsonResult.error(error));
        }
        return ResponseEntity.ok(JsonResult.success(result));
    }

    // ==================================================================================
    // 管理后台 CRUD（参考 TagRestController）
    // ==================================================================================

    @ActionAnnotation(title = I18Consts.I18N_PUSH_DEVICE, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query push device by org")
    @Operation(summary = "Query Push Devices by Organization", description = "Retrieve push device bindings for the current organization")
    @PreAuthorize(PushDevicePermissions.HAS_PUSH_DEVICE_READ)
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(PushDeviceRequest request) {

        Page<PushDeviceResponse> pushDevices = pushDeviceRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(pushDevices));
    }

    @ActionAnnotation(title = I18Consts.I18N_PUSH_DEVICE, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query push device by user")
    @Operation(summary = "Query Push Devices by User", description = "Retrieve push device bindings for the current user")
    @PreAuthorize(PushDevicePermissions.HAS_PUSH_DEVICE_READ)
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(PushDeviceRequest request) {

        Page<PushDeviceResponse> pushDevices = pushDeviceRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(pushDevices));
    }

    @ActionAnnotation(title = I18Consts.I18N_PUSH_DEVICE, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query push device by uid")
    @Operation(summary = "Query Push Device by UID", description = "Retrieve a specific push device binding by its unique identifier")
    @PreAuthorize(PushDevicePermissions.HAS_PUSH_DEVICE_READ)
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(PushDeviceRequest request) {

        PushDeviceResponse pushDevice = pushDeviceRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(pushDevice));
    }

    @ActionAnnotation(title = I18Consts.I18N_PUSH_DEVICE, action = I18Consts.I18N_ACTION_CREATE, description = "create push device")
    @Operation(summary = "Create Push Device", description = "Create a new push device binding (admin, idempotent upsert by account+type)")
    @Override
    @PreAuthorize(PushDevicePermissions.HAS_PUSH_DEVICE_CREATE)
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody PushDeviceRequest request) {

        PushDeviceResponse pushDevice = pushDeviceRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(pushDevice));
    }

    @ActionAnnotation(title = I18Consts.I18N_PUSH_DEVICE, action = I18Consts.I18N_ACTION_UPDATE, description = "update push device")
    @Operation(summary = "Update Push Device", description = "Update an existing push device binding")
    @Override
    @PreAuthorize(PushDevicePermissions.HAS_PUSH_DEVICE_UPDATE)
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody PushDeviceRequest request) {

        PushDeviceResponse pushDevice = pushDeviceRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(pushDevice));
    }

    @ActionAnnotation(title = I18Consts.I18N_PUSH_DEVICE, action = I18Consts.I18N_ACTION_DELETE, description = "delete push device")
    @Operation(summary = "Delete Push Device", description = "Soft-delete a push device binding")
    @Override
    @PreAuthorize(PushDevicePermissions.HAS_PUSH_DEVICE_DELETE)
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody PushDeviceRequest request) {

        pushDeviceRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_PUSH_DEVICE, action = I18Consts.I18N_ACTION_EXPORT, description = "export push device")
    @Operation(summary = "Export Push Devices", description = "Export push device bindings to Excel format")
    @Override
    @PreAuthorize(PushDevicePermissions.HAS_PUSH_DEVICE_EXPORT)
    @GetMapping("/export")
    public Object export(PushDeviceRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            pushDeviceRestService,
            PushDeviceExcel.class,
            "PushDevice",
            "push_device"
        );
    }
}
