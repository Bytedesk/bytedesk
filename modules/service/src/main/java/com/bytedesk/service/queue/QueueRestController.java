/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:03:29
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-09 12:40:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue;

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
@RequestMapping("/api/v1/queue")
public class QueueRestController extends BaseRestController <QueueRequest> {

    private final QueueRestService queueService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg (QueueRequest request) {
        
        Page <QueueResponse> page = queueService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUser (QueueRequest request) {
        
        Page <QueueResponse> page = queueService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUid(QueueRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    @Override
    public ResponseEntity<?> create (QueueRequest request) {
        
        return ResponseEntity.ok(JsonResult.success(queueService.create(request)));
    }

    @Override
    public ResponseEntity<?> update (QueueRequest request) {
        
        return ResponseEntity.ok(JsonResult.success(queueService.update(request)));
    }

    @Override
    public ResponseEntity<?> delete (QueueRequest request) {

        queueService.delete(request);
        
        return ResponseEntity.ok(JsonResult.success());
    }

    @Override
    public Object export(QueueRequest request, HttpServletResponse response) {
         // query data to export
         Page<QueueResponse> queuePage = queueService.queryByOrg(request);
         // 
         try {
             //
             response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
             response.setCharacterEncoding("utf-8");
             // download filename
             String fileName = "monitor-" + BdDateUtils.formatDatetimeUid() + ".xlsx";
             response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);
 
             // 转换数据
             List<QueueExcel> excelList = queuePage.getContent().stream().map(queueResponse -> queueService.convertToExcel(queueResponse)).toList();
             // write to excel
             EasyExcel.write(response.getOutputStream(), QueueExcel.class)
                     .autoCloseStream(Boolean.FALSE)
                     .sheet("queue")
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
