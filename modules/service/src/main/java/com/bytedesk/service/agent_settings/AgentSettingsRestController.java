package com.bytedesk.service.agent_settings;

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

@Tag(name = "Agent Settings Management", description = "Agent settings management APIs")
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/agent/settings")
public class AgentSettingsRestController extends BaseRestController<AgentSettingsRequest, AgentSettingsRestService> {

    private final AgentSettingsRestService agentSettingsRestService;

    @Operation(summary = "Query Agent Settings by Organization", description = "Retrieve agent settings list by organization ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = AgentSettingsResponse.class)))
    @GetMapping("/query/org")
    @PreAuthorize(AgentSettingsPermissions.HAS_AGENT_SETTINGS_READ)
    @Override
    public ResponseEntity<?> queryByOrg(AgentSettingsRequest request) {
        Page<AgentSettingsResponse> page = agentSettingsRestService.queryByOrg(request);
        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "Query Agent Settings by User", description = "Retrieve agent settings list by user ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = AgentSettingsResponse.class)))
    @GetMapping({ "/query", "/query/user" })
    @PreAuthorize(AgentSettingsPermissions.HAS_AGENT_SETTINGS_READ)
    @Override
    public ResponseEntity<?> queryByUser(AgentSettingsRequest request) {
        Page<AgentSettingsResponse> page = agentSettingsRestService.queryByUser(request);
        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "Query Agent Settings by Agent UID", description = "Retrieve the settings bound to the agent UID, or return the organization default when none is bound")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = AgentSettingsResponse.class)))
    @PreAuthorize(AgentSettingsPermissions.HAS_AGENT_SETTINGS_READ)
    @RequestMapping("/query/agent")
    public ResponseEntity<?> queryByAgent(AgentSettingsRequest request) {
        AgentSettingsResponse resp = agentSettingsRestService.queryByAgentUid(request.getAgentUid());
        if (resp == null) {
            return ResponseEntity.ok(JsonResult.error("query agent settings failed"));
        }
        return ResponseEntity.ok(JsonResult.success(resp));
    }

    @Operation(summary = "Query Agent Settings by UID", description = "Retrieve agent settings details by UID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = AgentSettingsResponse.class)))
    @GetMapping("/query/uid")
    @PreAuthorize(AgentSettingsPermissions.HAS_AGENT_SETTINGS_READ)
    @Override
    public ResponseEntity<?> queryByUid(AgentSettingsRequest request) {
        AgentSettingsResponse resp = agentSettingsRestService.queryByUid(request);
        return ResponseEntity.ok(JsonResult.success(resp));
    }

    @Operation(summary = "Create Agent Settings", description = "Create new agent settings")
    @ApiResponse(responseCode = "200", description = "Created successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = AgentSettingsResponse.class)))
    @PostMapping("/create")
    @PreAuthorize(AgentSettingsPermissions.HAS_AGENT_SETTINGS_CREATE)
    @Override
    public ResponseEntity<?> create(@RequestBody AgentSettingsRequest request) {
        AgentSettingsResponse resp = agentSettingsRestService.create(request);
        if (resp == null) {
            return ResponseEntity.ok(JsonResult.error("create agent settings failed"));
        }
        return ResponseEntity.ok(JsonResult.success(resp));
    }

    @Operation(summary = "Update Agent Settings", description = "Update agent settings information")
    @ApiResponse(responseCode = "200", description = "Updated successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = AgentSettingsResponse.class)))
    @PostMapping("/update")
    @PreAuthorize(AgentSettingsPermissions.HAS_AGENT_SETTINGS_UPDATE)
    @Override
    public ResponseEntity<?> update(@RequestBody AgentSettingsRequest request) {
        AgentSettingsResponse resp = agentSettingsRestService.update(request);
        return ResponseEntity.ok(JsonResult.success(resp));
    }

    @Operation(summary = "Delete Agent Settings", description = "Delete the specified agent settings")
    @ApiResponse(responseCode = "200", description = "Deleted successfully")
    @PostMapping("/delete")
    @PreAuthorize(AgentSettingsPermissions.HAS_AGENT_SETTINGS_DELETE)
    @Override
    public ResponseEntity<?> delete(@RequestBody AgentSettingsRequest request) {
        agentSettingsRestService.deleteByUid(request.getUid());
        return ResponseEntity.ok(JsonResult.success(request));
    }

    @Operation(summary = "Enable Agent Settings", description = "Enable the specified agent settings")
    @ApiResponse(responseCode = "200", description = "Enabled successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = AgentSettingsResponse.class)))
    @PreAuthorize(AgentSettingsPermissions.HAS_AGENT_SETTINGS_UPDATE)
    @RequestMapping("/enable")
    public ResponseEntity<?> enable(@RequestBody AgentSettingsRequest request) {
        AgentSettingsResponse resp = agentSettingsRestService.enable(request.getUid());
        return ResponseEntity.ok(JsonResult.success(resp));
    }

    @Operation(summary = "Disable Agent Settings", description = "Disable the specified agent settings")
    @ApiResponse(responseCode = "200", description = "Disabled successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = AgentSettingsResponse.class)))
    @PreAuthorize(AgentSettingsPermissions.HAS_AGENT_SETTINGS_UPDATE)
    @RequestMapping("/disable")
    public ResponseEntity<?> disable(@RequestBody AgentSettingsRequest request) {
        AgentSettingsResponse resp = agentSettingsRestService.disable(request.getUid());
        return ResponseEntity.ok(JsonResult.success(resp));
    }

    @Operation(summary = "Publish Agent Settings", description = "Publish the draft version to production")
    @ApiResponse(responseCode = "200", description = "Published successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = AgentSettingsResponse.class)))
    @PreAuthorize(AgentSettingsPermissions.HAS_AGENT_SETTINGS_UPDATE)
    @RequestMapping("/publish")
    public ResponseEntity<?> publish(@RequestBody AgentSettingsRequest request) {
        AgentSettingsResponse resp = agentSettingsRestService.publish(request.getUid());
        return ResponseEntity.ok(JsonResult.success(resp));
    }

}
