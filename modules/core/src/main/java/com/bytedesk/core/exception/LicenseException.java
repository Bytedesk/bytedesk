/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-07-07
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
 * License 相关异常：私钥文件缺失、签名失败等环境/配置问题。
 * GlobalExceptionHandler 会将其作为可预期的配置错误处理（warn 级别，不打印堆栈）。
 */
public class LicenseException extends RuntimeException {

    public LicenseException(String message) {
        super(message);
    }

    public LicenseException(String message, Throwable cause) {
        super(message, cause);
    }
}
