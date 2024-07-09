/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-06 10:05:25
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-06 11:52:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.keyword;

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.message.MessageTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KeywordRequest extends BaseRequest {
    
    // 可以存储多个关键词："关键词1|关键词2|关键词3"
    private String keyword;

    // 多个关键词可以匹配到同一个回复
    private String reply;
    
    @Builder.Default
    private KeywordMatchEnum matchType = KeywordMatchEnum.EXACT;

    @Builder.Default
    private MessageTypeEnum contentType = MessageTypeEnum.TEXT;

    @Builder.Default
    private Boolean enabled = true;
    
    private String categoryUid;
    
    private String robotUid;
}
