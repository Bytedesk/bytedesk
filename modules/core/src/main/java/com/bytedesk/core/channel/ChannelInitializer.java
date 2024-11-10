/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 13:43:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-06 10:21:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.channel;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.topic.TopicUtils;

// import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;

@Component
@Order(2)
@AllArgsConstructor
public class ChannelInitializer implements SmartInitializingSingleton {

    private final ChannelRepository channelRepository;

    private final ChannelService channelService;

    @Override
    public void afterSingletonsInstantiated() {
        init();
    }

    // @PostConstruct
    public void init() {

        if (channelRepository.count() > 0) {
            return;
        }

        ChannelRequest channelRequest = ChannelRequest.builder()
                .topic(TopicUtils.TOPIC_SYSTEM_NOTIFICATION)
                .nickname(I18Consts.I18N_SYSTEM_NOTIFICATION_NAME)
                .avatar(AvatarConsts.DEFAULT_SYSTEM_NOTIFICATION_AVATAR_URL)
                .description(I18Consts.I18N_SYSTEM_NOTIFICATION_DESCRIPTION)
                .build();
        channelRequest.setUid(BytedeskConsts.DEFAULT_CHANNEL_UID);
        channelRequest.setLevel(LevelEnum.PLATFORM.name());
        // channelRequest.setType(TypeConsts.TYPE_SYSTEM);
        // channelRequest.setOrgUid(BytedeskConsts.DEFAULT_ORGANIZATION_UID);
        channelService.create(channelRequest);
    }
    
}
