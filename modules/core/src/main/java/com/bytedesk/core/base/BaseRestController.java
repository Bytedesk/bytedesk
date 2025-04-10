/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-10 12:16:43
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-10 11:57:13
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.base;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.utils.BdDateUtils;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * @author jackning 270580156@qq.com
 */
public abstract class BaseRestController<T> {

    /**
     * 
     * @param request
     * @return
     */
    @GetMapping("/query/org")
    abstract public ResponseEntity<?> queryByOrg(T request);

    /**
     * query department users
     *
     * @return json
     */
    @GetMapping("/query")
    abstract public ResponseEntity<?> queryByUser(T request);

    /**
     * query by uid
     *
     * @param request role
     * @return json
     */
    @GetMapping("/query/uid")
    abstract public ResponseEntity<?> queryByUid(T request);

    /**
     * create
     *
     * @param request role
     * @return json
     */
    @PostMapping("/create")
    abstract public ResponseEntity<?> create(@RequestBody T request);

    /**
     * update
     *
     * @param request role
     * @return json
     */
    @PostMapping("/update")
    abstract public ResponseEntity<?> update(@RequestBody T request);

    /**
     * delete
     *
     * @param request role
     * @return json
     */
    @PostMapping("/delete")
    abstract public ResponseEntity<?> delete(@RequestBody T request);

    /**
     * 通用导出Excel模板方法
     */
    protected Object exportTemplate(
            T request, 
            HttpServletResponse response, 
            Object service, 
            Class<?> excelClass, 
            String sheetName, 
            String fileNamePrefix) {
        try {
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // 文件名
            String fileName = fileNamePrefix + "-" + BdDateUtils.formatDatetimeUid() + ".xlsx";
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);

            // 查询数据并转换 - 使用反射调用相应的服务方法
            Page<?> dataPage = null;
            List<?> excelList = null;
            
            // 反射获取queryByOrgEntity方法
            try {
                java.lang.reflect.Method queryMethod = service.getClass().getMethod("queryByOrgEntity", request.getClass());
                dataPage = (Page<?>) queryMethod.invoke(service, request);
                
                // 反射获取convertToExcel方法
                java.lang.reflect.Method convertMethod = service.getClass().getMethod("convertToExcel", dataPage.getContent().get(0).getClass());
                
                // 转换数据
                excelList = dataPage.getContent().stream()
                        .map(item -> {
                            try {
                                return convertMethod.invoke(service, item);
                            } catch (Exception e) {
                                throw new RuntimeException("Convert to Excel failed", e);
                            }
                        }).toList();
            } catch (Exception e) {
                throw new RuntimeException("Failed to get data for export", e);
            }

            // 写入Excel
            EasyExcel.write(response.getOutputStream(), excelClass)
                    .autoCloseStream(Boolean.FALSE)
                    .sheet(sheetName)
                    .doWrite(excelList);

        } catch (Exception e) {
            // 重置响应
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            //
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("status", "failure");
            jsonObject.put("message", "download failed " + e.getMessage());
            try {
                response.getWriter().println(JSON.toJSONString(jsonObject));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        return "";
    }

    /**
     * export
     *
     * @param request role
     * @return json
     */
    @GetMapping("/export")
    abstract public Object export(T request, HttpServletResponse response);

}