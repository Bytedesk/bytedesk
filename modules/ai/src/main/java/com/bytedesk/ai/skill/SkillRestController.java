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
package com.bytedesk.ai.skill;

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

@RestController
@RequestMapping("/api/v1/skill")
@AllArgsConstructor
@Tag(name = "Skill Management", description = "Skill management APIs for organizing and categorizing content with skills")
@Description("Skill Management Controller - Content skillging and categorization APIs")
public class SkillRestController extends BaseRestController<SkillRequest, SkillRestService> {

    private final SkillRestService skillRestService;

    @ActionAnnotation(title = I18Consts.I18N_SKILL, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query skill by org")
    @Operation(summary = "Query Skills by Organization", description = "Retrieve skills for the current organization")
    @PreAuthorize(SkillPermissions.HAS_SKILL_READ)
    @Override
    public ResponseEntity<?> queryByOrg(SkillRequest request) {
        
        Page<SkillResponse> skills = skillRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(skills));
    }

    @ActionAnnotation(title = I18Consts.I18N_SKILL, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query skill by user")
    @Operation(summary = "Query Skills by User", description = "Retrieve skills for the current user")
    @PreAuthorize(SkillPermissions.HAS_SKILL_READ)
    @Override
    public ResponseEntity<?> queryByUser(SkillRequest request) {
        
        Page<SkillResponse> skills = skillRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(skills));
    }

    @ActionAnnotation(title = I18Consts.I18N_SKILL, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query skill by uid")
    @Operation(summary = "Query Skill by UID", description = "Retrieve a specific skill by its unique identifier")
    @PreAuthorize(SkillPermissions.HAS_SKILL_READ)
    @Override
    public ResponseEntity<?> queryByUid(SkillRequest request) {
        
        SkillResponse skill = skillRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(skill));
    }

    @ActionAnnotation(title = I18Consts.I18N_SKILL, action = I18Consts.I18N_ACTION_CREATE, description = "create skill")
    @Operation(summary = "Create Skill", description = "Create a new skill")
    @Override
    @PreAuthorize(SkillPermissions.HAS_SKILL_CREATE)
    public ResponseEntity<?> create(SkillRequest request) {
        
        SkillResponse skill = skillRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(skill));
    }

    @ActionAnnotation(title = I18Consts.I18N_SKILL, action = I18Consts.I18N_ACTION_UPDATE, description = "update skill")
    @Operation(summary = "Update Skill", description = "Update an existing skill")
    @Override
    @PreAuthorize(SkillPermissions.HAS_SKILL_UPDATE)
    public ResponseEntity<?> update(SkillRequest request) {
        
        SkillResponse skill = skillRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(skill));
    }

    @ActionAnnotation(title = I18Consts.I18N_SKILL, action = I18Consts.I18N_ACTION_DELETE, description = "delete skill")
    @Operation(summary = "Delete Skill", description = "Delete a skill")
    @Override
    @PreAuthorize(SkillPermissions.HAS_SKILL_DELETE)
    public ResponseEntity<?> delete(SkillRequest request) {
        
        skillRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_SKILL, action = I18Consts.I18N_ACTION_EXPORT, description = "export skill")
    @Operation(summary = "Export Skills", description = "Export skills to Excel format")
    @Override
    @PreAuthorize(SkillPermissions.HAS_SKILL_EXPORT)
    @GetMapping("/export")
    public Object export(SkillRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            skillRestService,
            SkillExcel.class,
            "Skill",
            "skill"
        );
    }

    
    
}