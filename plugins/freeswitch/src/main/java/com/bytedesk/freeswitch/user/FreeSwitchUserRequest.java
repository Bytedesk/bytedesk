/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-09 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-09 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * FreeSwitch用户请求实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FreeSwitchUserRequest {

    /**
     * 用户名（SIP用户名）
     */
    @NotBlank(message = "用户名不能为空")
    @Size(max = 50, message = "用户名长度不能超过50字符")
    private String username;

    /**
     * SIP域名
     */
    @NotBlank(message = "SIP域名不能为空")
    @Size(max = 100, message = "SIP域名长度不能超过100字符")
    private String domain;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(max = 255, message = "密码长度不能超过255字符")
    private String password;

    /**
     * 显示名称
     */
    @Size(max = 100, message = "显示名称长度不能超过100字符")
    private String displayName;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100字符")
    private String email;

    /**
     * 账户代码
     */
    @Size(max = 50, message = "账户代码长度不能超过50字符")
    private String accountcode;

    /**
     * 是否启用
     */
    @NotNull(message = "启用标志不能为空")
    private Boolean enabled = true;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500字符")
    private String remarks;
}
