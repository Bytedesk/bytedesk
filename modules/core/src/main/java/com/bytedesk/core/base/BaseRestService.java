/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-10 12:13:37
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 21:03:05
 * @Description: 改进后的BaseRestService，增加通用方法实现
 */
package com.bytedesk.core.base;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.exception.NotFoundException;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;

/**
 * 改进的基础RestService类
 * 在原有抽象方法基础上，提供通用的默认实现来减少重复代码
 */
public abstract class BaseRestService<T, TRequest extends PageableRequest, TResponse> {

    protected AuthService authService;

    @Autowired
    protected void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    // === 原有的抽象方法 ===
    abstract public Optional<T> findByUid(String uid);
    abstract public TResponse create(TRequest request);
    abstract public TResponse update(TRequest request);
    abstract public void deleteByUid(String uid);
    abstract public void delete(TRequest request);
    abstract public T handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, T entity);
    abstract public TResponse convertToResponse(T entity);

    public TResponse restore(TRequest request) {
        throw new UnsupportedOperationException("Method restore needs to be implemented in child class");
    }

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
    public Page<TResponse> queryByOrg(TRequest request) {
        Pageable pageable = request.getPageable();
        Specification<T> spec = createSpecification(request);
        Page<T> page = executePageQuery(spec, pageable);
        return page.map(this::convertToResponse);
    }

    /**
     * 通用的queryByUser实现
     * 子类如果有特殊逻辑可以重写此方法
     */
    public Page<TResponse> queryByUser(TRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new NotFoundException(I18Consts.I18N_LOGIN_REQUIRED);
        }
        
        setUserUidToRequest(request, user.getUid());
        setOrgUidToRequestIfMissing(request, user.getOrgUid());
        return queryByOrg(request);
    }

    /**
     * 通用的queryByUid实现
     */
    public TResponse queryByUid(TRequest request) {
        Optional<T> optionalEntity = findByUid(getUidFromRequest(request));
        if (optionalEntity.isPresent()) {
            return convertToResponse(optionalEntity.get());
        } else {
            throw new NotFoundException(I18Consts.I18N_RESOURCE_NOT_FOUND);
        }
    }

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

    /**
     * 在用户维度查询中，自动继承当前登录用户所属组织，避免前端遗漏 orgUid 时触发基础组织校验。
     */
    protected void setOrgUidToRequestIfMissing(TRequest request, String orgUid) {
        if (!(request instanceof BaseRequest baseRequest)) {
            return;
        }
        if (baseRequest.getOrgUid() == null || baseRequest.getOrgUid().isBlank()) {
            baseRequest.setOrgUid(orgUid);
        }
    }

    // === 保留原有的方法 ===

    public List<T> findByOrgUid(String orgUid) {
        // 默认实现，子类可以覆盖
        throw new UnsupportedOperationException("Method findByOrgUid needs to be implemented in child class");
    }

    /**
     * 乐观锁重试次数（含首次尝试），与原 @Retryable(maxAttempts = 3) 保持一致
     */
    private static final int SAVE_MAX_ATTEMPTS = 3;

    /**
     * 重试退避基础时间（毫秒），按尝试次数线性递增：100ms、200ms，
     * 与原 @Backoff(delay = 100, multiplier = 2) 行为一致
     */
    private static final long SAVE_RETRY_BACKOFF_BASE_MS = 100L;

    /**
     * 保存实体，带乐观锁冲突重试机制。
     *
     * 说明：此前使用 spring-retry 的 @Retryable/@Recover 实现，但泛型基类
     * save(T) 的返回类型为 TypeVariable（擦除后为 Object），spring-retry 2.0.x
     * 的恢复方法返回类型匹配在泛型擦除场景下无法命中 @Recover 方法，重试耗尽后
     * 直接抛出 ExhaustedRetryException: Cannot locate recovery method
     * （生产 P1 问题根因）。因此改为自实现重试循环：耗尽后调用子类
     * handleOptimisticLockingFailureException 恢复钩子，两个基类一次性修复全部子类。
     */
    public T save(T entity) {
        ObjectOptimisticLockingFailureException lastException = null;
        for (int attempt = 1; attempt <= SAVE_MAX_ATTEMPTS; attempt++) {
            try {
                return doSave(entity);
            } catch (ObjectOptimisticLockingFailureException e) {
                lastException = e;
                if (attempt < SAVE_MAX_ATTEMPTS) {
                    try {
                        Thread.sleep(SAVE_RETRY_BACKOFF_BASE_MS * attempt);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
        // 重试耗尽（或线程被中断）：落入子类乐观锁恢复钩子，避免直接向调用方抛出异常
        return handleOptimisticLockingFailureException(lastException, entity);
    }

    /**
     * 子类实现具体的保存逻辑
     */
    protected abstract T doSave(T entity);

    public void deleteByOrgUid(String orgUid) {
        // 默认实现，子类可以覆盖
        throw new UnsupportedOperationException("Method deleteByOrgUid needs to be implemented in child class");
    }
}
