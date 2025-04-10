/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-10 12:16:43
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-10 11:38:01
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
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;

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
     * 导出通用模板实现
     *
     * @param request 请求参数
     * @param response HTTP响应
     * @param excelClass Excel实体类
     * @param sheetName Excel表格名称
     * @param fileNamePrefix 导出文件名前缀
     * @return 处理结果
     */
    protected <E, X> Object exportTemplate(
            T request, 
            HttpServletResponse response, 
            BaseRestServiceWithExcel<E, T, ?, X> service,
            Class<X> excelClass,
            String sheetName, 
            String fileNamePrefix) {
        try {
            // 查询要导出的数据
            Page<E> entityPage = service.queryByOrgEntity(request);
            
            // 设置响应格式
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            
            // 设置下载文件名
            String fileName = fileNamePrefix + "-" + BdDateUtils.formatDatetimeUid() + ".xlsx";
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);

            // 转换数据
            List<X> excelList = entityPage.getContent().stream()
                    .map(service::convertToExcel)
                    .toList();

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
            
            return JsonResult.error(e.getMessage());
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