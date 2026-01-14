/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-29 13:28:14
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.message_unreplied;

import com.bytedesk.core.base.BaseEntity;

import jakarta.persistence.Entity;
// import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
// @NoArgsConstructor
// @EntityListeners({MessageUnrepliedEntityListener.class})
@Table(name = "bytedesk_service_message_unreplied")
public class MessageUnrepliedEntity extends BaseEntity  {

    private static final long serialVersionUID = 1L;

    // 
}