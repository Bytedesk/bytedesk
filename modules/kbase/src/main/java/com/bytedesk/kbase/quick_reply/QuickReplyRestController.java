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
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/quickreply")
@AllArgsConstructor
public class QuickReplyRestController extends BaseRestController<QuickReplyRequest, QuickReplyRestService> {

    private final QuickReplyRestService quickReplyRestService;

    // 管理后台加载
    @PreAuthorize(QuickReplyPermissions.HAS_QUICKREPLY_READ_ANY_LEVEL)
    @ActionAnnotation(title = "快捷回复", action = "组织查询", description = "query quickReply by org")
    @Override
    public ResponseEntity<?> queryByOrg(QuickReplyRequest request) {
        
        Page<QuickReplyResponse> page = quickReplyRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    // 客服端加载
    @PreAuthorize(QuickReplyPermissions.HAS_QUICKREPLY_READ_ANY_LEVEL)
    @ActionAnnotation(title = "快捷回复", action = "用户查询", description = "query quickReply by user")
    @Override
    public ResponseEntity<?> queryByUser(QuickReplyRequest request) {

        Page<QuickReplyResponse> page = quickReplyRestService.queryByUser(request);
        
        return ResponseEntity.ok(JsonResult.success(page));
    }

    // @PreAuthorize(QuickReplyPermissions.HAS_QUICKREPLY_READ_ANY_LEVEL)
    @Override
    public ResponseEntity<?> queryByUid(QuickReplyRequest request) {
        
        QuickReplyResponse quickReply = quickReplyRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(quickReply));
    }
    
    @PreAuthorize(QuickReplyPermissions.HAS_QUICKREPLY_CREATE_ANY_LEVEL)
    @ActionAnnotation(title = "快捷回复", action = "新建", description = "create quickReply")
    @Override
    public ResponseEntity<?> create(@RequestBody QuickReplyRequest request) {
        
        QuickReplyResponse quickReply = quickReplyRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(quickReply));
    }

    @PreAuthorize(QuickReplyPermissions.HAS_QUICKREPLY_UPDATE_ANY_LEVEL)
    @ActionAnnotation(title = "快捷回复", action = "更新", description = "update quickReply")
    @Override
    public ResponseEntity<?> update(@RequestBody QuickReplyRequest request) {
        
        QuickReplyResponse quickReply = quickReplyRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(quickReply));
    }

    @PreAuthorize(QuickReplyPermissions.HAS_QUICKREPLY_DELETE_ANY_LEVEL)
    @ActionAnnotation(title = "快捷回复", action = "删除", description = "delete quickReply")
    @Override
    public ResponseEntity<?> delete(@RequestBody QuickReplyRequest request) {

        quickReplyRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success("delete success", request.getUid()));
    }
    
    @PreAuthorize(QuickReplyPermissions.HAS_QUICKREPLY_UPDATE_ANY_LEVEL)
    @ActionAnnotation(title = "快捷回复", action = "启用", description = "enable quickReply")
    @PostMapping("/enable")
    public ResponseEntity<?> enable(@RequestBody QuickReplyRequest request) {
        
        QuickReplyResponse quickReply = quickReplyRestService.enable(request);

        return ResponseEntity.ok(JsonResult.success(quickReply));
    }
    
    @PreAuthorize(QuickReplyPermissions.HAS_QUICKREPLY_EXPORT_ANY_LEVEL)
    @ActionAnnotation(title = "快捷回复", action = "导出", description = "export quickReply")
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
