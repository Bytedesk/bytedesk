/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-16 15:08:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.thread;

import java.io.IOException;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

/**
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/thread")
public class ThreadController extends BaseRestController<ThreadRequest> {

    private final ThreadService threadService;

    private final ModelMapper modelMapper;

    /**
     * 管理后台 根据 orgUids 查询
     * 
     * @param request
     * @return
     */
    @Override
    public ResponseEntity<?> queryByOrg(ThreadRequest request) {

        Page<ThreadResponse> threadPage = threadService.queryByOrg(request);
        //
        return ResponseEntity.ok(JsonResult.success(threadPage));
    }

    @Override
    public ResponseEntity<?> queryByUser(ThreadRequest request) {

        Page<ThreadResponse> threadPage = threadService.query(request);
        //
        return ResponseEntity.ok(JsonResult.success(threadPage));
    }

    @ActionAnnotation(title = "thread", action = "create", description = "create thread")
    @Override
    public ResponseEntity<?> create(@RequestBody ThreadRequest request) {
        //
        ThreadResponse thread = threadService.create(request);

        return ResponseEntity.ok(JsonResult.success(thread));
    }

    @ActionAnnotation(title = "thread", action = "update", description = "update thread")
    @Override
    public ResponseEntity<?> update(@RequestBody ThreadRequest request) {

        ThreadResponse threadResponse = threadService.update(request);

        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }

    @ActionAnnotation(title = "thread", action = "close", description = "close thread")
    @PostMapping("/close")
    public ResponseEntity<?> close(@RequestBody ThreadRequest request) {

        request.setAutoClose(false);
        request.setState(ThreadStateEnum.CLOSED.name());

        ThreadResponse threadResponse = threadService.close(request);
        if (threadResponse == null) {
            return ResponseEntity.ok(JsonResult.error("thread already closed"));
        }
        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }

    @Override
    public ResponseEntity<?> delete(ThreadRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    // https://github.com/alibaba/easyexcel
    // https://easyexcel.opensource.alibaba.com/docs/current/
    @ActionAnnotation(title = "thread", action = "export", description = "export thread")
    @GetMapping("/export")
    public Object export(ThreadRequest request, HttpServletResponse response) {
        // query data to export
        Page<ThreadResponse> threadPage = threadService.queryByOrg(request);
        // 
        try {
            //
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // download filename
            String fileName = "Thread-" + DateUtils.formatDatetimeUid() + ".xlsx";
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);

            // 转换数据
            List<ThreadExcel> excelList = threadPage.getContent().stream().map(threadResponse -> {
                return modelMapper.map(threadResponse, ThreadExcel.class);
            }).toList();

            // write to excel
            EasyExcel.write(response.getOutputStream(), ThreadExcel.class)
                    .autoCloseStream(Boolean.FALSE)
                    .sheet("Thread")
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
