/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:06:35
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-08 21:21:24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.customer;

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.constant.I18Consts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRequest extends BaseRequest {

    private String nickname;

    private String email;

    private String mobile;

    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    // private String note;

}
