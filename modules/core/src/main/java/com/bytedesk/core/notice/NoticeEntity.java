/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-01 09:27:49
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-14 16:25:04
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.notice;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.message.MessageStatusEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 注意与message类型notice区分
 */
@Getter
@Setter
@Entity
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({NoticeEntityListener.class})
@Table(name = "bytedesk_core_notice")
public class NoticeEntity extends BaseEntity {
    
    private String title;

    private String content;

    @Builder.Default
    @Column(name = "notice_type")
    private String type = NoticeTypeEnum.LOGIN.name();

    @Builder.Default
    @Column(name = "notice_status")
    private String status = MessageStatusEnum.TRANSFER_PENDING.name();

    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String extra = BytedeskConsts.EMPTY_JSON_STRING;

}
