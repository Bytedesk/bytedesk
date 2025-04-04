/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-10 12:13:37
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-17 21:16:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.base;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.orm.ObjectOptimisticLockingFailureException;


public abstract class BaseRestService<T, TRequest, TResponse> {

    abstract public Page<TResponse> queryByOrg(TRequest request);

    abstract public Page<TResponse> queryByUser(TRequest request);

    public TResponse queryByUid(TRequest request) {
        Optional<T> optionalEntity = findByUid(getUidFromRequest(request));
        if (optionalEntity.isPresent()) {
            return convertToResponse(optionalEntity.get());
        }
        return null;
    }

    protected String getUidFromRequest(TRequest request) {
        throw new UnsupportedOperationException("Method getUidFromRequest needs to be implemented in child class");
    }

    abstract public Optional<T> findByUid(String uid);

    abstract public TResponse create(TRequest request);

    abstract public TResponse update(TRequest request);

    abstract public T save(T entity);

    abstract public void deleteByUid(String uid);

    abstract public void delete(TRequest request);

    abstract public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            T entity);

    abstract public TResponse convertToResponse(T entity);
            
}