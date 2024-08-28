/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-23 08:12:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-28 17:01:21
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.redis;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLParameters;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;
// import lombok.EqualsAndHashCode;
import redis.clients.jedis.Protocol;

@Data
// @EqualsAndHashCode(callSuper=false)
@Component
@ConfigurationProperties(JedisProperties.CONFIG_PREFIX)
public class JedisProperties {
    // extends JedisPoolConfig

    public static final String CONFIG_PREFIX = "spring.data.redis";

    private String host;
    
    private int port;

    private String password;

    private int database;
    // 
    private int timeout;
    private int soTimeout = Protocol.DEFAULT_TIMEOUT;
    private String clientName;
    private boolean ssl;
    // private SSLSocketFactory sslSocketFactory;
    private SSLParameters sslParameters;
    private HostnameVerifier hostnameVerifier;
}
