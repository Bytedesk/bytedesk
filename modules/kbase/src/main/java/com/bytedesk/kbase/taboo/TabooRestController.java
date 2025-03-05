/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-27 22:34:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-05 13:20:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.taboo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/api/v1/taboo")
@AllArgsConstructor
public class TabooRestController extends BaseRestController<TabooRequest> {

    private final TabooRestService tabooService;

    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(TabooRequest request) {
        
        Page<TabooResponse> page = tabooService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUser(TabooRequest request) {
        
        Page<TabooResponse> page = tabooService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @ActionAnnotation(title = "taboo", action = "create", description = "create taboo")
    @Override
    public ResponseEntity<?> create(TabooRequest request) {
        
        TabooResponse taboo = tabooService.create(request);

        return ResponseEntity.ok(JsonResult.success(taboo));
    }

    @ActionAnnotation(title = "taboo", action = "update", description = "update taboo")
    @Override
    public ResponseEntity<?> update(TabooRequest request) {
        
        TabooResponse taboo = tabooService.update(request);

        return ResponseEntity.ok(JsonResult.success(taboo));
    }

    @ActionAnnotation(title = "taboo", action = "delete", description = "delete taboo")
    @Override
    public ResponseEntity<?> delete(TabooRequest request) {
        
        tabooService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }
    
    // https://github.com/alibaba/easyexcel
    // https://easyexcel.opensource.alibaba.com/docs/current/
    @ActionAnnotation(title = "taboo", action = "export", description = "export taboo")
    @Override
    public Object export(TabooRequest request, HttpServletResponse response) {
        // query data to export
        Page<TabooEntity> tabooPage = tabooService.queryByOrgExcel(request);
        // 
        try {
            //
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // download filename
            String fileName = "kbase-taboo-" + BdDateUtils.formatDatetimeUid() + ".xlsx";
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);

            // 转换数据
            List<TabooExcel> excelList = tabooPage.getContent().stream().map(tabooResponse -> tabooService.convertToExcel(tabooResponse)).toList();

            // write to excel
            EasyExcel.write(response.getOutputStream(), TabooExcel.class)
                    .autoCloseStream(Boolean.FALSE)
                    .sheet("Taboo")
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

    
}
