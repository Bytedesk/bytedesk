/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-05 21:41:15
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-15 10:22:21
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.clipboard;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.message.MessageTypeEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 剪贴板
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({ClipboardEntityListener.class})
@Table(name = "bytedesk_core_clipboard")
public class ClipboardEntity extends BaseEntity {

    //
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String content;

    @Builder.Default
    @Column(name = "message_type")
    private String type = MessageTypeEnum.TEXT.name();
    
    // 用户uid
    // private String userUid;
    
}
