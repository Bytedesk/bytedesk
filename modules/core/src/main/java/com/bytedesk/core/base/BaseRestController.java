/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-10 12:16:43
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-14 08:45:12
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

import java.lang.reflect.Method;
import java.util.ArrayList;
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
     * delete by org uid
     *
     * @param request role
     * @return json
     */
    @PostMapping("/delete/org")
    public ResponseEntity<?> deleteByOrgUid(@RequestBody T request) {
        // 默认实现，子类可以覆盖
        throw new UnsupportedOperationException("Method deleteByOrgUid needs to be implemented in child class");
    }

    /**
     * 通用导出Excel模板方法
     * 
     * @param <E> Excel实体类型
     * @param <S> Service类型
     * @param request 请求参数
     * @param response HTTP响应
     * @param service 服务对象
     * @param excelClass Excel类
     * @param sheetName 工作表名称
     * @param filePrefix 文件名前缀
     * @return 导出结果
     */
    protected <E, S> Object exportTemplate(
            T request, 
            HttpServletResponse response,
            S service,
            Class<E> excelClass,
            String sheetName, 
            String filePrefix) {
        try {
            // 设置响应类型
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            
            // 生成文件名
            String fileName = filePrefix + "-" + BdDateUtils.formatDatetimeUid() + ".xlsx";
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);

            // 获取数据并转换为Excel格式
            Object data = invokeMethod(service, "queryByOrgEntity", request);
            List<E> excelList = convertToExcelList(service, data);

            // 写入Excel
            EasyExcel.write(response.getOutputStream(), excelClass)
                    .autoCloseStream(Boolean.FALSE)
                    .sheet(sheetName)
                    .doWrite(excelList);

        } catch (Exception e) {
            // 发生异常时重置响应
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            return JsonResult.error(e.getMessage());
        }
        return "";
    }

    /**
     * 通过反射调用服务方法
     */
    private <S> Object invokeMethod(S service, String methodName, Object... args) throws Exception {
        Method method = null;
        for (Method m : service.getClass().getMethods()) {
            if (m.getName().equals(methodName)) {
                method = m;
                break;
            }
        }
        if (method != null) {
            return method.invoke(service, args);
        }
        throw new NoSuchMethodException("Method " + methodName + " not found");
    }

    /**
     * 转换数据为Excel列表
     */
    @SuppressWarnings("unchecked")
    private <E, S> List<E> convertToExcelList(S service, Object data) throws Exception {
        if (data instanceof Page) {
            Page<?> page = (Page<?>) data;
            Method convertMethod = null;
            for (Method m : service.getClass().getMethods()) {
                if (m.getName().equals("convertToExcel")) {
                    convertMethod = m;
                    break;
                }
            }
            
            if (convertMethod != null) {
                List<Object> content = (List<Object>) page.getContent();
                List<E> result = new ArrayList<>();
                for (Object entity : content) {
                    result.add((E) convertMethod.invoke(service, entity));
                }
                return result;
            }
        }
        throw new IllegalArgumentException("Invalid data format or convert method not found");
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