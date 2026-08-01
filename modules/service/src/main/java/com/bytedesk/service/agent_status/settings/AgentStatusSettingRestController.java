/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-10 11:04:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent_status.settings;

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
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/agent/status/setting")
@AllArgsConstructor
@Tag(name = "Agent Status Setting Management", description = "Agent status setting management APIs")
public class AgentStatusSettingRestController extends BaseRestController<AgentStatusSettingRequest, AgentStatusSettingRestService> {

    private final AgentStatusSettingRestService tagService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @ActionAnnotation(title = I18Consts.I18N_AGENT_STATUS_SETTING, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query agent status setting by org")
    @GetMapping("/query/org")
    @PreAuthorize(AgentStatusSettingPermissions.HAS_AGENT_STATUS_SETTING_READ)
    @Override
    public ResponseEntity<?> queryByOrg(AgentStatusSettingRequest request) {
        
        Page<AgentStatusSettingResponse> tags = tagService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(tags));
    }

    @ActionAnnotation(title = I18Consts.I18N_AGENT_STATUS_SETTING, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query agent status setting by user")
    @GetMapping({ "/query", "/query/user" })
    @PreAuthorize(AgentStatusSettingPermissions.HAS_AGENT_STATUS_SETTING_READ)
    @Override
    public ResponseEntity<?> queryByUser(AgentStatusSettingRequest request) {
        
        Page<AgentStatusSettingResponse> tags = tagService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(tags));
    }

    @ActionAnnotation(title = I18Consts.I18N_AGENT_STATUS_SETTING, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query agent status setting by uid")
    @GetMapping("/query/uid")
    @PreAuthorize(AgentStatusSettingPermissions.HAS_AGENT_STATUS_SETTING_READ)
    @Override
    public ResponseEntity<?> queryByUid(AgentStatusSettingRequest request) {
        
        AgentStatusSettingResponse tag = tagService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(tag));
    }

    @ActionAnnotation(title = I18Consts.I18N_AGENT_STATUS_SETTING, action = I18Consts.I18N_ACTION_CREATE, description = "create agent status setting")
    @Override
    @PostMapping("/create")
    @PreAuthorize(AgentStatusSettingPermissions.HAS_AGENT_STATUS_SETTING_CREATE)
    public ResponseEntity<?> create(@RequestBody AgentStatusSettingRequest request) {
        
        AgentStatusSettingResponse tag = tagService.create(request);

        return ResponseEntity.ok(JsonResult.success(tag));
    }

    @ActionAnnotation(title = I18Consts.I18N_AGENT_STATUS_SETTING, action = I18Consts.I18N_ACTION_UPDATE, description = "update agent status setting")
    @Override
    @PostMapping("/update")
    @PreAuthorize(AgentStatusSettingPermissions.HAS_AGENT_STATUS_SETTING_UPDATE)
    public ResponseEntity<?> update(@RequestBody AgentStatusSettingRequest request) {
        
        AgentStatusSettingResponse tag = tagService.update(request);

        return ResponseEntity.ok(JsonResult.success(tag));
    }

    @ActionAnnotation(title = I18Consts.I18N_AGENT_STATUS_SETTING, action = I18Consts.I18N_ACTION_DELETE, description = "delete agent status setting")
    @Override
    @PostMapping("/delete")
    @PreAuthorize(AgentStatusSettingPermissions.HAS_AGENT_STATUS_SETTING_DELETE)
    public ResponseEntity<?> delete(@RequestBody AgentStatusSettingRequest request) {
        
        tagService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_AGENT_STATUS_SETTING, action = I18Consts.I18N_ACTION_EXPORT, description = "export agent status setting")
    @Override
    @PreAuthorize(AgentStatusSettingPermissions.HAS_AGENT_STATUS_SETTING_EXPORT)
    @GetMapping("/export")
    public Object export(AgentStatusSettingRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            tagService,
            AgentStatusSettingExcel.class,
            "客服状态设置",
            "agent-status-setting"
        );
    }

    
    
}