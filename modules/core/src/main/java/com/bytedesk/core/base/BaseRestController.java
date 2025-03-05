/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-10 12:16:43
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-05 16:33:49
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.base;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.servlet.http.HttpServletResponse;

/**
 * @author jackning 270580156@qq.com
 */
public abstract class BaseRestController<T> {

    /**
     * 
     * @param request
     * @return
     */
    @GetMapping("/query/org")
    abstract public ResponseEntity<?> queryByOrg(T request);

    /**
     * query department users
     *
     * @return json
     */
    @GetMapping("/query")
    abstract public ResponseEntity<?> queryByUser(T request);

    /**
     * query by uid
     *
     * @param request role
     * @return json
     */
    @GetMapping("/query/uid")
    abstract public ResponseEntity<?> queryByUid(T request);

    /**
     * create
     *
     * @param request role
     * @return json
     */
    @PostMapping("/create")
    abstract public ResponseEntity<?> create(@RequestBody T request);

    /**
     * update
     *
     * @param request role
     * @return json
     */
    @PostMapping("/update")
    abstract public ResponseEntity<?> update(@RequestBody T request);

    /**
     * delete
     *
     * @param request role
     * @return json
     */
    @PostMapping("/delete")
    abstract public ResponseEntity<?> delete(@RequestBody T request);

    /**
     * export
     *
     * @param request role
     * @return json
     */
    @GetMapping("/export")
    abstract public Object export(T request, HttpServletResponse response);

}