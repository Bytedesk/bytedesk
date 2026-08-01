/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 16:37:01
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-07 17:21:19
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.thread.ThreadRequest;
import com.bytedesk.core.thread.ThreadResponse;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.context.annotation.Description;

@Tag(name = "Robot Management", description = "Robot management APIs")
@RestController
@RequestMapping("/api/v1/robot")
@RequiredArgsConstructor
@Description("Robot Management Controller - AI robot and chatbot management APIs")
public class RobotRestController extends BaseRestController<RobotRequest, RobotRestService> {

    private final RobotRestService robotRestService;

    @Operation(summary = "Query Robots by Organization", description = "Query the list of robots by organization ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = RobotResponse.class)))
    @PreAuthorize(RobotPermissions.HAS_ROBOT_READ)
    @ActionAnnotation(title = I18Consts.I18N_ROBOT, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query robot by org")
    @GetMapping("/query/org")
    @Override
    public ResponseEntity<?> queryByOrg(RobotRequest request) {
        
        Page<RobotResponse> page = robotRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "Query Robots by User", description = "Query the list of robots by user ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = RobotResponse.class)))
    @PreAuthorize(RobotPermissions.HAS_ROBOT_READ)
    @ActionAnnotation(title = I18Consts.I18N_ROBOT, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query robot by user")
    @GetMapping({ "/query", "/query/user" })
    @Override
    public ResponseEntity<?> queryByUser(RobotRequest request) {
        
        Page<RobotResponse> page = robotRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "Query Robot by UID", description = "Query robot details by UID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = RobotResponse.class)))
    @PreAuthorize(RobotPermissions.HAS_ROBOT_READ)
    @ActionAnnotation(title = I18Consts.I18N_ROBOT, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query robot by uid")
    @GetMapping("/query/uid")
    @Override
    public ResponseEntity<?> queryByUid(RobotRequest request) {

        RobotResponse robot = robotRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(robot));
    }
    
    @Operation(summary = "Create Robot", description = "Create a new robot")
    @ApiResponse(responseCode = "200", description = "Creation successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = RobotResponse.class)))
    @PreAuthorize(RobotPermissions.HAS_ROBOT_CREATE)
    @ActionAnnotation(title = I18Consts.I18N_ROBOT, action = I18Consts.I18N_ACTION_CREATE, description = "create robot")
    @PostMapping("/create")
    @Override
    public ResponseEntity<?> create(@RequestBody RobotRequest request) {

        RobotResponse robot = robotRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(robot));
    }
    
    @Operation(summary = "Create LLM Thread", description = "Allow staff or agents to create an LLM thread")
    @ApiResponse(responseCode = "200", description = "Creation successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = ThreadResponse.class)))
    @PreAuthorize(RobotPermissions.HAS_ROBOT_CREATE)
    @ActionAnnotation(title = I18Consts.I18N_ROBOT, action = I18Consts.I18N_ACTION_CREATE_THREAD, description = "create robot thread")
    @PostMapping("/create/llm/thread")
    public ResponseEntity<?> createLlmThread(@RequestBody ThreadRequest request) {
        //
        ThreadResponse thread = robotRestService.createLlmThread(request);

        return ResponseEntity.ok(JsonResult.success(thread));
    }

    @Operation(summary = "Update LLM Thread", description = "Update LLM thread information")
    @ApiResponse(responseCode = "200", description = "Update successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = ThreadResponse.class)))
    @PreAuthorize(RobotPermissions.HAS_ROBOT_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_ROBOT, action = I18Consts.I18N_ACTION_UPDATE_THREAD, description = "update robot thread")
    @PostMapping("/update/llm/thread")
    public ResponseEntity<?> updateLlmThread(@RequestBody ThreadRequest request) {
        //
        ThreadResponse thread = robotRestService.updateLlmThread(request);

        return ResponseEntity.ok(JsonResult.success(thread));
    }

    @Operation(summary = "Create Prompt Robot Template", description = "Create a prompt-based robot template")
    @ApiResponse(responseCode = "200", description = "Creation successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = RobotResponse.class)))
    @PreAuthorize(RobotPermissions.HAS_ROBOT_CREATE)
    @ActionAnnotation(title = I18Consts.I18N_ROBOT, action = I18Consts.I18N_ACTION_CREATE_PROMPT, description = "create prompt robot")
    @PostMapping("/create/prompt")
    public ResponseEntity<?> createPromptRobot(@RequestBody RobotRequest request) {
        //
        RobotResponse robot = robotRestService.createPromptRobot(request);

        return ResponseEntity.ok(JsonResult.success(robot));
    }

    @Operation(summary = "Update Robot", description = "Update robot information")
    @ApiResponse(responseCode = "200", description = "Update successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = RobotResponse.class)))
    @PreAuthorize(RobotPermissions.HAS_ROBOT_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_ROBOT, action = I18Consts.I18N_ACTION_UPDATE, description = "update robot")
    @PostMapping("/update")
    @Override
    public ResponseEntity<?> update(@RequestBody RobotRequest request) {

        RobotResponse robotResponse = robotRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(robotResponse));
    }

    @Operation(summary = "Update Robot Avatar", description = "Update the robot avatar")
    @ApiResponse(responseCode = "200", description = "Update successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = RobotResponse.class)))
    @PreAuthorize(RobotPermissions.HAS_ROBOT_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_ROBOT, action = I18Consts.I18N_ACTION_UPDATE_AVATAR, description = "update robot avatar")
    @PostMapping("/update/avatar")
    public ResponseEntity<?> updateAvatar(@RequestBody RobotRequest request) {

        RobotResponse robotResponse = robotRestService.updateAvatar(request);
        
        return ResponseEntity.ok(JsonResult.success(robotResponse));
    }

    @Operation(summary = "Update Prompt Robot", description = "Update prompt robot information")
    @ApiResponse(responseCode = "200", description = "Update successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = RobotResponse.class)))
    @PreAuthorize(RobotPermissions.HAS_ROBOT_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_ROBOT, action = I18Consts.I18N_ACTION_UPDATE_PROMPT, description = "update prompt robot")
    @PostMapping("/update/prompt")
    public ResponseEntity<?> updatePromptRobot(@RequestBody RobotRequest request) {

        RobotResponse robotResponse = robotRestService.updatePromptRobot(request);

        return ResponseEntity.ok(JsonResult.success(robotResponse));
    }

    @Operation(summary = "Update Robot Prompt Text", description = "Update robot prompt text by UID only")
    @ApiResponse(responseCode = "200", description = "Update successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = RobotResponse.class)))
    @PreAuthorize(RobotPermissions.HAS_ROBOT_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_ROBOT, action = I18Consts.I18N_ACTION_UPDATE_PROMPT_TEXT, description = "update robot prompt text")
    @PostMapping("/update/prompt/text")
    public ResponseEntity<?> updatePromptText(@RequestBody RobotRequest request) {

        RobotResponse robotResponse = robotRestService.updatePromptText(request);

        return ResponseEntity.ok(JsonResult.success(robotResponse));
    }

    @Operation(summary = "Update Robot Knowledge Base", description = "Update the robot knowledge base UID")
    @ApiResponse(responseCode = "200", description = "Update successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = RobotResponse.class)))
    @PreAuthorize(RobotPermissions.HAS_ROBOT_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_ROBOT, action = I18Consts.I18N_ACTION_UPDATE_KB_UID, description = "update robot kbUid")
    @PostMapping("/update/kbUid")
    public ResponseEntity<?> updateKbUid(@RequestBody RobotRequest request) {
        
        RobotResponse robotResponse = robotRestService.updateKbUid(request);

        return ResponseEntity.ok(JsonResult.success(robotResponse));
    }

    @Operation(summary = "Delete Robot", description = "Delete the specified robot")
    @ApiResponse(responseCode = "200", description = "Deletion successful")
    @PreAuthorize(RobotPermissions.HAS_ROBOT_DELETE)
    @ActionAnnotation(title = I18Consts.I18N_ROBOT, action = I18Consts.I18N_ACTION_DELETE, description = "delete robot")
    @PostMapping("/delete")
    @Override
    public ResponseEntity<?> delete(@RequestBody RobotRequest request) {
        
        robotRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success(request));
    }

    @Operation(summary = "Export Robots", description = "Export robot data")
    @ApiResponse(responseCode = "200", description = "Export successful")
    @PreAuthorize(RobotPermissions.HAS_ROBOT_EXPORT)
    @ActionAnnotation(title = I18Consts.I18N_ROBOT, action = I18Consts.I18N_ACTION_EXPORT, description = "export robot")
    @GetMapping("/export")
    @Override
    public Object export(RobotRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            robotRestService,
            RobotExcel.class,
            "Prompt",
            "prompt"
        );
    }
}
