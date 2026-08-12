/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-08-11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.exception;

/**
 * 黑名单 IP 拦截异常。
 *
 * <p>由 {@code BlackIpAspect} 在检测到请求方 IP 命中黑名单时抛出，
 * 属于已知的访问控制业务异常，{@link GlobalExceptionHandler} 会对该异常单独处理，
 * 仅以 WARN 级别记录摘要信息，不再打印完整堆栈。
 *
 * @author 270580156@qq.com
 */
public class BlackIpException extends BaseException {

    private static final long serialVersionUID = 1L;

    public BlackIpException(String message) {
        super(message);
    }

    public BlackIpException(String message, Throwable cause) {
        super(message, cause);
    }

}
