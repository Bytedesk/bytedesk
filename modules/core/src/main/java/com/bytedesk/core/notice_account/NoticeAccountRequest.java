/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-26 21:07:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-05 14:09:40
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.notice_account;

import com.bytedesk.core.base.BaseRequestNoOrg;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class NoticeAccountRequest extends BaseRequestNoOrg {
    
    private String topic;
    
    private String nickname;
    
    private String avatar;

    private String description;

    // @Builder.Default
    // private String level = LevelEnum.PLATFORM.name();
}
