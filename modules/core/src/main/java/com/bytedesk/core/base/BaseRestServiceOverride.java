/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-10 12:13:37
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-24 10:47:19
 * @Description: 改进后的BaseRestService，增加通用方法实现
 */
package com.bytedesk.core.base;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;

import com.bytedesk.core.rbac.auth.AuthService;

/**
 * 需要自己重新实现的抽象方法
 * 改进的基础RestService类
 * 在原有抽象方法基础上，提供通用的默认实现来减少重复代码
 */
public abstract class BaseRestServiceOverride<T, TRequest extends PageableRequest, TResponse> {

    @Autowired
    protected AuthService authService;

    // === 原有的抽象方法 ===
    abstract public Optional<T> findByUid(String uid);
    abstract public TResponse create(TRequest request);
    abstract public TResponse update(TRequest request);
    abstract public void deleteByUid(String uid);
    abstract public void delete(TRequest request);
    abstract public T handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, T entity);
    abstract public TResponse convertToResponse(T entity);

    // === 新增的抽象方法，用于支持通用实现 ===
    
    /**
     * 创建Specification对象，子类必须实现
     * 用于queryByOrg的通用实现
     */
    protected abstract Specification<T> createSpecification(TRequest request);
    
    /**
     * 执行分页查询，子类必须实现
     * 用于queryByOrg的通用实现
     */
    protected abstract Page<T> executePageQuery(Specification<T> spec, Pageable pageable);

    // === 提供默认实现的方法，减少子类重复代码 ===

    /**
     * 通用的queryByOrg实现
     * 子类如果有特殊逻辑可以重写此方法
     */
    public abstract Page<TResponse> queryByOrg(TRequest request);

    /**
     * 通用的queryByUser实现
     * 子类如果有特殊逻辑可以重写此方法
     */
    public abstract Page<TResponse> queryByUser(TRequest request);

    /**
     * 通用的queryByUid实现
     */
    public abstract TResponse queryByUid(TRequest request);

    /**
     * 从请求对象中获取UID的通用实现
     * 使用反射调用getUid方法
     */
    protected String getUidFromRequest(TRequest request) {
        try {
            Method getUidMethod = request.getClass().getMethod("getUid");
            return (String) getUidMethod.invoke(request);
        } catch (Exception e) {
            throw new UnsupportedOperationException("Method getUid not found in request class: " + request.getClass().getSimpleName(), e);
        }
    }

    /**
     * 设置用户UID到请求对象的通用实现
     * 使用反射调用setUserUid方法
     */
    protected void setUserUidToRequest(TRequest request, String userUid) {
        try {
            Method setUserUidMethod = request.getClass().getMethod("setUserUid", String.class);
            setUserUidMethod.invoke(request, userUid);
        } catch (Exception e) {
            throw new UnsupportedOperationException("Method setUserUid not found in request class: " + request.getClass().getSimpleName(), e);
        }
    }

    // === 保留原有的方法 ===

    public List<T> findByOrgUid(String orgUid) {
        // 默认实现，子类可以覆盖
        throw new UnsupportedOperationException("Method findByOrgUid needs to be implemented in child class");
    }

    /**
     * 保存实体，带重试机制
     */
    @Retryable(
        retryFor = ObjectOptimisticLockingFailureException.class,
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
    
    /**
     * 子类实现具体的保存逻辑
     */
    protected abstract T doSave(T entity);
    
    /**
     * 重试恢复方法
     */
    @Recover
    public T recover(ObjectOptimisticLockingFailureException e, T entity) {
        // 调用子类实现的处理方法
        return handleOptimisticLockingFailureException(e, entity);
    }

    public void deleteByOrgUid(String orgUid) {
        // 默认实现，子类可以覆盖
        throw new UnsupportedOperationException("Method deleteByOrgUid needs to be implemented in child class");
    }
}
