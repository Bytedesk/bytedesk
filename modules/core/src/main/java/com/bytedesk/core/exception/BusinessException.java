/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-08-31 10:30:00
 * @LastEditors: GitHub Copilot
 * @LastEditTime: 2026-08-31 10:30:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *   selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *   Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *   contact: 270580156@qq.com
 *   联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.core.exception;

import org.springframework.http.HttpStatus;

/**
 * 业务校验异常：旧密码错误、参数不合法等可预期的业务错误。
 * message 约定为 i18n key（如 i18n.old.password.wrong），
 * GlobalExceptionHandler 会将其作为可预期的业务错误处理
 * （warn 级别记录摘要，不打印堆栈，返回本地化文案）。
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 业务错误码，默认 400（客户端输入问题），可选
     */
    private final int code;

    public BusinessException(String message) {
        this(message, HttpStatus.BAD_REQUEST.value());
    }

    public BusinessException(String message, int code) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = HttpStatus.BAD_REQUEST.value();
    }

    public int getCode() {
        return code;
    }
}
