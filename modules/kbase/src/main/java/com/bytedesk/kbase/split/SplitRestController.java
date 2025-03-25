/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-25 14:20:56
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.split;

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
@RequestMapping("/api/v1/split")
@AllArgsConstructor
public class SplitRestController extends BaseRestController<SplitRequest> {

    private final SplitRestService splitService;

    @PreAuthorize("hasAuthority('KBASE_READ')")
    @Override
    public ResponseEntity<?> queryByOrg(SplitRequest request) {
        
        Page<SplitResponse> splits = splitService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(splits));
    }

    @PreAuthorize("hasAuthority('KBASE_READ')")
    @Override
    public ResponseEntity<?> queryByUser(SplitRequest request) {
        
        Page<SplitResponse> splits = splitService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(splits));
    }

    @PreAuthorize("hasAuthority('KBASE_READ')")
    @Override
    public ResponseEntity<?> queryByUid(SplitRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    @PreAuthorize("hasAuthority('KBASE_CREATE')")
    @Override
    public ResponseEntity<?> create(SplitRequest request) {
        
        SplitResponse split = splitService.create(request);

        return ResponseEntity.ok(JsonResult.success(split));
    }

    @PreAuthorize("hasAuthority('KBASE_UPDATE')")
    @Override
    public ResponseEntity<?> update(SplitRequest request) {
        
        SplitResponse split = splitService.update(request);

        return ResponseEntity.ok(JsonResult.success(split));
    }

    @PreAuthorize("hasAuthority('KBASE_DELETE')")
    @Override
    public ResponseEntity<?> delete(SplitRequest request) {
        
        splitService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @PreAuthorize("hasAuthority('KBASE_EXPORT')")
    @Override
    public Object export(SplitRequest request, HttpServletResponse response) {
        // query data to export
        Page<SplitEntity> splitPage = splitService.queryByOrgExcel(request);
        // 
        try {
            //
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // download filename
            String fileName = "kbase-split-" + BdDateUtils.formatDatetimeUid() + ".xlsx";
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);

            // 转换数据
            List<SplitExcel> excelList = splitPage.getContent().stream().map(splitResponse -> splitService.convertToExcel(splitResponse)).toList();
            // write to excel
            EasyExcel.write(response.getOutputStream(), SplitExcel.class)
                    .autoCloseStream(Boolean.FALSE)
                    .sheet("split")
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