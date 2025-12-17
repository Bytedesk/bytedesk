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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/ticket/process")
@AllArgsConstructor
public class ProcessRestController extends BaseRestController<ProcessRequest, ProcessRestService> {

    private final ProcessRestService processRestService;

    private final ProcessService processService;
    
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

    // 取消部署流程
    @PostMapping("/undeploy")
    public ResponseEntity<?> undeployProcess(@RequestBody ProcessRequest request) {

        List<ProcessDefinitionResponse> definitions = processService.undeploy(request);

        return ResponseEntity.ok(JsonResult.success(definitions));
    }

    @Override
    public Object export(ProcessRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Override
    public ResponseEntity<?> queryByUid(ProcessRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}