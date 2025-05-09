/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-09 12:13:28
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.base;

import java.io.Serializable;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.enums.PlatformEnum;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
// import lombok.experimental.Accessors;

/**
 * 基础请求类
 * 所有请求类的父类，提供通用字段和方法
 */
@Getter
@Setter
@SuperBuilder
@ToString
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 唯一标识
     */
    // @Setter(AccessLevel.PROTECTED)  // 只允许子类设置
    protected String uid;

    /**
     * 页码，从0开始
     */
    protected int pageNumber;

    /**
     * 每页大小，默认10
     */
    @Builder.Default
    protected int pageSize = 10;

    /**
     * 类型
     */
    protected String type;

    /**
     * 内容
     */
    protected String content;

    /**
     * 客户端标识
     */
    protected String client;

    /**
     * 用户唯一标识
     */
    protected String userUid;

    /**
     * 组织唯一标识
     */
    protected String orgUid;

    // platform: 只有超级管理员才有权限
    // organization: 管理员才有权限
    @Builder.Default
    private String level = LevelEnum.ORGANIZATION.name();

    // 默认bytedesk平台
    @Builder.Default
    private String platform = PlatformEnum.BYTEDESK.name();

    @Builder.Default
    private boolean isSuperUser = false;

    // 导出全部数据，默认false
    @Builder.Default
    private boolean exportAll = false;

    // 排序方式，默认按更新时间倒序
    // updatedAt/createdAt: 'ascend', 'descend'
    // 其他字段可以根据需要添加
    @Builder.Default
    private String sortBy = "updatedAt";

    @Builder.Default
    private String sortDirection = "descend";

    /**
     * 获取分页对象
     * 默认每页10条记录，按更新时间倒序排序
     * 
     * @return Pageable 分页对象
     */
    /**
     * 获取分页对象
     * 根据sortBy和sortDirection设置排序
     * 
     * @return Pageable 分页对象
     */
    public Pageable getPageable() {
        // java.lang.IllegalArgumentException: Page size must not be less than one
        if (pageSize < 1) {
            pageSize = 10;
        }
        
        Sort.Direction direction = "ascend".equals(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return PageRequest.of(pageNumber, pageSize, direction, sortBy);
    }

    /**
     * 获取升序分页对象
     * 按照sortBy字段升序排序
     * 
     * @return Pageable 分页对象
     */
    public Pageable getPageableAsc() {
        // java.lang.IllegalArgumentException: Page size must not be less than one
        if (pageSize < 1) {
            pageSize = 10;
        }
        return PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC, sortBy);
    }

    /**
     * 验证请求参数
     * 子类可以重写此方法添加自己的验证逻辑
     * 
     * @throws IllegalArgumentException 如果验证失败
     */
    protected void validate() {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page number cannot be negative");
        }
        if (pageSize < 1) {
            throw new IllegalArgumentException("Page size must be positive");
        }
    }

    public String toJson() {
        return JSON.toJSONString(this);
    }
}
