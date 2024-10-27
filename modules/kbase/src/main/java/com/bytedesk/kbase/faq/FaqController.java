/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-23 18:17:03
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.faq;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.action.ActionAnnotation;
import com.bytedesk.core.base.BaseController;
import com.bytedesk.core.utils.DateUtils;
import com.bytedesk.core.utils.JsonResult;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/faq")
@AllArgsConstructor
public class FaqController extends BaseController<FaqRequest> {

    private final FaqService faqService;

    @Override
    public ResponseEntity<?> queryByOrg(FaqRequest request) {

        Page<FaqResponse> page = faqService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUser(FaqRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'query'");
    }

    @ActionAnnotation(title = "faq", action = "create", description = "create faq")
    @Override
    public ResponseEntity<?> create(@RequestBody FaqRequest request) {

        FaqResponse Faq = faqService.create(request);

        return ResponseEntity.ok(JsonResult.success(Faq));
    }

    @ActionAnnotation(title = "faq", action = "update", description = "update faq")
    @Override
    public ResponseEntity<?> update(@RequestBody FaqRequest request) {

        FaqResponse Faq = faqService.update(request);

        return ResponseEntity.ok(JsonResult.success(Faq));
    }

    @ActionAnnotation(title = "faq", action = "delete", description = "delete faq")
    @Override
    public ResponseEntity<?> delete(@RequestBody FaqRequest request) {

        faqService.delete(request);

        return ResponseEntity.ok(JsonResult.success("delete success", request.getUid()));
    }

    // https://github.com/alibaba/easyexcel
    // https://easyexcel.opensource.alibaba.com/docs/current/
    @ActionAnnotation(title = "faq", action = "export", description = "export faq")
    @GetMapping("/export")
    public Object export(FaqRequest request, HttpServletResponse response) {
        // query data to export
        Page<FaqResponse> faqPage = faqService.queryByOrg(request);
        // 
        try {
            //
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // download filename
            String fileName = "Faq-" + DateUtils.formatDatetimeUid() + ".xlsx";
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
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("status", "failure");
            jsonObject.put("message", "download faied " + e.getMessage());
            try {
                response.getWriter().println(JSON.toJSONString(jsonObject));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        return "";
    }



}
