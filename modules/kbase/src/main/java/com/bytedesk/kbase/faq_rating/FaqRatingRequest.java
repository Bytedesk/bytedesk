/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:26:04
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-29 17:59:48
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.faq_rating;

import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import com.bytedesk.core.message.MessageStatusEnum;

import lombok.Builder;
import lombok.NoArgsConstructor;

@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class FaqRatingRequest extends BaseRequest {

    private String faqUid;

    private String messageUid;

    private String threadUid;

    // 区分是 rateUp 还是 rateDown
    @Builder.Default    
    private String rateType = MessageStatusEnum.RATE_UP.name();
    
    // 点踩的情况下的反馈意见
    @Builder.Default
    private List<String> rateDownTagList = new ArrayList<>();

    // 点踩的原因
    private String rateDownReason;

    // 点踩的用户
    private String user;

}
