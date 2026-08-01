/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:19:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 19:59:53
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent;

import java.util.concurrent.ExecutorService;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bytedesk.ai.robot.RobotService;
import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.annotation.BlackIpFilter;
import com.bytedesk.core.annotation.BlackUserFilter;
import com.bytedesk.core.annotation.TabooJsonFilter;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.thread.ThreadRequest;
import com.bytedesk.core.thread.ThreadResponseSimple;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Description;

@Tag(name = "Agent Management", description = "Agent management APIs")
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/agent")
@Description("Agent Management Controller - Customer service agent management and chat APIs")
public class AgentRestController extends BaseRestController<AgentRequest, AgentRestService> {

    private final AgentRestService agentRestService;

    private final RobotService robotService;

    @Qualifier("virtualAsyncExecutor")
    private final ExecutorService executorService;

    // @PreAuthorize(AgentPermissions.HAS_AGENT_READ) 前端很多地方需要查询，所以不需要权限
    @ActionAnnotation(title = I18Consts.I18N_AGENT, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query agent by org")
    @Operation(summary = "Query Agents by Organization", description = "Retrieve agent list by organization ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = AgentResponse.class)))
    @PreAuthorize(AgentPermissions.HAS_AGENT_READ)
    @GetMapping("/query/org")
    @Override
    public ResponseEntity<?> queryByOrg(AgentRequest request) {

        Page<AgentResponse> page = agentRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @ActionAnnotation(title = I18Consts.I18N_AGENT, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query agent by user")
    @Operation(summary = "Query Agents by User", description = "Retrieve agent information by user ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = AgentResponse.class)))
    @PreAuthorize(AgentPermissions.HAS_AGENT_READ)
    @GetMapping({ "/query", "/query/user" })
    @Override
    public ResponseEntity<?> queryByUser(AgentRequest request) {

        Page<AgentResponse> agentResponse = agentRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(agentResponse));
    }

    @ActionAnnotation(title = I18Consts.I18N_AGENT, action = I18Consts.I18N_ACTION_QUERY_USER_UID, description = "query agent by user uid")
    @Operation(summary = "Query Agent by User UID", description = "Retrieve a single agent by userUid, with optional orgUid for precise matching")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = AgentResponse.class)))
    @PreAuthorize(AgentPermissions.HAS_AGENT_READ)
    @GetMapping("/query/user/uid")
    public ResponseEntity<?> queryByUserUid(AgentRequest request) {

        AgentResponse agent = agentRestService.queryByUserUid(request);

        return ResponseEntity.ok(JsonResult.success(agent));
    }

    @ActionAnnotation(title = I18Consts.I18N_AGENT, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query agent by uid")
    @Operation(summary = "Query Agent by UID", description = "Retrieve agent details by UID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = AgentResponse.class)))
    @PreAuthorize(AgentPermissions.HAS_AGENT_READ)
    @GetMapping("/query/uid")
    @Override
    public ResponseEntity<?> queryByUid(AgentRequest request) {
        
        AgentResponse agent = agentRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(agent));
    }

    @Operation(summary = "Agent Accepts Thread", description = "Allow the agent to accept a thread request")
    @ApiResponse(responseCode = "200", description = "Accepted successfully",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = ThreadResponseSimple.class)))
    @ActionAnnotation(title = I18Consts.I18N_THREAD, action = I18Consts.I18N_ACTION_ACCEPT, description = "accept thread")
    @PostMapping("/accept")
    public ResponseEntity<?> acceptByAgent(@RequestBody ThreadRequest request) {
        
        ThreadResponseSimple threadResponse = agentRestService.acceptByAgent(request);

        return ResponseEntity.ok(JsonResult.success(threadResponse));   
    }

    @ActionAnnotation(title = I18Consts.I18N_AGENT, action = I18Consts.I18N_ACTION_CREATE, description = "create agent")
    @Operation(summary = "Create Agent", description = "Create a new agent")
    @ApiResponse(responseCode = "200", description = "Created successfully",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = AgentResponse.class)))
    @PreAuthorize(AgentPermissions.HAS_AGENT_CREATE)
    @PostMapping("/create")
    @Override
    public ResponseEntity<?> create(@RequestBody AgentRequest request) {

        AgentResponse agent = agentRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(agent));
    }

    @ActionAnnotation(title = I18Consts.I18N_AGENT, action = I18Consts.I18N_ACTION_UPDATE, description = "update agent")
    @Operation(summary = "Update Agent", description = "Update agent information")
    @ApiResponse(responseCode = "200", description = "Updated successfully",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = AgentResponse.class)))
    @PreAuthorize(AgentPermissions.HAS_AGENT_UPDATE)
    @PostMapping("/update")
    @Override
    public ResponseEntity<?> update(@RequestBody AgentRequest request) {

        AgentResponse agent = agentRestService.update(request);
        //
        return ResponseEntity.ok(JsonResult.success(agent));
    }

    @PreAuthorize(AgentPermissions.HAS_AGENT_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_AGENT, action = I18Consts.I18N_ACTION_UPDATE_AVATAR, description = "update agent avatar")
    @Operation(summary = "Update Agent Avatar", description = "Update the agent avatar")
    @ApiResponse(responseCode = "200", description = "Updated successfully",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = AgentResponse.class)))
    @PostMapping("/update/avatar")
    public ResponseEntity<?> updateAvatar(@RequestBody AgentRequest request) {

        AgentResponse agent = agentRestService.updateAvatar(request);

        return ResponseEntity.ok(JsonResult.success(agent));
    }

    @PreAuthorize(AgentPermissions.HAS_AGENT_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_AGENT, action = I18Consts.I18N_ACTION_UPDATE_AGENT_STATUS, description = "update agent status")
    @Operation(summary = "Update Agent Status", description = "Update the agent online status")
    @ApiResponse(responseCode = "200", description = "Updated successfully",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = AgentResponse.class)))
    @PostMapping("/update/status")
    public ResponseEntity<?> updateStatus(@RequestBody AgentRequest request) {

        AgentResponse agent = agentRestService.updateStatus(request);
        //
        return ResponseEntity.ok(JsonResult.success(agent));
    }

    @PreAuthorize(AgentPermissions.HAS_AGENT_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_AGENT, action = I18Consts.I18N_ACTION_UPDATE_AUTO_REPLY, description = "update agent autoreply")
    @Operation(summary = "Update Agent Auto Reply", description = "Update the agent auto-reply settings")
    @ApiResponse(responseCode = "200", description = "Updated successfully",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = AgentResponse.class)))
    @PostMapping("/update/autoreply")
    public ResponseEntity<?> updateAutoReply(@RequestBody AgentRequest request) {

        AgentResponse agent = agentRestService.updateAutoReply(request);
        //
        return ResponseEntity.ok(JsonResult.success(agent));
    }

    @PreAuthorize(AgentPermissions.HAS_AGENT_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_AGENT, action = I18Consts.I18N_ACTION_UPDATE, description = "force logout agent")
    @Operation(summary = "Force Logout Agent", description = "Force the specified agent to logout from desktop and block re-login")
    @ApiResponse(responseCode = "200", description = "Force logout applied successfully",
        content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = AgentResponse.class)))
    @PostMapping("/force/logout")
    public ResponseEntity<?> forceLogout(@RequestBody AgentRequest request) {

        AgentResponse agent = agentRestService.forceLogout(request);
        return ResponseEntity.ok(JsonResult.success(agent));
    }

    @PreAuthorize(AgentPermissions.HAS_AGENT_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_AGENT, action = I18Consts.I18N_ACTION_UPDATE, description = "restore agent login")
    @Operation(summary = "Restore Agent Login", description = "Restore the specified agent login after a forced logout")
    @ApiResponse(responseCode = "200", description = "Agent login restored successfully",
        content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = AgentResponse.class)))
    @PostMapping("/restore/login")
    public ResponseEntity<?> restoreLogin(@RequestBody AgentRequest request) {

        AgentResponse agent = agentRestService.restoreLogin(request);
        return ResponseEntity.ok(JsonResult.success(agent));
    }
    
    @PreAuthorize(AgentPermissions.HAS_AGENT_DELETE)
    @ActionAnnotation(title = I18Consts.I18N_AGENT, action = I18Consts.I18N_ACTION_DELETE, description = "delete agent")
    @Operation(summary = "Delete Agent", description = "Delete the specified agent")
    @ApiResponse(responseCode = "200", description = "Deleted successfully")
    @PostMapping("/delete")
    @Override
    public ResponseEntity<?> delete(@RequestBody AgentRequest request) {

        agentRestService.deleteByUid(request.getUid());
        //
        return ResponseEntity.ok(JsonResult.success(request));
    }

    @PreAuthorize(AgentPermissions.HAS_AGENT_EXPORT)
    @ActionAnnotation(title = I18Consts.I18N_AGENT, action = I18Consts.I18N_ACTION_EXPORT, description = "export agent")
    @Operation(summary = "Export Agents", description = "Export agent data")
    @ApiResponse(responseCode = "200", description = "Export successful")
    @GetMapping("/export")
    @Override
    public Object export(AgentRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            agentRestService,
            AgentExcel.class,
            "客服",
            "agent"
        );
    }

    @BlackIpFilter(title = "black", action = "sendAgentSseMessage")
    @BlackUserFilter(title = "black", action = "sendAgentSseMessage")
    @TabooJsonFilter(title = "敏感词", action = "sendAgentSseMessage")
    @Operation(summary = "Agent Message SSE Push", description = "Real-time SSE push endpoint for agent messages")
    @ApiResponse(responseCode = "200", description = "Pushed successfully")
    @ActionAnnotation(title = I18Consts.I18N_AGENT, action = I18Consts.I18N_ACTION_SEND_AGENT_SSE_MESSAGE, description = "sendAgentSseMessage")
    @GetMapping(value = "/message/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sendAgentSseMessage(@RequestParam(value = "message") String message) {

        // 创建 SseEmitter 前先进行权限验证
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // if (authentication == null || !authentication.isAuthenticated()) {
        //     log.warn("Unauthorized access to SSE endpoint");
        //     throw new AccessDeniedException("Unauthorized");
        // }
        
        SseEmitter emitter = new SseEmitter(180_000L); // 3分钟超时

        // 添加完成/超时/错误处理器
        // emitter.onCompletion(() -> log.debug("SSE emitter completed"));
        // emitter.onTimeout(() -> log.debug("SSE emitter timed out"));
        // emitter.onError((ex) -> log.error("SSE emitter error: {}", ex.getMessage()));
        
        executorService.execute(() -> {
            try {
                robotService.processSseMemberMessage(message, emitter);
            } catch (Exception e) {
                log.error("Error processing SSE request", e);
                emitter.completeWithError(e);
            }
        });
        
        // 添加超时和完成时的回调
        emitter.onTimeout(() -> {
            log.warn("SSE connection timed out");
            emitter.complete();
        });
        
        emitter.onCompletion(() -> {
            log.info("SSE connection completed");
        });
        
        return emitter;
    }

    // 在 Bean 销毁时关闭线程池
    public void destroy() {
        // shared virtual executor managed by Spring container
    }

}
