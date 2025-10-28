package com.bytedesk.ai.robot_settings;

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

@Tag(name = "机器人配置管理", description = "机器人配置相关接口")
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/robot/settings")
public class RobotSettingsRestController extends BaseRestController<RobotSettingsRequest, RobotSettingsRestService> {

    private final RobotSettingsRestService robotSettingsRestService;

    @Operation(summary = "查询组织下的机器人配置", description = "根据组织ID查询机器人配置列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = RobotSettingsResponse.class)))
    @Override
    public ResponseEntity<?> queryByOrg(RobotSettingsRequest request) {
        Page<RobotSettingsResponse> page = robotSettingsRestService.queryByOrg(request);
        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "根据UID查询机器人配置", description = "根据UID查询机器人配置详情")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = RobotSettingsResponse.class)))
    @Override
    public ResponseEntity<?> queryByUid(RobotSettingsRequest request) {
        RobotSettingsResponse resp = robotSettingsRestService.queryByUid(request);
        return ResponseEntity.ok(JsonResult.success(resp));
    }

    @Operation(summary = "创建机器人配置", description = "创建新的机器人配置")
    @ApiResponse(responseCode = "200", description = "创建成功",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = RobotSettingsResponse.class)))
    @Override
    public ResponseEntity<?> create(@RequestBody RobotSettingsRequest request) {
        RobotSettingsResponse resp = robotSettingsRestService.create(request);
        if (resp == null) {
            return ResponseEntity.ok(JsonResult.error("create robot settings failed"));
        }
        return ResponseEntity.ok(JsonResult.success(resp));
    }

    @Operation(summary = "更新机器人配置", description = "更新机器人配置信息")
    @ApiResponse(responseCode = "200", description = "更新成功",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = RobotSettingsResponse.class)))
    @Override
    public ResponseEntity<?> update(@RequestBody RobotSettingsRequest request) {
        RobotSettingsResponse resp = robotSettingsRestService.update(request);
        return ResponseEntity.ok(JsonResult.success(resp));
    }

    @Operation(summary = "删除机器人配置", description = "删除指定的机器人配置")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @Override
    public ResponseEntity<?> delete(@RequestBody RobotSettingsRequest request) {
        robotSettingsRestService.deleteByUid(request.getUid());
        return ResponseEntity.ok(JsonResult.success(request));
    }

    @Operation(summary = "发布机器人配置", description = "将草稿版本发布为线上版本")
    @ApiResponse(responseCode = "200", description = "发布成功",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = RobotSettingsResponse.class)))
    @RequestMapping("/publish")
    public ResponseEntity<?> publish(@RequestBody RobotSettingsRequest request) {
        RobotSettingsResponse resp = robotSettingsRestService.publish(request.getUid());
        return ResponseEntity.ok(JsonResult.success(resp));
    }

}
