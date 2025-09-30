/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-01 09:28:40
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-15 13:39:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.notice;

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.message.MessageStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder  // 正确使用，因为NoticeRequest继承自BaseRequest
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class NoticeRequest extends BaseRequest {
    
    private static final long serialVersionUID = 1L;
    
    private String title;

    // private String content;

    // @Builder.Default
    // private String type = NoticeTypeEnum.LOGIN.name();

    @Builder.Default  // 与@SuperBuilder配合使用来设置默认值
    private String status = MessageStatusEnum.TRANSFER_PENDING.name();

    @Builder.Default
    private String extra = BytedeskConsts.EMPTY_JSON_STRING;

}
