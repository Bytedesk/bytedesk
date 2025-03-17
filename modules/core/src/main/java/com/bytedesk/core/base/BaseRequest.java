/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-14 15:11:29
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

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.AllArgsConstructor;
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

    /**
     * 获取分页对象
     * 默认每页10条记录，按更新时间倒序排序
     * 
     * @return Pageable 分页对象
     */
    public Pageable getPageable() {
        return PageRequest.of(pageNumber, pageSize, Sort.Direction.DESC, "updatedAt");
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
}
