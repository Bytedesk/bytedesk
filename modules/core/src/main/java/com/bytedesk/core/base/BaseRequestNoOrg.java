/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-24 12:55:44
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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseRequestNoOrg implements Serializable {

    protected String uid;

    protected int pageNumber;

    protected int pageSize;

    protected String type;

    protected String content;

    protected String client;

    // platform: 只有超级管理员才有权限
    // organization: 管理员才有权限
    @Builder.Default
    private String level = LevelEnum.ORGANIZATION.name();

    // 默认bytedesk平台
    @Builder.Default
    private String platform = PlatformEnum.BYTEDESK.name();

    /**
     * 获取分页对象
     * 默认每页10条记录，按更新时间倒序排序
     * 
     * @return Pageable 分页对象
     */
    public Pageable getPageable() {
        // java.lang.IllegalArgumentException: Page size must not be less than one
        if (pageSize < 1) {
            pageSize = 10;
        }
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


    public String toJson() {
        return JSON.toJSONString(this);
    }
}
