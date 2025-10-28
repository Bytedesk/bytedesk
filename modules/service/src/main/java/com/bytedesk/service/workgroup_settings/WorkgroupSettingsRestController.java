package com.bytedesk.service.workgroup_settings;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
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

@Tag(name = "技能组配置管理", description = "技能组配置相关接口")
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/workgroup/settings")
public class WorkgroupSettingsRestController extends BaseRestController<WorkgroupSettingsRequest, WorkgroupSettingsRestService> {

    private final WorkgroupSettingsRestService workgroupSettingsRestService;

    @Operation(summary = "查询组织下的技能组配置", description = "根据组织ID查询技能组配置列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = WorkgroupSettingsResponse.class)))
    @Override
    public ResponseEntity<?> queryByOrg(WorkgroupSettingsRequest request) {
        Page<WorkgroupSettingsResponse> page = workgroupSettingsRestService.queryByOrg(request);
        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "根据UID查询技能组配置", description = "根据UID查询技能组配置详情")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = WorkgroupSettingsResponse.class)))
    @Override
    public ResponseEntity<?> queryByUid(WorkgroupSettingsRequest request) {
        WorkgroupSettingsResponse resp = workgroupSettingsRestService.queryByUid(request);
        return ResponseEntity.ok(JsonResult.success(resp));
    }

    @Operation(summary = "创建技能组配置", description = "创建新的技能组配置")
    @ApiResponse(responseCode = "200", description = "创建成功",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = WorkgroupSettingsResponse.class)))
    @Override
    public ResponseEntity<?> create(@RequestBody WorkgroupSettingsRequest request) {
        WorkgroupSettingsResponse resp = workgroupSettingsRestService.create(request);
        if (resp == null) {
            return ResponseEntity.ok(JsonResult.error("create workgroup settings failed"));
        }
        return ResponseEntity.ok(JsonResult.success(resp));
    }

    @Operation(summary = "更新技能组配置", description = "更新技能组配置信息")
    @ApiResponse(responseCode = "200", description = "更新成功",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = WorkgroupSettingsResponse.class)))
    @Override
    public ResponseEntity<?> update(@RequestBody WorkgroupSettingsRequest request) {
        WorkgroupSettingsResponse resp = workgroupSettingsRestService.update(request);
        return ResponseEntity.ok(JsonResult.success(resp));
    }

    @Operation(summary = "删除技能组配置", description = "删除指定的技能组配置")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @Override
    public ResponseEntity<?> delete(@RequestBody WorkgroupSettingsRequest request) {
        workgroupSettingsRestService.deleteByUid(request.getUid());
        return ResponseEntity.ok(JsonResult.success(request));
    }

    @Operation(summary = "发布技能组配置", description = "将草稿版本发布为线上版本")
    @ApiResponse(responseCode = "200", description = "发布成功",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = WorkgroupSettingsResponse.class)))
    @RequestMapping("/publish")
    public ResponseEntity<?> publish(@RequestBody WorkgroupSettingsRequest request) {
        WorkgroupSettingsResponse resp = workgroupSettingsRestService.publish(request.getUid());
        return ResponseEntity.ok(JsonResult.success(resp));
    }

}
