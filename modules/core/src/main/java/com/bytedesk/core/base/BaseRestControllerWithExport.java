
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
public abstract class BaseRestControllerWithExport<T extends PageableRequest, S> {

    /**
     * 获取对应的服务实例
     * 提供默认实现，通过反射自动查找服务字段
     * 子类也可以重写此方法来提供具体的服务对象
     */
    @SuppressWarnings("unchecked")
    protected S getService() {
        try {
            // 通过反射查找以 Service 结尾的字段
            java.lang.reflect.Field[] fields = this.getClass().getDeclaredFields();
            for (java.lang.reflect.Field field : fields) {
                if (field.getName().endsWith("Service") || field.getName().endsWith("RestService")) {
                    field.setAccessible(true);
                    Object service = field.get(this);
                    if (service != null) {
                        return (S) service;
                    }
                }
            }
            
            // 如果没找到，查找所有字段中第一个非基本类型的字段（可能是服务）
            for (java.lang.reflect.Field field : fields) {
                if (!field.getType().isPrimitive() && 
                    !field.getType().equals(String.class) && 
                    !field.getName().equals("log") &&
                    !java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                    field.setAccessible(true);
                    Object service = field.get(this);
                    if (service != null) {
                        return (S) service;
                    }
                }
            }
            
            throw new RuntimeException("No service field found. Please override getService() method or ensure you have a field ending with 'Service' or 'RestService'");
        } catch (Exception e) {
            throw new RuntimeException("Failed to get service instance: " + e.getMessage(), e);
        }
    }

    /**
     * 通用的queryByOrg实现
     * 减少子类重复代码
     */
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(T request) {
        try {
            S service = getService();
            Method method = service.getClass().getMethod("queryByOrg", request.getClass());
            Object result = method.invoke(service, request);
            return ResponseEntity.ok(JsonResult.success(result));
        } catch (Exception e) {
            return ResponseEntity.ok(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 通用的queryByUser实现
     * 减少子类重复代码
     */
    @GetMapping("/query")
    public ResponseEntity<?> queryByUser(T request) {
        try {
            S service = getService();
            Method method = service.getClass().getMethod("queryByUser", request.getClass());
            Object result = method.invoke(service, request);
            return ResponseEntity.ok(JsonResult.success(result));
        } catch (Exception e) {
            return ResponseEntity.ok(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 通用的queryByUid实现
     * 减少子类重复代码
     */
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(T request) {
        try {
            S service = getService();
            Method method = service.getClass().getMethod("queryByUid", request.getClass());
            Object result = method.invoke(service, request);
            return ResponseEntity.ok(JsonResult.success(result));
        } catch (Exception e) {
            return ResponseEntity.ok(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 通用的create实现
     * 减少子类重复代码
     */
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody T request) {
        try {
            S service = getService();
            Method method = service.getClass().getMethod("create", request.getClass());
            Object result = method.invoke(service, request);
            return ResponseEntity.ok(JsonResult.success(result));
        } catch (Exception e) {
            return ResponseEntity.ok(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 通用的update实现
     * 减少子类重复代码
     */
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody T request) {
        try {
            S service = getService();
            Method method = service.getClass().getMethod("update", request.getClass());
            Object result = method.invoke(service, request);
            return ResponseEntity.ok(JsonResult.success(result));
        } catch (Exception e) {
            return ResponseEntity.ok(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 通用的delete实现
     * 减少子类重复代码
     */
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody T request) {
        try {
            S service = getService();
            Method method = service.getClass().getMethod("delete", request.getClass());
            method.invoke(service, request);
            return ResponseEntity.ok(JsonResult.success());
        } catch (Exception e) {
            return ResponseEntity.ok(JsonResult.error(e.getMessage()));
        }
    }

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
     * @param <SVC> Service类型
     * @param request 请求参数
     * @param response HTTP响应
     * @param service 服务对象
     * @param excelClass Excel类
     * @param sheetName 工作表名称
     * @param filePrefix 文件名前缀
     * @return 导出结果
     */
    protected <E, SVC> Object exportTemplate(
            T request, 
            HttpServletResponse response,
            SVC service,
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
    private <SVC> Object invokeMethod(SVC service, String methodName, Object... args) throws Exception {
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
    private <E, SVC> List<E> convertToExcelList(SVC service, Object data) throws Exception {
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
    public Object export(T request, HttpServletResponse response) {
        throw new UnsupportedOperationException("Method export needs to be implemented in child class");
    }

}