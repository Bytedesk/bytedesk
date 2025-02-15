/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-15 15:45:34
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
import java.util.stream.Collectors;

import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/ticket/process")
@AllArgsConstructor
public class TicketProcessRestController extends BaseRestController<TicketProcessRequest> {

    private final TicketProcessRestService processRestService;

    private final TicketProcessService processService;
    
    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(TicketProcessRequest request) {
        
        Page<TicketProcessResponse> process = processRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(process));
    }

    @Override
    public ResponseEntity<?> queryByUser(TicketProcessRequest request) {
        
        Page<TicketProcessResponse> process = processRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(process));
    }

    @Override
    public ResponseEntity<?> create(TicketProcessRequest request) {
        
        TicketProcessResponse process = processRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(process));
    }

    @Override
    public ResponseEntity<?> update(TicketProcessRequest request) {
        
        TicketProcessResponse process = processRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(process));
    }

    @Override
    public ResponseEntity<?> delete(TicketProcessRequest request) {
        
        processRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }
    // 修改查询方法
    @RequestMapping("/query/deployments")
    public ResponseEntity<?> queryProcessDefinition(TicketProcessRequest request) {
        List<ProcessDefinition> definitions = processService.query(request);
        
        List<ProcessDefinitionResponse> dtos = definitions.stream()
            .map(def -> ProcessDefinitionResponse.builder()
                .id(def.getId())
                .key(def.getKey())
                .name(def.getName())
                .description(def.getDescription())
                .version(def.getVersion())
                .deploymentId(def.getDeploymentId())
                .tenantId(def.getTenantId())
                .build())
            .collect(Collectors.toList());

        return ResponseEntity.ok(JsonResult.success(dtos));
    }

    // 部署流程
    @RequestMapping("/deploy")
    public ResponseEntity<?> deployProcess(TicketProcessRequest request) {
        
        ProcessDefinition processDefinition = processService.deploy(request);

        return ResponseEntity.ok(JsonResult.success(processDefinition));
    }

    // 取消部署流程
    @RequestMapping("/undeploy")
    public ResponseEntity<?> undeployProcess(TicketProcessRequest request) {

        List<ProcessDefinition> processDefinition = processService.undeploy(request);

        return ResponseEntity.ok(JsonResult.success(processDefinition));
    }
    
}