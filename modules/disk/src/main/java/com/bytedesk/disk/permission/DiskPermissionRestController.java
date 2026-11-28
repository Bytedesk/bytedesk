package com.bytedesk.disk.permission;

import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.constant.I18Consts;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * 网盘目录授权 Controller
 */
@Tag(name = "网盘目录授权", description = "文件夹级权限管理 API")
@RestController
@RequestMapping("/api/v1/disk/permission")
@RequiredArgsConstructor
public class DiskPermissionRestController {

    private final DiskPermissionRestService diskPermissionRestService;

    @PreAuthorize(DiskPermissions.HAS_DISK_READ)
    @ActionAnnotation(title = I18Consts.I18N_DISK, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query disk")
    @Operation(summary = "查询目录授权列表")
    @GetMapping("/query")
    public ResponseEntity<?> query(DiskPermissionRequest request) {
        Page<DiskPermissionResponse> page = diskPermissionRestService.queryByFolder(request);
        return ResponseEntity.ok(JsonResult.success(page));
    }

    @PreAuthorize(DiskPermissions.HAS_DISK_CREATE)
    @ActionAnnotation(title = I18Consts.I18N_DISK, action = I18Consts.I18N_ACTION_CREATE, description = "create disk")
    @Operation(summary = "创建/合并授权")
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody DiskPermissionRequest request) {
        return ResponseEntity.ok(JsonResult.success(diskPermissionRestService.create(request)));
    }

    @PreAuthorize(DiskPermissions.HAS_DISK_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_DISK, action = I18Consts.I18N_ACTION_UPDATE, description = "update disk")
    @Operation(summary = "更新授权")
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody DiskPermissionRequest request) {
        return ResponseEntity.ok(JsonResult.success(diskPermissionRestService.update(request)));
    }

    @PreAuthorize(DiskPermissions.HAS_DISK_DELETE)
    @ActionAnnotation(title = I18Consts.I18N_DISK, action = I18Consts.I18N_ACTION_DELETE, description = "delete disk")
    @Operation(summary = "删除授权")
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody DiskPermissionRequest request) {
        diskPermissionRestService.delete(request);
        return ResponseEntity.ok(JsonResult.success("delete success", request.getUid()));
    }
}
