/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-26 20:34:52
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-20 12:45:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.channel;

import com.bytedesk.core.base.BaseEntityNoOrg;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.I18Consts;

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
 * channel 频道 - 类似公众号
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({ ChannelEntityListener.class })
@Table(name = "bytedesk_core_channel")
public class ChannelEntity extends BaseEntityNoOrg {

    private static final long serialVersionUID = 1L;

    private String topic;

    // @Column(name = "channel_type")
    // private String type;

    private String nickname;

    @Builder.Default
    private String avatar = AvatarConsts.getDefaultAvatarUrl();

    @Builder.Default
    private String description = I18Consts.I18N_USER_DESCRIPTION;

    // @Builder.Default
    // private String level = LevelEnum.PLATFORM.name();
}
