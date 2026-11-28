/*
 * @Author: jackning 270580156@qq.com
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * Copyright (c) 2024 by bytedesk.com. All Rights Reserved. 
 */
package com.bytedesk.ai.provider.ollama.ollama4j;

import com.bytedesk.core.base.BasePermissions;

public class Ollama4jPermissions extends BasePermissions {

    // 模块前缀
    public static final String OLLAMA4J_PREFIX = "OLLAMA4J_";

    // 模块名称，用于权限检查
    public static final String MODULE_NAME = "OLLAMA4J";

    // 统一权限（不再在权限字符串中编码层级）
    public static final String OLLAMA4J_READ = "OLLAMA4J_READ";
    public static final String OLLAMA4J_CREATE = "OLLAMA4J_CREATE";
    public static final String OLLAMA4J_UPDATE = "OLLAMA4J_UPDATE";
    public static final String OLLAMA4J_DELETE = "OLLAMA4J_DELETE";
    public static final String OLLAMA4J_EXPORT = "OLLAMA4J_EXPORT";

    // PreAuthorize 表达式（兼容：ConvertUtils 会为新旧权限互相补齐别名）
    public static final String HAS_OLLAMA4J_READ = "hasAuthority('" + OLLAMA4J_READ + "')";
    public static final String HAS_OLLAMA4J_CREATE = "hasAuthority('" + OLLAMA4J_CREATE + "')";
    public static final String HAS_OLLAMA4J_UPDATE = "hasAuthority('" + OLLAMA4J_UPDATE + "')";
    public static final String HAS_OLLAMA4J_DELETE = "hasAuthority('" + OLLAMA4J_DELETE + "')";
    public static final String HAS_OLLAMA4J_EXPORT = "hasAuthority('" + OLLAMA4J_EXPORT + "')";

}
