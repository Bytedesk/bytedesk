/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-07-28 19:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-07-28 19:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.exception;

/**
 * Thrown when an HTTP REST call fails due to connectivity issues
 * (e.g. proxy unreachable, DNS failure, connection timeout).
 * <p>
 * The stack trace is intentionally suppressed because these errors are
 * typically infrastructure-level and do not indicate a code defect.
 * The exception message contains the URL and the underlying cause.
 */
public class HttpRestAccessException extends BaseException {

    private static final long serialVersionUID = 202607281900L;

    public HttpRestAccessException(String url, String method, Throwable cause) {
        super("HTTP " + method + " " + url + " failed: " + cause.getMessage(), cause);
    }

    /**
     * Suppress the stack trace to avoid polluting logs with
     * infrastructure-level connectivity errors.
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
