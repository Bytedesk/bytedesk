/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-27 22:39:49
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-13 17:06:10
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.auto_reply.fixed;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.action.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
// import com.bytedesk.core.rbac.role.RolePermissions;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.core.utils.JsonResult;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/autoreply")
@AllArgsConstructor
public class AutoReplyFixedRestController extends BaseRestController<AutoReplyFixedRequest> {

    private final AutoReplyFixedRestService autoReplyService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(AutoReplyFixedRequest request) {
        
        Page<AutoReplyFixedResponse> page = autoReplyService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUser(AutoReplyFixedRequest request) {
        
        Page<AutoReplyFixedResponse> page = autoReplyService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> create(AutoReplyFixedRequest request) {
        
        AutoReplyFixedResponse response = autoReplyService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    public ResponseEntity<?> update(AutoReplyFixedRequest request) {
        
        AutoReplyFixedResponse response = autoReplyService.update(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    public ResponseEntity<?> delete(AutoReplyFixedRequest request) {
        
        autoReplyService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }
    
    // https://github.com/alibaba/easyexcel
    // https://easyexcel.opensource.alibaba.com/docs/current/
    @ActionAnnotation(title = "autoReply", action = "export", description = "export autoReply")
    @GetMapping("/export")
    public Object export(AutoReplyFixedRequest request, HttpServletResponse response) {
        // query data to export
        Page<AutoReplyFixedResponse> autoReplyPage = autoReplyService.queryByOrg(request);
        // 
        try {
            //
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // download filename
            String fileName = "AutoReplyFixed-" + BdDateUtils.formatDatetimeUid() + ".xlsx";
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);

            // 转换数据
            List<AutoReplyFixedExcel> excelList = autoReplyPage.getContent().stream().map(autoReplyResponse -> autoReplyService.convertToExcel(autoReplyResponse)).toList();

            // write to excel
            EasyExcel.write(response.getOutputStream(), AutoReplyFixedExcel.class)
                    .autoCloseStream(Boolean.FALSE)
                    .sheet("AutoReplyFixed")
                    .doWrite(excelList);

        } catch (Exception e) {
            // reset response
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            //
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("status", "failure");
            jsonObject.put("message", "download failed " + e.getMessage());
            try {
                response.getWriter().println(JSON.toJSONString(jsonObject));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        return "";
    }

    
}
