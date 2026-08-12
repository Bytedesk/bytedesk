/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-08-11 22:10:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-08-11 22:10:00
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
 * IP 在白名单中，不能添加到黑名单时抛出。
 * 
 * 属于可预期的业务冲突，GlobalExceptionHandler 会以 warn 级别记录、不打印堆栈，
 * 并返回 HTTP 409 Conflict。
 */
public class IpInWhitelistException extends BaseException {

    private static final long serialVersionUID = 1L;

    private final String ip;

    public IpInWhitelistException(String ip) {
        super("IP is in whitelist, cannot be added to blacklist");
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }
}
