/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-07 20:45:09
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-12 16:41:59
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

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BytedeskPropertiesResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean debug;
    private String version; 
    private String licenseKey; // 注意：这是加密后的licenseKey，前端需要先解密再解码

    // 
    private Custom custom;
    private Features features;
    private Testing testing;

    @Getter
    @Setter
    public static class Custom {
        private Boolean enabled;
        private String name;
        private String logo;
        private String description;
        private Boolean showRightCornerChat;
        private Boolean showDemo = true; // 是否显示演示
        private String privacyPolicyUrl;
        private String termsOfServiceUrl;
        private Boolean loginUsernameEnable = true;
        private Integer loginMaxRetryCount = 3;
        private Integer loginMaxRetryLockTime = 10;
        private Boolean loginMobileEnable = true;
        // private Boolean loginEmailEnable = false;
        private Boolean loginScanEnable = false;
        private Boolean docUrlShow = true;
        private String docUrl;
        // default lang: en-US, zh-CN, zh-TW
        private String lang = "zh-CN";
        // 
        private Boolean allowRegister;
        private Boolean forceValidateMobile;
        private Boolean forceValidateEmail;
        private Boolean forceVisitorAuth; // 是否强制访客认证，默认false
    }

    @Getter
    @Setter
    private static class Testing {
        private Boolean enabled = false;
        // private Integer accountCount = 300;
        // private String accountUsername = "test_username";
        // private String accountPassword = "password";
        private Boolean disableCaptcha = false;
        private Boolean disableIpFilter = false;
    }

    @Getter
    @Setter
    public static class Features {
        // private Integer freeDays = 30;
        private String avatarBaseUrl;
    }
    
}
