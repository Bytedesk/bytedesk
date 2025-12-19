/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-15 15:10:47
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-22 16:08:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.process;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * 流程服务 - 委托给 ProcessRestService 处理
 * 保留此类以保持 Controller 层的兼容性
 */
@Service
@RequiredArgsConstructor
public class ProcessService {
    
    private final ProcessRestService processRestService;

    /**
     * 查询已部署的流程定义列表
     */
    public List<ProcessDefinitionResponse> query(ProcessRequest request) {
        return processRestService.queryDeployedProcessDefinitions(request);
    }

    /**
     * 部署流程
     */
    public ProcessDefinitionResponse deploy(ProcessRequest request) {
        String processUid = request.getUid();
        if (processUid == null) {
            throw new RuntimeException("流程UID不能为空");
        }
        return processRestService.deployProcess(processUid, false);
    }

    /**
     * 取消部署流程
     */
    public List<ProcessDefinitionResponse> undeploy(ProcessRequest request) {
        String processUid = request.getUid();
        if (processUid == null) {
            throw new RuntimeException("流程UID不能为空");
        }
        return processRestService.undeployProcess(processUid);
    }
}
