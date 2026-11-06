/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-22 16:38:09
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.process;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.exception.NotLoginException;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/ticket/process")
@AllArgsConstructor
public class ProcessRestController extends BaseRestController<ProcessRequest, ProcessRestService> {

    private final ProcessRestService processRestService;

    private final ProcessService processService;

    private final ProcessInitializationService processInitializationService;

    private final AuthService authService;

    @Override
    public ResponseEntity<?> queryByOrg(ProcessRequest request) {
        
        Page<ProcessResponse> process = processRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(process));
    }

    @Override
    public ResponseEntity<?> queryByUser(ProcessRequest request) {
        
        Page<ProcessResponse> process = processRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(process));
    }

    @Override
    public ResponseEntity<?> queryByUid(ProcessRequest request) {
        
        ProcessResponse process = processRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(process));
    }

    @Override
    public ResponseEntity<?> create(ProcessRequest request) {
        
        ProcessResponse process = processRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(process));
    }

    @Override
    public ResponseEntity<?> update(ProcessRequest request) {
        
        ProcessResponse process = processRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(process));
    }

    @Override
    public ResponseEntity<?> delete(ProcessRequest request) {
        
        processRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    // 查询流程定义列表
    @GetMapping("/query/deployments")
    public ResponseEntity<?> queryProcesses(ProcessRequest request) {

        List<ProcessDefinitionResponse> definitions = processService.query(request);

        return ResponseEntity.ok(JsonResult.success(definitions));
    }

    // 部署流程
    @PostMapping("/deploy")
    public ResponseEntity<?> deployProcess(@RequestBody ProcessRequest request) {

        ProcessDefinitionResponse definition = processService.deploy(request);

        return ResponseEntity.ok(JsonResult.success(definition));
    }

    // 校验流程结构和业务语义配置
    @PostMapping("/validate")
    public ResponseEntity<?> validateProcess(@RequestBody ProcessRequest request) {

        ProcessValidationResponse validation = processRestService.validateProcess(request);

        return ResponseEntity.ok(JsonResult.success(validation));
    }

    // 取消部署流程
    @PostMapping("/undeploy")
    public ResponseEntity<?> undeployProcess(@RequestBody ProcessRequest request) {

        List<ProcessDefinitionResponse> definitions = processService.undeploy(request);

        return ResponseEntity.ok(JsonResult.success(definitions));
    }

    // 重置流程为默认内容
    @PostMapping("/reset")
    public ResponseEntity<?> resetProcess(@RequestBody ProcessRequest request) {

        ProcessResponse process = processRestService.resetProcess(request);

        return ResponseEntity.ok(JsonResult.success(process));
    }

    // 初始化（补齐）组织的默认工单流程（内部/外部各一条，幂等）
    @PreAuthorize(ProcessPermissions.HAS_PROCESS_CREATE)
    @ActionAnnotation(title = I18Consts.I18N_TICKET, action = I18Consts.I18N_ACTION_INIT, description = "init default ticket processes")
    @Operation(summary = "Init Default Ticket Processes", description = "Create or repair the two default ticket processes (internal/external) for the target organization")
    @ApiResponse(responseCode = "200", description = "Initialized successfully")
    @PostMapping("/init")
    public ResponseEntity<?> init(@RequestBody ProcessRequest request) {

        String orgUid = resolveTargetOrgUid(request.getOrgUid());
        ProcessInitializationResult result = processInitializationService.initDefaultProcesses(orgUid);

        return ResponseEntity.ok(JsonResult.success(result));
    }

    // 基于后端配对模板创建自定义工单流程（DRAFT，不自动部署、不占用默认固定 uid）
    @PreAuthorize(ProcessPermissions.HAS_PROCESS_CREATE)
    @ActionAnnotation(title = I18Consts.I18N_TICKET, action = I18Consts.I18N_ACTION_CREATE, description = "create ticket process from template")
    @Operation(summary = "Create Ticket Process From Template", description = "Create a draft custom ticket process with backend paired BPMN + flowgram templates")
    @ApiResponse(responseCode = "200", description = "Created successfully")
    @PostMapping("/create/template")
    public ResponseEntity<?> createFromTemplate(@RequestBody ProcessRequest request) {

        String orgUid = resolveTargetOrgUid(request.getOrgUid());
        ProcessResponse process = processRestService.createFromTemplate(request.getName(),
                request.getDescription(), request.getType(), orgUid);

        return ResponseEntity.ok(JsonResult.success(process));
    }

    /**
     * 解析初始化/模板创建的目标组织：
     * - 未传 orgUid：回退当前登录用户所属组织（普通管理员只能初始化本组织）
     * - 显式传 orgUid：须为当前用户组织，或当前用户为后端验证的超级管理员；
     *   前端 superUser 参数与查询侧平台跨组织只读能力均不构成写授权
     */
    private String resolveTargetOrgUid(String requestedOrgUid) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new NotLoginException("login required");
        }
        if (requestedOrgUid == null || requestedOrgUid.isBlank()) {
            String currentOrgUid = user.getOrgUid();
            if (currentOrgUid == null || currentOrgUid.isBlank()) {
                throw new RuntimeException("current organization unavailable, orgUid required");
            }
            return currentOrgUid;
        }
        if (user.isSuperUser() || requestedOrgUid.equals(user.getOrgUid())) {
            return requestedOrgUid;
        }
        throw new RuntimeException("no permission to operate on organization " + requestedOrgUid);
    }

    @Override
    public Object export(ProcessRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    
    
}