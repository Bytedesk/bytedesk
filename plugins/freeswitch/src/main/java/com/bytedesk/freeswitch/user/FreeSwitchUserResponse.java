/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-08 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-08 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.user;

import com.bytedesk.core.base.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * FreeSwitch用户响应实体
 */
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class FreeSwitchUserResponse extends BaseResponse {

    /**
     * 用户名（SIP用户名）
     */
    private String username;

    /**
     * SIP域名
     */
    private String domain;

    /**
     * 显示名称
     */
    private String displayName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 账户代码
     */
    private String accountcode;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 最后注册时间
     */
    private LocalDateTime lastRegister;

    /**
     * 注册IP地址
     */
    private String registerIp;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 备注
     */
    private String remarks;

    /**
     * SIP地址
     */
    private String sipAddress;

    /**
     * 是否在线
     */
    private Boolean online;

}
