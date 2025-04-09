/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:04:34
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-09 09:31:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.message_leave;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.excel.EasyExcel;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.kbase.faq.FaqEntity;
import com.bytedesk.kbase.faq.FaqExcel;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/leavemsg")
@AllArgsConstructor
public class MessageLeaveRestController extends BaseRestController<MessageLeaveRequest> {

    private final MessageLeaveRestService messageLeaveService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(MessageLeaveRequest request) {

        Page<MessageLeaveResponse> page = messageLeaveService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUser(MessageLeaveRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'query'");
    }

    @Override
    public ResponseEntity<?> queryByUid(MessageLeaveRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    @Override
    public ResponseEntity<?> create(@RequestBody MessageLeaveRequest request) {

        MessageLeaveResponse response = messageLeaveService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    public ResponseEntity<?> update(MessageLeaveRequest request) {

        MessageLeaveResponse response = messageLeaveService.update(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    public ResponseEntity<?> delete(MessageLeaveRequest request) {
        
        messageLeaveService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Override
    public Object export(MessageLeaveRequest request, HttpServletResponse response) {
        // query data to export
        Page<MessageLeaveEntity> messageLeavePage = messageLeaveService.queryByOrgExcel(request);
        // 
        try {
            //
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // download filename
            // String fileName = "Faq-" + BdDateUtils.formatDatetimeUid() + ".xlsx";
            String fileName = "message-leave-" + BdDateUtils.formatDatetimeUid() + ".xlsx";
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);

            // 转换数据
            List<FaqExcel> excelList = messageLeavePage.getContent().stream().map(faqResponse -> messageLeaveService.convertToExcel(faqResponse)).toList();

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
