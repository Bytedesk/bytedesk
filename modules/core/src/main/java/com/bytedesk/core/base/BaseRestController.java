/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-10 12:16:43
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-05 10:30:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
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

}