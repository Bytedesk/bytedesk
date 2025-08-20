/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-20 12:31:47
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 15:38:54
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * 改进的带Excel导出功能的基础RestService类
 * 继承自BaseRestServiceImproved，增加Excel相关抽象方法
 */
public abstract class BaseRestServiceWithExcel<T, TRequest extends PageableRequest, TResponse, TExcel> 
        extends BaseRestService<T, TRequest, TResponse> {
    
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
