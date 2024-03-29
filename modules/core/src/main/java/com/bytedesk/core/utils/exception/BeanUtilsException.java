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

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

/**
 * BeanUtils exception.
 *
 * @author johnniang
 */
public class BeanUtilsException extends BytedeskException {

    private static final long serialVersionUID = -2693716689479955009L;

    public BeanUtilsException(String message) {
        super(message);
    }

    public BeanUtilsException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public @NonNull HttpStatus getStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
