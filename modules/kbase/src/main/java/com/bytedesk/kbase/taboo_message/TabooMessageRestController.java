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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/taboo/message")
@AllArgsConstructor
public class TabooMessageRestController extends BaseRestController<TabooMessageRequest> {

    private final TabooMessageRestService tabooMessageService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(TabooMessageRequest request) {
        
        Page<TabooMessageResponse> page = tabooMessageService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUser(TabooMessageRequest request) {
        
        Page<TabooMessageResponse> page = tabooMessageService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @ActionAnnotation(title = "taboo_message", action = "新建", description = "create taboo_message")
    @Override
    public ResponseEntity<?> create(TabooMessageRequest request) {
        
        TabooMessageResponse tabooMessage = tabooMessageService.initVisitor(request);

        return ResponseEntity.ok(JsonResult.success(tabooMessage));
    }

    @ActionAnnotation(title = "taboo_message", action = "更新", description = "update taboo_message")
    @Override
    public ResponseEntity<?> update(TabooMessageRequest request) {
        
        TabooMessageResponse tabooMessage = tabooMessageService.update(request);

        return ResponseEntity.ok(JsonResult.success(tabooMessage));
    }

    @ActionAnnotation(title = "taboo_message", action = "删除", description = "delete taboo_message")
    @Override
    public ResponseEntity<?> delete(TabooMessageRequest request) {
        
        tabooMessageService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }
    
    @ActionAnnotation(title = "taboo_message", action = "导出", description = "export taboo_message")
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

    @Override
    public ResponseEntity<?> queryByUid(TabooMessageRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    
}
