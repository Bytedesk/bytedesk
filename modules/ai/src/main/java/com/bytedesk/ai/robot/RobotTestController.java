/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-12 16:20:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-19 09:07:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot;

import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.annotation.ApiRateLimiter;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AI机器人服务测试控制器
 * 用于测试各种AI服务功能
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/test/api/v1/robot")
@Tag(name = "AI机器人测试", description = "AI机器人服务测试接口")
public class RobotTestController {

    private final RobotService robotService;

    /**
     * 备用回复服务测试接口
     * http://127.0.0.1:9003/test/api/v1/robot/fallback-response
     */
    @Operation(summary = "备用回复测试", description = "测试AI备用回复生成功能")
    @ApiResponse(responseCode = "200", description = "测试成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = String.class)))
    @ApiRateLimiter(value = 5.0, timeout = 1)
    // @PreAuthorize("hasAuthority('ROBOT_TEST')")
    @ActionAnnotation(title = "机器人测试", action = "备用回复", description = "fallback response test")
    @PostMapping("/fallback-response")
    public ResponseEntity<?> testFallbackResponse(@RequestBody RobotRequest request) {
        
        String response = robotService.fallbackResponse(request.getContent(), request.getOrgUid());
        
        return ResponseEntity.ok(JsonResult.success("fallback response generated", response));
    }

    /**
     * 查询重写服务测试接口
     * http://127.0.0.1:9003/test/api/v1/robot/query-rewrite
     */
    @Operation(summary = "查询重写测试", description = "测试AI查询重写功能")
    @ApiResponse(responseCode = "200", description = "测试成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = String.class)))
    @ApiRateLimiter(value = 5.0, timeout = 1)
    // @PreAuthorize("hasAuthority('ROBOT_TEST')")
    @ActionAnnotation(title = "机器人测试", action = "查询重写", description = "query rewrite test")
    @PostMapping("/query-rewrite")
    public ResponseEntity<?> testQueryRewrite(@RequestBody RobotRequest request) {

        String response = robotService.queryRewrite(request.getContent(), request.getOrgUid());

        return ResponseEntity.ok(JsonResult.success("query rewritten", response));
    }

    /**
     * 摘要生成服务测试接口
     * http://127.0.0.1:9003/test/api/v1/robot/summary-generation
     */
    @Operation(summary = "摘要生成测试", description = "测试AI摘要生成功能")
    @ApiResponse(responseCode = "200", description = "测试成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = String.class)))
    @ApiRateLimiter(value = 5.0, timeout = 1)
    // @PreAuthorize("hasAuthority('ROBOT_TEST')")
    @ActionAnnotation(title = "机器人测试", action = "摘要生成", description = "summary generation test")
    @PostMapping("/summary-generation")
    public ResponseEntity<?> testSummaryGeneration(@RequestBody RobotRequest request) {

        String response = robotService.summaryGeneration(request.getContent(), request.getOrgUid());

        return ResponseEntity.ok(JsonResult.success("summary generated", response));
    }

    /**
     * 会话标题生成服务测试接口
     * http://127.0.0.1:9003/test/api/v1/robot/session-title-generation
     */
    @Operation(summary = "会话标题生成测试", description = "测试AI会话标题生成功能")
    @ApiResponse(responseCode = "200", description = "测试成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = String.class)))
    @ApiRateLimiter(value = 5.0, timeout = 1)
    // @PreAuthorize("hasAuthority('ROBOT_TEST')")
    @ActionAnnotation(title = "机器人测试", action = "会话标题生成", description = "session title generation test")
    @PostMapping("/session-title-generation")
    public ResponseEntity<?> testSessionTitleGeneration(@RequestBody RobotRequest request) {

        String response = robotService.threadTitleGeneration(request.getContent(), request.getOrgUid());

        return ResponseEntity.ok(JsonResult.success("session title generated", response));
    }

    /**
     * 上下文模板摘要服务测试接口
     * http://127.0.0.1:9003/test/api/v1/robot/context-template-summary
     */
    @Operation(summary = "上下文模板摘要测试", description = "测试AI上下文模板摘要功能")
    @ApiResponse(responseCode = "200", description = "测试成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = String.class)))
    @ApiRateLimiter(value = 5.0, timeout = 1)
    // @PreAuthorize("hasAuthority('ROBOT_TEST')")
    @ActionAnnotation(title = "机器人测试", action = "上下文模板摘要", description = "context template summary test")
    @PostMapping("/context-template-summary")
    public ResponseEntity<?> testContextTemplateSummary(@RequestBody RobotRequest request) {

        String response = robotService.contextTemplateSummary(request.getContent(), request.getOrgUid());

        return ResponseEntity.ok(JsonResult.success("context template summary generated", response));
    }

    /**
     * 实体提取服务测试接口
     * http://127.0.0.1:9003/test/api/v1/robot/entity-extraction
     */
    @Operation(summary = "实体提取测试", description = "测试AI实体提取功能")
    @ApiResponse(responseCode = "200", description = "测试成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = String.class)))
    @ApiRateLimiter(value = 5.0, timeout = 1)
    // @PreAuthorize("hasAuthority('ROBOT_TEST')")
    @ActionAnnotation(title = "机器人测试", action = "实体提取", description = "entity extraction test")
    @PostMapping("/entity-extraction")
    public ResponseEntity<?> testEntityExtraction(@RequestBody RobotRequest request) {

        String response = robotService.entityExtraction(request.getContent(), request.getOrgUid());

        return ResponseEntity.ok(JsonResult.success("entities extracted", response));
    }

    /**
     * 关系提取服务测试接口
     * http://127.0.0.1:9003/test/api/v1/robot/relationship-extraction
     */
    @Operation(summary = "关系提取测试", description = "测试AI关系提取功能")
    @ApiResponse(responseCode = "200", description = "测试成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = String.class)))
    @ApiRateLimiter(value = 5.0, timeout = 1)
    // @PreAuthorize("hasAuthority('ROBOT_TEST')")
    @ActionAnnotation(title = "机器人测试", action = "关系提取", description = "relationship extraction test")
    @PostMapping("/relationship-extraction")
    public ResponseEntity<?> testRelationshipExtraction(@RequestBody RobotRequest request) {

        String response = robotService.relationshipExtraction(request.getContent(), request.getOrgUid());

        return ResponseEntity.ok(JsonResult.success("relationships extracted", response));
    }
    
}