/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-26 21:07:22
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-15 11:12:56
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.notice_account;

import com.bytedesk.core.base.BaseResponseNoOrg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class NoticeAccountResponse extends BaseResponseNoOrg {
    
    private String topic;

    private String type;
    
    private String nickname;
    
    private String avatar;

    private String description;
}
