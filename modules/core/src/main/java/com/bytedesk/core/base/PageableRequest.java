/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-20 14:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 14:00:00
 * @Description: 可分页请求接口
 */
package com.bytedesk.core.base;

import org.springframework.data.domain.Pageable;

/**
 * 可分页请求接口
 * 为不同的Request基类提供统一的分页方法接口
 */
public interface PageableRequest {
    
    /**
     * 获取分页对象
     * @return Pageable
     */
    Pageable getPageable();
    
}
