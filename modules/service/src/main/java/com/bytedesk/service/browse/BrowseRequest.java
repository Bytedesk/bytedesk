/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:07:32
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-09 16:34:21
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.browse;

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.service.visitor.VisitorStatusEnum;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class BrowseRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    // 来源
    private String referrer;

    // 浏览的URL
    private String url;

    // 浏览的标题
    private String title;

    // 浏览的IP
    private String ip;

    // 浏览的IP地址
    private String ipLocation;

    // 状态
    @Builder.Default
	private String status = VisitorStatusEnum.ONLINE.name();

    private String visitorUid;

}
