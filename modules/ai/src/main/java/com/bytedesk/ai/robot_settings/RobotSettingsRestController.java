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

@Tag(name = "Robot Settings Management", description = "Robot settings management APIs")
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/robot/settings")
public class RobotSettingsRestController extends BaseRestController<RobotSettingsRequest, RobotSettingsRestService> {

    private final RobotSettingsRestService robotSettingsRestService;

    @Operation(summary = "Query Robot Settings by Organization", description = "Query the list of robot settings by organization ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = RobotSettingsResponse.class)))
    @Override
    public ResponseEntity<?> queryByOrg(RobotSettingsRequest request) {
        Page<RobotSettingsResponse> page = robotSettingsRestService.queryByOrg(request);
        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "Query Robot Settings by UID", description = "Query robot settings details by UID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = RobotSettingsResponse.class)))
    @Override
    public ResponseEntity<?> queryByUid(RobotSettingsRequest request) {
        RobotSettingsResponse resp = robotSettingsRestService.queryByUid(request);
        return ResponseEntity.ok(JsonResult.success(resp));
    }

    @Operation(summary = "Create Robot Settings", description = "Create new robot settings")
    @ApiResponse(responseCode = "200", description = "Creation successful",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = RobotSettingsResponse.class)))
    @Override
    public ResponseEntity<?> create(@RequestBody RobotSettingsRequest request) {
        RobotSettingsResponse resp = robotSettingsRestService.create(request);
        if (resp == null) {
            return ResponseEntity.ok(JsonResult.error("create robot settings failed"));
        }
        return ResponseEntity.ok(JsonResult.success(resp));
    }

    @Operation(summary = "Update Robot Settings", description = "Update robot settings information")
    @ApiResponse(responseCode = "200", description = "Update successful",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = RobotSettingsResponse.class)))
    @Override
    public ResponseEntity<?> update(@RequestBody RobotSettingsRequest request) {
        RobotSettingsResponse resp = robotSettingsRestService.update(request);
        return ResponseEntity.ok(JsonResult.success(resp));
    }

    @Operation(summary = "Delete Robot Settings", description = "Delete the specified robot settings")
    @ApiResponse(responseCode = "200", description = "Deletion successful")
    @Override
    public ResponseEntity<?> delete(@RequestBody RobotSettingsRequest request) {
        robotSettingsRestService.deleteByUid(request.getUid());
        return ResponseEntity.ok(JsonResult.success(request));
    }

    @Operation(summary = "Enable Robot Settings", description = "Enable the specified robot settings")
    @ApiResponse(responseCode = "200", description = "Enable successful",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = RobotSettingsResponse.class)))
    @RequestMapping("/enable")
    public ResponseEntity<?> enable(@RequestBody RobotSettingsRequest request) {
        RobotSettingsResponse resp = robotSettingsRestService.enable(request.getUid());
        return ResponseEntity.ok(JsonResult.success(resp));
    }

    @Operation(summary = "Disable Robot Settings", description = "Disable the specified robot settings")
    @ApiResponse(responseCode = "200", description = "Disable successful",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = RobotSettingsResponse.class)))
    @RequestMapping("/disable")
    public ResponseEntity<?> disable(@RequestBody RobotSettingsRequest request) {
        RobotSettingsResponse resp = robotSettingsRestService.disable(request.getUid());
        return ResponseEntity.ok(JsonResult.success(resp));
    }

    @Operation(summary = "Publish Robot Settings", description = "Publish the draft version to production")
    @ApiResponse(responseCode = "200", description = "Publish successful",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = RobotSettingsResponse.class)))
    @RequestMapping("/publish")
    public ResponseEntity<?> publish(@RequestBody RobotSettingsRequest request) {
        RobotSettingsResponse resp = robotSettingsRestService.publish(request.getUid());
        return ResponseEntity.ok(JsonResult.success(resp));
    }

}
