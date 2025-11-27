/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-16 19:13:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-02 08:14:30
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.demos.order;

import com.bytedesk.core.base.BaseRequest;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.Builder;

@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest extends BaseRequest {
    
    private static final long serialVersionUID = 1L;

    private String title;

    private String description;

    @Builder.Default
    private Double price = 0.0;

    @Builder.Default
    private String state = OrderStateEnum.WAIT_PAYMENT.name();
}
