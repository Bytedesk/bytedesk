/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-03 07:28:46
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.faq;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.excel.EasyExcel;
import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.core.utils.JsonResult;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/faq")
@AllArgsConstructor
public class FaqRestController extends BaseRestController<FaqRequest> {

    private final FaqRestService faqService;

    // @PreAuthorize("hasAuthority('KBASE_READ')")
    @Override
    public ResponseEntity<?> queryByOrg(FaqRequest request) {

        Page<FaqResponse> page = faqService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    // @PreAuthorize("hasAuthority('KBASE_READ')")
    @Override
    public ResponseEntity<?> queryByUser(FaqRequest request) {
        
        Page<FaqResponse> page = faqService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    // @PreAuthorize("hasAuthority('KBASE_READ')")
    @Override
    public ResponseEntity<?> queryByUid(FaqRequest request) {
        
        FaqResponse faq = faqService.queryByUid(request);
        if (faq == null) {
            return ResponseEntity.ok(JsonResult.error("faq not found"));
        }
        return ResponseEntity.ok(JsonResult.success(faq));
    }

    @ActionAnnotation(title = "faq", action = "新建", description = "create faq")
    @Override
    @PreAuthorize("hasAuthority('KBASE_CREATE')")
    public ResponseEntity<?> create(@RequestBody FaqRequest request) {

        FaqResponse Faq = faqService.create(request);

        return ResponseEntity.ok(JsonResult.success(Faq));
    }

    @ActionAnnotation(title = "faq", action = "更新", description = "update faq")
    @Override
    @PreAuthorize("hasAuthority('KBASE_UPDATE')")
    public ResponseEntity<?> update(@RequestBody FaqRequest request) {

        FaqResponse Faq = faqService.update(request);

        return ResponseEntity.ok(JsonResult.success(Faq));
    }

    @ActionAnnotation(title = "faq", action = "删除", description = "delete faq")
    @Override
    @PreAuthorize("hasAuthority('KBASE_DELETE')")
    public ResponseEntity<?> delete(@RequestBody FaqRequest request) {

        faqService.delete(request);

        return ResponseEntity.ok(JsonResult.success("delete success", request.getUid()));
    }

    // https://github.com/alibaba/easyexcel
    // https://easyexcel.opensource.alibaba.com/docs/current/
    @ActionAnnotation(title = "faq", action = "导出", description = "export faq")
    @GetMapping("/export")
    @PreAuthorize("hasAuthority('KBASE_EXPORT')")
    public Object export(FaqRequest request, HttpServletResponse response) {
        // query data to export
        Page<FaqEntity> faqPage = faqService.queryByOrgExcel(request);
        // 
        try {
            //
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // download filename
            // String fileName = "Faq-" + BdDateUtils.formatDatetimeUid() + ".xlsx";
            String fileName = "faq-" + BdDateUtils.formatDatetimeUid() + ".xlsx";
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);

            // 转换数据
            List<FaqExcel> excelList = faqPage.getContent().stream().map(faqResponse -> faqService.convertToExcel(faqResponse)).toList();

            // write to excel
            EasyExcel.write(response.getOutputStream(), FaqExcel.class)
                    .autoCloseStream(Boolean.FALSE)
                    .sheet("Faq")
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
