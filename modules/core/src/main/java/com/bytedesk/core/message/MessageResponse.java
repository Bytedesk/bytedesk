/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-21 10:00:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-03-04 14:18:57
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;

import com.bytedesk.core.rbac.user.UserResponseSimple;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class MessageResponse {

    @Value("${bytedesk.timezone}")
    private static final String timezone = "GMT+8";

    private String mid;

    /**
     *
     */
    private String type;

    /**
     * 
     */
    private String content;

    /**
     * 
     */
    private String extra;

    /**
     * 
     */
    private String status;

    /**
     * 
     */
    private String client;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = timezone)
    private Date createdAt;


    private UserResponseSimple user;

}
