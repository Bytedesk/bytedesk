/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-25 12:59:05
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.text;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.excel.EasyExcel;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/text")
@AllArgsConstructor
public class TextRestController extends BaseRestController<TextRequest> {

    private final TextRestService textService;

    @PreAuthorize("hasAnyRole('SUPER', 'ADMIN')")
    @Override
    public ResponseEntity<?> queryByOrg(TextRequest request) {
        
        Page<TextResponse> texts = textService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(texts));
    }

    @PreAuthorize("hasAnyRole('SUPER', 'ADMIN', 'MEMBER', 'AGENT')")
    @Override
    public ResponseEntity<?> queryByUser(TextRequest request) {
        
        Page<TextResponse> texts = textService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(texts));
    }

    @PreAuthorize("hasAuthority('KBASE_CREATE')")
    @Override
    public ResponseEntity<?> create(TextRequest request) {
        
        TextResponse text = textService.create(request);

        return ResponseEntity.ok(JsonResult.success(text));
    }

    @PreAuthorize("hasAuthority('KBASE_UPDATE')")
    @Override
    public ResponseEntity<?> update(TextRequest request) {
        
        TextResponse text = textService.update(request);

        return ResponseEntity.ok(JsonResult.success(text));
    }

    @PreAuthorize("hasAuthority('KBASE_DELETE')")
    @Override
    public ResponseEntity<?> delete(TextRequest request) {
        
        textService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @PreAuthorize("hasAuthority('KBASE_EXPORT')")
    @Override
    public Object export(TextRequest request, HttpServletResponse response) {
        // query data to export
        Page<TextEntity> textPage = textService.queryByOrgEntity(request);
        // 
        try {
            //
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // download filename
            // String fileName = "kbase-text-" + BdDateUtils.formatDatetimeUid() + ".xlsx";
            String fileName = "text-" + BdDateUtils.formatDatetimeUid() + ".xlsx";
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);

            // 转换数据
            List<TextExcel> excelList = textPage.getContent().stream().map(textResponse -> textService.convertToExcel(textResponse)).toList();
            // write to excel
            EasyExcel.write(response.getOutputStream(), TextExcel.class)
                    .autoCloseStream(Boolean.FALSE)
                    .sheet("text")
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

    @PreAuthorize("hasAnyRole('SUPER', 'ADMIN', 'MEMBER', 'AGENT')")
    @Override
    public ResponseEntity<?> queryByUid(TextRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}