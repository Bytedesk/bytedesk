/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-07 20:45:09
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-10 09:51:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.config.properties;

import java.io.Serializable;

import lombok.Data;

@Data
public class BytedeskPropertiesResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean debug;
    private String edition;
    private String version; 

    // 
    private Custom custom;
    private Admin admin;
    private Features features;
    private Testing testing;

    @Data
    public static class Custom {
        private Boolean enabled;
        private String name;
        private String logo;
        private String description;
        private Boolean showRightCornerChat;
        private String privacyPolicyUrl;
        private String termsOfServiceUrl;
    }

    // 
    @Data
    public static class Admin {
        private Boolean allowRegister;
        private Boolean forceValidateMobile;
        private Boolean forceValidateEmail;
    }

    @Data
    private static class  Testing {
        private Boolean enabled = false;
        private Integer accountCount = 300;
        private String accountUsername = "test";
        private String accountPassword = "password";
        private Boolean disableCaptcha = true;
        private Boolean disableIpFilter = true;
    }

    @Data
    public static class Features {
        // private Integer freeDays = 30;
        private String avatarBaseUrl;
    }
    
}
