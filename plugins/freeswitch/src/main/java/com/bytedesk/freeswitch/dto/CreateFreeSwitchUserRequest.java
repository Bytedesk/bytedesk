/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-08 16:02:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-08 16:25:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.dto;

import lombok.Data;


import javax.validation.constraints.Size;

/**
 * 创建FreeSwitch用户请求DTO
 */
@Data
public class CreateFreeSwitchUserRequest {
    
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 100, message = "密码长度必须在6-100个字符之间")
    private String password;
    
    @NotBlank(message = "域名不能为空")
    private String domain;
    
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @Size(max = 100, message = "显示名称不能超过100个字符")
    private String displayName;
    
    private Boolean enabled = true;
}
