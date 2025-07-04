/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-10 12:13:37
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-04 13:25:29
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
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;


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

    @Retryable(
        value = ObjectOptimisticLockingFailureException.class,
        maxAttempts = 3,
        backoff = @Backoff(delay = 100, multiplier = 2)
    )
    public T save(T entity) {
        try {
            return doSave(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw e; // 抛出异常以触发重试机制
        }
    }
    
    // 子类实现具体的保存逻辑
    protected abstract T doSave(T entity);
    
    @Recover
    public T recover(ObjectOptimisticLockingFailureException e, T entity) {
        // 调用子类实现的处理方法
        return handleOptimisticLockingFailureException(e, entity);
    }

    abstract public void deleteByUid(String uid);

    abstract public void delete(TRequest request);

    abstract public T handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            T entity);
            
    abstract public TResponse convertToResponse(T entity);
            
}