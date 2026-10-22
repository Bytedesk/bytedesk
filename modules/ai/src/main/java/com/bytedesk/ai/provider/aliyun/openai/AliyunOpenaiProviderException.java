/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-10-21 09:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-10-21 09:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.ai.provider.aliyun.openai;

/**
 * 阿里云百炼 OpenAI 兼容模式配置/开关错误。
 *
 * <p>用于区分“配置错误/功能关闭”与“远端调用失败”：前者必须在 HTTP 出站前抛出，
 * 调用方据此走明确错误路径，不得回退到其他供应商或默认模型（规划 B1/B4/B5）。</p>
 */
public class AliyunOpenaiProviderException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AliyunOpenaiProviderException(String message) {
        super(message);
    }

    public AliyunOpenaiProviderException(String message, Throwable cause) {
        super(message, cause);
    }

}
