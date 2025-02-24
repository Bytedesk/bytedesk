/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-06 10:05:48
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-18 17:10:13
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.auto_reply.keyword;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.excel.EasyExcel;
import com.bytedesk.core.action.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.rbac.role.RolePermissions;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/keyword")
@AllArgsConstructor
public class AutoReplyKeywordRestController extends BaseRestController<AutoReplyKeywordRequest> {

    private final AutoReplyKeywordRestService keywordService;

    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(AutoReplyKeywordRequest request) {
        
        Page<AutoReplyKeywordResponse> page = keywordService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUser(AutoReplyKeywordRequest request) {
        
        Page<AutoReplyKeywordResponse> page = keywordService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @ActionAnnotation(title = "keyword", action = "create", description = "create keyword")
    @Override
    public ResponseEntity<?> create(@RequestBody AutoReplyKeywordRequest request) {
        
        AutoReplyKeywordResponse response = keywordService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @ActionAnnotation(title = "keyword", action = "update", description = "update keyword")
    @Override
    public ResponseEntity<?> update(@RequestBody AutoReplyKeywordRequest request) {
        
        AutoReplyKeywordResponse response = keywordService.update(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @ActionAnnotation(title = "keyword", action = "delete", description = "delete keyword")
    @Override
    public ResponseEntity<?> delete(@RequestBody AutoReplyKeywordRequest request) {

        keywordService.delete(request);

        return ResponseEntity.ok(JsonResult.success(request.getUid()));
    }

    // https://github.com/alibaba/easyexcel
    // https://easyexcel.opensource.alibaba.com/docs/current/
    @ActionAnnotation(title = "keyword", action = "export", description = "export keyword")
    @GetMapping("/export")
    public Object export(AutoReplyKeywordRequest request, HttpServletResponse response) {
        // query data to export
        Page<AutoReplyKeywordResponse> keywordPage = keywordService.queryByOrg(request);
        // 
        try {
            //
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // download filename
            String fileName = "AutoReplyKeyword-" + BdDateUtils.formatDatetimeUid() + ".xlsx";
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);

            // 转换数据
            List<AutoReplyKeywordExcel> excelList = keywordPage.getContent().stream().map(keywordResponse -> keywordService.convertToExcel(keywordResponse)).toList();

            // write to excel
            EasyExcel.write(response.getOutputStream(), AutoReplyKeywordExcel.class)
                    .autoCloseStream(Boolean.FALSE)
                    .sheet("AutoReplyKeyword")
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
