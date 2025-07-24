/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-24 19:56:39
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.server;

/**
 * Server type enumeration
 * Defines different types of servers in the system
 */
public enum ServerTypeEnum {
    APPLICATION, // 应用服务器
    DATABASE,    // 数据库服务器
    CACHE,       // 缓存服务器
    LOAD_BALANCER, // 负载均衡器
    WEB_SERVER,  // Web服务器
    FILE_SERVER, // 文件服务器
    MAIL_SERVER, // 邮件服务器
    DNS_SERVER,  // DNS服务器
    PROXY_SERVER, // 代理服务器
    MONITORING,  // 监控服务器
    BACKUP,      // 备份服务器
    GATEWAY,     // 网关服务器
    API_SERVER,  // API服务器
    MESSAGE_QUEUE, // 消息队列服务器
    SEARCH_ENGINE, // 搜索引擎服务器
    CDN,         // CDN服务器
    OTHER;       // 其他类型

    /**
     * Get Chinese name for the server type
     * @return Chinese name
     */
    public String getChineseName() {
        switch (this) {
            case APPLICATION:
                return "应用服务器";
            case DATABASE:
                return "数据库服务器";
            case CACHE:
                return "缓存服务器";
            case LOAD_BALANCER:
                return "负载均衡器";
            case WEB_SERVER:
                return "Web服务器";
            case FILE_SERVER:
                return "文件服务器";
            case MAIL_SERVER:
                return "邮件服务器";
            case DNS_SERVER:
                return "DNS服务器";
            case PROXY_SERVER:
                return "代理服务器";
            case MONITORING:
                return "监控服务器";
            case BACKUP:
                return "备份服务器";
            case GATEWAY:
                return "网关服务器";
            case API_SERVER:
                return "API服务器";
            case MESSAGE_QUEUE:
                return "消息队列服务器";
            case SEARCH_ENGINE:
                return "搜索引擎服务器";
            case CDN:
                return "CDN服务器";
            case OTHER:
                return "其他类型";
            default:
                return this.name();
        }
    }

    /**
     * Convert from string value to enum
     * @param value string value
     * @return ServerTypeEnum
     */
    public static ServerTypeEnum fromValue(String value) {
        for (ServerTypeEnum type : ServerTypeEnum.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No ServerTypeEnum constant with value: " + value);
    }
} 