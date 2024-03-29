/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 *  bytedesk.com https://github.com/Bytedesk/bytedesk
 *
 *  Copyright (C)  2013-2024 bytedesk.com
 *
 *  License restrictions
 * 
 *      Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售
 *  
 *  Business Source License 1.1: 
 *  https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 * 
 *  contact: 270580156@qq.com
 *  联系：270580156@qq.com
 */
package com.bytedesk.core.utils.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DepartmentNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -8502840684293721269L;

    public DepartmentNotFoundException(String did) {
        super("department not found");
        log.error("department {} not fount", did);
    }
}
