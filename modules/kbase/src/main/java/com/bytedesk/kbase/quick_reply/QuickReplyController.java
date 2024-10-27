/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-23 18:18:14
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.quick_reply;

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
@RequestMapping("/api/v1/quickreply")
@AllArgsConstructor
public class QuickReplyController extends BaseController<QuickReplyRequest> {

    private final QuickReplyService quickReplyService;

    // 管理后台加载
    @Override
    public ResponseEntity<?> queryByOrg(QuickReplyRequest request) {
        
        Page<QuickReplyResponse> page = quickReplyService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    // 客服端加载
    @Override
    public ResponseEntity<?> queryByUser(QuickReplyRequest request) {
        
        return ResponseEntity.ok(JsonResult.success(false));
    }

    @ActionAnnotation(title = "quick_reply", action = "create", description = "create quick_reply")
    @Override
    public ResponseEntity<?> create(@RequestBody QuickReplyRequest request) {
        
        QuickReplyResponse quickReply = quickReplyService.create(request);

        return ResponseEntity.ok(JsonResult.success(quickReply));
    }

    @ActionAnnotation(title = "quick_reply", action = "update", description = "update quick_reply")
    @Override
    public ResponseEntity<?> update(@RequestBody QuickReplyRequest request) {
        
        QuickReplyResponse quickReply = quickReplyService.update(request);

        return ResponseEntity.ok(JsonResult.success(quickReply));
    }

    @ActionAnnotation(title = "quick_reply", action = "delete", description = "delete quick_reply")
    @Override
    public ResponseEntity<?> delete(@RequestBody QuickReplyRequest request) {

        quickReplyService.delete(request);

        return ResponseEntity.ok(JsonResult.success("delete success", request.getUid()));
    }
    
    // https://github.com/alibaba/easyexcel
    // https://easyexcel.opensource.alibaba.com/docs/current/
    @ActionAnnotation(title = "quickReply", action = "export", description = "export quickReply")
    @GetMapping("/export")
    public Object export(QuickReplyRequest request, HttpServletResponse response) {
        // query data to export
        Page<QuickReplyResponse> quickReplyPage = quickReplyService.queryByOrg(request);
        // 
        try {
            //
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // download filename
            String fileName = "QuickReply-" + DateUtils.formatDatetimeUid() + ".xlsx";
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);

            // 转换数据
            List<QuickReplyExcel> excelList = quickReplyPage.getContent().stream().map(quickReplyResponse -> quickReplyService.convertToExcel(quickReplyResponse)).toList();

            // write to excel
            EasyExcel.write(response.getOutputStream(), QuickReplyExcel.class)
                    .autoCloseStream(Boolean.FALSE)
                    .sheet("QuickReply")
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
