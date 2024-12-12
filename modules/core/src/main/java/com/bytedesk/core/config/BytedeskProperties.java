/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-30 09:14:39
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-28 17:52:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.Data;

@Data
@Component
@ConfigurationProperties(BytedeskProperties.CONFIG_PREFIX)
public class BytedeskProperties {

    public static final String CONFIG_PREFIX = "bytedesk";

    private static volatile BytedeskProperties instance; // 使用volatile关键字确保可见性

    @PostConstruct
    public void init() {
        // 这里我们使用双重检查锁定来确保线程安全地初始化instance变量
        if (instance == null) {
            synchronized (BytedeskProperties.class) {
                if (instance == null) {
                    instance = this;
                }
            }
        }
    }

    public static BytedeskProperties getInstance() {
        // 如果instance尚未初始化，将调用init方法进行初始化（由Spring的@PostConstruct保证）
        return instance;
    }

    private Boolean debug;
    
    private String email;

    private String password;

    private String passwordDefault;

    private String nickname;

    private String mobile;

    private List<String> mobileWhitelist = new ArrayList<>();
    private List<String> emailWhiteList = new ArrayList<>();

    private String validateCode;
    private String organizationName;
    private String organizationCode;
    // private String timezone;

    // ai
    private Boolean javaAi;
    private Boolean pythonAi;

    // 配置邮件发送方式，默认使用javamail，可选值：javamail/aliyun
    private String emailType;
    // 是否支持注册，默认不支持
    private Boolean enableRegistration;

    // cors
    private String corsAllowedOrigins;

    // jwt
    private String jwtSecretKey;
    private String jwtExpiration;

    // cache
    private Integer cacheLevel;
    private String cachePrefix;
    private String redisStreamKey;

    // upload
    private String uploadType;
    private String uploadDir;
    private String uploadUrl;
    
    // cluster
    private Boolean cluster;
    private List<String> clusterNodes = new ArrayList<>();

    // 
    public Boolean isAdmin(@NonNull String receiver) {
        // receiver 是否为空
        if (receiver == null || receiver.isEmpty()) {
            return false;
        }
        // receiver 是否等于当前用户的手机号或者邮箱
        return receiver.equals(this.mobile) || receiver.equals(this.email);
    }

    // 
    public Boolean isInWhitelist(@NonNull String receiver) {
        // receiver 是否为空
        if (receiver == null || receiver.isEmpty()) {
            return false;
        }
        if (this.mobileWhitelist == null || this.emailWhiteList == null) {
            return false;
        }
        return this.mobileWhitelist.contains(receiver) || this.emailWhiteList.contains(receiver);
    }

}
