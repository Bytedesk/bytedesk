/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-14 17:57:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-05 16:40:37
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue_member;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.excel.EasyExcel;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/queue/member")
public class QueueMemberRestController extends BaseRestController<QueueMemberRequest> {

    private final QueueMemberRestService queueMemberRestService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(QueueMemberRequest request) {
        
        Page<QueueMemberResponse> page = queueMemberRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUser(QueueMemberRequest request) {
        
        Page<QueueMemberResponse> page = queueMemberRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> create(QueueMemberRequest request) {
        
        QueueMemberResponse response = queueMemberRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    public ResponseEntity<?> update(QueueMemberRequest request) {
        
        QueueMemberResponse response = queueMemberRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    public ResponseEntity<?> delete(QueueMemberRequest request) {
        
        queueMemberRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Override
    public Object export(QueueMemberRequest request, HttpServletResponse response) {
         // query data to export
         Page<QueueMemberResponse> memberPage = queueMemberRestService.queryByOrg(request);
         // 
         try {
             //
             response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
             response.setCharacterEncoding("utf-8");
             // download filename
             String fileName = "service-queue-member-" + BdDateUtils.formatDatetimeUid() + ".xlsx";
             response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);
 
             // 转换数据
             List<QueueMemberExcel> excelList = memberPage.getContent().stream().map(memberResponse -> queueMemberRestService.convertToExcel(memberResponse)).toList();
             // write to excel
             EasyExcel.write(response.getOutputStream(), QueueMemberExcel.class)
                     .autoCloseStream(Boolean.FALSE)
                     .sheet("queue-member")
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

    @Override
    public ResponseEntity<?> queryByUid(QueueMemberRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}
