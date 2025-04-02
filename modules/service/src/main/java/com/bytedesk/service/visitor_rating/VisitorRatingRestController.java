/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:00:38
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-02 17:28:28
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor_rating;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.excel.EasyExcel;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.kbase.faq.FaqExcel;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/visitor/rating")
@AllArgsConstructor
public class VisitorRatingRestController extends BaseRestController<VisitorRatingRequest> {

    private final VisitorRatingRestService rateRestService;

    @Override
    public ResponseEntity<?> queryByOrg(VisitorRatingRequest request) {
        
        Page<VisitorRatingResponse> page = rateRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUser(VisitorRatingRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'query'");
    }

    @Override
    public ResponseEntity<?> queryByUid(VisitorRatingRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    @Override
    public ResponseEntity<?> create(VisitorRatingRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public ResponseEntity<?> update(VisitorRatingRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public ResponseEntity<?> delete(VisitorRatingRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public Object export(VisitorRatingRequest request, HttpServletResponse response) {
        // query data to export
        Page<VisitorRatingEntity> VisitorRatingPage = rateRestService.queryByOrgExcel(request);
        // 
        try {
            //
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // download filename
            String fileName = "VisitorRating-" + BdDateUtils.formatDatetimeUid() + ".xlsx";
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);

            // 转换数据
            List<VisitorRatingExcel> excelList = VisitorRatingPage.getContent().stream().map(VisitorRatingResponse -> rateRestService.convertToExcel(VisitorRatingResponse)).toList();

            // write to excel
            EasyExcel.write(response.getOutputStream(), FaqExcel.class)
                    .autoCloseStream(Boolean.FALSE)
                    .sheet("VisitorRating")
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

    // TODO: 邀请评价，客服主动邀请用户评价，需要检查会话是否已经评价过
    @GetMapping("/invite")
    public ResponseEntity<?> invite(VisitorRatingRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'invite'");
    }

    
}
