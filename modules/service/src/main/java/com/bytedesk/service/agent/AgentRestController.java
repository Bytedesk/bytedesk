/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:19:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-25 09:21:31
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
import java.util.concurrent.Executors;

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
import com.bytedesk.core.thread.ThreadRequest;
import com.bytedesk.core.thread.ThreadResponse;
// import com.bytedesk.core.rbac.role.RolePermissions;
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

@Tag(name = "客服管理", description = "客服管理相关接口")
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/agent")
@Description("Agent Management Controller - Customer service agent management and chat APIs")
public class AgentRestController extends BaseRestController<AgentRequest> {

    private final AgentRestService agentRestService;

    private final RobotService robotService;

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    // @PreAuthorize("hasAuthority('AGENT_READ')") 前端很多地方需要查询，所以不需要权限
    @Operation(summary = "查询组织下的客服", description = "根据组织ID查询客服列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = AgentResponse.class)))
    @Override
    public ResponseEntity<?> queryByOrg(AgentRequest request) {

        Page<AgentResponse> page = agentRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    // @PreAuthorize("hasAuthority('AGENT_READ')")
    @Operation(summary = "查询用户下的客服", description = "根据用户ID查询客服信息")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = AgentResponse.class)))
    @Override
    public ResponseEntity<?> queryByUser(AgentRequest request) {

        AgentResponse agentResponse = agentRestService.query(request);
        if (agentResponse == null) {
            return ResponseEntity.ok(JsonResult.error("agent not found"));
        }
        
        return ResponseEntity.ok(JsonResult.success(agentResponse));
    }

    // @PreAuthorize("hasAuthority('AGENT_READ')")
    @Operation(summary = "根据UID查询客服", description = "根据UID查询客服详情")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = AgentResponse.class)))
    @Override
    public ResponseEntity<?> queryByUid(AgentRequest request) {
        
        AgentResponse agent = agentRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(agent));
    }

    @Operation(summary = "客服接受会话", description = "客服接受会话请求")
    @ApiResponse(responseCode = "200", description = "接受成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = ThreadResponse.class)))
    @ActionAnnotation(title = "会话", action = "accept", description = "accept thread")
    @PostMapping("/accept")
    public ResponseEntity<?> acceptByAgent (@RequestBody ThreadRequest request) {
        
        ThreadResponse threadResponse = agentRestService.acceptByAgent(request);

        return ResponseEntity.ok(JsonResult.success(threadResponse));   
    }

    // @PreAuthorize("hasAuthority('AGENT_READ')")
    // @ActionAnnotation(title = "客服", action = "syncCurrentThreadCount", description = "sync agent current thread count")
    @Operation(summary = "同步当前会话数", description = "同步客服当前会话数量")
    @ApiResponse(responseCode = "200", description = "同步成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = AgentResponse.class)))
    @ActionAnnotation(title = "客服", action = "syncCurrentThreadCount", description = "sync agent current thread count")
    @PostMapping("/sync/current/thread/count")
    public ResponseEntity<?> syncCurrentThreadCount(@RequestBody AgentRequest request) {

        AgentResponse agent = agentRestService.syncCurrentThreadCount(request);
        if (agent != null) {
            return ResponseEntity.ok(JsonResult.success(agent));
        }
        return ResponseEntity.ok(JsonResult.error("sync current thread count failed"));
    }

    // @PreAuthorize("hasAuthority('AGENT_CREATE')")
    @Operation(summary = "创建客服", description = "创建新的客服")
    @ApiResponse(responseCode = "200", description = "创建成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = AgentResponse.class)))
    @ActionAnnotation(title = "客服", action = "新建", description = "create agent")
    @Override
    public ResponseEntity<?> create(@RequestBody AgentRequest request) {

        AgentResponse agent = agentRestService.create(request);
        if (agent == null) {
            return ResponseEntity.ok(JsonResult.error("create agent failed"));
        }

        return ResponseEntity.ok(JsonResult.success(agent));
    }

    // @PreAuthorize("hasAuthority('AGENT_UPDATE')")
    @Operation(summary = "更新客服", description = "更新客服信息")
    @ApiResponse(responseCode = "200", description = "更新成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = AgentResponse.class)))
    @ActionAnnotation(title = "客服", action = "更新", description = "update agent")
    @Override
    public ResponseEntity<?> update(@RequestBody AgentRequest request) {

        AgentResponse agent = agentRestService.update(request);
        //
        return ResponseEntity.ok(JsonResult.success(agent));
    }

    // updateAvatar
    // @PreAuthorize("hasAuthority('AGENT_UPDATE')") // 客服自己修改头像，不需要权限限制
    @Operation(summary = "更新客服头像", description = "更新客服的头像")
    @ApiResponse(responseCode = "200", description = "更新成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = AgentResponse.class)))
    @ActionAnnotation(title = "客服", action = "更新头像", description = "update agent avatar")
    @PostMapping("/update/avatar")
    public ResponseEntity<?> updateAvatar(@RequestBody AgentRequest request) {

        AgentResponse agent = agentRestService.updateAvatar(request);

        return ResponseEntity.ok(JsonResult.success(agent));
    }

    // @PreAuthorize("hasAuthority('AGENT_UPDATE')") // 客服自己修改在线状态，不需要权限限制
    @Operation(summary = "更新客服状态", description = "更新客服的在线状态")
    @ApiResponse(responseCode = "200", description = "更新成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = AgentResponse.class)))
    @ActionAnnotation(title = "客服", action = "更新状态", description = "update agent status")
    @PostMapping("/update/status")
    public ResponseEntity<?> updateStatus(@RequestBody AgentRequest request) {

        AgentResponse agent = agentRestService.updateStatus(request);
        //
        return ResponseEntity.ok(JsonResult.success(agent));
    }

    // @PreAuthorize("hasAuthority('AGENT_UPDATE')") // 客服自己修改自动回复，不需要权限限制
    @Operation(summary = "更新客服自动回复", description = "更新客服的自动回复设置")
    @ApiResponse(responseCode = "200", description = "更新成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = AgentResponse.class)))
    @ActionAnnotation(title = "客服", action = "更新自动回复", description = "update agent autoreply")
    @PostMapping("/update/autoreply")
    public ResponseEntity<?> updateAutoReply(@RequestBody AgentRequest request) {

        AgentResponse agent = agentRestService.updateAutoReply(request);
        //
        return ResponseEntity.ok(JsonResult.success(agent));
    }
    
    @PreAuthorize("hasAuthority('AGENT_DELETE')")
    @Operation(summary = "删除客服", description = "删除指定的客服")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @ActionAnnotation(title = "客服", action = "删除", description = "delete agent")
    @Override
    public ResponseEntity<?> delete(@RequestBody AgentRequest request) {

        agentRestService.deleteByUid(request.getUid());
        //
        return ResponseEntity.ok(JsonResult.success(request));
    }

    @Operation(summary = "导出客服", description = "导出客服数据")
    @ApiResponse(responseCode = "200", description = "导出成功")
    @Override
    public Object export(AgentRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    // @PreAuthorize("hasAnyRole('SUPER', 'ADMIN', 'MEMBER', 'AGENT')")
    @BlackIpFilter(title = "black", action = "sendAgentSseMessage")
    @BlackUserFilter(title = "black", action = "sendAgentSseMessage")
    @TabooJsonFilter(title = "敏感词", action = "sendAgentSseMessage")
    @Operation(summary = "客服消息SSE推送", description = "客服消息SSE实时推送接口")
    @ApiResponse(responseCode = "200", description = "推送成功")
    @ActionAnnotation(title = "客服", action = "sendAgentSseMessage", description = "sendAgentSseMessage")
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
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

}
