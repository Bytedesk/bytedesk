/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-23 07:53:01
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-28 10:37:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.auth;

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.enums.PlatformEnum;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 20580156@qq.com
 * @project bytedesk-im
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class AuthRequest extends BaseRequest {
    
    private static final long serialVersionUID = 1L;

    private String username;
    private String password;

    //
    private String country;
    private String mobile;
    private String email;
    private String code;

    // 
    @NotBlank
    private String platform = PlatformEnum.BYTEDESK.name();

    // 登录验证码
    private String captchaUid;
    private String captchaCode;

    private String deviceUid;
    
    // 客户端类型 WEB, ANDROID, IOS 等
    private String client;
    
    // 设备信息
    private String device;
    // 修改邮箱、手机号需要记录用户uid和组织uid
    private String userUid;

    // 
    public String getReceiver() {
        if (this.isEmail()) {
            return this.getEmail();
        }
        if (this.isMobile()) {
            return this.getMobile();
        }
        return null;
    }

    // 
    public Boolean isEmail() {
        return this.getType().toLowerCase().contains("email");
    }

    public Boolean isMobile() {
        return this.getType().toLowerCase().contains("mobile");
    }

}
