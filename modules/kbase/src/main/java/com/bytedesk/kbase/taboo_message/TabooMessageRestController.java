/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-27 22:34:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-05 16:39:38
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.taboo_message;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.excel.EasyExcel;
import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.rbac.role.RolePermissions;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.core.utils.JsonResult;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/taboo_message")
@AllArgsConstructor
public class TabooMessageRestController extends BaseRestController<TabooMessageRequest> {

    private final TabooMessageRestService taboo_messageService;

    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(TabooMessageRequest request) {
        
        Page<TabooMessageResponse> page = taboo_messageService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUser(TabooMessageRequest request) {
        
        Page<TabooMessageResponse> page = taboo_messageService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @ActionAnnotation(title = "taboo_message", action = "新建", description = "create taboo_message")
    @Override
    public ResponseEntity<?> create(TabooMessageRequest request) {
        
        TabooMessageResponse taboo_message = taboo_messageService.create(request);

        return ResponseEntity.ok(JsonResult.success(taboo_message));
    }

    @ActionAnnotation(title = "taboo_message", action = "更新", description = "update taboo_message")
    @Override
    public ResponseEntity<?> update(TabooMessageRequest request) {
        
        TabooMessageResponse taboo_message = taboo_messageService.update(request);

        return ResponseEntity.ok(JsonResult.success(taboo_message));
    }

    @ActionAnnotation(title = "taboo_message", action = "删除", description = "delete taboo_message")
    @Override
    public ResponseEntity<?> delete(TabooMessageRequest request) {
        
        taboo_messageService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }
    
    // https://github.com/alibaba/easyexcel
    // https://easyexcel.opensource.alibaba.com/docs/current/
    @ActionAnnotation(title = "taboo_message", action = "导出", description = "export taboo_message")
    @Override
    public Object export(TabooMessageRequest request, HttpServletResponse response) {
        // query data to export
        Page<TabooMessageEntity> taboo_messagePage = taboo_messageService.queryByOrgExcel(request);
        // 
        try {
            //
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // download filename
            String fileName = "kbase-taboo_message-" + BdDateUtils.formatDatetimeUid() + ".xlsx";
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);

            // 转换数据
            List<TabooMessageExcel> excelList = taboo_messagePage.getContent().stream().map(taboo_messageResponse -> taboo_messageService.convertToExcel(taboo_messageResponse)).toList();

            // write to excel
            EasyExcel.write(response.getOutputStream(), TabooMessageExcel.class)
                    .autoCloseStream(Boolean.FALSE)
                    .sheet("TabooMessage")
                    .doWrite(excelList);

        } catch (Exception e) {
            // reset response
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            // 
            return JsonResult.error(e.getMessage());
        }

        return "";
    }

    @Override
    public ResponseEntity<?> queryByUid(TabooMessageRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    
}
