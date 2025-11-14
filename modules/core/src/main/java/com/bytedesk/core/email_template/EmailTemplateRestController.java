/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-28 10:40:27
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 17:41:39
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.email_template;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;

@RestController
@RequestMapping("/api/v1/email-template")
public class EmailTemplateRestController extends BaseRestController<EmailTemplateRequest, EmailTemplateService> {


    
}
