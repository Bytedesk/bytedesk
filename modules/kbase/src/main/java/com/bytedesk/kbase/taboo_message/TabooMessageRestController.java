/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-27 22:34:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-20 07:37:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.taboo_message;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/taboo/message")
@AllArgsConstructor
@Tag(name = "敏感词消息管理", description = "敏感词消息管理相关接口")
public class TabooMessageRestController extends BaseRestController<TabooMessageRequest, TabooMessageRestService> {

    private final TabooMessageRestService tabooMessageService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Operation(summary = "根据组织查询敏感词消息", description = "查询组织的敏感词消息列表")
    @PreAuthorize(TabooMessagePermissions.HAS_TABOO_MESSAGE_READ)
    @Override
    public ResponseEntity<?> queryByOrg(TabooMessageRequest request) {
        
        Page<TabooMessageResponse> page = tabooMessageService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "根据用户查询敏感词消息", description = "查询用户的敏感词消息列表")
    @PreAuthorize(TabooMessagePermissions.HAS_TABOO_MESSAGE_READ)
    @Override
    public ResponseEntity<?> queryByUser(TabooMessageRequest request) {
        
        Page<TabooMessageResponse> page = tabooMessageService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "根据UID查询敏感词消息", description = "通过UID查询具体的敏感词消息")
    @PreAuthorize(TabooMessagePermissions.HAS_TABOO_MESSAGE_READ)
    @Override
    public ResponseEntity<?> queryByUid(TabooMessageRequest request) {
        
        TabooMessageResponse tabooMessage = tabooMessageService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(tabooMessage));
    }

    @ActionAnnotation(title = "taboo_message", action = "新建", description = "create taboo_message")
    @Operation(summary = "创建敏感词消息", description = "创建新的敏感词消息")
    @PreAuthorize(TabooMessagePermissions.HAS_TABOO_MESSAGE_CREATE)
    @Override
    public ResponseEntity<?> create(TabooMessageRequest request) {
        
        TabooMessageResponse tabooMessage = tabooMessageService.create(request);

        return ResponseEntity.ok(JsonResult.success(tabooMessage));
    }

    @ActionAnnotation(title = "taboo_message", action = "更新", description = "update taboo_message")
    @Operation(summary = "更新敏感词消息", description = "更新现有的敏感词消息")
    @PreAuthorize(TabooMessagePermissions.HAS_TABOO_MESSAGE_UPDATE)
    @Override
    public ResponseEntity<?> update(TabooMessageRequest request) {
        
        TabooMessageResponse tabooMessage = tabooMessageService.update(request);

        return ResponseEntity.ok(JsonResult.success(tabooMessage));
    }

    @ActionAnnotation(title = "taboo_message", action = "删除", description = "delete taboo_message")
    @Operation(summary = "删除敏感词消息", description = "删除指定的敏感词消息")
    @PreAuthorize(TabooMessagePermissions.HAS_TABOO_MESSAGE_DELETE)
    @Override
    public ResponseEntity<?> delete(TabooMessageRequest request) {
        
        tabooMessageService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }
    
    @ActionAnnotation(title = "taboo_message", action = "导出", description = "export taboo_message")
    @Operation(summary = "导出敏感词消息", description = "导出敏感词消息数据")
    @PreAuthorize(TabooMessagePermissions.HAS_TABOO_MESSAGE_EXPORT)
    @Override
    public Object export(TabooMessageRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            tabooMessageService,
            TabooMessageExcel.class,
            "敏感词",
            "TabooMessage"
        );
    }

    
    
}
