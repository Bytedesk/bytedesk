/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-20 12:20:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 12:20:00
 * @Description: 改进后的BaseRestServiceWithExcel，结合Excel导出功能
 */
package com.bytedesk.core.base;

import org.springframework.data.domain.Page;

/**
 * 改进的带Excel导出功能的基础RestService类
 * 继承自BaseRestServiceImproved，增加Excel相关抽象方法
 */
public abstract class BaseRestServiceWithExcelImproved<T, TRequest extends BaseRequest, TResponse, TExcel> 
        extends BaseRestServiceImproved<T, TRequest, TResponse> {
    
    /**
     * 查询实体对象（用于Excel导出）
     * 子类必须实现此方法
     */
    public Page<T> queryByOrgEntity(TRequest request) {
        Pageable pageable = request.getPageable();
        Specification<T> spec = createSpecification(request);
        return executePageQuery(spec, pageable);
    }

    /**
     * 转换为Excel对象
     * 子类必须实现此方法
     */
    abstract public TExcel convertToExcel(T entity);

}
