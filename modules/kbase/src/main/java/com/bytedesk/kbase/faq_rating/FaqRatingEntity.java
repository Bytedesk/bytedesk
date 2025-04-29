/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-29 17:48:59
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

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.converter.StringListConverter;
import com.bytedesk.core.message.MessageStatusEnum;
import com.bytedesk.kbase.faq.FaqEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
// @AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({FaqRatingEntityListener.class})
@Table(name = "bytedesk_service_faq_rating")
public class FaqRatingEntity extends BaseEntity  {

    private static final long serialVersionUID = 1L;

    private String messageUid;

    private String threadUid;

    // 区分是 rateUp 还是 rateDown
    @Builder.Default    
    private String rateType = MessageStatusEnum.RATE_UP.name();
    
    // 点踩的情况下的反馈意见
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> rateDownTagList = new ArrayList<>();

    // 点踩的原因
    private String rateDownReason;

    // 关联FaqEntity
    @ManyToOne(fetch = FetchType.LAZY)
    private FaqEntity faq;

    // 评价者信息的JSON表示
    @Builder.Default
    @Column(name = "rating_user", length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String user = BytedeskConsts.EMPTY_JSON_STRING;
}