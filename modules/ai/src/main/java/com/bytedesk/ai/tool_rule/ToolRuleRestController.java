/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-11-29 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.tool_rule;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Description;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

/**
 * 工具审批管理控制器 - 用于管理工具审批的API接口
 * ToolRule Management Controller - API endpoints for managing tool rules
 */
@RestController
@RequestMapping("/api/v1/tool/rule")
@AllArgsConstructor
@Tag(name = "ToolRule Management", description = "ToolRule management APIs for organizing and categorizing content with tool_rules")
@Description("ToolRule Management Controller - Content tool_ruleging and categorization APIs")
public class ToolRuleRestController extends BaseRestController<ToolRuleRequest, ToolRuleRestService> {

    private final ToolRuleRestService tool_ruleRestService;

    @ActionAnnotation(title = I18Consts.I18N_TOOL_RULE, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query tool_rule by org")
    @Operation(summary = "Query ToolRules by Organization", description = "Retrieve tool_rules for the current organization")
    @PreAuthorize(ToolRulePermissions.HAS_TOOL_RULE_READ)
    @Override
    public ResponseEntity<?> queryByOrg(ToolRuleRequest request) {
        
        Page<ToolRuleResponse> tool_rules = tool_ruleRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(tool_rules));
    }

    @ActionAnnotation(title = I18Consts.I18N_TOOL_RULE, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query tool_rule by user")
    @Operation(summary = "Query ToolRules by User", description = "Retrieve tool_rules for the current user")
    @PreAuthorize(ToolRulePermissions.HAS_TOOL_RULE_READ)
    @Override
    public ResponseEntity<?> queryByUser(ToolRuleRequest request) {
        
        Page<ToolRuleResponse> tool_rules = tool_ruleRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(tool_rules));
    }

    @ActionAnnotation(title = I18Consts.I18N_TOOL_RULE, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query tool_rule by uid")
    @Operation(summary = "Query ToolRule by UID", description = "Retrieve a specific tool_rule by its unique identifier")
    @PreAuthorize(ToolRulePermissions.HAS_TOOL_RULE_READ)
    @Override
    public ResponseEntity<?> queryByUid(ToolRuleRequest request) {
        
        ToolRuleResponse tool_rule = tool_ruleRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(tool_rule));
    }

    @ActionAnnotation(title = I18Consts.I18N_TOOL_RULE, action = I18Consts.I18N_ACTION_CREATE, description = "create tool_rule")
    @Operation(summary = "Create ToolRule", description = "Create a new tool_rule")
    @Override
    @PreAuthorize(ToolRulePermissions.HAS_TOOL_RULE_CREATE)
    public ResponseEntity<?> create(ToolRuleRequest request) {
        
        ToolRuleResponse tool_rule = tool_ruleRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(tool_rule));
    }

    @ActionAnnotation(title = I18Consts.I18N_TOOL_RULE, action = I18Consts.I18N_ACTION_UPDATE, description = "update tool_rule")
    @Operation(summary = "Update ToolRule", description = "Update an existing tool_rule")
    @Override
    @PreAuthorize(ToolRulePermissions.HAS_TOOL_RULE_UPDATE)
    public ResponseEntity<?> update(ToolRuleRequest request) {
        
        ToolRuleResponse tool_rule = tool_ruleRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(tool_rule));
    }

    @ActionAnnotation(title = I18Consts.I18N_TOOL_RULE, action = I18Consts.I18N_ACTION_DELETE, description = "delete tool_rule")
    @Operation(summary = "Delete ToolRule", description = "Delete a tool_rule")
    @Override
    @PreAuthorize(ToolRulePermissions.HAS_TOOL_RULE_DELETE)
    public ResponseEntity<?> delete(ToolRuleRequest request) {
        
        tool_ruleRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_TOOL_RULE, action = I18Consts.I18N_ACTION_EXPORT, description = "export tool_rule")
    @Operation(summary = "Export ToolRules", description = "Export tool_rules to Excel format")
    @Override
    @PreAuthorize(ToolRulePermissions.HAS_TOOL_RULE_EXPORT)
    @GetMapping("/export")
    public Object export(ToolRuleRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            tool_ruleRestService,
            ToolRuleExcel.class,
            "ToolRule",
            "tool_rule"
        );
    }

    
    
}