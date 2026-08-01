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
package com.bytedesk.call.xml_curl_trace;

import com.bytedesk.core.base.BasePermissions;

public class XmlCurlTracePermissions extends BasePermissions {

    // 模块前缀
    public static final String XML_CURL_TRACE_PREFIX = "XML_CURL_TRACE_";

    // 模块名称，用于权限检查
    public static final String MODULE_NAME = "XML_CURL_TRACE";

    // 统一权限（不再在权限字符串中编码层级）
    public static final String XML_CURL_TRACE_READ = "XML_CURL_TRACE_READ";
    public static final String XML_CURL_TRACE_CREATE = "XML_CURL_TRACE_CREATE";
    public static final String XML_CURL_TRACE_UPDATE = "XML_CURL_TRACE_UPDATE";
    public static final String XML_CURL_TRACE_DELETE = "XML_CURL_TRACE_DELETE";
    public static final String XML_CURL_TRACE_EXPORT = "XML_CURL_TRACE_EXPORT";

    // 新 PreAuthorize 表达式（兼容：ConvertUtils 会为新旧权限互相补齐别名）
    public static final String HAS_XML_CURL_TRACE_READ = "hasAuthority('" + XML_CURL_TRACE_READ + "')";
    public static final String HAS_XML_CURL_TRACE_CREATE = "hasAuthority('" + XML_CURL_TRACE_CREATE + "')";
    public static final String HAS_XML_CURL_TRACE_UPDATE = "hasAuthority('" + XML_CURL_TRACE_UPDATE + "')";
    public static final String HAS_XML_CURL_TRACE_DELETE = "hasAuthority('" + XML_CURL_TRACE_DELETE + "')";
    public static final String HAS_XML_CURL_TRACE_EXPORT = "hasAuthority('" + XML_CURL_TRACE_EXPORT + "')";

}
