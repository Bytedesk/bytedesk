package com.bytedesk.core.base;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.alibaba.excel.EasyExcel;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;

/**
 * 基础RestController，不需要指定Service类型
 * 适用于所有方法都被子类重写的场景
 * 
 * @param <T> 请求对象类型
 * @author jackning 270580156@qq.com
 */
public abstract class BaseRestControllerOverride<T extends PageableRequest> {

    /**
     * 查询组织数据
     */
    @GetMapping("/query/org")
    public abstract ResponseEntity<?> queryByOrg(T request);

    /**
     * 查询用户数据
     */
    @GetMapping("/query")
    public abstract ResponseEntity<?> queryByUser(T request);

    /**
     * 根据UID查询
     */
    @GetMapping("/query/uid")
    public abstract ResponseEntity<?> queryByUid(T request);

    /**
     * 创建
     */
    @PostMapping("/create")
    public abstract ResponseEntity<?> create(@RequestBody T request);

    /**
     * 更新
     */
    @PostMapping("/update")
    public abstract ResponseEntity<?> update(@RequestBody T request);

    /**
     * 删除
     */
    @PostMapping("/delete")
    public abstract ResponseEntity<?> delete(@RequestBody T request);

    /**
     * 根据组织UID删除
     */
    @PostMapping("/delete/org")
    public ResponseEntity<?> deleteByOrgUid(@RequestBody T request) {
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
     * 导出
     */
    @GetMapping("/export")
    public Object export(T request, HttpServletResponse response) {
        throw new UnsupportedOperationException("Method export needs to be implemented in child class");
    }
}
