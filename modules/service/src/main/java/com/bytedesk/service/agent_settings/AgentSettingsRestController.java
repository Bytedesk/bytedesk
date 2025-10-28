package com.bytedesk.service.agent_settings;

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

@Tag(name = "客服配置管理", description = "客服配置相关接口")
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/agent/settings")
public class AgentSettingsRestController extends BaseRestController<AgentSettingsRequest, AgentSettingsRestService> {

    private final AgentSettingsRestService agentSettingsRestService;

    @Operation(summary = "查询组织下的客服配置", description = "根据组织ID查询客服配置列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = AgentSettingsResponse.class)))
    @Override
    public ResponseEntity<?> queryByOrg(AgentSettingsRequest request) {
        Page<AgentSettingsResponse> page = agentSettingsRestService.queryByOrg(request);
        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "根据UID查询客服配置", description = "根据UID查询客服配置详情")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = AgentSettingsResponse.class)))
    @Override
    public ResponseEntity<?> queryByUid(AgentSettingsRequest request) {
        AgentSettingsResponse resp = agentSettingsRestService.queryByUid(request);
        return ResponseEntity.ok(JsonResult.success(resp));
    }

    @Operation(summary = "创建客服配置", description = "创建新的客服配置")
    @ApiResponse(responseCode = "200", description = "创建成功",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = AgentSettingsResponse.class)))
    @Override
    public ResponseEntity<?> create(@RequestBody AgentSettingsRequest request) {
        AgentSettingsResponse resp = agentSettingsRestService.create(request);
        if (resp == null) {
            return ResponseEntity.ok(JsonResult.error("create agent settings failed"));
        }
        return ResponseEntity.ok(JsonResult.success(resp));
    }

    @Operation(summary = "更新客服配置", description = "更新客服配置信息")
    @ApiResponse(responseCode = "200", description = "更新成功",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = AgentSettingsResponse.class)))
    @Override
    public ResponseEntity<?> update(@RequestBody AgentSettingsRequest request) {
        AgentSettingsResponse resp = agentSettingsRestService.update(request);
        return ResponseEntity.ok(JsonResult.success(resp));
    }

    @Operation(summary = "删除客服配置", description = "删除指定的客服配置")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @Override
    public ResponseEntity<?> delete(@RequestBody AgentSettingsRequest request) {
        agentSettingsRestService.deleteByUid(request.getUid());
        return ResponseEntity.ok(JsonResult.success(request));
    }

    @Operation(summary = "启用客服配置", description = "启用指定的客服配置")
    @ApiResponse(responseCode = "200", description = "启用成功",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = AgentSettingsResponse.class)))
    public ResponseEntity<?> enable(@RequestBody AgentSettingsRequest request) {
        AgentSettingsResponse resp = agentSettingsRestService.enable(request.getUid());
        return ResponseEntity.ok(JsonResult.success(resp));
    }

    @Operation(summary = "停用客服配置", description = "停用指定的客服配置")
    @ApiResponse(responseCode = "200", description = "停用成功",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = AgentSettingsResponse.class)))
    public ResponseEntity<?> disable(@RequestBody AgentSettingsRequest request) {
        AgentSettingsResponse resp = agentSettingsRestService.disable(request.getUid());
        return ResponseEntity.ok(JsonResult.success(resp));
    }

    @Operation(summary = "发布客服配置", description = "将草稿版本发布为线上版本")
    @ApiResponse(responseCode = "200", description = "发布成功",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = AgentSettingsResponse.class)))
    @RequestMapping("/publish")
    public ResponseEntity<?> publish(@RequestBody AgentSettingsRequest request) {
        AgentSettingsResponse resp = agentSettingsRestService.publish(request.getUid());
        return ResponseEntity.ok(JsonResult.success(resp));
    }

}
