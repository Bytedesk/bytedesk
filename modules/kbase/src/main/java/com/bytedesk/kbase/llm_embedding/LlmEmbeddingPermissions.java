/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 16:58:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-06 11:55:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_embedding;

import com.bytedesk.core.base.BasePermissions;

public class LlmEmbeddingPermissions extends BasePermissions {

    // 模块前缀
    public static final String LLM_EMBEDDING_PREFIX = "LLM_EMBEDDING_";

    // 模块名称，用于权限检查
    public static final String MODULE_NAME = "LLM_EMBEDDING";

    // 统一权限（不再在权限字符串中编码层级）
    public static final String LLM_EMBEDDING_READ = "LLM_EMBEDDING_READ";
    public static final String LLM_EMBEDDING_CREATE = "LLM_EMBEDDING_CREATE";
    public static final String LLM_EMBEDDING_UPDATE = "LLM_EMBEDDING_UPDATE";
    public static final String LLM_EMBEDDING_DELETE = "LLM_EMBEDDING_DELETE";
    public static final String LLM_EMBEDDING_EXPORT = "LLM_EMBEDDING_EXPORT";

    // 新 PreAuthorize 表达式（兼容：ConvertUtils 会为新旧权限互相补齐别名）
    public static final String HAS_LLM_EMBEDDING_READ = "hasAuthority('" + LLM_EMBEDDING_READ + "')";
    public static final String HAS_LLM_EMBEDDING_CREATE = "hasAuthority('" + LLM_EMBEDDING_CREATE + "')";
    public static final String HAS_LLM_EMBEDDING_UPDATE = "hasAuthority('" + LLM_EMBEDDING_UPDATE + "')";
    public static final String HAS_LLM_EMBEDDING_DELETE = "hasAuthority('" + LLM_EMBEDDING_DELETE + "')";
    public static final String HAS_LLM_EMBEDDING_EXPORT = "hasAuthority('" + LLM_EMBEDDING_EXPORT + "')";

}
