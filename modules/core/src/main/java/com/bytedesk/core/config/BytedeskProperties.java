/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-30 09:14:39
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-03 12:08:11
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
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "bytedesk")
public class BytedeskProperties {

    private Boolean debug;

    // private String username;

    private String email;

    private String password;

    private String passwordDefault;

    private String nickname;

    private String mobile;

    private List<String> mobileWhitelist = new ArrayList<>();

    private String mobileCode;

    private String organizationName;

    private String organizationCode;
    
    // private String timezone;

    // cors
    private String corsAllowedOrigins;

    // jwt
    private String jwtSecretKey;

    private String jwtExpiration;

    // cache
    private Integer cacheLevel;
    private String cachePrefix;

    // upload
    private String uploadType;
    private String uploadDir;
    private String uploadUrl;

    // 
    public Boolean isInWhitelist(String mobile) {
        return this.mobileWhitelist.contains(mobile);
    }

}
