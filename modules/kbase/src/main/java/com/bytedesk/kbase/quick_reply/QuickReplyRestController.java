/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-03 21:52:57
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.quick_reply;

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
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/v1/quickreply")
@AllArgsConstructor
public class QuickReplyRestController extends BaseRestController<QuickReplyRequest, QuickReplyRestService> {

    private final QuickReplyRestService quickReplyRestService;

    // 管理后台加载
    @Operation(summary = "Query Quick Replies by Organization", description = "Retrieve quick replies for the current organization")
    @PreAuthorize(QuickReplyPermissions.HAS_QUICK_REPLY_READ)
    @ActionAnnotation(title = I18Consts.I18N_QUICK_REPLY, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query quickReply by org")
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(QuickReplyRequest request) {
        
        Page<QuickReplyResponse> page = quickReplyRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    // 客服端加载
    @Operation(summary = "Query Quick Replies by User", description = "Retrieve quick replies for the current user")
    @PreAuthorize(QuickReplyPermissions.HAS_QUICK_REPLY_READ)
    @ActionAnnotation(title = I18Consts.I18N_QUICK_REPLY, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query quickReply by user")
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(QuickReplyRequest request) {

        Page<QuickReplyResponse> page = quickReplyRestService.queryByUser(request);
        
        return ResponseEntity.ok(JsonResult.success(page));
    }

    // @PreAuthorize(QuickReplyPermissions.HAS_QUICK_REPLY_READ)
    @Operation(summary = "Query Quick Replies by UID", description = "Retrieve a specific quick replie entry by its unique identifier")
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(QuickReplyRequest request) {
        
        QuickReplyResponse quickReply = quickReplyRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(quickReply));
    }
    
    @Operation(summary = "Create Quick Replies", description = "Create a new quick replie entry")
    @PreAuthorize(QuickReplyPermissions.HAS_QUICK_REPLY_CREATE)
    @ActionAnnotation(title = I18Consts.I18N_QUICK_REPLY, action = I18Consts.I18N_ACTION_CREATE, description = "create quickReply")
    @Override
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody QuickReplyRequest request) {
        
        QuickReplyResponse quickReply = quickReplyRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(quickReply));
    }

    @Operation(summary = "Update Quick Replies", description = "Update an existing quick replie entry")
    @PreAuthorize(QuickReplyPermissions.HAS_QUICK_REPLY_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_QUICK_REPLY, action = I18Consts.I18N_ACTION_UPDATE, description = "update quickReply")
    @Override
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody QuickReplyRequest request) {
        
        QuickReplyResponse quickReply = quickReplyRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(quickReply));
    }

    @Operation(summary = "Delete Quick Replies", description = "Delete a quick replie entry")
    @PreAuthorize(QuickReplyPermissions.HAS_QUICK_REPLY_DELETE)
    @ActionAnnotation(title = I18Consts.I18N_QUICK_REPLY, action = I18Consts.I18N_ACTION_DELETE, description = "delete quickReply")
    @Override
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody QuickReplyRequest request) {

        quickReplyRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success("delete success", request.getUid()));
    }
    
    @Operation(summary = "Enable / Disable", description = "Enable or disable quick replie entries")
    @PreAuthorize(QuickReplyPermissions.HAS_QUICK_REPLY_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_QUICK_REPLY, action = I18Consts.I18N_ACTION_ENABLE, description = "enable quickReply")
    @PostMapping("/enable")
    public ResponseEntity<?> enable(@RequestBody QuickReplyRequest request) {
        
        QuickReplyResponse quickReply = quickReplyRestService.enable(request);

        return ResponseEntity.ok(JsonResult.success(quickReply));
    }
    
    @Operation(summary = "Export Quick Replies", description = "Export quick replies to Excel format")
    @PreAuthorize(QuickReplyPermissions.HAS_QUICK_REPLY_EXPORT)
    @ActionAnnotation(title = I18Consts.I18N_QUICK_REPLY, action = I18Consts.I18N_ACTION_EXPORT, description = "export quickReply")
    @GetMapping("/export")
    @Override
    public Object export(QuickReplyRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            quickReplyRestService,
            QuickReplyExcel.class,
            "快捷回复",
            "quick_reply"
        );
    }

    

}
