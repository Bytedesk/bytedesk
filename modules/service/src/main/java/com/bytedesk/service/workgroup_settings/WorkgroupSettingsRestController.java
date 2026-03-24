package com.bytedesk.service.workgroup_settings;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Workgroup Settings Management", description = "Workgroup settings management APIs")
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/workgroup/settings")
public class WorkgroupSettingsRestController extends BaseRestController<WorkgroupSettingsRequest, WorkgroupSettingsRestService> {

    private final WorkgroupSettingsRestService workgroupSettingsRestService;

    @Operation(summary = "Query Workgroup Settings by Organization", description = "Retrieve workgroup settings list by organization ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = WorkgroupSettingsResponse.class)))
    @GetMapping("/query/org")
    @PreAuthorize(WorkgroupSettingsPermissions.HAS_WORKGROUP_SETTINGS_READ)
    @Override
    public ResponseEntity<?> queryByOrg(WorkgroupSettingsRequest request) {
        Page<WorkgroupSettingsResponse> page = workgroupSettingsRestService.queryByOrg(request);
        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "Query Workgroup Settings by User", description = "Retrieve workgroup settings list by user ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = WorkgroupSettingsResponse.class)))
    @GetMapping({ "/query", "/query/user" })
    @PreAuthorize(WorkgroupSettingsPermissions.HAS_WORKGROUP_SETTINGS_READ)
    @Override
    public ResponseEntity<?> queryByUser(WorkgroupSettingsRequest request) {
        Page<WorkgroupSettingsResponse> page = workgroupSettingsRestService.queryByUser(request);
        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "Query Workgroup Settings by UID", description = "Retrieve workgroup settings details by UID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = WorkgroupSettingsResponse.class)))
    @GetMapping("/query/uid")
    @PreAuthorize(WorkgroupSettingsPermissions.HAS_WORKGROUP_SETTINGS_READ)
    @Override
    public ResponseEntity<?> queryByUid(WorkgroupSettingsRequest request) {
        WorkgroupSettingsResponse resp = workgroupSettingsRestService.queryByUid(request);
        return ResponseEntity.ok(JsonResult.success(resp));
    }

    @Operation(summary = "Create Workgroup Settings", description = "Create new workgroup settings")
    @ApiResponse(responseCode = "200", description = "Created successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = WorkgroupSettingsResponse.class)))
    @PostMapping("/create")
    @PreAuthorize(WorkgroupSettingsPermissions.HAS_WORKGROUP_SETTINGS_CREATE)
    @Override
    public ResponseEntity<?> create(@RequestBody WorkgroupSettingsRequest request) {
        WorkgroupSettingsResponse resp = workgroupSettingsRestService.create(request);
        if (resp == null) {
            return ResponseEntity.ok(JsonResult.error("create workgroup settings failed"));
        }
        return ResponseEntity.ok(JsonResult.success(resp));
    }

    @Operation(summary = "Update Workgroup Settings", description = "Update workgroup settings information")
    @ApiResponse(responseCode = "200", description = "Updated successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = WorkgroupSettingsResponse.class)))
    @PostMapping("/update")
    @PreAuthorize(WorkgroupSettingsPermissions.HAS_WORKGROUP_SETTINGS_UPDATE)
    @Override
    public ResponseEntity<?> update(@RequestBody WorkgroupSettingsRequest request) {
        WorkgroupSettingsResponse resp = workgroupSettingsRestService.update(request);
        return ResponseEntity.ok(JsonResult.success(resp));
    }

    @Operation(summary = "Delete Workgroup Settings", description = "Delete the specified workgroup settings")
    @ApiResponse(responseCode = "200", description = "Deleted successfully")
    @PostMapping("/delete")
    @PreAuthorize(WorkgroupSettingsPermissions.HAS_WORKGROUP_SETTINGS_DELETE)
    @Override
    public ResponseEntity<?> delete(@RequestBody WorkgroupSettingsRequest request) {
        workgroupSettingsRestService.deleteByUid(request.getUid());
        return ResponseEntity.ok(JsonResult.success(request));
    }

    @Operation(summary = "Enable Workgroup Settings", description = "Enable the specified workgroup settings")
    @ApiResponse(responseCode = "200", description = "Enabled successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = WorkgroupSettingsResponse.class)))
    @PreAuthorize(WorkgroupSettingsPermissions.HAS_WORKGROUP_SETTINGS_UPDATE)
    @RequestMapping("/enable")
    public ResponseEntity<?> enable(@RequestBody WorkgroupSettingsRequest request) {
        WorkgroupSettingsResponse resp = workgroupSettingsRestService.enable(request.getUid());
        return ResponseEntity.ok(JsonResult.success(resp));
    }

    @Operation(summary = "Disable Workgroup Settings", description = "Disable the specified workgroup settings")
    @ApiResponse(responseCode = "200", description = "Disabled successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = WorkgroupSettingsResponse.class)))
    @PreAuthorize(WorkgroupSettingsPermissions.HAS_WORKGROUP_SETTINGS_UPDATE)
    @RequestMapping("/disable")
    public ResponseEntity<?> disable(@RequestBody WorkgroupSettingsRequest request) {
        WorkgroupSettingsResponse resp = workgroupSettingsRestService.disable(request.getUid());
        return ResponseEntity.ok(JsonResult.success(resp));
    }

    @Operation(summary = "Publish Workgroup Settings", description = "Publish the draft version to production")
    @ApiResponse(responseCode = "200", description = "Published successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = WorkgroupSettingsResponse.class)))
    @PreAuthorize(WorkgroupSettingsPermissions.HAS_WORKGROUP_SETTINGS_UPDATE)
    @RequestMapping("/publish")
    public ResponseEntity<?> publish(@RequestBody WorkgroupSettingsRequest request) {
        WorkgroupSettingsResponse resp = workgroupSettingsRestService.publish(request.getUid());
        return ResponseEntity.ok(JsonResult.success(resp));
    }

}
