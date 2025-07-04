/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 16:37:01
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-25 11:25:37
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
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

@Tag(name = "机器人管理", description = "机器人管理相关接口")
@RestController
@RequestMapping("/api/v1/robot")
@RequiredArgsConstructor
public class RobotRestController extends BaseRestController<RobotRequest> {

    private final RobotRestService robotRestService;

    @Operation(summary = "查询组织下的机器人", description = "根据组织ID查询机器人列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = RobotResponse.class)))
    // @PreAuthorize("hasAuthority('ROBOT_READ')")
    @Override
    public ResponseEntity<?> queryByOrg(RobotRequest request) {
        
        Page<RobotResponse> page = robotRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "查询用户下的机器人", description = "根据用户ID查询机器人列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = RobotResponse.class)))
    // @PreAuthorize("hasAuthority('ROBOT_READ')")
    @Override
    public ResponseEntity<?> queryByUser(RobotRequest request) {
        
        Page<RobotResponse> page = robotRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "查询指定机器人", description = "根据UID查询机器人详情")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = RobotResponse.class)))
    // @PreAuthorize("hasAuthority('ROBOT_READ')")
    @Override
    public ResponseEntity<?> queryByUid(RobotRequest request) {

        RobotResponse robot = robotRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(robot));
    }
    
    @Operation(summary = "创建机器人", description = "创建新的机器人")
    @ApiResponse(responseCode = "200", description = "创建成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = RobotResponse.class)))
    @PreAuthorize("hasAuthority('ROBOT_CREATE')")
    @ActionAnnotation(title = "机器人", action = "新建", description = "create robot")
    @Override
    public ResponseEntity<?> create(@RequestBody RobotRequest request) {

        RobotResponse robot = robotRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(robot));
    }
    
    @Operation(summary = "创建智能体会话", description = "员工/客服创建智能体会话")
    @ApiResponse(responseCode = "200", description = "创建成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = ThreadResponse.class)))
    @PreAuthorize("hasAuthority('ROBOT_CREATE')")
    @ActionAnnotation(title = "机器人", action = "新建", description = "create robot thread")
    @PostMapping("/create/llm/thread")
    public ResponseEntity<?> createLlmThread(@RequestBody ThreadRequest request) {
        //
        ThreadResponse thread = robotRestService.createLlmThread(request);

        return ResponseEntity.ok(JsonResult.success(thread));
    }

    @Operation(summary = "更新智能体会话", description = "更新智能体会话信息")
    @ApiResponse(responseCode = "200", description = "更新成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = ThreadResponse.class)))
    @PreAuthorize("hasAuthority('ROBOT_UPDATE')")
    @ActionAnnotation(title = "机器人", action = "更新", description = "update robot thread")
    @PostMapping("/update/llm/thread")
    public ResponseEntity<?> updateLlmThread(@RequestBody ThreadRequest request) {
        //
        ThreadResponse thread = robotRestService.updateLlmThread(request);

        return ResponseEntity.ok(JsonResult.success(thread));
    }

    @Operation(summary = "创建智能体模板", description = "创建提示词机器人模板")
    @ApiResponse(responseCode = "200", description = "创建成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = RobotResponse.class)))
    // @PreAuthorize("hasAuthority('ROBOT_CREATE')")
    // @ActionAnnotation(title = "机器人", action = "新建", description = "create prompt robot")
    @PostMapping("/create/prompt")
    public ResponseEntity<?> createPromptRobot(@RequestBody RobotRequest request) {
        //
        RobotResponse robot = robotRestService.createPromptRobot(request);

        return ResponseEntity.ok(JsonResult.success(robot));
    }

    @Operation(summary = "更新机器人", description = "更新机器人信息")
    @ApiResponse(responseCode = "200", description = "更新成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = RobotResponse.class)))
    // @PreAuthorize("hasAuthority('ROBOT_UPDATE')")
    @ActionAnnotation(title = "机器人", action = "更新", description = "update robot")
    @Override
    public ResponseEntity<?> update(@RequestBody RobotRequest request) {

        RobotResponse robotResponse = robotRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(robotResponse));
    }

    @Operation(summary = "更新机器人头像", description = "更新机器人的头像")
    @ApiResponse(responseCode = "200", description = "更新成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = RobotResponse.class)))
    @PreAuthorize("hasAuthority('ROBOT_UPDATE')")
    @ActionAnnotation(title = "机器人", action = "更新", description = "update robot avatar")
    @PostMapping("/update/avatar")
    public ResponseEntity<?> updateAvatar(@RequestBody RobotRequest request) {

        RobotResponse robotResponse = robotRestService.updateAvatar(request);
        
        return ResponseEntity.ok(JsonResult.success(robotResponse));
    }

    @Operation(summary = "更新提示词机器人", description = "更新提示词机器人信息")
    @ApiResponse(responseCode = "200", description = "更新成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = RobotResponse.class)))
    @PreAuthorize("hasAuthority('ROBOT_UPDATE')")
    @ActionAnnotation(title = "机器人", action = "更新", description = "update prompt robot")
    @PostMapping("/update/prompt")
    public ResponseEntity<?> updatePromptRobot(@RequestBody RobotRequest request) {

        RobotResponse robotResponse = robotRestService.updatePromptRobot(request);

        return ResponseEntity.ok(JsonResult.success(robotResponse));
    }

    @Operation(summary = "更新机器人知识库", description = "更新机器人的知识库UID")
    @ApiResponse(responseCode = "200", description = "更新成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = RobotResponse.class)))
    @PreAuthorize("hasAuthority('ROBOT_UPDATE')")
    @ActionAnnotation(title = "机器人", action = "更新", description = "update robot kbUid")
    @PostMapping("/update/kbUid")
    public ResponseEntity<?> updateKbUid(@RequestBody RobotRequest request) {
        
        RobotResponse robotResponse = robotRestService.updateKbUid(request);

        return ResponseEntity.ok(JsonResult.success(robotResponse));
    }

    @Operation(summary = "删除机器人", description = "删除指定的机器人")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @PreAuthorize("hasAuthority('ROBOT_DELETE')")
    @ActionAnnotation(title = "机器人", action = "删除", description = "delete robot")
    @Override
    public ResponseEntity<?> delete(@RequestBody RobotRequest request) {
        
        robotRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success(request));
    }

    @Operation(summary = "导出机器人", description = "导出机器人数据")
    @ApiResponse(responseCode = "200", description = "导出成功")
    @PreAuthorize("hasAuthority('ROBOT_EXPORT')")
    @Override
    public Object export(RobotRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            robotRestService,
            RobotExcel.class,
            "提示词",
            "prompt"
        );
    }
}
