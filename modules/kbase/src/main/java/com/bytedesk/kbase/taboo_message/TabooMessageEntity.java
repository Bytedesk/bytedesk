/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-22 16:51:20
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-13 23:30:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.taboo_message;

import com.bytedesk.core.message.AbstractMessageEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
@EntityListeners({TabooMessageEntityListener.class})
@Table(name = "bytedesk_kbase_taboo_message")
public class TabooMessageEntity  extends AbstractMessageEntity  {

    private static final long serialVersionUID = 1L;

    // 
}