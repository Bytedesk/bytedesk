/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-27 22:34:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-23 18:18:35
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.taboo;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.action.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.DateUtils;
import com.bytedesk.core.utils.JsonResult;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/taboo")
@AllArgsConstructor
public class TabooController extends BaseRestController<TabooRequest> {

    private final TabooService tabooService;

    @Override
    public ResponseEntity<?> queryByOrg(TabooRequest request) {
        
        Page<TabooResponse> page = tabooService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUser(TabooRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'query'");
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
    @GetMapping("/export")
    public Object export(TabooRequest request, HttpServletResponse response) {
        // query data to export
        Page<TabooResponse> tabooPage = tabooService.queryByOrg(request);
        // 
        try {
            //
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // download filename
            String fileName = "Taboo-" + DateUtils.formatDatetimeUid() + ".xlsx";
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
